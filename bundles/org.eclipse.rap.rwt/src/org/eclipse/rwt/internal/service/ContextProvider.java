/*******************************************************************************
 * Copyright (c) 2002, 2011 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.service;

import java.util.Map;
import java.util.WeakHashMap;

import javax.servlet.http.*;

import org.eclipse.rwt.internal.SingletonManager;
import org.eclipse.rwt.internal.protocol.ProtocolMessageWriter;
import org.eclipse.rwt.internal.util.ParamCheck;
import org.eclipse.rwt.service.IServiceStore;
import org.eclipse.rwt.service.ISessionStore;


/**
 * <p>This class enables application wide access to the context of the
 * currently processed request during the service handler execution.</p>
 *
 * <p>Note: It is possible to register a context to a thread that isn't the
 * request thread (in particular not the current running thread). This may be
 * useful to enable background processes the access to data that is stored in a
 * session the same way as it is done in the request thread.</p>
 *
 * <p>Note: In case that a context was already added using the
 * <code>setContext(ServiceContext)</code> method and it's tried to add another
 * context using the <code>setContext(ServiceContext,Thread)</code> no
 * <code>IllegalStateException</code> will be thrown. This is because due to
 * implementation details a check is not possible. In such a case the
 * context added with <code>setContext(ServiceContext)</code> will be
 * preferred.</p>
 */
public class ContextProvider {

  // The context mapping mechanism used in standard UI requests from
  // the client is the CONTEXT_HOLDER.
  private final static ThreadLocal<ServiceContext> CONTEXT_HOLDER
    = new ThreadLocal<ServiceContext>();

  // For background threads that need access to data stored in the session
  // a context can be mapped from outside the thread execution.
  // Herefore the CONTEXT_HOLDER_FOR_BG_THREADS is used. In theory it would
  // be possible to use the map also to replace the CONTEXT_HOLDER, but
  // due to the smaller synchronization impact the thread local mechanism
  // stays in place for the most common usecase.
  private final static Map<Thread,ServiceContext> CONTEXT_HOLDER_FOR_BG_THREADS
    = new WeakHashMap<Thread,ServiceContext>();

  /**
   * Maps the {@link ServiceContext} to the currently
   * processed request.
   *
   * <p>Note: to dispose of contexts that are added with this method
   * use <code>disposeContext()</code>. </p>
   */
  public static void setContext( ServiceContext context ) {
    ParamCheck.notNull( context, "context" );
    if( getContextInternal() != null ) {
      String msg = "Current thread has already a context instance buffered.";
      throw new IllegalStateException( msg );
    }
    CONTEXT_HOLDER.set( context );
  }

  /**
   * Maps the {@link ServiceContext} to the specified
   * thread. This may be useful to allow background processes access
   * to data stored in the session.
   *
   * <p>Note: to dispose of contexts that are mapped with this method
   * use <code>disposeContext(Thread)</code>. In case you want to map
   * the context to the current thread use
   * <code>setContext(ServiceContext)</code> instead.</p>
   */
  public static void setContext( ServiceContext context, Thread thread ) {
    ParamCheck.notNull( context, "context" );
    ParamCheck.notNull( thread, "thread" );
    synchronized( CONTEXT_HOLDER_FOR_BG_THREADS ){
      if( CONTEXT_HOLDER_FOR_BG_THREADS.containsKey( thread ) ) {
        String msg = "The given thread has already a context instance mapped.";
        throw new IllegalStateException( msg );
      }
      CONTEXT_HOLDER_FOR_BG_THREADS.put( thread, context );
    }
  }

  /**
   * Returns the {@link ServiceContext} mapped to the currently
   * processed request.
   */
  public static ServiceContext getContext() {
    ServiceContext result = getContextInternal();
    if( result == null ) {
      String msg = "No context available outside of the request service lifecycle.";
      throw new IllegalStateException( msg );
    }
    return result;
  }

  /**
   * Returns the <code>HttpServletRequest</code> that is currently
   * processed. This is a convenience method that delegates to
   * <code>ContextProvider.getContext().getRequest()</code>;
   */
  public static HttpServletRequest getRequest() {
    return getContext().getRequest();
  }

  /**
   * Returns the <code>HttpServletResponse</code> that is mapped
   * to the currently processed request. This is a convenience method
   * that delegates to <code>ContextProvider.getContext().getResponse()</code>;
   */
  public static HttpServletResponse getResponse() {
    return getContext().getResponse();
  }

  /**
   * Returns the <code>ISessionStore</code> of the <code>HttpSession</code>
   * to which the currently processed request belongs.
   */
  public static ISessionStore getSessionStore() {
    SessionStoreImpl result = ( SessionStoreImpl )getContext().getSessionStore();
    if( result == null ) {
      HttpSession httpSession = getRequest().getSession( true );
      result = SessionStoreImpl.getInstanceFromSession( httpSession );
      if( result == null ) {
        result = new SessionStoreImpl( httpSession );
        SessionStoreImpl.attachInstanceToSession( httpSession, result );
        SingletonManager.install( result );
      }
      result.attachHttpSession( httpSession );
      getContext().setSessionStore( result );
    }
    return result;
  }

  /**
   * Returns the {@link IServiceStore} that is mapped to the currently processed
   * request. This is a convenience method that delegates to
   * <code>ContextProvider.getContext().getServiceStore()</code>;
   */
  public static IServiceStore getServiceStore() {
    return getContext().getServiceStore();
  }

  /**
   * Returns the protocol writer for the current request. This is a convenience method that
   * delegates to <code>ContextProvider.getContext().getProtocolWriter()</code>;
   */
  public static ProtocolMessageWriter getProtocolWriter() {
    return getContext().getProtocolWriter();
  }

  /**
   * Releases the currently buffered context instance. Note that this is
   * automatically called by the library to end the context's lifecycle.
   * A premature call will cause failure of the currently processed
   * request lifecycle.
   *
   * <p>Note: only <code>ServiceContext</code> instances that where mapped
   * by calling <code>setContext()</code> from the running thread can be
   * disposed of using this method. Contexts that were registered by
   * <code>setContext(Thread,ServiceContext)</code> must be disposed of
   * by using <code>disposeContext(Thread)</code>.</p>
   */
  public static void disposeContext() {
    ServiceContext context = CONTEXT_HOLDER.get();
    if( context != null ) {
      if( !context.isDisposed() ) {
        context.dispose();
      }
    }
    // DO NOT MOVE THIS LINE INTO THE IF BLOCK
    // This would cause a memory leak as disposeContext() is used to dispose
    // of a context *and* disassociate the context from the thread
    releaseContextHolder();
  }

  public static boolean releaseContextHolder() {
    boolean result = false;
    Object object = CONTEXT_HOLDER.get();
    if( object != null ) {
      CONTEXT_HOLDER.set( null );
    } else {
      synchronized( CONTEXT_HOLDER_FOR_BG_THREADS ) {
        ServiceContext toRemove = CONTEXT_HOLDER_FOR_BG_THREADS.get( Thread.currentThread() );
        if( toRemove != null ) {
          CONTEXT_HOLDER_FOR_BG_THREADS.remove( Thread.currentThread() );
        }
      }
      result = true;
    }
    return result;
  }

  /**
   * Releases the association between a thread and it's context. This may be
   * useful in applications that use background processing that needs
   * access to data stored in session contexts.
   *
   * <p>Note: only <code>ServiceContext</code> instances that were mapped
   * with the <code>setContext(Thread,ServiceContext)</code> method can be
   * disposed by this method. Contexts that were registered by
   * <code>setContext(ServiceContext)</code> by the running thread
   * must be disposed of by calling <code>disposeContext()</code> from the
   * same thread.</p>
   */
  public static void disposeContext( Thread thread ) {
    ParamCheck.notNull( thread, "thread" );
    synchronized( CONTEXT_HOLDER_FOR_BG_THREADS ) {
      ServiceContext toRemove = CONTEXT_HOLDER_FOR_BG_THREADS.get( thread );
      if( toRemove != null ) {
        CONTEXT_HOLDER_FOR_BG_THREADS.remove( thread );
        toRemove.dispose();
      }
    }
  }

  /**
   * Returns whether the current thread has a mapped service context.
   */
  public static boolean hasContext() {
    return getContextInternal() != null;
  }


  //////////////////
  // helping methods

  private static ServiceContext getContextInternal() {
    ServiceContext result = CONTEXT_HOLDER.get();
    if( result == null ) {
      synchronized( CONTEXT_HOLDER_FOR_BG_THREADS ) {
        Thread currentThread = Thread.currentThread();
        result = CONTEXT_HOLDER_FOR_BG_THREADS.get( currentThread );
      }
    }
    return result;
  }
}
