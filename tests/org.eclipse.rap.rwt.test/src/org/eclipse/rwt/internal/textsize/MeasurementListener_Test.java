/*******************************************************************************
 * Copyright (c) 2011, 2012 Frank Appel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Frank Appel - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rwt.internal.textsize;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.testfixture.*;
import org.eclipse.rap.rwt.testfixture.internal.PhaseListenerHelper;
import org.eclipse.rwt.RWT;
import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.rwt.internal.application.RWTFactory;
import org.eclipse.rwt.internal.lifecycle.LifeCycleUtil;
import org.eclipse.rwt.lifecycle.PhaseId;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;


public class MeasurementListener_Test extends TestCase {
  private static final int EXPAND_AND_RESTORE = 2;
  private static final FontData FONT_DATA = new FontData( "arial", 12, SWT.BOLD );
  private static final String DISPLAY_ID = "w1";
  private static final String METHOD_MEASURE_STRINGS = "measureStrings";
  private static final String METHOD_PROBE = "probe";

  private MeasurementListener listener;
  private int resizeCount;

  public void testGetPhaseId() {
    PhaseId phaseId = listener.getPhaseId();

    assertSame( PhaseId.ANY, phaseId );
  }

  public void testAfterPhaseWithoutMeasurementItemsOrProbes() {
    listener.afterPhase( PhaseListenerHelper.createRenderEvent() );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findCallOperation( DISPLAY_ID, METHOD_PROBE ) );
    assertNull( message.findCallOperation( DISPLAY_ID, METHOD_MEASURE_STRINGS ) );
  }

  public void testAfterPhaseWithMeasurementItems() {
    MeasurementOperator.getInstance().addItemToMeasure( createItem() );

    listener.afterPhase( PhaseListenerHelper.createRenderEvent() );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findCallOperation( DISPLAY_ID, METHOD_PROBE ) );
    assertNotNull( message.findCallOperation( DISPLAY_ID, METHOD_MEASURE_STRINGS ) );
  }

  public void testAfterPhaseWithProbes() {
    MeasurementOperator.getInstance().addProbeToMeasure( FONT_DATA );

    listener.afterPhase( PhaseListenerHelper.createRenderEvent() );

    Message message = Fixture.getProtocolMessage();
    assertNotNull( message.findCallOperation( DISPLAY_ID, METHOD_PROBE ) );
    assertNull( message.findCallOperation( DISPLAY_ID, METHOD_MEASURE_STRINGS ) );
  }

  public void testAfterPhaseWithMeasurementItemsButWrongPhaseId() {
    MeasurementOperator.getInstance().addItemToMeasure( createItem() );

    executeNonRenderPhases();

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findCallOperation( DISPLAY_ID, METHOD_PROBE ) );
    assertNull( message.findCallOperation( DISPLAY_ID, METHOD_MEASURE_STRINGS ) );
  }


  public void testAfterPhaseWithProbesButWrongPhaseId() {
    MeasurementOperator.getInstance().addProbeToMeasure( FONT_DATA );

    executeNonRenderPhases();

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findCallOperation( DISPLAY_ID, METHOD_PROBE ) );
    assertNull( message.findCallOperation( DISPLAY_ID, METHOD_MEASURE_STRINGS ) );
  }

  public void testBeforePhaseWithMeasuredProbes() {
    fakeRequestWithProbeMeasurementResults();

    listener.beforePhase( PhaseListenerHelper.createProcessActionEvent() );

    checkProbeResultHasBeenStored();
  }

  public void testBeforePhaseWithMeasuredItems() {
    createShellWithResizeListener();
    fakeRequestWithItemMeasurementResults();

    listener.beforePhase( PhaseListenerHelper.createProcessActionEvent() );

    checkTextMeasurementResultHasBeenStored();
    checkShellHasBeenResized();
  }

  public void testBeforePhaseWithoutMeasuredItemsMustNotResizeShell() {
    createShellWithResizeListener();

    listener.beforePhase( PhaseListenerHelper.createProcessActionEvent() );

    checkShellHasNotBeenResized();
  }

  public void testBeforePhaseProbeMeasurementOfStartupProbes() {
    createProbe();
    fakeRequestWithProbeMeasurementResults();

    listener.beforePhase( PhaseListenerHelper.createPrepareUIRootEvent() );

    checkProbeResultWasStored();
  }

  @Override
  protected void setUp() throws Exception {
    listener = new MeasurementListener();

    Fixture.setUp();
    Fixture.fakeNewRequest( new Display() );
    initResizeCount();
  }

  @Override
  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  private void checkProbeResultWasStored() {
    assertNotNull( ProbeResultStore.getInstance().getProbeResult( FONT_DATA ) );
  }

  private void initResizeCount() {
    resizeCount = 0;
  }

  private void createProbe() {
    ProbeStore textSizeProbeStore = RWTFactory.getProbeStore();
    textSizeProbeStore.createProbe( FONT_DATA );
  }

  private void checkShellHasBeenResized() {
    assertEquals( EXPAND_AND_RESTORE, resizeCount );
  }

  private void checkShellHasNotBeenResized() {
    assertEquals( 0, resizeCount );
  }

  private void checkProbeResultHasBeenStored() {
    ProbeResult probeResult = ProbeResultStore.getInstance().getProbeResult( FONT_DATA );
    assertEquals( new Point( 5, 10 ), probeResult.getSize() );
  }

  private void checkTextMeasurementResultHasBeenStored() {
    Font font = Graphics.getFont( FONT_DATA );
    assertEquals( new Point( 100, 10 ), Graphics.stringExtent( font, "text" ) );
  }

  private void fakeRequestWithProbeMeasurementResults() {
    MeasurementOperator.getInstance().addProbeToMeasure( FONT_DATA );
    listener.afterPhase( PhaseListenerHelper.createRenderEvent() );
    TestRequest request = ( TestRequest )RWT.getRequest();
    request.addParameter( String.valueOf( FONT_DATA.hashCode() ), "5,10" );
  }

  private void fakeRequestWithItemMeasurementResults() {
    MeasurementItem itemToMeasure = createItem();
    MeasurementOperator.getInstance().addItemToMeasure( itemToMeasure );
    fakeRequestWithProbeMeasurementResults();
    TestRequest request = ( TestRequest )RWT.getRequest();
    request.addParameter( String.valueOf( itemToMeasure.hashCode() ), "100,10" );
  }

  private void executeNonRenderPhases() {
    listener.afterPhase( PhaseListenerHelper.createPrepareUIRootEvent() );
    listener.afterPhase( PhaseListenerHelper.createReadDataEvent() );
    listener.afterPhase( PhaseListenerHelper.createProcessActionEvent() );
  }

  private void createShellWithResizeListener() {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    Shell shell = new Shell( LifeCycleUtil.getSessionDisplay() );
    shell.addControlListener( new ControlAdapter() {
      @Override
      public void controlResized( ControlEvent evt ) {
        resizeCount++;
      }
    } );
  }

  private MeasurementItem createItem() {
    return new MeasurementItem( "text", FONT_DATA, SWT.DEFAULT, TextSizeUtil.STRING_EXTENT );
  }
}