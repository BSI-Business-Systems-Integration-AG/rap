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
package org.eclipse.rwt.internal;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.rwt.RWT;
import org.eclipse.rwt.internal.service.ServletLog;

public final class RWTMessages {

  private static final String BUNDLE_NAME
    = "org.eclipse.rwt.internal.RWTMessages";

  private RWTMessages() {
    // prevent instantiation
  }

  public static String getMessage( String key ) {
    return getMessage( key, BUNDLE_NAME );
  }

  public static String getMessage( String key, String bundleName ) {
    String result = key;
    ResourceBundle bundle = null;
    try {
      bundle = getBundle( bundleName );
    } catch( MissingResourceException ex ) {
      result = key + " (no resource bundle)";
    }
    if( bundle != null ) {
      try {
        result = bundle.getString( key );
      } catch( MissingResourceException ex2 ) {
      }
    }
    return result;
  }

  private static ResourceBundle getBundle( String baseName ) {
    ResourceBundle result = null;
    try {
      ClassLoader loader = RWTMessages.class.getClassLoader();
      result = ResourceBundle.getBundle( baseName, RWT.getLocale(), loader );
    } catch( RuntimeException re ) {
      String msg = "Warning: could not retrieve resource bundle "
                 + "- loading system default";
      ServletLog.log( msg, re );
      result = ResourceBundle.getBundle( baseName );
    }
    return result;
  }
}
