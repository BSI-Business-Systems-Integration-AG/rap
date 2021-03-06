/*******************************************************************************
 * Copyright (c) 2011, 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.lifecycle;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.testfixture.*;
import org.eclipse.rap.rwt.testfixture.internal.LoggingPhaseListener;
import org.eclipse.rap.rwt.testfixture.internal.LoggingPhaseListener.PhaseEventInfo;
import org.eclipse.rwt.RWT;
import org.eclipse.rwt.internal.application.ApplicationContextUtil;
import org.eclipse.rwt.internal.application.RWTFactory;
import org.eclipse.rwt.internal.service.ContextProvider;
import org.eclipse.rwt.internal.service.RequestParams;
import org.eclipse.rwt.internal.service.SessionStoreImpl;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.rwt.service.ISessionStore;
import org.eclipse.rwt.service.SessionStoreEvent;
import org.eclipse.rwt.service.SessionStoreListener;
import org.eclipse.swt.widgets.Display;


// TODO [rh] see if it is possible to move this test to org.eclipse.rwt.test
public class SimpleLifeCycle_Test extends TestCase {

  private LifeCycle lifeCycle;

  @Override
  protected void setUp() throws Exception {
    Fixture.setUp();
    ISessionStore sessionSore = ContextProvider.getSessionStore();
    ApplicationContextUtil.set( sessionSore, ApplicationContextUtil.getInstance() );
    lifeCycle = new SimpleLifeCycle();
  }

  @Override
  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testPhaseOrderForInitialRequest() throws Exception {
    registerEntryPoint( TestEntryPoint.class );
    LoggingPhaseListener phaseListener = new LoggingPhaseListener( PhaseId.ANY );
    lifeCycle.addPhaseListener( phaseListener );
    lifeCycle.execute();
    PhaseEventInfo[] loggedEvents = phaseListener.getLoggedEvents();
    assertEquals( 4, loggedEvents.length );
    assertBeforePhaseEvent( loggedEvents[ 0 ], PhaseId.PREPARE_UI_ROOT );
    assertAfterPhaseEvent( loggedEvents[ 1 ], PhaseId.PREPARE_UI_ROOT );
    assertBeforePhaseEvent( loggedEvents[ 2 ], PhaseId.RENDER );
    assertAfterPhaseEvent( loggedEvents[ 3 ], PhaseId.RENDER );
  }

  public void testPhaseOrderForSubsequentRequest() throws Exception {
    new Display();
    LoggingPhaseListener phaseListener = new LoggingPhaseListener( PhaseId.ANY );
    lifeCycle.addPhaseListener( phaseListener );
    lifeCycle.execute();
    PhaseEventInfo[] loggedEvents = phaseListener.getLoggedEvents();
    assertEquals( 8, loggedEvents.length );
    assertBeforePhaseEvent( loggedEvents[ 0 ], PhaseId.PREPARE_UI_ROOT );
    assertAfterPhaseEvent( loggedEvents[ 1 ], PhaseId.PREPARE_UI_ROOT );
    assertBeforePhaseEvent( loggedEvents[ 2 ], PhaseId.READ_DATA );
    assertAfterPhaseEvent( loggedEvents[ 3 ], PhaseId.READ_DATA );
    assertBeforePhaseEvent( loggedEvents[ 4 ], PhaseId.PROCESS_ACTION );
    assertAfterPhaseEvent( loggedEvents[ 5 ], PhaseId.PROCESS_ACTION );
    assertBeforePhaseEvent( loggedEvents[ 6 ], PhaseId.RENDER );
    assertAfterPhaseEvent( loggedEvents[ 7 ], PhaseId.RENDER );
  }

  public void testThreadIsAttachedInInitialRequest() throws IOException {
    registerEntryPoint( TestEntryPoint.class );
    ThreadRecordingPhaseListener phaseListener = new ThreadRecordingPhaseListener( );
    lifeCycle.addPhaseListener( phaseListener );
    lifeCycle.execute();
    Thread[] threads = phaseListener.getThreads();
    for( int i = 0; i < threads.length; i++ ) {
      assertSame( Thread.currentThread(), threads[ i ] );
    }
  }

  public void testThreadIsDetachedInInitialRequest() throws IOException {
    registerEntryPoint( TestEntryPoint.class );
    lifeCycle.execute();
    assertNull( Display.getCurrent() );
    assertNull( LifeCycleUtil.getSessionDisplay().getThread() );
  }

  public void testThreadIsAttachedInSubsequentRequest() throws IOException {
    registerEntryPoint( TestEntryPoint.class );
    lifeCycle.execute();
    Fixture.fakeNewRequest();
    ThreadRecordingPhaseListener phaseListener = new ThreadRecordingPhaseListener( );
    lifeCycle.addPhaseListener( phaseListener );
    lifeCycle.execute();
    Thread[] threads = phaseListener.getThreads();
    for( int i = 0; i < threads.length; i++ ) {
      assertSame( Thread.currentThread(), threads[ i ] );
    }
  }

  public void testThreadIsDetachedInSubsequentRequest() throws IOException {
    registerEntryPoint( TestEntryPoint.class );
    Fixture.fakeRequestParam( RequestParams.STARTUP, EntryPointUtil.DEFAULT );
    lifeCycle.execute();
    Fixture.fakeNewRequest();
    lifeCycle.execute();
    assertNull( Display.getCurrent() );
    assertNull( LifeCycleUtil.getSessionDisplay().getThread() );
  }

  // bug 361753
  public void testDefaultDisplayIsAvailableInInitialRequest() throws IOException {
    registerEntryPoint( DefaultDisplayEntryPoint.class );
    Fixture.fakeNewRequest();

    lifeCycle.execute();

    assertNotNull( LifeCycleUtil.getSessionDisplay( ContextProvider.getSessionStore() ) );
  }

  public void testPhaseListenersHaveApplicationScope() throws Exception {
    registerEntryPoint( TestEntryPoint.class );
    LoggingPhaseListener phaseListener = new LoggingPhaseListener( PhaseId.ANY );
    lifeCycle.addPhaseListener( phaseListener );
    newSession();
    lifeCycle.execute();
    assertTrue( phaseListener.getLoggedEvents().length > 0 );
  }

  public void testAddPhaseListener() throws Exception {
    registerEntryPoint( TestEntryPoint.class );
    LoggingPhaseListener phaseListener = new LoggingPhaseListener( PhaseId.ANY );
    lifeCycle.addPhaseListener( phaseListener );
    lifeCycle.execute();
    assertTrue( phaseListener.getLoggedEvents().length > 0 );
  }

  public void testRemovePhaseListener() throws Exception {
    registerEntryPoint( TestEntryPoint.class );
    LoggingPhaseListener phaseListener = new LoggingPhaseListener( PhaseId.ANY );
    lifeCycle.addPhaseListener( phaseListener );
    lifeCycle.removePhaseListener( phaseListener );
    lifeCycle.execute();
    assertEquals( 0, phaseListener.getLoggedEvents().length );
  }

  public void testRequestThreadExecRunsRunnableOnCallingThread() {
    final Thread[] invocationThread = { null };
    Runnable runnable = new Runnable() {
      public void run() {
        invocationThread[ 0 ] = Thread.currentThread();
      }
    };

    lifeCycle.requestThreadExec( runnable );

    assertSame( Thread.currentThread(), invocationThread[ 0 ] );
  }

  public void testGetUIThreadWhileLifeCycleInExecute() throws IOException {
    new Display();
    final Thread[] uiThread = { null };
    lifeCycle.addPhaseListener( new PhaseListener() {
      private static final long serialVersionUID = 1L;
      public PhaseId getPhaseId() {
        return PhaseId.PREPARE_UI_ROOT;
      }
      public void beforePhase( PhaseEvent event ) {
      }
      public void afterPhase( PhaseEvent event ) {
        uiThread[ 0 ] = LifeCycleUtil.getUIThread( ContextProvider.getSessionStore() ).getThread();
      }
    } );

    lifeCycle.execute();

    assertSame( Thread.currentThread(), uiThread[ 0 ] );
  }

  public void testGetUIThreadAfterLifeCycleExecuted() throws IOException {
    registerEntryPoint( TestEntryPoint.class );
    lifeCycle.execute();

    IUIThreadHolder threadHolder = LifeCycleUtil.getUIThread( ContextProvider.getSessionStore() );

    assertNull( threadHolder );
  }

  public void testInvalidateDisposesDisplay() throws Throwable {
    final ISessionStore sessionStore = ContextProvider.getSessionStore();
    Display display = new Display();
    lifeCycle.execute();

    Fixture.runInThread( new Runnable() {
      public void run() {
        sessionStore.getHttpSession().invalidate();
      }
    } );

    assertTrue( display.isDisposed() );
  }

  public void testSessionRestartDisposesDisplay() throws IOException {
    final ISessionStore sessionStore = ContextProvider.getSessionStore();
    Display display = new Display();
    lifeCycle.execute();

    sessionStore.getHttpSession().invalidate();

    assertTrue( display.isDisposed() );
  }

  public void testSleep() {
    try {
      lifeCycle.sleep();
      fail();
    } catch( UnsupportedOperationException expected ) {
      assertTrue( expected.getMessage().length() > 0 );
    }
  }

  public void testContextOnShutdownFromBackgroundThread() throws Exception {
    final boolean[] log = new boolean[ 1 ];
    // Activate SimpleLifeCycle
    RWTFactory.getLifeCycleFactory().deactivate();
    RWTFactory.getLifeCycleFactory().activate();
    registerEntryPoint( TestEntryPoint.class );
    final SessionStoreImpl sessionStore = ( SessionStoreImpl )RWT.getSessionStore();
    sessionStore.addSessionStoreListener( new SessionStoreListener() {
      public void beforeDestroy( SessionStoreEvent event ) {
        log[ 0 ] = ContextProvider.hasContext();
      }
    } );
    // Initialize shutdown adapter
    ( ( LifeCycle )RWT.getLifeCycle() ).execute();

    Thread thread = new Thread( new Runnable() {
      public void run() {
        sessionStore.getShutdownAdapter().interceptShutdown();
        // Prevents NPE in tearDown
        sessionStore.setShutdownAdapter( null );
      }
    } );
    thread.setDaemon( true );
    thread.start();
    thread.join();

    assertTrue( log[ 0 ] );
  }

  private void assertBeforePhaseEvent( PhaseEventInfo beforePrepareUIRoot, PhaseId phaseId ) {
    assertTrue( beforePrepareUIRoot.before );
    assertEquals( phaseId, beforePrepareUIRoot.phaseId );
    assertSame( lifeCycle, beforePrepareUIRoot.source );
  }

  private void assertAfterPhaseEvent( PhaseEventInfo beforePrepareUIRoot, PhaseId phaseId ) {
    assertFalse( beforePrepareUIRoot.before );
    assertEquals( phaseId, beforePrepareUIRoot.phaseId );
    assertSame( lifeCycle, beforePrepareUIRoot.source );
  }

  private static void registerEntryPoint( Class<? extends IEntryPoint> type ) {
    RWTFactory.getEntryPointManager().registerByName( EntryPointUtil.DEFAULT, type );
  }

  private static void newSession() {
    ContextProvider.disposeContext();
    Fixture.createServiceContext();
  }

  private static class ThreadRecordingPhaseListener implements PhaseListener {
    private static final long serialVersionUID = 1L;

    private final List<Thread> threads;

    private ThreadRecordingPhaseListener() {
      threads = new LinkedList<Thread>();
    }

    public void beforePhase( PhaseEvent event ) {
      threads.add( Display.getCurrent().getThread() );
    }

    public void afterPhase( PhaseEvent event ) {
      threads.add( Display.getCurrent().getThread() );
    }

    public PhaseId getPhaseId() {
      return PhaseId.ANY;
    }

    Thread[] getThreads() {
      Thread[] result = new Thread[ threads.size() ];
      threads.toArray( result );
      return result;
    }
  }

  private static class TestEntryPoint implements IEntryPoint {
    public int createUI() {
      new Display();
      return 0;
    }
  }

  private static class DefaultDisplayEntryPoint implements IEntryPoint {
    public int createUI() {
      Display.getDefault();
      return 0;
    }
  }
}
