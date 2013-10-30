/*******************************************************************************
 * Copyright (c) 2010, 2013 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/

namespace( "rwt.widgets.util" );

(function(){

var INHERIT = rwt.client.Client.isMshtml() ? "" : "inherit";

var renderer = rwt.widgets.util.CellRendererRegistry.getInstance().getAll();

rwt.widgets.util.Template = function( cells ) {
  this._cells = cells;
  this._cellRenderer = [];
  this._parseCells();
};

rwt.widgets.util.Template.prototype = {

  hasCellLayout : true,

  createContainer : function( options ) {
    if( !options.element || typeof options.element.nodeName !== "string" ) {
      throw new Error( "Not a valid target for TemplateContainer:" + options.element );
    }
    if( typeof options.zIndexOffset !== "number" ) {
      throw new Error( "Not a valid target for TemplateContainer:" + options.element );
    }
    return {
      "element" : options.element,
      "template" : this,
      "zIndexOffset" : options.zIndexOffset,
      "cellElements" : []
    };
  },

  getCellElement : function( container, cell ) {
    return container.cellElements[ cell ] || null;
  },

  getCellByElement : function( container, element ) {
    return container.cellElements.indexOf( element );
  },

  render : function( options ) {
    if( !options.container || options.container.template !== this ) {
      throw new Error( "No valid TemplateContainer: " + options.container );
    }
    this._createElements( options );
    this._renderAllContent( options );
    this._renderAllBounds( options );
  },

  getCellCount : function() {
    return this._cells.length;
  },

  getCellType : function( cell ) {
    return this._cells[ cell ].type;
  },

  isCellSelectable : function( cell ) {
    return this._cells[ cell ].selectable === true;
  },

  hasContent : function( item, cell ) {
    switch( this._getContentType( cell ) ) {
      case "text":
        return this._hasText( item, cell );
      case "image":
        return this._hasImage( item, cell );
      default:
        return false;
    }
  },

  getCellName : function( cell ) {
    return this._cells[ cell ].name || null;
  },

  getCellContent : function( item, cell, cellRenderOptions ) {
    if( !item ) {
      return null;
    }
    switch( this._getContentType( cell ) ) {
      case "text":
        return this._getText( item, cell, cellRenderOptions || {} );
      case "image":
        return this._getImage( item, cell, cellRenderOptions || {} );
      default:
        return null;
    }
  },

  _getText : function( item, cell, cellRenderOptions ) {
    if( this._isBound( cell ) ) {
      return item.getText( this._getIndex( cell ), cellRenderOptions.escaped );
    } else {
      return this._cells[ cell ].defaultText || "";
    }
  },

  _getImage : function( item, cell ) {
    if( this._isBound( cell ) ) {
      return item.getImage( this._getIndex( cell ) );
    } else {
      var defaultImage = this._cells[ cell ].defaultImage;
      return defaultImage ? defaultImage[ 0 ] : null;
    }
  },

  getCellForeground : function( item, cell ) {
    var result = null;
    if( item ) {
      if( this._isBound( cell ) ) {
        result = item.getCellForeground( this._getIndex( cell ) );
      }
      if( ( result === null || result === "" ) && this._cells[ cell ].foreground ) {
        result = this._cells[ cell ].foreground;
      }
    }
    return result;
  },

  getCellBackground : function( item, cell ) {
    var result = null;
    if( item ) {
      if( this._isBound( cell ) ) {
        result = item.getCellBackground( this._getIndex( cell ) );
      }
      if( ( result === null || result === "" ) && this._cells[ cell ].background ) {
        result = this._cells[ cell ].background;
      }
    }
    return result;
  },

  getCellFont : function( item, cell ){
    var result = null;
    if( item ) {
      if( this._isBound( cell ) ) {
        result = item.getCellFont( this._getIndex( cell ) );
      }
      if( ( result === null || result === "" ) && this._cells[ cell ].font ) {
        result = this._cells[ cell ].font;
      }
    }
    return result;
  },

  _createElements : function( options ) { // TODO [tb] : do during renderContent
    var elements = options.container.cellElements;
    var item = options.item;
    for( var i = 0; i < this._cells.length; i++ ) {
      if(    !elements[ i ]
          && this._cellRenderer[ i ]
          && ( this.hasContent( item, i ) || ( this.getCellBackground( item, i ) != null ) )
      ) {
        var element = this._cellRenderer[ i ].createElement( this._cells[ i ] );
        element.style.zIndex = options.container.zIndexOffset + i;
        options.container.element.appendChild( element );
        options.container.cellElements[ i ] = element;
      }
    }
  },
  _renderAllContent : function( options ) {
    var cellRenderOptions = {
      "markupEnabled" : options.markupEnabled,
      "enabled" : options.enabled,
      "seeable" : options.seeable
    };
    for( var i = 0; i < this._cells.length; i++ ) {
      var element = options.container.cellElements[ i ];
      if( element ) {
        var cellRenderer = renderer[ this._cells[ i ].type ];
        var renderContent = cellRenderer.renderContent;
        cellRenderOptions.escaped = cellRenderer.shouldEscapeText( options );
        // TODO : render styles only if changing
        this._renderBackground( element, this.getCellBackground( options.item, i ) );
        this._renderForeground( element, this.getCellForeground( options.item, i ) );
        this._renderFont( element, this.getCellFont( options.item, i ) );
        renderContent( element,
                       this.getCellContent( options.item, i, cellRenderOptions ),
                       this._cells[ i ],
                       cellRenderOptions );
      }
    }
  },

  // TODO [tb] : optimize to render only flexi content or if bounds changed or if new
  _renderAllBounds : function( options ) {
    for( var i = 0; i < this._cells.length; i++ ) {
      var element = options.container.cellElements[ i ];
      if( element ) {
        element.style.left = this._getCellLeft( options, i ) + "px";
        element.style.top = this._getCellTop( options, i ) + "px";
        element.style.width = this._getCellWidth( options, i ) + "px";
        element.style.height = this._getCellHeight( options, i ) + "px";
      }
    }
  },

  _renderBackground : function( element, color ) {
    element.style.backgroundColor = color || "transparent";
  },

  _renderForeground : function( element, color ) {
    element.style.color = color || INHERIT;
  },

  _renderFont : function( element, font ) {
    if( font ) {
      element.style.font = font;
    } else {
      rwt.html.Font.resetElement( element );
    }
  },

  /**
   * The type of content "text/image" the renderer expects
   */
  _getContentType : function( cell ) {
    var cellRenderer = this._cellRenderer[ cell ];
    return cellRenderer ? cellRenderer.contentType : null;
  },

  _getCellLeft : function( options, cell ) {
    var cellData = this._cells[ cell ];
    return   cellData.left !== undefined
           ? cellData.left
           : options.bounds[ 2 ] - cellData.width - cellData.right;
  },

  _getCellTop : function( options, cell ) {
    var cellData = this._cells[ cell ];
    return   cellData.top !== undefined
           ? cellData.top
           : options.bounds[ 3 ] - cellData.height - cellData.bottom;
  },

  _getCellWidth : function( options, cell ) {
    var cellData = this._cells[ cell ];
    return   cellData.width !== undefined
           ? cellData.width
           : options.bounds[ 2 ] - cellData.left - cellData.right;
  },

  _getCellHeight : function( options, cell ) {
    var cellData = this._cells[ cell ];
    return   cellData.height !== undefined
           ? cellData.height
           : options.bounds[ 3 ] - cellData.top - cellData.bottom;
  },

  _hasText : function( item, cell ) {
    if( !item ) {
      return false;
    } else if( this._isBound( cell ) ) {
      return item.hasText( this._getIndex( cell ) );
    } else {
      return this._cells[ cell ].defaultText != null;
    }
  },

  _hasImage : function( item, cell ) {
    if( !item ) {
      return false;
    } else if( this._isBound( cell ) ) {
      return item.getImage( this._getIndex( cell ) ) !== null;
    } else {
      return this._cells[ cell ].defaultImage != null;
    }
  },

  _isBound : function( cell ) {
    return typeof this._cells[ cell ].bindingIndex === "number";
  },

  _getIndex : function( cell ) {
    return this._cells[ cell ].bindingIndex;
  },

  _parseCells : function() {
    for( var i = 0; i < this._cells.length; i++ ) {
      this._cellRenderer[ i ] = renderer[ this._cells[ i ].type ];
      if( this._cells[ i ].font ) {
        var font = this._cells[ i ].font;
        this._cells[ i ].font = rwt.html.Font.fromArray( font ).toCss();
      }
      if( this._cells[ i ].foreground ) {
        var foreground = this._cells[ i ].foreground;
        this._cells[ i ].foreground = rwt.util.Colors.rgbToRgbString( foreground );
      }
      if( this._cells[ i ].background ) {
        var background = this._cells[ i ].background;
        this._cells[ i ].background = rwt.util.Colors.rgbToRgbString( background );
      }
    }
  }

};

}());

