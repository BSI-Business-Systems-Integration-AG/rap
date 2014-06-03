/*******************************************************************************
 * Copyright (c) 2002, 2014 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.service;

import static javax.servlet.http.HttpServletResponse.SC_FORBIDDEN;
import static javax.servlet.http.HttpServletResponse.SC_PRECONDITION_FAILED;
import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.RWT_INITIALIZE;
import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.SHUTDOWN;
import static org.eclipse.rap.rwt.internal.service.ContextProvider.getContext;
import static org.eclipse.rap.rwt.internal.service.ContextProvider.getServiceStore;
import static org.eclipse.rap.rwt.internal.service.ContextProvider.getUISession;
import static org.eclipse.rap.rwt.internal.util.HTTP.CHARSET_UTF_8;
import static org.eclipse.rap.rwt.internal.util.HTTP.CONTENT_TYPE_JSON;
import static org.eclipse.rap.rwt.internal.util.HTTP.METHOD_POST;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.json.JsonValue;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.client.WebClient;
import org.eclipse.rap.rwt.internal.lifecycle.RequestCounter;
import org.eclipse.rap.rwt.internal.protocol.ClientMessage;
import org.eclipse.rap.rwt.internal.protocol.ProtocolMessageWriter;
import org.eclipse.rap.rwt.internal.protocol.ProtocolUtil;
import org.eclipse.rap.rwt.internal.protocol.RequestMessage;
import org.eclipse.rap.rwt.internal.protocol.ResponseMessage;
import org.eclipse.rap.rwt.internal.remote.MessageChainReference;
import org.eclipse.rap.rwt.service.ServiceHandler;
import org.eclipse.rap.rwt.service.UISession;


public class LifeCycleServiceHandler implements ServiceHandler {

  private static final String PROP_ERROR = "error";
  private static final String PROP_REQUEST_COUNTER = "requestCounter";
  private static final String ATTR_LAST_RESPONSE_MESSAGE
    = LifeCycleServiceHandler.class.getName() + "#lastResponseMessage";
  private static final String ATTR_SESSION_STARTED
    = LifeCycleServiceHandler.class.getName() + "#isSessionStarted";

  private final MessageChainReference messageChainReference;
  private final StartupPage startupPage;

  public LifeCycleServiceHandler( MessageChainReference messageChainReference,
                                  StartupPage startupPage )
  {
    this.messageChainReference = messageChainReference;
    this.startupPage = startupPage;
  }

  public void service( HttpServletRequest request, HttpServletResponse response )
    throws IOException
  {
    // Do not use session store itself as a lock
    // see bug https://bugs.eclipse.org/bugs/show_bug.cgi?id=372946
    UISessionImpl uiSession = ( UISessionImpl )getUISession();
    synchronized( uiSession.getRequestLock() ) {
      synchronizedService( request, response );
    }
  }

  void synchronizedService( HttpServletRequest request, HttpServletResponse response )
    throws IOException
  {
    if( METHOD_POST.equals( request.getMethod() ) && isContentTypeValid( request ) ) {
      handleUIRequest( request, response );
    } else {
      handleStartupRequest( request, response );
    }
  }

  private void handleStartupRequest( HttpServletRequest request, HttpServletResponse response )
    throws IOException
  {
    try {
      sendStartupContent( request, response );
    } finally {
      // The GET request currently creates a dummy UI session needed for accessing the client
      // information. It is not meant to be reused by other requests.
      shutdownUISession();
    }
  }

  private void handleUIRequest( HttpServletRequest request, HttpServletResponse response )
    throws IOException
  {
    try {
      processUIRequest( request, response );
    } catch( IOException exception ) {
      shutdownUISession();
      throw exception;
    } catch( RuntimeException exception ) {
      shutdownUISession();
      throw exception;
    }
  }

  private void sendStartupContent( ServletRequest request, HttpServletResponse response )
    throws IOException
  {
    if( RWT.getClient() instanceof WebClient ) {
      startupPage.send( response );
    } else {
      StartupJson.send( response );
    }
  }

  private void processUIRequest( HttpServletRequest request, HttpServletResponse response )
    throws IOException
  {
    RequestMessage requestMessage = readRequestMessage( request );
    setJsonResponseHeaders( response );
    if( isSessionShutdown( requestMessage ) ) {
      shutdownUISession();
      writeEmptyMessage( response );
    } else if( isSessionTimeout( requestMessage ) ) {
      writeSessionTimeoutError( response );
    } else if( !isRequestCounterValid( requestMessage ) ) {
      if( isDuplicateRequest( requestMessage ) ) {
        writeBufferedResponse( response );
      } else {
        writeInvalidRequestCounterError( response );
      }
    } else {
      if( isSessionRestart( requestMessage ) ) {
        reinitializeUISession( request );
        reinitializeServiceStore();
      }
      UrlParameters.merge( requestMessage );
      ResponseMessage responseMessage = processMessage( requestMessage );
      writeResponseMessage( responseMessage, response );
      markSessionStarted();
    }
  }

  private RequestMessage readRequestMessage( HttpServletRequest request ) {
    try {
      return new ClientMessage( JsonObject.readFrom( getReader( request ) ) );
    } catch( IOException ioe ) {
      throw new IllegalStateException( "Unable to read the json message", ioe );
    }
  }

  /*
   * Workaround for bug in certain servlet containers where the reader is sometimes empty.
   * 411616: Application crash with very long messages
   * https://bugs.eclipse.org/bugs/show_bug.cgi?id=411616
   */
  private static Reader getReader( HttpServletRequest request ) throws IOException {
    String encoding = request.getCharacterEncoding();
    if( encoding == null ) {
      encoding = CHARSET_UTF_8;
    }
    return new InputStreamReader( request.getInputStream(), encoding );
  }

  private ResponseMessage processMessage( RequestMessage requestMessage ) {
    return messageChainReference.get().handleMessage( requestMessage );
  }

  private static boolean isRequestCounterValid( RequestMessage requestMessage ) {
    return hasInitializeParameter( requestMessage ) || hasValidRequestCounter( requestMessage );
  }

  static boolean hasValidRequestCounter( RequestMessage requestMessage ) {
    int currentRequestId = RequestCounter.getInstance().currentRequestId();
    JsonValue sentRequestId = requestMessage.getHead().get( PROP_REQUEST_COUNTER );
    if( sentRequestId == null ) {
      return currentRequestId == 0;
    }
    return currentRequestId == sentRequestId.asInt();
  }

  private static boolean isDuplicateRequest( RequestMessage requestMessage ) {
    int currentRequestId = RequestCounter.getInstance().currentRequestId();
    JsonValue sentRequestId = requestMessage.getHead().get( PROP_REQUEST_COUNTER );
    return sentRequestId != null && sentRequestId.asInt() == currentRequestId - 1;
  }

  private static boolean isContentTypeValid( ServletRequest request ) {
    String contentType = request.getContentType();
    return contentType != null && contentType.startsWith( CONTENT_TYPE_JSON );
  }

  private static void shutdownUISession() {
    UISessionImpl uiSession = ( UISessionImpl )getUISession();
    uiSession.shutdown();
  }

  private static void writeInvalidRequestCounterError( HttpServletResponse response )
    throws IOException
  {
    writeError( response, SC_PRECONDITION_FAILED, "invalid request counter" );
  }

  private static void writeSessionTimeoutError( HttpServletResponse response ) throws IOException {
    writeError( response, SC_FORBIDDEN, "session timeout" );
  }

  private static void writeError( HttpServletResponse response,
                                  int statusCode,
                                  String errorType ) throws IOException
  {
    response.setStatus( statusCode );
    ProtocolMessageWriter writer = new ProtocolMessageWriter();
    writer.appendHead( PROP_ERROR, JsonValue.valueOf( errorType ) );
    writer.createMessage().toJson().writeTo( response.getWriter() );
  }

  private static void reinitializeUISession( HttpServletRequest request ) {
    ServiceContext serviceContext = getContext();
    UISessionImpl uiSession = ( UISessionImpl )getUISession();
    uiSession.shutdown();
    UISessionBuilder builder = new UISessionBuilder( serviceContext );
    uiSession = builder.buildUISession();
    serviceContext.setUISession( uiSession );
  }

  private static void reinitializeServiceStore() {
    ClientMessage clientMessage = ProtocolUtil.getClientMessage();
    getServiceStore().clear();
    ProtocolUtil.setClientMessage( clientMessage );
  }

  /*
   * Session restart: we're in the same HttpSession and start over (e.g. by pressing F5)
   */
  private static boolean isSessionRestart( RequestMessage requestMessage ) {
    return isSessionStarted() && hasInitializeParameter( requestMessage );
  }

  private static boolean isSessionTimeout( RequestMessage message ) {
    // Session is not initialized because we got a new HTTPSession
    return !isSessionStarted() && !hasInitializeParameter( message );
  }

  static void markSessionStarted() {
    getUISession().setAttribute( ATTR_SESSION_STARTED, Boolean.TRUE );
  }

  private static boolean isSessionStarted() {
    return Boolean.TRUE.equals( getUISession().getAttribute( ATTR_SESSION_STARTED ) );
  }

  private static boolean isSessionShutdown( RequestMessage requestMessage ) {
    return JsonValue.TRUE.equals( requestMessage.getHead().get( SHUTDOWN ) );
  }

  private static boolean hasInitializeParameter( RequestMessage requestMessage ) {
    return JsonValue.TRUE.equals( requestMessage.getHead().get( RWT_INITIALIZE ) );
  }

  private static void setJsonResponseHeaders( ServletResponse response ) {
    response.setContentType( CONTENT_TYPE_JSON );
    response.setCharacterEncoding( CHARSET_UTF_8 );
  }

  private static void writeEmptyMessage( ServletResponse response ) throws IOException {
    new ProtocolMessageWriter().createMessage().toJson().writeTo( response.getWriter() );
  }

  private static void writeResponseMessage( ResponseMessage responseMessage,
                                            ServletResponse response )
    throws IOException
  {
    bufferMessage( responseMessage );
    responseMessage.toJson().writeTo( response.getWriter() );
  }

  private static void writeBufferedResponse( HttpServletResponse response ) throws IOException {
    getBufferedMessage().toJson().writeTo( response.getWriter() );
  }

  private static void bufferMessage( ResponseMessage responseMessage ) {
    UISession uiSession = getUISession();
    if( uiSession != null ) {
      uiSession.setAttribute( ATTR_LAST_RESPONSE_MESSAGE, responseMessage );
    }
  }

  private static ResponseMessage getBufferedMessage() {
    return ( ResponseMessage )getUISession().getAttribute( ATTR_LAST_RESPONSE_MESSAGE );
  }

}
