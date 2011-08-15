/*******************************************************************************
 * Copyright (c) 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

namespace( "org.eclipse.rwt.protocol" );

org.eclipse.rwt.protocol.AdapterUtil = {
  
  _controlDestructor : function( widget ) {
    var shell = org.eclipse.rwt.protocol.AdapterUtil.getShell( widget );
    if( shell ) {
      // remove from shells list of widgets listening for activate events (if present)
      shell.removeActivateListenerWidget( widget );          
    }
    widget.setToolTip( null );
    widget.setUserData( "toolTipText", null );
    widget.destroy();
  },
  
  _controlProperties : [
    "zIndex",
    "tabIndex",
    "toolTip",
    "visibility",
    "enabled",
    "foreground",
    "background",
    "backgroundImage",
    "cursor",
    "customVariant",
    "bounds",
    "font"
  ],
  
  _controlPropertyHandler : {
    "foreground" : function( widget, value ) {
      if( value === null ) {
        widget.resetTextColor();
      } else {
        widget.setTextColor( value );
      }
    },
    "background" : function( widget, value ) {
      if( value === null ) {
        widget.resetBackgroundColor();
        widget.resetBackgroundGradient();
      } else {
        widget.setBackgroundGradient( null );
        widget.setBackgroundColor( value );
      }
    },
    "backgroundImage" : function( widget, value ) {
      if( value === null ) {
        widget.resetBackgroundImage();
        widget.setUserData( "backgroundImageSize", null );
      } else {
        widget.setBackgroundImage( value[ 0 ] );
        widget.setUserData( "backgroundImageSize", value.slice( 1 ) );
      }
    },
    "cursor" : function( widget, value ) {
      if( value === null ) {
        widget.resetCursor();
      } else {
        widget.setCursor( value );
      }
    },
    "bounds" : function( widget, value ) {
      widget.setLeft( value[ 0 ] );
      widget.setTop( value[ 1 ] );
      widget.setWidth( value[ 2 ] );
      widget.setHeight( value[ 3 ] );
    },
    "toolTip" : function( widget, toolTipText ) {
      if( toolTipText != null && toolTipText != "" ) {
        widget.setUserData( "toolTipText", toolTipText );
        var toolTip = org.eclipse.rwt.widgets.WidgetToolTip.getInstance()
        widget.setToolTip( toolTip );
        // make sure "boundToWidget" is initialized:
        if( toolTip.getParent() != null ) {  
          if( toolTip.getBoundToWidget() == widget ) {
            toolTip.updateText( widget );
          }
        }
      } else {
        this._removeToolTipPopup( widget );
      }
    },
    "font" : function( widget, fontData ) {
      if( widget.setFont ) { // test if font property is supported - why wouldn't it? [tb]
        if( fontData === null ) {
          widget.resetFont();
        } else {
          var wm = org.eclipse.swt.WidgetManager.getInstance();
          // TODO [tb] : move helper
          var font = wm._createFont.apply( wm, fontData );
          widget.setFont( font );
        }
      }
    }    
  },

  _controlListenerHandler : {
    "key" : function( widget, value ) {
      widget.setUserData( "keyListener", value ? true : null );
    },
    "traverse" : function( widget, value ) {
      widget.setUserData( "traverseListener", value ? true : null );
    },
    "focus" : function( widget, value ) {
      var context = org.eclipse.swt.EventUtil;
      var focusGained = org.eclipse.swt.EventUtil.focusGained;
      var focusLost = org.eclipse.swt.EventUtil.focusLost;
      if( value ) {
        widget.addEventListener( "focusin", focusGained, context );
        widget.addEventListener( "focusout", focusLost, context );
      } else {
        widget.removeEventListener( "focusin", focusGained, context );
        widget.removeEventListener( "focusout", focusLost, context );
      }
    },
    "mouse" : function( widget, value ) {
      var context = undefined;
      var mouseDown = org.eclipse.swt.EventUtil.mouseDown;
      var mouseUp = org.eclipse.swt.EventUtil.mouseUp;
      if( value ) {
        widget.addEventListener( "mousedown", mouseDown, context );
        widget.addEventListener( "mouseup", mouseUp, context );
      } else {
        widget.removeEventListener( "mousedown", mouseDown, context );
        widget.removeEventListener( "mouseup", mouseUp, context );
      }
    },
    "menuDetect" : function( widget, value ) {
      var context = undefined;
      var detectByKey = org.eclipse.swt.EventUtil.menuDetectedByKey;
      var detectByMouse = org.eclipse.swt.EventUtil.menuDetectedByMouse;
      if( value ) {
        widget.addEventListener( "keydown", detectByKey, context );
        widget.addEventListener( "mouseup", detectByMouse, context );
      } else {
        widget.removeEventListener( "keydown", detectByKey, context );
        widget.removeEventListener( "mouseup", detectByMouse, context );
      }
    },
    "help" : function( widget, value ) {
      var context = undefined;
      var helpRequested = org.eclipse.swt.EventUtil.helpRequested;
      if( value ) {
        widget.addEventListener( "keydown", helpRequested, context );
      } else {
        widget.removeEventListener( "keydown", helpRequested, context );
      }
    }
  },

  getControlDestructor : function() {
    return this._controlDestructor;
  },
  
  extendControlProperties : function( list ) {
    return list.concat( this._controlProperties );
  },
  
  extendControlPropertyHandler : function( handler ) {
    return qx.lang.Object.mergeWith( handler, this._controlPropertyHandler, false );
  },
  
  extendControlListenerHandler : function( handler ) {
    return qx.lang.Object.mergeWith( handler, this._controlListenerHandler, false );    
  },

  addStatesForStyles : function( targetOject, styleArray ) {
    for( var i = 0; i < styleArray.length; i++ ) {
      targetOject.addState( "rwt_" + styleArray[ i ] );
    }
  },

  createStyleMap : function( styleArray ) {
    var result = {};
    for( var i = 0; i < styleArray.length; i++ ) {
      result[ styleArray[ i ] ] = true;
    }
    return result;
  },

  callWithTarget : function( id, fun ) {
    var wm = org.eclipse.swt.WidgetManager.getInstance();
    if( id === null ) {
      fun( null );
    } else {
      var target = org.eclipse.swt.WidgetManager.getInstance().findWidgetById( id );
      if( target ) {
        fun( target );
      } else {
        wm.addRegistrationCallback( id, fun );
      }
    }
  },

  getShell : function( widget ) {
    var result = widget;
    while( result && !( result instanceof org.eclipse.swt.widgets.Shell ) ) {
      result = result.getParent();
    }
    return result;
  }

};