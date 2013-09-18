/*******************************************************************************
 * Copyright (c) 2002, 2013 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.internal.browser.browserkit;

import static org.eclipse.rap.rwt.internal.protocol.JsonUtil.createJsonArray;
import static org.eclipse.rap.rwt.internal.protocol.RemoteObjectFactory.createRemoteObject;
import static org.eclipse.rap.rwt.internal.protocol.RemoteObjectFactory.getRemoteObject;
import static org.eclipse.rap.rwt.internal.service.ContextProvider.getApplicationContext;
import static org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil.getStyles;
import static org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil.preserveListener;
import static org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil.renderListener;
import static org.eclipse.rap.rwt.lifecycle.WidgetUtil.getId;
import static org.eclipse.swt.internal.events.EventLCAUtil.isListening;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.internal.lifecycle.LifeCycle;
import org.eclipse.rap.rwt.internal.lifecycle.LifeCycleUtil;
import org.eclipse.rap.rwt.internal.protocol.JsonUtil;
import org.eclipse.rap.rwt.internal.service.ContextProvider;
import org.eclipse.rap.rwt.internal.service.ServiceStore;
import org.eclipse.rap.rwt.lifecycle.AbstractWidgetLCA;
import org.eclipse.rap.rwt.lifecycle.ControlLCAUtil;
import org.eclipse.rap.rwt.lifecycle.PhaseEvent;
import org.eclipse.rap.rwt.lifecycle.PhaseId;
import org.eclipse.rap.rwt.lifecycle.PhaseListener;
import org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil;
import org.eclipse.rap.rwt.lifecycle.WidgetUtil;
import org.eclipse.rap.rwt.remote.RemoteObject;
import org.eclipse.rap.rwt.service.ResourceManager;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.internal.events.EventTypes;
import org.eclipse.swt.internal.widgets.IBrowserAdapter;
import org.eclipse.swt.widgets.Widget;


public final class BrowserLCA extends AbstractWidgetLCA {

  private static final String TYPE = "rwt.widgets.Browser";
  private static final String[] ALLOWED_STYLES = new String[] { "BORDER" };

  // Background color is set due to bug 401300 and 401278
  static final String BLANK_HTML
    = "<html><script></script><body style=\"background-color: transparent;\"></body></html>";

  public static final String EVENT_PROGRESS = "Progress";

  private static final String PARAM_PROGRESS_LISTENER = "Progress";
  private static final String PARAM_SCRIPT = "script";
  private static final String METHOD_EVALUATE = "evaluate";
  private static final String PARAM_FUNCTIONS = "functions";
  private static final String METHOD_CREATE_FUNCTIONS = "createFunctions";
  private static final String METHOD_DESTROY_FUNCTIONS = "destroyFunctions";
  private static final String PARAM_FUNCTION_RESULT = "functionResult";

  private static final String PREFIX = Browser.class.getName();
  static final String EXECUTED_FUNCTION_NAME = PREFIX.concat( "#executedFunctionName." );
  static final String EXECUTED_FUNCTION_RESULT = PREFIX.concat( "#executedFunctionResult." );
  static final String EXECUTED_FUNCTION_ERROR = PREFIX.concat( "#executedFunctionError." );
  private static final String FUNCTIONS_TO_CREATE = PREFIX.concat( "#functionsToCreate." );
  private static final String FUNCTIONS_TO_DESTROY = PREFIX.concat( "#functionsToDestroy." );

  @Override
  public void preserveValues( Widget widget ) {
    Browser browser = ( Browser )widget;
    ControlLCAUtil.preserveValues( browser );
    WidgetLCAUtil.preserveCustomVariant( browser );
    preserveListener( browser, PARAM_PROGRESS_LISTENER, hasProgressListener( browser ) );
  }

  @Override
  public void renderInitialization( Widget widget ) throws IOException {
    Browser browser = ( Browser )widget;
    RemoteObject remoteObject = createRemoteObject( browser, TYPE );
    remoteObject.setHandler( new BrowserOperationHandler( browser ) );
    remoteObject.set( "parent", getId( browser.getParent() ) );
    remoteObject.set( "style", createJsonArray( getStyles( browser, ALLOWED_STYLES ) ) );
  }

  @Override
  public void renderChanges( Widget widget ) throws IOException {
    Browser browser = ( Browser )widget;
    ControlLCAUtil.renderChanges( browser );
    WidgetLCAUtil.renderCustomVariant( browser );
    destroyBrowserFunctions( browser );
    renderUrl( browser );
    createBrowserFunctions( browser );
    renderEvaluate( browser );
    renderFunctionResult( browser );
    renderListener( browser, PARAM_PROGRESS_LISTENER, hasProgressListener( browser ), false );
  }

  private static void renderUrl( Browser browser ) throws IOException {
    if( hasUrlChanged( browser ) ) {
      getRemoteObject( browser ).set( "url", getUrl( browser ) );
      browser.getAdapter( IBrowserAdapter.class ).resetUrlChanged();
    }
  }

  static boolean hasUrlChanged( Browser browser ) {
    boolean initialized = WidgetUtil.getAdapter( browser ).isInitialized();
    return !initialized || browser.getAdapter( IBrowserAdapter.class ).hasUrlChanged();
  }

  static String getUrl( Browser browser ) throws IOException {
    String text = getText( browser );
    String url = browser.getUrl();
    String result;
    if( !"".equals( text.trim() ) ) {
      result = registerHtml( text );
    } else if( !"".equals( url.trim() ) ) {
      result = url;
    } else {
      result = registerHtml( BLANK_HTML );
    }
    return result;
  }

  private static void renderEvaluate( final Browser browser ) {
    IBrowserAdapter adapter = browser.getAdapter( IBrowserAdapter.class );
    final String executeScript = adapter.getExecuteScript();
    boolean executePending = adapter.getExecutePending();
    if( executeScript != null && !executePending ) {
      // [if] Put the execution to the end of the rendered script. This is very
      // important when Browser#execute is called from within a BrowserFunction,
      // because then we have a synchronous requests.
      final LifeCycle lifeCycle = getApplicationContext().getLifeCycleFactory().getLifeCycle();
      lifeCycle.addPhaseListener( new PhaseListener() {
        public void beforePhase( PhaseEvent event ) {
        }
        public void afterPhase( PhaseEvent event ) {
          if( browser.getDisplay() == LifeCycleUtil.getSessionDisplay() ) {
            try {
              JsonObject parameters = new JsonObject().add( PARAM_SCRIPT, executeScript );
              getRemoteObject( browser ).call( METHOD_EVALUATE, parameters );
            } finally {
              lifeCycle.removePhaseListener( this );
            }
          }
        }
        public PhaseId getPhaseId() {
          return PhaseId.RENDER;
        }
      } );
      adapter.setExecutePending( true );
    }
  }

  private static String registerHtml( String html ) throws IOException {
    String name = createUrlFromHtml( html );
    byte[] bytes = html.getBytes( "UTF-8" );
    InputStream inputStream = new ByteArrayInputStream( bytes );
    ResourceManager resourceManager = RWT.getResourceManager();
    resourceManager.register( name, inputStream );
    return resourceManager.getLocation( name );
  }

  private static String createUrlFromHtml( String html ) {
    StringBuilder result = new StringBuilder();
    result.append( "org.eclipse.swt.browser/text" );
    result.append( String.valueOf( html.hashCode() ) );
    result.append( ".html" );
    return result.toString();
  }

  private static String getText( Browser browser ) {
    Object adapter = browser.getAdapter( IBrowserAdapter.class );
    IBrowserAdapter browserAdapter = ( IBrowserAdapter )adapter;
    return browserAdapter.getText();
  }

  //////////////////////////////////////
  // Helping methods for BrowserFunction

  private static void createBrowserFunctions( Browser browser ) {
    ServiceStore serviceStore = ContextProvider.getServiceStore();
    String id = WidgetUtil.getId( browser );
    String[] functions = ( String[] )serviceStore.getAttribute( FUNCTIONS_TO_CREATE + id );
    if( functions != null ) {
      JsonObject parameters = new JsonObject()
        .add( PARAM_FUNCTIONS, JsonUtil.createJsonArray( functions ) );
      getRemoteObject( browser ).call( METHOD_CREATE_FUNCTIONS, parameters );
    }
  }

  private static void destroyBrowserFunctions( Browser browser ) {
    ServiceStore serviceStore = ContextProvider.getServiceStore();
    String id = WidgetUtil.getId( browser );
    String[] functions = ( String[] )serviceStore.getAttribute( FUNCTIONS_TO_DESTROY + id );
    if( functions != null ) {
      JsonObject parameters = new JsonObject()
        .add( PARAM_FUNCTIONS, JsonUtil.createJsonArray( functions ) );
      getRemoteObject( browser ).call( METHOD_DESTROY_FUNCTIONS, parameters );
    }
  }

  private static void renderFunctionResult( Browser browser ) {
    ServiceStore serviceStore = ContextProvider.getServiceStore();
    String id = getId( browser );
    String name = ( String )serviceStore.getAttribute( EXECUTED_FUNCTION_NAME + id );
    if( name != null ) {
      Object result = serviceStore.getAttribute( EXECUTED_FUNCTION_RESULT + id );
      String error = ( String )serviceStore.getAttribute( EXECUTED_FUNCTION_ERROR + id );
      Object[] value = new Object[] {
        name, result, error
      };
      getRemoteObject( browser ).set( PARAM_FUNCTION_RESULT, createJsonArray( value ) );
    }
  }

  private boolean hasProgressListener( Browser browser ) {
    return isListening( browser, EventTypes.PROGRESS_CHANGED )
        || isListening( browser, EventTypes.PROGRESS_COMPLETED );
  }

}
