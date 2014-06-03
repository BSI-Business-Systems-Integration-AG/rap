/*******************************************************************************
 * Copyright (c) 2010, 2014 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

(function() {

var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
var Client = rwt.client.Client;
var Style = rwt.html.Style;

rwt.qx.Class.define( "org.eclipse.rwt.test.tests.StyleTest", {

  extend : rwt.qx.Object,

  members : {

    testSetStylePropertyOnWidget : function() {
      var red = "red";
      var widget = this._createWidget();
      Style.setStyleProperty( widget, "backgroundColor", red);
      assertEquals( red, TestUtil.getCssBackgroundColor( widget ) );
      widget.destroy();
    },

    testSetStylePropertyOnElement : function() {
      var red = "red";
      var element = document.createElement( "div" );
      Style.setStyleProperty( element, "backgroundColor", red );
      assertEquals( red, element.style.backgroundColor );
    },

    testRemoveStylePropertyOnWidget : function() {
      var red = "red";
      var widget = this._createWidget();
      Style.setStyleProperty( widget, "backgroundColor", red );
      Style.removeStyleProperty( widget, "backgroundColor" );
      assertNull( TestUtil.getCssBackgroundColor( widget ) );
      widget.destroy();
    },

    testRemoveStylePropertyOnElement : function() {
      var red = "red";
      var element = document.createElement( "div" );
      Style.setStyleProperty( element, "backgroundColor", red );
      Style.removeStyleProperty( element, "backgroundColor" );
      assertEquals( "", element.style.backgroundColor );
    },

    testSetStylePropertyOnWidgetBeforeCreate : function() {
      var widget = this._createWidget( true );
      var red = "red";
      Style.setStyleProperty( widget, "foo", red );
      TestUtil.flush();
      assertEquals( red, widget._style.foo );
      widget.destroy();
    },

    testRemoveStylePropertyOnWidgetBeforeCreate : function() {
      var red = "red";
      var widget = this._createWidget( true );
      Style.setStyleProperty( widget, "foo", red );
      Style.removeStyleProperty( widget, "foo" );
      TestUtil.flush();
      assertEquals( undefined, widget._style.foo );
      widget.destroy();
    },

    testSetOpacity : function() {
      var element = document.createElement( "div" );
      var widget = this._createWidget();
      Style.setOpacity( element, 0.5 );
      Style.setOpacity( widget, 0.5 );
      assertTrue( TestUtil.hasElementOpacity( element ) );
      assertTrue( TestUtil.hasElementOpacity( widget.getElement() ) );
      Style.setOpacity( element, 1 );
      Style.setOpacity( widget, 1 );
      assertFalse( TestUtil.hasElementOpacity( element ) );
      assertFalse( TestUtil.hasElementOpacity( widget.getElement() ) );
      var css1 = element.style.cssText.toLowerCase();
      var css2 = widget.getElement().style.cssText.toLowerCase();
      // additional check for IE:
      assertTrue( css1.indexOf( "filter" ) == -1 );
      assertTrue( css2.indexOf( "filter" ) == -1 );
      widget.destroy();
    },

    testSetOpacityBeforeWidgetCreation : function() {
      var widgetNormal = this._createWidget( true );
      var widgetTransp = this._createWidget( true );
      widgetNormal.setOpacity( 0.5 );
      widgetTransp.setOpacity( 0.5 );
      widgetNormal.setOpacity( 1 );
      TestUtil.flush();
      assertFalse( TestUtil.hasElementOpacity( widgetNormal.getElement() ) );
      assertTrue( TestUtil.hasElementOpacity( widgetTransp.getElement() ) );
      // additional check for IE:
      var css = widgetNormal.getElement().style.cssText.toLowerCase();
      assertTrue( css.indexOf( "filter" ) == -1 );
      widgetNormal.destroy();
      widgetTransp.destroy();
    },

    testSetOpacityOnOuterElement : function() {
      var element = document.createElement( "div" );
      var widget = this._createWidget();
      widget.prepareEnhancedBorder();
      Style.setOpacity( element, 0.5 );
      Style.setOpacity( widget, 0.5 );
      assertTrue( TestUtil.hasElementOpacity( element ) );
      assertTrue( TestUtil.hasElementOpacity( widget.getElement() ) );
      Style.setOpacity( element, 1 );
      Style.setOpacity( widget, 1 );
      assertFalse( TestUtil.hasElementOpacity( element ) );
      assertFalse( TestUtil.hasElementOpacity( widget.getElement() ) );
      var css1 = element.style.cssText.toLowerCase();
      var css2 = widget.getElement().style.cssText.toLowerCase();
      // additional check for IE:
      assertTrue( css1.indexOf( "filter" ) == -1 );
      assertTrue( css2.indexOf( "filter" ) == -1 );
      widget.destroy();
    },

    testSetRemoveTextShadow : function() {
      var widget = this._createWidget( true );
      var shadow = [ false, 1, 1, 0, 0, "#ff0000", 0.5 ];
      widget.setTextShadow( shadow );
      TestUtil.flush();
      var element = widget.getElement();
      var css = element.style.cssText.toLowerCase();
      var isMshtml = Client.isMshtml() || Client.isNewMshtml();
      if( isMshtml && Client.getMajor() < 10 ) {
        assertFalse( css.indexOf( "text-shadow:" ) !== -1 );
      } else {
        assertTrue( css.indexOf( "text-shadow:" ) !== -1 );
      }
      widget.setTextShadow( null );
      TestUtil.flush();
      css = element.style.cssText.toLowerCase();
      assertTrue( css.indexOf( "text-shadow:" ) === -1 );
      widget.destroy();
    },

    testSetBackgroundImage : function() {
      var el = document.createElement( "div" );

      Style.setBackgroundImage( el, "foo.png" );

      var actual = TestUtil.getCssBackgroundImage( el );
      if( Client.isMshtml() ) {
        assertTrue( actual.indexOf( "http" ) === 0 );
      }
      if( Client.isMshtml() || Client.isWebkit() ) {
        assertTrue( actual.indexOf( "foo.png" ) !== -1 );
      } else {
        assertEquals( "foo.png", actual );
      }
    },

    testSetBackgroundImage_externalImage : function() {
      var el = document.createElement( "div" );

      Style.setBackgroundImage( el, "http://foo.org/bar.png" );

      var actual = TestUtil.getCssBackgroundImage( el );
      assertEquals( "http://foo.org/bar.png", actual );
    },

    /////////
    // Helper

    _createWidget : function( noFlush ) {
      var result = new rwt.widgets.base.MultiCellWidget( [] );
      result.addToDocument();
      result.setLocation( 0, 0 );
      result.setDimension( 100, 100 );
      if( noFlush !== true ) {
        rwt.widgets.base.Widget.flushGlobalQueues();
      }
      return result;
    }

  }

} );

}() );
