/*******************************************************************************
 * Copyright (c) 2010, 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

qx.Class.define( "org.eclipse.swt.widgets.List", {
  extend : org.eclipse.rwt.widgets.BasicList,

  construct : function( multiSelection ) {
    this.base( arguments, multiSelection );
    this._topIndex = 0;
    this._hasSelectionListener = false;
    // Listen to send event of request to report topIndex
    var req = org.eclipse.swt.Request.getInstance();
    req.addEventListener( "send", this._onSendRequest, this );
    var selMgr = this.getManager();
    selMgr.addEventListener( "changeLeadItem", this._onChangeLeadItem, this );
    selMgr.addEventListener( "changeSelection", this._onSelectionChange, this );
    this.addEventListener( "focus", this._onFocusIn, this );
    this.addEventListener( "blur", this._onFocusOut, this );
    this.addEventListener( "dblclick", this._onDblClick, this );
    this.addEventListener( "mouseup", this._onMouseUp, this );
    this.addEventListener( "appear", this._onAppear, this );
  },
  
  destruct : function() {
    var req = org.eclipse.swt.Request.getInstance();
    req.removeEventListener( "send", this._onSendRequest, this );
    var selMgr = this.getManager();
    selMgr.removeEventListener( "changeLeadItem", this._onChangeLeadItem, this );
    selMgr.removeEventListener( "changeSelection", this._onSelectionChange, this );
    this.removeEventListener( "focus", this._onFocusIn, this );
    this.removeEventListener( "blur", this._onFocusOut, this );
    this.removeEventListener( "dblclick", this._onDblClick, this );
    this.removeEventListener( "mouseup", this._onMouseUp, this );
    this.removeEventListener( "appear", this._onAppear, this );
  },

  members : {

    setTopIndex : function( value ) {
      this._topIndex = value;
      this._applyTopIndex( value );
    },    
        
    _applyTopIndex : function( newIndex ) {
      var items = this.getManager().getItems();
      if( items.length > 0 && items[ 0 ].isCreated() ) {
        var itemHeight = this.getManager().getItemHeight( items[ 0 ] );
        if( itemHeight > 0 ) {
          var func = function() {
            if(this._clientArea != null) {
              this._clientArea.setScrollTop( newIndex * itemHeight );
            }
          };
          qx.client.Timer.once( func, this, 60 );
        }
      }
    },
    
    _getTopIndex : function() {
      var topIndex = 0;
      var scrollTop = this._clientArea.getScrollTop();
      var items = this.getManager().getItems();
      if( items.length > 0 ) {
        var itemHeight = this.getManager().getItemHeight( items[ 0 ] );
        if( itemHeight > 0 ) {
          topIndex = Math.round( scrollTop / itemHeight );
        }
      }
      return topIndex;
    },

    _onAppear : function( evt ) {
      // [ad] Fix for Bug 277678 
      // when #showSelection() is called for invisible widget
      this._applyTopIndex( this._topIndex );
    },

    setHasSelectionListener : function( value ) {
      this._hasSelectionListener = value;
    },

    _onChangeLeadItem : function( evt ) {
      if( !org.eclipse.swt.EventUtil.getSuspended() ) {
        var wm = org.eclipse.swt.WidgetManager.getInstance();
        var id = wm.findIdByWidget( this );
        var req = org.eclipse.swt.Request.getInstance();
        var focusIndex = this._clientArea.indexOf( this.getManager().getLeadItem() );
        req.addParameter( id + ".focusIndex", focusIndex );
      }
    },

    _onSelectionChange : function( evt ) {
      if( !org.eclipse.swt.EventUtil.getSuspended() ) {
        var wm = org.eclipse.swt.WidgetManager.getInstance();
        var id = wm.findIdByWidget( this );
        var req = org.eclipse.swt.Request.getInstance();
        req.addParameter( id + ".selection", this._getSelectionIndices() );
        if( this._hasSelectionListener ) {
          req.addEvent( "org.eclipse.swt.events.widgetSelected", id );
          org.eclipse.swt.EventUtil.addWidgetSelectedModifier();
          req.send();
        }
      }
      this._updateSelectedItemState();
    },
    
    _onSendRequest : function( evt ) {
      var topIndex = this._isCreated ? this._getTopIndex() : 0;
      if( this._topIndex != topIndex ) {
        var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
        var id = widgetManager.findIdByWidget( this );
        var req = org.eclipse.swt.Request.getInstance();
        req.addParameter( id + ".topIndex", topIndex );
        this._topIndex = topIndex;
      }
    },

    _onDblClick : function( evt ) {
      if( !org.eclipse.swt.EventUtil.getSuspended() ) {
        if( this._hasSelectionListener ) {
          var wm = org.eclipse.swt.WidgetManager.getInstance();
          var id = wm.findIdByWidget( this );
          var req = org.eclipse.swt.Request.getInstance();
          req.addEvent( "org.eclipse.swt.events.widgetDefaultSelected", id );
          org.eclipse.swt.EventUtil.addWidgetSelectedModifier();
          req.send();
        }
      }
    },
    
    _onFocusIn : function( evt ) {
      this._updateSelectedItemState();
    },

    _onFocusOut : function( evt ) {
      this._updateSelectedItemState();
    },
    
    _onMouseUp : function( event ) {
      var target = event.getOriginalTarget();
      if( target instanceof org.eclipse.rwt.widgets.ListItem ) {
        this._onListItemMouseUp( target, event );
      }
    },

    _onListItemMouseUp : function( row, event ) {            
      this._sendHyperlinkActivated( event );      
    },
    
    _sendHyperlinkActivated : function( event ) {
        var targetNode = event.getDomTarget();
        if( targetNode
         && targetNode.nodeName=="span" || targetNode.nodeName=="SPAN"
            && targetNode.className=="link"
            && targetNode.getAttribute("href") ){
          event.setDefaultPrevented( true );
          var wm = org.eclipse.swt.WidgetManager.getInstance();
          var id = wm.findIdByWidget( this );
          var req = org.eclipse.swt.Request.getInstance();
          req.addEvent( "org.eclipse.swt.events.hyperlinkActivated", id );
          req.addParameter( "org.eclipse.swt.events.hyperlinkActivated.url", targetNode.getAttribute("href") );
          req.send();
        }
      },

    _updateSelectedItemState : function() {
      var selectedItems = this.getManager().getSelectedItems();      
      for( var i = 0; i < selectedItems.length; i++ ) {
        if( this.getFocused() ) {
          selectedItems[ i ].removeState( "parent_unfocused" );
        } else {
          selectedItems[ i ].addState( "parent_unfocused" );
        }
      }
    }
    
  }
} );
