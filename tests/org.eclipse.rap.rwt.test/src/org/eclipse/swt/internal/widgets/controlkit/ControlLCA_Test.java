/*******************************************************************************
 * Copyright (c) 2002, 2012 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 *    Frank Appel - replaced singletons and static fields (Bug 337787)
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.controlkit;

import java.io.IOException;
import java.util.ArrayList;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.rwt.internal.lifecycle.DisplayUtil;
import org.eclipse.rwt.internal.lifecycle.JSConst;
import org.eclipse.rwt.internal.protocol.ProtocolTestUtil;
import org.eclipse.rwt.internal.service.RequestParams;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.widgets.*;

public class ControlLCA_Test extends TestCase {

  protected void setUp() throws Exception {
    Fixture.setUp();
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testPreserveValues() {
    Display display = new Display();
    Composite shell = new Shell( display , SWT.NONE );
    Control control = new Button( shell, SWT.PUSH );
    Fixture.markInitialized( display );
    //bound
    Rectangle rectangle = new Rectangle( 10, 10, 10, 10 );
    control.setBounds( rectangle );
    Fixture.preserveWidgets();
    IWidgetAdapter adapter = WidgetUtil.getAdapter( control );
    assertEquals( rectangle, adapter.getPreserved( Props.BOUNDS ) );
    Fixture.clearPreserved();
    //z-index
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( control );
    assertTrue( adapter.getPreserved( Props.Z_INDEX ) != null );
    Fixture.clearPreserved();
    //visible
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( control );
    assertEquals( Boolean.TRUE, adapter.getPreserved( Props.VISIBLE ) );
    Fixture.clearPreserved();
    control.setVisible( false );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( control );
    assertEquals( Boolean.FALSE, adapter.getPreserved( Props.VISIBLE ) );
    Fixture.clearPreserved();
    //enabled
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( control );
    assertEquals( Boolean.TRUE, adapter.getPreserved( Props.ENABLED ) );
    Fixture.clearPreserved();
    control.setEnabled( false );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( control );
    assertEquals( Boolean.FALSE, adapter.getPreserved( Props.ENABLED ));
    Fixture.clearPreserved();
    control.setEnabled( true );
    //foreground background font
    Color background = Graphics.getColor( 122, 33, 203 );
    control.setBackground( background );
    Color foreground = Graphics.getColor( 211, 178, 211 );
    control.setForeground( foreground );
    Font font = Graphics.getFont( "font", 12, SWT.BOLD );
    control.setFont( font );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( control );
    assertEquals( background, adapter.getPreserved( Props.BACKGROUND ) );
    assertEquals( foreground, adapter.getPreserved( Props.FOREGROUND ) );
    assertEquals( font, adapter.getPreserved( Props.FONT ) );
    Fixture.clearPreserved();
    //tab_index
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( control );
    assertTrue( adapter.getPreserved( Props.Z_INDEX ) != null );
    Fixture.clearPreserved();
    //tooltiptext
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( control );
    assertEquals( null, control.getToolTipText() );
    Fixture.clearPreserved();
    control.setToolTipText( "some text" );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( control );
    assertEquals( "some text", control.getToolTipText() );
  }

  public void testWriteVisibility() throws IOException {
    Display display = new Display();
    Shell shell = new Shell( display , SWT.NONE );
    Button button = new Button( shell, SWT.PUSH );
    button.setSize( 10, 10 );
    shell.open();
    Fixture.fakeResponseWriter();
    ControlLCAUtil.preserveValues( button );
    Fixture.markInitialized( button );
    Fixture.markInitialized( display );

    // Initial JavaScript code must not contain setVisibility()
    ControlLCAUtil.writeChanges( button );
    assertFalse( ProtocolTestUtil.getMessageScript().contains( "setVisibility" ) );

    // Unchanged visible attribute must not be rendered
    Fixture.fakeResponseWriter();
    Fixture.markInitialized( display );
    Fixture.markInitialized( button );
    Fixture.preserveWidgets();
    ControlLCAUtil.writeChanges( button );
    assertFalse( ProtocolTestUtil.getMessageScript().contains( "setVisibility" ) );

    // Changed visible attribute must not be rendered
    Fixture.fakeResponseWriter();
    Fixture.preserveWidgets();
    button.setVisible( false );
    ControlLCAUtil.writeChanges( button );
    assertTrue( ProtocolTestUtil.getMessageScript().contains( "setVisibility" ) );
  }

  public void testWriteBounds() throws IOException {
    Display display = new Display();
    Shell shell = new Shell( display , SWT.NONE );
    Control control = new Button( shell, SWT.PUSH );
    Composite parent = control.getParent();

    // call writeBounds once to elimniate the uninteresting JavaScript prolog
    Fixture.fakeResponseWriter();
    WidgetLCAUtil.writeBounds( control, parent, control.getBounds() );

    // Test without clip
    Fixture.fakeResponseWriter();
    control.setBounds( 1, 2, 100, 200 );
    WidgetLCAUtil.writeBounds( control, parent, control.getBounds() );
    String expected = "w.setSpace( 1, 100, 2, 200 );";
    assertTrue( ProtocolTestUtil.getMessageScript().contains( expected ) );
  }

  public void testMenuDetectListener() {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    Display display = new Display();
    Shell shell = new Shell( display );
    Label label = new Label( shell, SWT.NONE );
    final java.util.List<MenuDetectEvent> log = new ArrayList<MenuDetectEvent>();
    label.addMenuDetectListener( new MenuDetectListener() {
      public void menuDetected( MenuDetectEvent event ) {
        log.add( event );
      }
    });
    String labelId = WidgetUtil.getId( label );
    Fixture.fakeResponseWriter();
    Fixture.fakeRequestParam( JSConst.EVENT_MENU_DETECT, labelId );
    Fixture.fakeRequestParam( JSConst.EVENT_MENU_DETECT_X, "10" );
    Fixture.fakeRequestParam( JSConst.EVENT_MENU_DETECT_Y, "30" );
    Fixture.readDataAndProcessAction( display );
    MenuDetectEvent event = log.get( 0 );
    assertSame( label, event.widget );
    assertEquals( 10, event.x );
    assertEquals( 30, event.y );
  }

  public void testRedrawAndDispose() {
    final StringBuilder log = new StringBuilder();
    // Set up test scenario
    Display display = new Display();
    Shell shell = new Shell( display );
    Control control = new Composite( shell, SWT.NONE ) {
      private static final long serialVersionUID = 1L;
      @SuppressWarnings("unchecked")
      public <T> T getAdapter( Class<T> adapter ) {
        Object result;
        if( adapter == ILifeCycleAdapter.class ) {
          result = new AbstractWidgetLCA() {
            public void preserveValues( Widget widget ) {
            }
            public void renderChanges( Widget widget )
              throws IOException
            {
            }
            public void renderDispose( Widget widget )
              throws IOException
            {
              log.append( "renderDispose" );
            }
            public void renderInitialization( Widget widget )
              throws IOException
            {
            }
            public void readData( Widget widget ) {
            }
            public void doRedrawFake( Control control ) {
              log.append( "FAILED: doRedrawFake was called" );
            }
          };
        } else {
          result = super.getAdapter( adapter );
        }
        return ( T )result;
      }
    };
    Fixture.markInitialized( display );
    Fixture.markInitialized( shell );
    Fixture.markInitialized( control );
    // redraw & dispose: must revoke redraw
    control.redraw();
    control.dispose();
    // run life cycle that (in this case) won't call doRedrawFake
    Fixture.fakeResponseWriter();
    String displayId = DisplayUtil.getId( display );
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.executeLifeCycleFromServerThread();
    assertEquals( "renderDispose", log.toString() );
  }

}
