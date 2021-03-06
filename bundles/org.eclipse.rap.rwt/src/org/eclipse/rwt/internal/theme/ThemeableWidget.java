/*******************************************************************************
 * Copyright (c) 2008, 2011 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rwt.internal.theme;

import org.eclipse.rwt.internal.theme.css.StyleSheet;
import org.eclipse.rwt.resources.ResourceLoader;


final class ThemeableWidget {

  final Class widget;
  final ResourceLoader loader;
  IThemeCssElement[] elements;
  StyleSheet defaultStyleSheet;

  ThemeableWidget( Class widget, ResourceLoader loader ) {
    this.widget = widget;
    this.loader = loader;
  }
}
