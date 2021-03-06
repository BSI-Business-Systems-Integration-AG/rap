/*******************************************************************************
 * Copyright (c) 2002, 2012 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.labelkit;

import java.io.IOException;

import org.eclipse.rwt.internal.protocol.ClientObjectFactory;
import org.eclipse.rwt.lifecycle.AbstractWidgetLCA;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Widget;


public class LabelLCA extends AbstractWidgetLCA {

  private static final AbstractLabelLCADelegate SEPARATOR_LCA = new SeparatorLabelLCA();
  private static final AbstractLabelLCADelegate LABEL_LCA = new StandardLabelLCA();

  public void preserveValues( Widget widget ) {
    getDelegate( widget ).preserveValues( ( Label )widget );
  }

  public void readData( Widget widget ) {
    getDelegate( widget ).readData( ( Label )widget );
  }

  public void renderInitialization( Widget widget ) throws IOException {
    getDelegate( widget ).renderInitialization( ( Label )widget );
  }

  public void renderChanges( Widget widget ) throws IOException {
    getDelegate( widget ).renderChanges( ( Label )widget );
  }

  public void renderDispose( Widget widget ) throws IOException {
    ClientObjectFactory.getClientObject( widget ).destroy();
  }

  private static AbstractLabelLCADelegate getDelegate( Widget widget ) {
    AbstractLabelLCADelegate result;
    if( ( widget.getStyle() & SWT.SEPARATOR ) != 0 ) {
      result = SEPARATOR_LCA;
    } else {
      result = LABEL_LCA;
    }
    return result;
  }
}
