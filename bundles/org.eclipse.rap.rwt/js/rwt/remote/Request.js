/*******************************************************************************
 * Copyright (c) 2012, 2014 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

/*global ActiveXObject: false */

namespace( "rwt.remote" );

(function(){

var Client = rwt.client.Client;

rwt.remote.Request = function( url, method, responseType ) {
  this._url = url;
  this._method = method;
  this._async = true;
  this._success = null;
  this._error = null;
  this._data = null;
  this._timeout = null;
  this._timeoutObject = null;
  this._aborted = false;
  this._responseType = responseType;
  this._request = rwt.remote.Request.createXHR();
};

rwt.remote.Request.createXHR = function() {
  return new XMLHttpRequest();
};

rwt.remote.Request.prototype = {

    dispose : function() {
      if( this._request != null ) {
        this._request.onreadystatechange = null;
        this._request.abort();
        this._success = null;
        this._error = null;
        if (this._timeoutObject !== null) {
          clearTimeout(this._timeoutObject);
          this._timeoutObject = null;
        }
        this._aborted = false;
        this._request = null;
      }
    },

    send : function() {
      var urlpar = null;
      var post = this._method === "POST";
      if( !post && this._data ) {
        urlpar = this._data;
      }
      var url = this._url;
      if( urlpar ) {
        url += ( url.indexOf( "?" ) >= 0 ? "&" : "?" ) + urlpar;
      }
      this._bindOnReadyStateChange();
      this._request.open( this._method, url, this._async );
      this._configRequest();
      this._request.send( post ? this._data : undefined );
      if( !this._shouldUseStateListener() ) {
        this._onReadyStateChange();
      }
      if( !this._async ) {
        this.dispose();
      }
    },

    setAsynchronous : function( value ) {
      this._async = value;
    },

    getAsynchronous : function() {
      return this._async;
    },

    setTimeout : function( value ) {
      this._timeout = value;
    },

    getTimeout : function() {
      return this._timeout;
    },


    setSuccessHandler : function( handler, context ) {
      this._success = function(){ handler.apply( context, arguments ); };
    },

    setErrorHandler : function( handler, context ) {
      this._error = function(){ handler.apply( context, arguments ); };
    },

    setData : function( value ) {
      this._data = value;
    },

    getData : function() {
      return this._data;
    },

    _bindOnReadyStateChange : function() {
      if( this._shouldUseStateListener() ) {
        this._request.onreadystatechange = rwt.util.Functions.bind( this._onReadyStateChange, this );
      }
    },

    _configRequest : function() {
      if( !Client.isWebkit() ) {
        this._request.setRequestHeader( "Referer", window.location.href );
      }
      var contentType = "application/json; charset=UTF-8";
      this._request.setRequestHeader( "Content-Type", contentType );

      var that = this;
      if( this._timeout > 0 ) {
        this._timeoutObject = setTimeout(function() {
          if( that._request != null ) {
            that._aborted = true;
            that._request.abort();
            console.log( new Date() +": ServerPush request aborted due to timeout." );
          }
        }, this._timeout);
      }
    },

    _shouldUseStateListener : function() {
      var result = true;
      if( !this._async && Client.isGecko() && Client.getMajor() < 4 ) {
        // see Bug 398951 - RAP does not start in Firefox 3.x
        result = false;
      }
      return result;
    },

    _onReadyStateChange : function() {
      var text;
      if( this._request.readyState === 4 ) {
        // [if] typeof(..) == "unknown" is IE specific. Used to prevent error:
        // "The data necessary to complete this operation is not yet available"
        if( typeof this._request.responseText !== "unknown" ) {
          text = this._request.responseText;
        }
        var event = {
          "responseText" : text,
          "status" : this._request.status,
          "responseHeaders" : this._getHeaders(),
          "target" : this
        };
        if( this._request.status === 200 ) {
          if( this._success ) {
            this._success( event );
          }
        } else {
          if( this._error ) {
            this._error( event );
          }
        }
        if( this._async ) {
          this.dispose();
        }
      } else {
        if (this._aborted) { // only true if serverpush was aborted due to timeout
          // [if] typeof(..) == "unknown" is IE specific. Used to prevent error:
          // "The data necessary to complete this operation is not yet available"
          if( typeof this._request.responseText !== "unknown" ) {
            text = this._request.responseText;
          }
          console.log( new Date() +": About to handle the aborted ServerPush request." );
          var event = {
            "responseText" : text,
            "status" : this._request.status,
            "responseHeaders" : {},
            "target" : this
          };
          if( this._error ) {
            this._error( event );
          }
          if( this._async ) {
            this.dispose();
          }
        }
      }
    },

    _getHeaders : function() {
      var text = this._request.getAllResponseHeaders();
      var values = text.split( /[\r\n]+/g );
      var result = {};
      for( var i=0; i < values.length; i++ ) {
        var pair = values[ i ].match( /^([^:]+)\s*:\s*(.+)$/i );
        if( pair ) {
          result[ pair[ 1 ] ] = pair[ 2 ];
        }
      }
      return result;
    }

};

}());
