/*******************************************************************************
 * Copyright (c) 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.textsize;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;


public class MeasurementUtil_Test extends TestCase {

  private static final String TEXT_TO_MEASURE = " text \"to\" measure ";

  protected void setUp() throws Exception {
    Fixture.setUp();
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testCreateProbeParamObject() {
    Probe probe = createProbe();

    Object probeObject = MeasurementUtil.createProbeParamObject( probe );

    checkProbeObject( probeObject, probe );
  }

  public void testCreateItemParamObject() {
    MeasurementItem item = createMeasurementItem();

    Object itemObject = MeasurementUtil.createItemParamObject( item );

    checkItemObject( itemObject, item );
  }

  private void checkItemObject( Object itemObject, MeasurementItem item ) {
    assertTrue( itemObject instanceof Object[] );
    Object[] itemObjectArray = ( Object[] )itemObject;
    assertEquals( 8, itemObjectArray.length );
    assertEquals( new Integer( item.hashCode() ),  itemObjectArray[ 0 ] );
    String escaped = " text \"to\" measure ";
    assertEquals( escaped, itemObjectArray[ 1 ] );
    assertTrue( itemObjectArray[ 2 ] instanceof String[] );
    String[] fontNameArray = ( String[] )itemObjectArray[ 2 ];
    assertEquals( 1, fontNameArray.length );
    assertEquals( "fontName", fontNameArray[ 0 ] );
    assertEquals( new Integer( 1 ), itemObjectArray[ 3 ] );
    assertEquals( Boolean.FALSE, itemObjectArray[ 4 ] );
    assertEquals( Boolean.FALSE, itemObjectArray[ 5 ] );
    assertEquals( new Integer( 17 ), itemObjectArray[ 6 ] );
    assertEquals( Boolean.FALSE, itemObjectArray[ 7 ] );
  }

  private void checkProbeObject( Object probeObject, Probe probe ) {
    assertTrue( probeObject instanceof Object[] );
    Object[] probeObjectArray = ( Object[] )probeObject;
    assertEquals( 6, probeObjectArray.length );
    assertEquals( new Integer( probe.getFontData().hashCode() ),  probeObjectArray[ 0 ] );
    assertEquals( TEXT_TO_MEASURE, probeObjectArray[ 1 ] );
    assertTrue( probeObjectArray[ 2 ] instanceof String[] );
    String[] fontNameArray = ( String[] )probeObjectArray[ 2 ];
    assertEquals( 1, fontNameArray.length );
    assertEquals( "fontName", fontNameArray[ 0 ] );
    assertEquals( new Integer( 1 ), probeObjectArray[ 3 ] );
    assertEquals( Boolean.FALSE, probeObjectArray[ 4 ] );
    assertEquals( Boolean.FALSE, probeObjectArray[ 5 ] );
  }

  private Probe createProbe() {
    FontData fontData = new FontData( "fontName", 1, SWT.NORMAL );
    return new Probe( TEXT_TO_MEASURE, fontData );
  }

  private MeasurementItem createMeasurementItem() {
    FontData fontData = new FontData( "fontName", 1, SWT.NORMAL );
    return new MeasurementItem( TEXT_TO_MEASURE, fontData, 17, TextSizeUtil.STRING_EXTENT );
  }
}
