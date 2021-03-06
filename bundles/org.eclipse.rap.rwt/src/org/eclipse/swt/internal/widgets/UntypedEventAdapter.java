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
package org.eclipse.swt.internal.widgets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.internal.SerializableCompatibility;
import org.eclipse.swt.internal.events.*;
import org.eclipse.swt.widgets.*;


public final class UntypedEventAdapter
  implements SerializableCompatibility,
             ControlListener,
             DisposeListener,
             SelectionListener,
             FocusListener,
             TreeListener,
             ExpandListener,
             ShellListener,
             MenuListener,
             ModifyListener,
             SetDataListener,
             VerifyListener,
             MouseListener,
             KeyListener,
             TraverseListener,
             ShowListener,
             ActivateListener,
             HelpListener,
             DragDetectListener,
             MenuDetectListener,
             ArmListener
{

  private static final class Entry implements SerializableCompatibility {
    final int eventType;
    final Listener listener;
    private Entry( int eventType, Listener listener ) {
      this.eventType = eventType;
      this.listener = listener;
    }
  }

  private final java.util.Map<Widget,  java.util.List< Entry >> listeners;

  public UntypedEventAdapter() {
    listeners = new HashMap<Widget, List<Entry>>();
  }

  // XXXListener interface imlementations

  public void controlMoved( ControlEvent typedEvent ) {
    Event event = createEvent( SWT.Move, typedEvent.getSource() );
    copyFields( typedEvent, event );
    dispatchEvent( event );
  }

  public void controlResized( ControlEvent typedEvent ) {
    Event event = createEvent( SWT.Resize, typedEvent.getSource() );
    copyFields( typedEvent, event );
    dispatchEvent( event );
  }

  public void widgetDisposed( DisposeEvent typedEvent ) {
    Event event = createEvent( SWT.Dispose, typedEvent.getSource() );
    copyFields( typedEvent, event );
    dispatchEvent( event );
  }

  public void widgetDefaultSelected( SelectionEvent typedEvent ) {
    Event event = createEvent( SWT.DefaultSelection, typedEvent.getSource() );
    copyFields( typedEvent, event );
    dispatchEvent( event );
  }

  public void widgetSelected( SelectionEvent typedEvent ) {
    Event event = createEvent( SWT.Selection, typedEvent.getSource() );
    copyFields( typedEvent, event );
    dispatchEvent( event );
  }

  public void focusGained( FocusEvent typedEvent ) {
    Event event = createEvent( SWT.FocusIn, typedEvent.getSource() );
    copyFields( typedEvent, event );
    dispatchEvent( event );
  }

  public void focusLost( FocusEvent typedEvent ) {
    Event event = createEvent( SWT.FocusOut, typedEvent.getSource() );
    copyFields( typedEvent, event );
    dispatchEvent( event );
  }

  public void treeCollapsed( TreeEvent typedEvent ) {
    Event event = createEvent( SWT.Collapse, typedEvent.getSource() );
    copyFields( typedEvent, event );
    dispatchEvent( event );
  }

  public void treeExpanded( TreeEvent typedEvent ) {
    Event event = createEvent( SWT.Expand, typedEvent.getSource() );
    copyFields( typedEvent, event );
    dispatchEvent( event );
  }

  public void itemCollapsed( ExpandEvent typedEvent ) {
    Event event = createEvent( SWT.Collapse, typedEvent.getSource() );
    copyFields( typedEvent, event );
    dispatchEvent( event );
  }

  public void itemExpanded( ExpandEvent typedEvent ) {
    Event event = createEvent( SWT.Expand, typedEvent.getSource() );
    copyFields( typedEvent, event );
    dispatchEvent( event );
  }

  public void shellActivated( ShellEvent typedEvent ) {
    Event event = createEvent( SWT.Activate, typedEvent.getSource() );
    copyFields( typedEvent, event );
    dispatchEvent( event );
  }

  public void shellClosed( ShellEvent typedEvent ) {
    Event event = createEvent( SWT.Close, typedEvent.getSource() );
    copyFields( typedEvent, event );
    dispatchEvent( event );
    typedEvent.doit = event.doit;
  }

  public void shellDeactivated( ShellEvent typedEvent ) {
    Event event = createEvent( SWT.Deactivate, typedEvent.getSource() );
    copyFields( typedEvent, event );
    dispatchEvent( event );
  }

  public void menuHidden( MenuEvent typedEvent ) {
    Event event = createEvent( SWT.Hide, typedEvent.getSource() );
    copyFields( typedEvent, event );
    dispatchEvent( event );
  }

  public void menuShown( MenuEvent typedEvent ) {
    Event event = createEvent( SWT.Show, typedEvent.getSource() );
    copyFields( typedEvent, event );
    dispatchEvent( event );
  }

  public void modifyText( ModifyEvent typedEvent ) {
    Event event = createEvent( SWT.Modify, typedEvent.getSource() );
    copyFields( typedEvent, event );
    dispatchEvent( event );
  }

  public void verifyText( VerifyEvent typedEvent ) {
    Event event = createEvent( SWT.Verify, typedEvent.getSource() );
    copyFields( typedEvent, event );
    dispatchEvent( event );
  }

  public void update( SetDataEvent typedEvent ) {
    Event event = createEvent( SWT.SetData, typedEvent.getSource() );
    copyFields( typedEvent, event );
    dispatchEvent( event );
  }

  public void mouseDown( MouseEvent typedEvent ) {
    Event event = createEvent( SWT.MouseDown, typedEvent.getSource() );
    copyFields( typedEvent, event );
    dispatchEvent( event );
  }

  public void mouseUp( MouseEvent typedEvent ) {
    Event event = createEvent( SWT.MouseUp, typedEvent.getSource() );
    copyFields( typedEvent, event );
    dispatchEvent( event );
  }

  public void mouseDoubleClick( MouseEvent typedEvent ) {
    Event event = createEvent( SWT.MouseDoubleClick, typedEvent.getSource() );
    copyFields( typedEvent, event );
    dispatchEvent( event );
  }

  public void keyPressed( KeyEvent typedEvent ) {
    Event event = createEvent( SWT.KeyDown, typedEvent.getSource() );
    copyFields( typedEvent, event );
    dispatchEvent( event );
    typedEvent.doit = event.doit;
  }

  public void keyReleased( KeyEvent typedEvent ) {
    Event event = createEvent( SWT.KeyUp, typedEvent.getSource() );
    copyFields( typedEvent, event );
    dispatchEvent( event );
  }

  public void keyTraversed( TraverseEvent typedEvent ) {
    Event event = createEvent( SWT.Traverse, typedEvent.getSource() );
    copyFields( typedEvent, event );
    dispatchEvent( event );
    typedEvent.doit = event.doit;
  }

  public void controlShown( ShowEvent typedEvent ) {
    Event event = createEvent( SWT.Show, typedEvent.getSource() );
    copyFields( typedEvent, event );
    dispatchEvent( event );
  }

  public void controlHidden( ShowEvent typedEvent ) {
    Event event = createEvent( SWT.Hide, typedEvent.getSource() );
    copyFields( typedEvent, event );
    dispatchEvent( event );
  }

  public void activated( ActivateEvent typedEvent ) {
    Event event = createEvent( SWT.Activate, typedEvent.getSource() );
    copyFields( typedEvent, event );
    dispatchEvent( event );
  }

  public void deactivated( ActivateEvent typedEvent ) {
    Event event = createEvent( SWT.Deactivate, typedEvent.getSource() );
    copyFields( typedEvent, event );
    dispatchEvent( event );
  }

  public void helpRequested( HelpEvent typedEvent ) {
    Event event = createEvent( SWT.Help, typedEvent.getSource() );
    copyFields( typedEvent, event );
    dispatchEvent( event );
  }

  public void dragDetected( DragDetectEvent typedEvent ) {
    Event event = createEvent( SWT.DragDetect, typedEvent.getSource() );
    copyFields( typedEvent, event );
    dispatchEvent( event );
  }

  public void menuDetected( MenuDetectEvent typedEvent ) {
    Event event = createEvent( SWT.MenuDetect, typedEvent.getSource() );
    copyFields( typedEvent, event );
    dispatchEvent( event );
  }

  public void widgetArmed( ArmEvent typedEvent ) {
    Event event = createEvent( SWT.Arm, typedEvent.getSource() );
    copyFields( typedEvent, event );
    dispatchEvent( event );
  }

  //////////////////////
  // Listener management

  public void addListener( Widget widget, int eventType, Listener listener ) {
    boolean validEventType = true;
    if(!hasListener(widget, eventType)) {
    switch( eventType ) {
      case SWT.Move:
      case SWT.Resize:
        ControlEvent.addListener( widget, this );
      break;
      case SWT.Dispose:
        DisposeEvent.addListener( widget, this );
      break;
      case SWT.Selection:
      case SWT.DefaultSelection:
        SelectionEvent.addListener( widget, this );
      break;
      case SWT.FocusIn:
      case SWT.FocusOut:
        FocusEvent.addListener( widget, this );
      break;
      case SWT.Expand:
      case SWT.Collapse:
          if( widget instanceof ExpandBar ) {
            ExpandEvent.addListener( widget, ( ExpandListener )this );
          } else {
        TreeEvent.addListener( widget, ( TreeListener )this );
          }
      break;
      case SWT.Activate:
      case SWT.Deactivate:
        if( widget instanceof Shell ) {
          ShellEvent.addListener( widget, this );
        } else {
          ActivateEvent.addListener( widget, this );
        }
      break;
      case SWT.Close:
        ShellEvent.addListener( widget, this );
      break;
      case SWT.Hide:
        if( widget instanceof Control ) {
          ShowEvent.addListener( widget, this );
        } else {
          MenuEvent.addListener( widget, this );
        }
        break;
      case SWT.Show:
        if( widget instanceof Control ) {
          ShowEvent.addListener( widget, this );
        } else {
          MenuEvent.addListener( widget, this );
        }
      break;
      case SWT.MenuDetect:
        MenuDetectEvent.addListener( widget, this );
      break;
      case SWT.Modify:
        ModifyEvent.addListener( widget, this );
      break;
      case SWT.Verify:
        VerifyEvent.addListener( widget, ( VerifyListener )this );
      break;
      case SWT.SetData:
        SetDataEvent.addListener( widget, this );
      break;
      case SWT.MouseDown:
      case SWT.MouseUp:
      case SWT.MouseDoubleClick:
        MouseEvent.addListener( widget, this );
      break;
      case SWT.KeyDown:
      case SWT.KeyUp:
        KeyEvent.addListener( widget, this );
      break;
      case SWT.Traverse:
        TraverseEvent.addListener( widget, ( TraverseListener )this );
      break;
      case SWT.Help:
        HelpEvent.addListener( widget, this );
      break;
      case SWT.DragDetect:
        DragDetectEvent.addListener( widget, ( DragDetectListener )this );
      break;
      case SWT.Arm:
        ArmEvent.addListener( widget, this );
      break;
      default:
        validEventType = false;
    }
    }
    if( validEventType ) {
      List< Entry > list = listeners.get( widget );
      if(list == null) {
        list = new ArrayList< Entry >();
        listeners.put( widget,  list);
    }
      list.add( new Entry( eventType, listener ) );
  }
  }

  public void removeListener( Widget widget, int eventType, Listener listener ) {
    boolean validEventType = true;
    if(!hasOtherListeners(widget, eventType, listener)) {
    switch( eventType ) {
      case SWT.Move:
      case SWT.Resize:
        ControlEvent.removeListener( widget, this );
      break;
      case SWT.Dispose:
        DisposeEvent.removeListener( widget, this );
      break;
      case SWT.Selection:
      case SWT.DefaultSelection:
        SelectionEvent.removeListener( widget, this );
      break;
      case SWT.FocusIn:
      case SWT.FocusOut:
        FocusEvent.removeListener( widget, this );
      break;
      case SWT.Expand:
      case SWT.Collapse:
          if( widget instanceof ExpandBar ) {
            ExpandEvent.removeListener( widget, ( ExpandListener )this );
          } else {
        TreeEvent.removeListener( widget, ( TreeListener )this );
          }
      break;
      case SWT.Activate:
      case SWT.Deactivate:
        if( widget instanceof Shell ) {
          ShellEvent.removeListener( widget, this );
        } else {
          ActivateEvent.removeListener( widget, this );
        }
      break;
      case SWT.Close:
        ShellEvent.removeListener( widget, this );
      break;
      case SWT.Hide:
        if( widget instanceof Control ) {
          ShowEvent.removeListener( widget, this );
        } else {
          MenuEvent.removeListener( widget, this );
        }
        break;
      case SWT.Show:
        if( widget instanceof Control ) {
          ShowEvent.removeListener( widget, this );
        } else {
          MenuEvent.removeListener( widget, this );
        }
      break;
      case SWT.MenuDetect:
        MenuDetectEvent.removeListener( widget, this );
      break;
      case SWT.Modify:
        ModifyEvent.removeListener( widget, this );
      break;
      case SWT.Verify:
        VerifyEvent.removeListener( widget, ( VerifyListener )this );
      break;
      case SWT.SetData:
        SetDataEvent.removeListener( widget, this );
      break;
      case SWT.MouseDown:
      case SWT.MouseUp:
      case SWT.MouseDoubleClick:
        MouseEvent.removeListener( widget, this );
      break;
      case SWT.KeyDown:
      case SWT.KeyUp:
        KeyEvent.removeListener( widget, this );
      break;
      case SWT.Traverse:
        TraverseEvent.removeListener( widget, ( TraverseListener )this );
      break;
      case SWT.Help:
        HelpEvent.removeListener( widget, this );
      break;
      case SWT.DragDetect:
        DragDetectEvent.removeListener( widget, ( DragDetectListener )this );
      break;
      case SWT.Arm:
        ArmEvent.removeListener( widget, this );
      break;
      default:
        validEventType = false;
    }
    }
    if( validEventType ) {
      List< Entry > list = listeners.get( widget );
      if(list != null) {
        Entry[] entries = new Entry[ list.size() ];
        list.toArray( entries );
    boolean found = false;
    for( int i = 0; !found && i < entries.length; i++ ) {
      // TODO [fappel]: check whether we have also to compare eventType!
      found = entries[ i ].listener == listener;
      if( found ) {
            list.remove( entries[ i ] );
          }
        }
      }
    }
  }

  public Listener[] getListeners( int eventType ) {
    Entry[] entries = getEntries();
    java.util.List<Listener> result = new ArrayList<Listener>();
    for( int i = 0; i < entries.length; i++ ) {
      Entry entry = entries[ i ];
      if( entry.eventType == eventType ) {
        result.add( entry.listener );
      }
    }
    return result.toArray( new Listener[ result.size() ] );
  }

  public boolean hasUntypedListener( int eventType ) {
    boolean result = false;
    Entry[] entries = getEntries();
    for( int i = 0; !result && i < entries.length; i++ ) {
      Entry entry = entries[ i ];
      if( entry.eventType == eventType ) {
        result = true;
      }
    }
    return result;
  }

  public static boolean hasTypedListener( Widget widget, int eventType ) {
    boolean result = false;
    switch( eventType ) {
      case SWT.Move:
      case SWT.Resize:
        result = ControlEvent.hasListener( widget );
        break;
      case SWT.Dispose:
        result = DisposeEvent.hasListener( widget );
        break;
      case SWT.Selection:
      case SWT.DefaultSelection:
        result = SelectionEvent.hasListener( widget );
        break;
      case SWT.FocusIn:
      case SWT.FocusOut:
        result = FocusEvent.hasListener( widget );
        break;
      case SWT.Expand:
      case SWT.Collapse:
        if( widget instanceof ExpandBar ) {
          result = ExpandEvent.hasListener( widget );
        } else {
        result = TreeEvent.hasListener( widget );
        }
        break;
      case SWT.Activate:
      case SWT.Deactivate:
        if( widget instanceof Shell ) {
          result = ShellEvent.hasListener( widget );
        } else {
          result = ActivateEvent.hasListener( widget );
        }
        break;
      case SWT.Close:
        result = ShellEvent.hasListener( widget );
        break;
      case SWT.Hide:
      case SWT.Show:
        if( widget instanceof Control ) {
          result = ShowEvent.hasListener( widget );
        } else {
          result = MenuEvent.hasListener( widget );
        }
        break;
      case SWT.MenuDetect:
        result = MenuDetectEvent.hasListener( widget );
        break;
      case SWT.Modify:
        result = ModifyEvent.hasListener( widget );
        break;
      case SWT.Verify:
        result = VerifyEvent.hasListener( widget );
        break;
      case SWT.SetData:
        result = SetDataEvent.hasListener( widget );
        break;
      case SWT.MouseDown:
      case SWT.MouseUp:
      case SWT.MouseDoubleClick:
        result = MouseEvent.hasListener( widget );
        break;
      case SWT.KeyDown:
      case SWT.KeyUp:
        result = KeyEvent.hasListener( widget );
        break;
      case SWT.Traverse:
        result = TraverseEvent.hasListener( widget );
        break;
      case SWT.Help:
        result = HelpEvent.hasListener( widget );
        break;
      case SWT.DragDetect:
        result = DragDetectEvent.hasListener( widget );
        break;
      case SWT.Arm:
        result = ArmEvent.hasListener( widget );
        break;
    }
    return result;
  }

  public static void notifyListeners( int eventType, Event event ) {
    TypedEvent typedEvent = null;
    switch( eventType ) {
      case SWT.Move:
      case SWT.Resize:
        typedEvent = new ControlEvent( event );
      break;
      case SWT.Dispose:
        typedEvent = new DisposeEvent( event );
      break;
      case SWT.Selection:
      case SWT.DefaultSelection:
        typedEvent = new SelectionEvent( event );
      break;
      case SWT.FocusIn:
      case SWT.FocusOut:
        typedEvent = new FocusEvent( event );
      break;
      case SWT.Expand:
      case SWT.Collapse:
        if( event.widget instanceof ExpandBar ) {
          typedEvent = new ExpandEvent( event );
        } else {
          typedEvent = new TreeEvent( event );
        }
      break;
      case SWT.Activate:
      case SWT.Deactivate:
        if( event.widget instanceof Shell ) {
          typedEvent = new ShellEvent( event );
        } else {
          typedEvent = new ActivateEvent( event );
        }
      break;
      case SWT.Close:
        typedEvent = new ShellEvent( event );
      break;
      case SWT.Hide:
      case SWT.Show:
        if( event.widget instanceof Control ) {
          typedEvent = new ShowEvent( event );
        } else {
          typedEvent = new MenuEvent( event );
        }
      break;
      case SWT.MenuDetect:
        typedEvent = new MenuDetectEvent( event );
      break;
      case SWT.Modify:
        typedEvent = new ModifyEvent( event );
      break;
      case SWT.Verify:
        typedEvent = new VerifyEvent( event );
      break;
      case SWT.SetData:
        typedEvent = new SetDataEvent( event );
      break;
      case SWT.MouseDown:
      case SWT.MouseUp:
      case SWT.MouseDoubleClick:
        typedEvent = new MouseEvent( event );
      break;
      case SWT.KeyDown:
      case SWT.KeyUp:
        typedEvent = new KeyEvent( event );
      break;
      case SWT.Traverse:
        typedEvent = new TraverseEvent( event );
      break;
      case SWT.Help:
        typedEvent = new HelpEvent( event );
      break;
      case SWT.DragDetect:
        typedEvent = new DragDetectEvent( event );
      break;
      case SWT.Arm:
        typedEvent = new ArmEvent( event );
      break;
    }
    if( typedEvent != null ) {
      typedEvent.processEvent();
    }
  }

  public boolean isEmpty() {
    return listeners.isEmpty();
  }

  private boolean hasListener(Widget widget, int eventType) {
    List< Entry > list = listeners.get( widget );
    if(list != null ) {
      for( int i = 0; i < list.size(); i++ ) {
        if(list.get( i ).eventType == eventType) {
          return true;
        }
      }
    }
    return false;
  }

  private boolean hasOtherListeners(Widget widget, int eventType, Listener listener) {
    List< Entry > list = listeners.get( widget );
    if(list != null ) {
      for( int i = 0; i < list.size(); i++ ) {
        Entry entry = list.get( i );
        if(entry.eventType == eventType
            && !entry.listener.equals( listener )) {
          return true;
        }
      }
    }
    return false;
  }

  //////////////////
  // helping methods

  private void dispatchEvent( Event event ) {
    // [rh] protect against manipulating the event type in listener code
    int eventType = event.type;
    Entry[] entries = getEntries();
    for( int i = 0; i < entries.length; i++ ) {
      if( entries[ i ].eventType == eventType ) {
        entries[ i ].listener.handleEvent( event );
      }
    }
  }

  private Entry[] getEntries() {
    ArrayList< Entry > listenersList = new ArrayList< Entry >();
    Iterator iterator = listeners.keySet().iterator();
    while(iterator.hasNext()) {
      List< Entry > list = listeners.get(iterator.next());
      listenersList.addAll( list );
    }
    Entry[] result = new Entry[ listenersList.size() ];
    listenersList.toArray( result );
    return result;
  }

  private static Event createEvent( int eventType, Object source ) {
    Widget widget = ( Widget )source;
    Event result = new Event();
    result.type = eventType;
    result.widget = widget;
    result.display = widget.getDisplay();
    return result;
  }

  private static void copyFields( TypedEvent from, Event to ) {
    to.display = from.display;
    to.widget = from.widget;
    to.data = from.data;
  }

  private static void copyFields( SelectionEvent from, Event to ) {
    copyFields( ( TypedEvent )from, to );
    to.detail = from.detail;
    to.doit = from.doit;
    to.x = from.x;
    to.y = from.y;
    to.width = from.width;
    to.height = from.height;
    to.item = from.item;
    to.text = from.text;
    to.stateMask = from.stateMask;
  }

  private static void copyFields( TreeEvent from, Event to ) {
    copyFields( ( TypedEvent )from, to );
    to.detail = from.detail;
    to.doit = from.doit;
    to.x = from.x;
    to.y = from.y;
    to.height = from.height;
    to.width = from.width;
    to.item = from.item;
    to.text = from.text;
  }

  private static void copyFields( ExpandEvent from, Event to ) {
    copyFields( ( TypedEvent )from, to );
    to.detail = from.detail;
    to.doit = from.doit;
    to.x = from.x;
    to.y = from.y;
    to.height = from.height;
    to.width = from.width;
    to.item = from.item;
    to.text = from.text;
  }

  private static void copyFields( VerifyEvent from, Event to ) {
    copyFields( ( TypedEvent )from, to );
    to.start = from.start;
    to.end = from.end;
    to.doit = from.doit;
    to.text = from.text;
  }

  private static void copyFields( SetDataEvent from, Event to ) {
    copyFields( ( TypedEvent )from, to );
    to.index = from.index;
    to.item = from.item;
  }

  private static void copyFields( MouseEvent from, Event to ) {
    copyFields( ( TypedEvent )from, to );
    to.button = from.button;
    to.time = from.time;
    to.x = from.x;
    to.y = from.y;
    to.stateMask = from.stateMask;
  }

  private static void copyFields( ShellEvent from, Event to ) {
    copyFields( ( TypedEvent )from, to );
    to.doit = from.doit;
  }

  private static void copyFields( KeyEvent from, Event to ) {
    copyFields( ( TypedEvent )from, to );
    to.character = from.character;
    to.keyCode = from.keyCode;
    to.stateMask = from.stateMask;
    to.doit = from.doit;
  }

  private static void copyFields( TraverseEvent from, Event to ) {
    copyFields( ( TypedEvent )from, to );
    to.character = from.character;
    to.keyCode = from.keyCode;
    to.stateMask = from.stateMask;
    to.detail = from.detail;
    to.doit = from.doit;
  }

  private static void copyFields( MenuDetectEvent from, Event to ) {
    copyFields( ( TypedEvent )from, to );
    to.x = from.x;
    to.y = from.y;
    to.doit = from.doit;
  }
}
