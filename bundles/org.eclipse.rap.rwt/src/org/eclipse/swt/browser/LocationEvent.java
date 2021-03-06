/*******************************************************************************
 * Copyright (c) 2007, 2012 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.browser;

import org.eclipse.rwt.Adaptable;
import org.eclipse.swt.events.TypedEvent;


/**
 * A <code>LocationEvent</code> is sent by a {@link Browser} to
 * {@link LocationListener}'s when the <code>Browser</code>
 * navigates to a different URL. This notification typically
 * occurs when the application navigates to a new location with
 * {@link Browser#setUrl(String)} or when the user activates a
 * hyperlink.
 *
 * <p><strong>IMPORTANT:</strong> All <code>public static</code> members of
 * this class are <em>not</em> part of the RWT public API. They are marked
 * public only so that they can be shared within the packages provided by RWT.
 * They should never be accessed from application code.
 * </p>
 *
 * @since 1.0
 */
public class LocationEvent extends TypedEvent {

  private static final long serialVersionUID = 1L;

  public static final int CHANGING = 5011;
  public static final int CHANGED = 5012;

  private static final Class LISTENER = LocationListener.class;

  /** current location */
  public String location;

  /**
   * A flag indicating whether the location opens in the top frame
   * or not.
   */
  public boolean top;

  /**
   * A flag indicating whether the location loading should be allowed.
   * Setting this field to <code>false</code> will cancel the operation.
   */
  public boolean doit = true;

  LocationEvent( Object source, int id, String location ) {
    super( source, id );
    this.location = location;
  }

  @Override
  protected void dispatchToObserver( Object listener ) {
    switch( getID() ) {
      case CHANGING:
        ( ( LocationListener )listener ).changing( this );
      break;
      case CHANGED:
        ( ( LocationListener )listener ).changed( this );
      break;
      default:
        throw new IllegalStateException( "Invalid event handler type." );
    }
  }

  @Override
  protected Class getListenerType() {
    return LISTENER;
  }

  @Override
  protected boolean allowProcessing() {
    // It is safe to always allow to fire this event as it is only generated
    // server-side
    return true;
  }

  public static boolean hasListener( Adaptable adaptable ) {
    return hasListener( adaptable, LISTENER );
  }

  public static void addListener( Adaptable adaptable, LocationListener listener ) {
    addListener( adaptable, LISTENER, listener );
  }

  public static void removeListener( Adaptable adaptable, LocationListener listener ) {
    removeListener( adaptable, LISTENER, listener );
  }

  public static Object[] getListeners( Adaptable adaptable ) {
    return getListener( adaptable, LISTENER );
  }
}
