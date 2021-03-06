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

org.eclipse.rwt.protocol.AdapterRegistry.add( "rwt.widgets.Shell", {

  factory : function( properties ) {
    var adapterUtil = org.eclipse.rwt.protocol.AdapterUtil;
    var styles = adapterUtil.createStyleMap( properties.style );
    var result = new org.eclipse.swt.widgets.Shell( styles );
    adapterUtil.addStatesForStyles( result, properties.style );
    result.setUserData( "isControl", true );
    adapterUtil.callWithTarget( properties.parentShell, function( parentShell ) {
      if( parentShell ) {
        result.setParentShell( parentShell );    
      }
      result.initialize();
    } );
    return result;
  },

  destructor : org.eclipse.rwt.protocol.AdapterUtil.getControlDestructor(),

  properties : org.eclipse.rwt.protocol.AdapterUtil.extendControlProperties( [
    "showMinimize", 
    "allowMinimize", 
    "showMaximize", 
    "allowMaximize", 
    "showClose", 
    "allowClose",
    "resizable",
    "image",
    "text",
    "alpha",
    "active",
    "mode",
    "hasShellListener",
    "minimumSize",
    "defaultButton",
    "activeControl"
  ] ),

  propertyHandler : org.eclipse.rwt.protocol.AdapterUtil.extendControlPropertyHandler( {
    // Overwrites original bounds handler, see bug 306042 and 354597
    "bounds" : function( widget, value ) {
      if( !widget.isDisableResize() ) {
        widget.setLeft( value[ 0 ] );
        widget.setTop( value[ 1 ] );
        widget.setWidth( value[ 2 ] );
        widget.setHeight( value[ 3 ] );
      }
    },
    "alpha" : function( shell, alpha ) {
      shell.setOpacity( alpha / 255 );
    },
    "defaultButton" : function( shell, value ) {
      org.eclipse.rwt.protocol.AdapterUtil.callWithTarget( value, function( widget ) {
        shell.setDefaultButton( widget );
      } );
    },
    "activeControl" : function( shell, value ) {
      org.eclipse.rwt.protocol.AdapterUtil.callWithTarget( value, function( widget ) {
        shell.setActiveControl( widget );
      } );
    },
    "mode" : function( shell, value ) {
      var fullscreen = value === "fullscreen";
      shell.setMode( fullscreen ? "maximized" : value );
      shell.setFullScreen( fullscreen );
    },
    "minimumSize" : function( shell, value ) {
      shell.setMinWidth( value[ 0 ] );
      shell.setMinHeight( value[ 1 ] );
    },
    "text" : function( shell, value ) {
      var text = org.eclipse.rwt.protocol.EncodingUtil.escapeText( value, false );
      shell.setCaption( text );
    },
    "image" : function( shell, value ) {
      if( value === null ) {
        shell.setIcon( value );
      } else {
        shell.setIcon( value[ 0 ] );
      }
    },
    "visibility" : function( shell, value ) {
      if( value ) {
        shell.show();
      } else {
        shell.hide();
      }
      org.eclipse.swt.widgets.Shell.reorderShells( shell.getWindowManager() );
    }
  } ),

  listeners : org.eclipse.rwt.protocol.AdapterUtil.extendControlListeners( [
    "shell"
  ] ),

  listenerHandler : org.eclipse.rwt.protocol.AdapterUtil.extendControlListenerHandler( {} ),

  methods : []

} );