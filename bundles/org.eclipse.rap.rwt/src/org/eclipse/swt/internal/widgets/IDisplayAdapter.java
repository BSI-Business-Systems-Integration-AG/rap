/*******************************************************************************
 * Copyright (c) 2002, 2011 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.internal.widgets;

import org.eclipse.rwt.service.ISessionStore;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.*;


public interface IDisplayAdapter {

  interface IFilterEntry {
    int getType();
    Listener getListener();
  }

  void setBounds( Rectangle bounds );
  void setCursorLocation( int x, int y );
  void setActiveShell( Shell shell );
  void setFocusControl( Control control );
  void invalidateFocus();
  boolean isFocusInvalidated();
  Shell[] getShells();
  ISessionStore getSessionStore();
  IFilterEntry[] getFilters();

  void attachThread();
  void detachThread();
  boolean isValidThread();

  boolean isBeepCalled();
  void resetBeep();
}
