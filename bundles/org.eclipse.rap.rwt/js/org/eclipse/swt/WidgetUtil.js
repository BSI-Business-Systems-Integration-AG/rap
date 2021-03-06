/*******************************************************************************
 * Copyright (c) 2007, 2012 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/

/**
 * This class contains static helper functions for widgets.
 */
qx.Class.define( "org.eclipse.swt.WidgetUtil", {

  statics : {

    setPropertyParam : function( widget, propertyName, propertyValue ) {
      var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
      var id = widgetManager.findIdByWidget( widget );
      var req = org.eclipse.swt.Request.getInstance();
      req.addParameter( id + "." + propertyName, propertyValue );
    },

    /**
     * workaround for IE bug
     * div's have the height of the font even if they are empty
     */
    fixIEBoxHeight : qx.core.Variant.select( "qx.client", {
      "mshtml" : function( widget ) {
        widget.setStyleProperty( "fontSize", "0" );
        widget.setStyleProperty( "lineHeight", "0" );
      },
      "default" : qx.lang.Function.returnTrue
    } ),

    getControl : function( widget ) {
      var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
      var result = widget;
      while( result != null && !widgetManager.isControl( result ) ) {
        result = result.getParent ? result.getParent() : null;
      }
      return result;
    },
    
    /**
     * Can be used simulate mouseEvents on the qooxdoo event-layer.
     * Manager and handler that are usually notified by 
     * org.eclipse.rwt.EventHandler will not receive the event. 
     */
    _fakeMouseEvent : function( originalTarget, type ) {
      var domTarget = originalTarget._getTargetNode();
      var EventHandlerUtil = org.eclipse.rwt.EventHandlerUtil;
      var target = EventHandlerUtil.getTargetObject( null, originalTarget, true );
      var domEvent = {
        "type" : type,
        "target" : domTarget,
        "button" : 0,
        "wheelData" : 0,
        "detail" : 0,
        "pageX" : 0,
        "pageY" : 0,
        "clientX" : 0,
        "clientY" : 0,
        "screenX" : 0,
        "screenY" : 0,
        "shiftKey" : false,
        "ctrlKey" : false,
        "altKey" : false,
        "metaKey" : false,
        "preventDefault" : function(){}
      };
      var event = new qx.event.type.MouseEvent( type, 
                                                domEvent, 
                                                domTarget, 
                                                target,
                                                originalTarget,
                                                null );
      target.dispatchEvent( event );
    }

  }
});