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
package org.eclipse.rap.demo.controls;

import java.util.Iterator;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;


public class ButtonTab extends ExampleTab {

  private static final String BUTTON_IMAGE_PATH = "resources/button-image.gif";

  private Image buttonImage;

  private boolean showImage;
  private boolean setGrayed;

  private Button pushButton;
  private Button toggleButton;
  private Button checkButton1;
  private Button checkButton2;
  private Button radioButton1;
  private Button radioButton2;
  private Button radioButton3;
  private Button defaultButton;

  public ButtonTab( CTabFolder folder ) {
    super( folder, "Button" );
  }

  @Override
  protected void createStyleControls( Composite parent ) {
    createStyleButton( "BORDER", SWT.BORDER );
    createStyleButton( "FLAT", SWT.FLAT );
    createStyleButton( "LEFT", SWT.LEFT );
    createStyleButton( "CENTER", SWT.CENTER );
    createStyleButton( "RIGHT", SWT.RIGHT );
    createStyleButton( "UP", SWT.UP );
    createStyleButton( "DOWN", SWT.DOWN );
    createStyleButton( "WRAP", SWT.WRAP );
    createVisibilityButton();
    createEnablementButton();
    createImageButton( parent );
    createGrayedButton( parent );
    createFgColorButton();
    createBgColorButton();
    createBgImageButton();
    createFontChooser();
    createCursorCombo();
    Button button = createPropertyButton( "Toggle Button", SWT.PUSH );
    button.setToolTipText( "Remote control the toggle button" );
    button.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        toggleButton.setSelection( !toggleButton.getSelection() );
      }
    } );
  }

  @Override
  protected void createExampleControls( Composite parent ) {
    parent.setLayout( new GridLayout( 1, false ) );
    int style = getStyle();
    pushButton = new Button( parent, style | SWT.PUSH );
    pushButton.setText( "Push\n Button" );
    updateButtonImage( pushButton );
    toggleButton = new Button( parent, style | SWT.TOGGLE );
    toggleButton.setText( "Toggle" );
    checkButton1 = new Button( parent, style | SWT.CHECK );
    checkButton1.setText( "Check" );
    checkButton2 = new Button( parent, style | SWT.CHECK );
    checkButton2.setText( "Check with image" );
    createButtonImage( parent.getDisplay() );
    checkButton2.setImage( buttonImage );
    radioButton1 = new Button( parent, style | SWT.RADIO );
    radioButton1.setText( "Radio 1" );
    radioButton2 = new Button( parent, style | SWT.RADIO );
    radioButton2.setText( "Radio 2" );
    radioButton3 = new Button( parent, style | SWT.RADIO );
    radioButton3.setText( "Radio 3" );
    Button arrowButton = new Button( parent, style | SWT.ARROW );
    registerControl( pushButton );
    registerControl( toggleButton );
    registerControl( checkButton1 );
    registerControl( checkButton2 );
    registerControl( radioButton1 );
    registerControl( radioButton2 );
    registerControl( radioButton3 );
    registerControl( arrowButton );
    // default button
    final Group group = new Group( parent, SWT.NONE );
    group.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
    group.setText( "Default Button" );
    group.setLayout( new RowLayout( SWT.HORIZONTAL ) );
    final Label label = new Label( group, SWT.NONE );
    label.setText( "Enter some text and press Return" );
    final Text text = new Text( group, SWT.BORDER | SWT.SINGLE );
    text.setLayoutData( new RowData( 100, SWT.DEFAULT ) );
    defaultButton = new Button( group, style | SWT.PUSH );
    defaultButton.setText( "Default Button" );
    defaultButton.getShell().setDefaultButton( defaultButton );
    defaultButton.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( final SelectionEvent event ) {
        String message = "The text You entered: " + text.getText();
        MessageDialog.openInformation( group.getShell(),
                                       "Information",
                                       message );
      }
    } );

    // Set a context menu
    Menu menu = new Menu( parent );
    for( int i = 0; i < 5; i++ ) {
      MenuItem item = new MenuItem( menu, SWT.PUSH );
      item.setText( "Item " + ( i + 1 ) );
    }
    parent.setMenu( menu );
  }

  private void createButtonImage( Display display ) {
    if( buttonImage == null ) {
      buttonImage = Util.loadImage( display, BUTTON_IMAGE_PATH );
    }
  }

  private void createImageButton( Composite parent ) {
    final Button imageButton = new Button( parent, SWT.CHECK );
    imageButton.setText( "Push Button with Image" );
    imageButton.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( final SelectionEvent event ) {
        showImage = imageButton.getSelection();
        updateButtonImage( pushButton );
        pushButton.getParent().layout();
      }
    } );
  }

  private void updateButtonImage( Button button ) {
    if( showImage ) {
      createButtonImage( button.getDisplay() );
      button.setImage( buttonImage );
    } else {
      button.setImage( null );
    }
  }

  private void createGrayedButton( Composite parent ) {
    final Button grayedButton = new Button( parent, SWT.CHECK );
    grayedButton.setText( "Grayed Check Buttons" );
    grayedButton.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( final SelectionEvent event ) {
        setGrayed = grayedButton.getSelection();
        updateButtonGrayed();
      }
    } );
  }

  private void updateButtonGrayed( ) {
    Iterator iter = controls.iterator();
    while( iter.hasNext() ) {
      Button button = ( Button )iter.next();
      button.setGrayed( setGrayed );
    }
  }
}
