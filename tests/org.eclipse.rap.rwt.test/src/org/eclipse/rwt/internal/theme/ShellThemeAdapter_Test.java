/*******************************************************************************
 * Copyright (c) 2008, 2012 Innoopract Informationssysteme GmbH and others.
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

import static org.eclipse.rwt.internal.theme.ThemeTestUtil.setCustomTheme;

import java.io.IOException;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rwt.lifecycle.WidgetUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.widgets.shellkit.ShellThemeAdapter;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;


public class ShellThemeAdapter_Test extends TestCase {

  private Display display;

  @Override
  protected void setUp() {
    Fixture.setUp();
    Fixture.fakeNewRequest();
    display = new Display();
  }

  @Override
  protected void tearDown() {
    Fixture.tearDown();
  }

  public void testPlainShell() throws IOException {
    Color yellow = display.getSystemColor( SWT.COLOR_YELLOW );
    Color blue = display.getSystemColor( SWT.COLOR_BLUE );
    Shell shell = new Shell( display, SWT.NONE );
    ShellThemeAdapter themeAdapter = getShellThemeAdapter( shell );

    setCustomTheme(   " * { color: blue; }"
                    + "Shell { border: 3px solid blue; background-color: yellow; }" );

    assertEquals( 3, themeAdapter.getBorderWidth( shell ) );
    assertEquals( blue, themeAdapter.getForeground( shell ) );
    assertEquals( yellow, themeAdapter.getBackground( shell ) );
  }

  public void testShellWithBorder() {
    Shell shell = new Shell( display, SWT.BORDER );
    ShellThemeAdapter themeAdapter = getShellThemeAdapter( shell );
    assertEquals( 1, themeAdapter.getBorderWidth( shell ) );
    shell.setMaximized( true );
    assertEquals( 0, themeAdapter.getBorderWidth( shell ) );
  }

  public void testTitleBarHeightFromCustomVariant() throws IOException {
    Shell shell = new Shell( display, SWT.TITLE );
    ShellThemeAdapter shellThemeAdapter = getShellThemeAdapter( shell );

    setCustomTheme( "Shell-Titlebar.special { height: 50px }" );
    shell.setData( WidgetUtil.CUSTOM_VARIANT, "special" );

    assertEquals( 50, shellThemeAdapter.getTitleBarHeight( shell ) );
  }

  public void testTitleBarMarginFromCustomVariant() throws IOException {
    Shell shell = new Shell( display, SWT.TITLE );
    ShellThemeAdapter shellThemeAdapter = getShellThemeAdapter( shell );

    setCustomTheme( "Shell-Titlebar.special { margin: 1px 2px 3px 4px }" );
    shell.setData( WidgetUtil.CUSTOM_VARIANT, "special" );

    assertEquals( new Rectangle( 4, 1, 6, 4 ), shellThemeAdapter.getTitleBarMargin( shell ) );
  }

  public void testStyle_APPLICATION_MODAL() throws IOException {
    Shell shell = new Shell( display, SWT.APPLICATION_MODAL );
    ShellThemeAdapter shellThemeAdapter = getShellThemeAdapter( shell );

    setCustomTheme( "Shell[APPLICATION_MODAL] { padding: 23px 57px }" );

    assertEquals( new Rectangle( 57, 23, 114, 46 ), shellThemeAdapter.getPadding( shell ) );
  }

  public void testStyle_TOOL() throws IOException {
    Shell shell = new Shell( display, SWT.TOOL );
    ShellThemeAdapter shellThemeAdapter = getShellThemeAdapter( shell );

    setCustomTheme( "Shell[TOOL] { padding: 23px 57px }" );

    assertEquals( new Rectangle( 57, 23, 114, 46 ), shellThemeAdapter.getPadding( shell ) );
  }

  public void testStyle_TITLE() throws IOException {
    Shell shell = new Shell( display, SWT.TITLE );
    ShellThemeAdapter shellThemeAdapter = getShellThemeAdapter( shell );

    setCustomTheme( "Shell[TITLE] { padding: 23px 57px }" );

    assertEquals( new Rectangle( 57, 23, 114, 46 ), shellThemeAdapter.getPadding( shell ) );
  }

  public void testStyle_SHEET() throws IOException {
    Shell shell = new Shell( display, SWT.SHEET );
    ShellThemeAdapter shellThemeAdapter = getShellThemeAdapter( shell );

    setCustomTheme( "Shell[SHEET] { padding: 23px 57px }" );

    assertEquals( new Rectangle( 57, 23, 114, 46 ), shellThemeAdapter.getPadding( shell ) );
  }

  private static ShellThemeAdapter getShellThemeAdapter( Shell shell ) {
    return ( ShellThemeAdapter )shell.getAdapter( IThemeAdapter.class );
  }

}
