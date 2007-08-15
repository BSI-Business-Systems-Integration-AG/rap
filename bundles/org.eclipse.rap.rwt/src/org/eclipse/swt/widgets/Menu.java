/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.swt.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MenuListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.widgets.*;

/**
 * Instances of this class are user interface objects that contain
 * menu items.
 * <dl>
 * <dt><b>Styles:</b></dt>
 * <dd>BAR, DROP_DOWN, POP_UP, NO_RADIO_GROUP</dd>
 * <dd>LEFT_TO_RIGHT, RIGHT_TO_LEFT</dd>
 * <dt><b>Events:</b></dt>
 * <dd>Help, Hide, Show </dd>
 * </dl>
 * <p>
 * Note: Only one of BAR, DROP_DOWN and POP_UP may be specified.
 * Only one of LEFT_TO_RIGHT or RIGHT_TO_LEFT may be specified.
 * </p><p>
 * IMPORTANT: This class is <em>not</em> intended to be subclassed.
 * </p>
 */
public class Menu extends Widget {

  private final Shell parent;
  private final ItemHolder itemHolder;
  private int x;
  private int y;
  private boolean visible = false;
  MenuItem cascade;

  /**
   * Constructs a new instance of this class given its parent
   * (which must be a <code>Menu</code>) and sets the style
   * for the instance so that the instance will be a drop-down
   * menu on the given parent's parent.
   *
   * @param parentMenu a menu which will be the parent of the new instance (cannot be null)
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the parent is null</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the parent</li>
   *    <li>ERROR_INVALID_SUBCLASS - if this class is not an allowed subclass</li>
   * </ul>
   *
   * @see SWT#DROP_DOWN
   * @see Widget#checkSubclass
   * @see Widget#getStyle
   */
  public Menu( final Menu menu ) {
    this( menu.getParent(), SWT.DROP_DOWN );
  }

  /**
   * Constructs a new instance of this class given its parent
   * (which must be a <code>MenuItem</code>) and sets the style
   * for the instance so that the instance will be a drop-down
   * menu on the given parent's parent menu.
   *
   * @param parentItem a menu item which will be the parent of the new instance (cannot be null)
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the parent is null</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the parent</li>
   *    <li>ERROR_INVALID_SUBCLASS - if this class is not an allowed subclass</li>
   * </ul>
   *
   * @see SWT#DROP_DOWN
   * @see Widget#checkSubclass
   * @see Widget#getStyle
   */
  public Menu( final MenuItem parent ) {
    this( parent.getParent().getParent(), SWT.DROP_DOWN );
  }

  /**
   * Constructs a new instance of this class given its parent,
   * and sets the style for the instance so that the instance
   * will be a popup menu on the given parent's shell.
   *
   * @param parent a control which will be the parent of the new instance (cannot be null)
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the parent is null</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the parent</li>
   *    <li>ERROR_INVALID_SUBCLASS - if this class is not an allowed subclass</li>
   * </ul>
   *
   * @see SWT#POP_UP
   * @see Widget#checkSubclass
   * @see Widget#getStyle
   */
  public Menu( final Control parent ) {
    this( parent.getShell(), SWT.POP_UP );
  }

  /**
   * Constructs a new instance of this class given its parent
   * (which must be a <code>Decorations</code>) and a style value
   * describing its behavior and appearance.
   * <p>
   * The style value is either one of the style constants defined in
   * class <code>SWT</code> which is applicable to instances of this
   * class, or must be built by <em>bitwise OR</em>'ing together 
   * (that is, using the <code>int</code> "|" operator) two or more
   * of those <code>SWT</code> style constants. The class description
   * lists the style constants that are applicable to the class.
   * Style bits are also inherited from superclasses.
   * </p>
   *
   * @param parent a decorations control which will be the parent of the new instance (cannot be null)
   * @param style the style of menu to construct
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the parent is null</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the parent</li>
   *    <li>ERROR_INVALID_SUBCLASS - if this class is not an allowed subclass</li>
   * </ul>
   *
   * @see SWT#BAR
   * @see SWT#DROP_DOWN
   * @see SWT#POP_UP
   * @see Widget#checkSubclass
   * @see Widget#getStyle
   */
  public Menu( final Shell parent, final int style ) {
    super( parent, checkStyle( style ) );
    this.parent = parent;
    itemHolder = new ItemHolder( MenuItem.class );
    MenuHolder.addMenu( parent, this );
  }

  public final Display getDisplay() {
    checkWidget();
    return parent.getDisplay();
  }

  /**
   * Returns the receiver's parent, which must be a <code>Decorations</code>.
   *
   * @return the receiver's parent
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public final Shell getParent() {
    checkWidget();
    return parent;
  }

  /**
   * Returns the receiver's parent item, which must be a
   * <code>MenuItem</code> or null when the receiver is a
   * root.
   *
   * @return the receiver's parent item
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public MenuItem getParentItem() {
    checkWidget ();
    return cascade;
  }
  
  /**
   * Returns the receiver's parent item, which must be a
   * <code>Menu</code> or null when the receiver is a
   * root.
   *
   * @return the receiver's parent item
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public Menu getParentMenu() {
    checkWidget();
    if( cascade != null ) {
      return cascade.getParent();
    }
    return null;
  }
  
  public Object getAdapter( final Class adapter ) {
    Object result;
    if( adapter == IItemHolderAdapter.class ) {
      result = itemHolder;
    } else {
      result = super.getAdapter( adapter );
    }
    return result;
  }
  
  /**
   * Sets the location of the receiver, which must be a popup,
   * to the point specified by the arguments which are relative
   * to the display.
   * <p>
   * Note that this is different from most widgets where the
   * location of the widget is relative to the parent.
   * </p><p>
   * Note that the platform window manager ultimately has control
   * over the location of popup menus.
   * </p>
   *
   * @param x the new x coordinate for the receiver
   * @param y the new y coordinate for the receiver
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public void setLocation( final int x, final int y ) {
    checkWidget();
    if( ( style & ( SWT.BAR | SWT.DROP_DOWN ) ) == 0 ) {
      this.x = x;
      this.y = y;
    }
  }
  
  /**
   * Sets the location of the receiver, which must be a popup,
   * to the point specified by the argument which is relative
   * to the display.
   * <p>
   * Note that this is different from most widgets where the
   * location of the widget is relative to the parent.
   * </p><p>
   * Note that the platform window manager ultimately has control
   * over the location of popup menus.
   * </p>
   *
   * @param location the new location for the receiver
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the point is null</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   * 
   * @since 1.0
   */
  public void setLocation( final Point location ) {
    checkWidget();
    if( location == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    setLocation( location.x, location.y );
  }
  
  public Rectangle getBounds() {
    checkWidget();
    // TODO: [fappel] how to calculate width and height?
    return new Rectangle( x, y, 0, 0 );
  }
  
  /**
   * Returns the receiver's shell. For all controls other than
   * shells, this simply returns the control's nearest ancestor
   * shell. Shells return themselves, even if they are children
   * of other shells.
   *
   * @return the receiver's shell
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   *
   * @see #getParent
   */
  public Shell getShell() {
    checkWidget();
    return parent;
  }

  ///////////
  // Visible
  
  /**
   * Marks the receiver as visible if the argument is <code>true</code>,
   * and marks it invisible otherwise. 
   * <p>
   * If one of the receiver's ancestors is not visible or some
   * other condition makes the receiver not visible, marking
   * it visible may not actually cause it to be displayed.
   * </p>
   *
   * @param visible the new visibility state
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public void setVisible( final boolean visible ) {
    checkWidget();
    if( ( style & ( SWT.BAR | SWT.DROP_DOWN ) ) == 0 ) {
      if( this.visible != visible ) {
        this.visible = visible;
        if( visible ) {
          MenuEvent event = new MenuEvent( this, MenuEvent.MENU_SHOWN );
          event.processEvent();
        }
      }
    }
  }
  
  /**
   * Returns <code>true</code> if the receiver is visible, and
   * <code>false</code> otherwise.
   * <p>
   * If one of the receiver's ancestors is not visible or some
   * other condition makes the receiver not visible, this method
   * may still indicate that it is considered visible even though
   * it may not actually be showing.
   * </p>
   *
   * @return the receiver's visibility state
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public boolean getVisible() {
    checkWidget();
    boolean result;
    if( ( style & SWT.BAR ) != 0 ) {
      result = ( this == parent.getMenuBar() );
    } else if( ( style & SWT.POP_UP ) != 0 ) {
      result = visible;
    } else {
      // we don't know which menus are currently visible on the client
      result = false;
    }
    return result;
  }
  
  /**
   * Returns <code>true</code> if the receiver is visible and all
   * of the receiver's ancestors are visible and <code>false</code>
   * otherwise.
   *
   * @return the receiver's visibility state
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   *
   * @see #getVisible
   */
  public boolean isVisible (){
    checkWidget();
    return getVisible();
  }
  
  ///////////
  // Enabled
  
  /**
   * Enables the receiver if the argument is <code>true</code>,
   * and disables it otherwise. A disabled menu is typically
   * not selectable from the user interface and draws with an
   * inactive or "grayed" look.
   *
   * @param enabled the new enabled state
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public void setEnabled( final boolean enabled ) {
    checkWidget();
    state &= ~DISABLED;
    if( !enabled ) {
      state |= DISABLED;
    }
  }

  /**
   * Returns <code>true</code> if the receiver is enabled, and
   * <code>false</code> otherwise. A disabled menu is typically
   * not selectable from the user interface and draws with an
   * inactive or "grayed" look.
   *
   * @return the receiver's enabled state
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   * 
   * @see #isEnabled
   */
  public boolean getEnabled() {
    checkWidget();
    return ( state & DISABLED ) == 0;
  }

  /**
   * Returns <code>true</code> if the receiver is enabled and all
   * of the receiver's ancestors are enabled, and <code>false</code>
   * otherwise. A disabled menu is typically not selectable from the
   * user interface and draws with an inactive or "grayed" look.
   *
   * @return the receiver's enabled state
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   * 
   * @see #getEnabled
   */
  public boolean isEnabled() {
    checkWidget();
    Menu parentMenu = getParentMenu();
    if( parentMenu == null ) {
      return getEnabled();
    }
    return getEnabled() && parentMenu.isEnabled();
  }
  
  ////////////////////////////
  // Management of menu items
  
  /**
   * Returns the number of items contained in the receiver.
   *
   * @return the number of items
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public int getItemCount() {
    checkWidget();
    return itemHolder.size();
  }

  /**
   * Returns a (possibly empty) array of <code>MenuItem</code>s which
   * are the items in the receiver. 
   * <p>
   * Note: This is not the actual structure used by the receiver
   * to maintain its list of items, so modifying the array will
   * not affect the receiver. 
   * </p>
   *
   * @return the items in the receiver
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public MenuItem[] getItems() {
    checkWidget();
    return (org.eclipse.swt.widgets.MenuItem[] )itemHolder.getItems();
  }

  /**
   * Returns the item at the given, zero-relative index in the
   * receiver. Throws an exception if the index is out of range.
   *
   * @param index the index of the item to return
   * @return the item at the given index
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_INVALID_RANGE - if the index is not between 0 and the number of elements in the list minus 1 (inclusive)</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public MenuItem getItem( final int index ) {
    checkWidget();
    return ( MenuItem )itemHolder.getItem( index );
  }
  
  /**
   * Searches the receiver's list starting at the first item
   * (index 0) until an item is found that is equal to the 
   * argument, and returns the index of that item. If no item
   * is found, returns -1.
   *
   * @param item the search item
   * @return the index of the item
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the string is null</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public int indexOf( final MenuItem menuItem ) {
    checkWidget();
    if( menuItem == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    if( menuItem.isDisposed() ) {
      SWT.error( SWT.ERROR_INVALID_ARGUMENT );
    }
    return itemHolder.indexOf( menuItem );
  }
  
  ////////////////////
  // Widget overrides
  
  // TODO [rh] disposal of Menu and its items not yet completely implemented
  protected final void releaseChildren() {
    MenuItem[] menuItems = ( MenuItem[] )ItemHolder.getItems( this );
    for( int i = 0; i < menuItems.length; i++ ) {
      menuItems[ i ].dispose();
    }
  }

  protected final void releaseParent() {
    // do nothing
  }

  protected final void releaseWidget() {
    MenuHolder.removeMenu( parent, this );
  }

  String getNameText() {
    String result = "";
    MenuItem[] items = getItems();
    int length = items.length;
    if( length > 0 ) {
      for( int i = 0; i < length - 1; i++ ) {
        result = result + items[ i ].getNameText() + ", ";
      }
      result = result + items[ length - 1 ].getNameText();
    }
    return result;
  }

  ///////////////////////////////////////
  // Listener registration/deregistration
  
  public void addMenuListener( final MenuListener listener ) {
    checkWidget();
    MenuEvent.addListener( this, listener );
  }

  public void removeMenuListener( final MenuListener listener ) {
    checkWidget();
    MenuEvent.removeListener( this, listener );
  }

  //////////////////
  // Helping methods
  
  private static int checkStyle( final int style ) {
    return checkBits( style, SWT.POP_UP, SWT.BAR, SWT.DROP_DOWN, 0, 0, 0 );
  }
}
