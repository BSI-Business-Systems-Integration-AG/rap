/*******************************************************************************
 *  Copyright: 2004, 2010 1&1 Internet AG, Germany, http://www.1und1.de,
 *                        and EclipseSource
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *    1&1 Internet AG and others - original API and implementation
 *    EclipseSource - adaptation for the Eclipse Rich Ajax Platform
 ******************************************************************************/

/**
 * The qooxdoo core event object. Each event object for qx.core.Targets should extend this class.
 */
qx.Class.define("qx.event.type.Event",
{
  extend : qx.core.Object,




  /*
  *****************************************************************************
     CONSTRUCTOR
  *****************************************************************************
  */

  construct : function(vType)
  {
    this.base(arguments);

    this.setType(vType);
  },




  /*
  *****************************************************************************
     PROPERTIES
  *****************************************************************************
  */

  properties :
  {
    type :
    {
      _fast       : true,
      setOnlyOnce : true
    },

    originalTarget :
    {
      _fast       : true,
      setOnlyOnce : true
    },

    target :
    {
      _fast       : true,
      setOnlyOnce : true
    },

    relatedTarget :
    {
      _fast       : true,
      setOnlyOnce : true
    },

    currentTarget : { _fast : true },

    bubbles :
    {
      _fast        : true,
      defaultValue : false,
      noCompute    : true
    },

    propagationStopped :
    {
      _fast        : true,
      defaultValue : true,
      noCompute    : true
    },

    defaultPrevented :
    {
      _fast        : true,
      defaultValue : false,
      noCompute    : true
    }
  },




  /*
  *****************************************************************************
     MEMBERS
  *****************************************************************************
  */

  members :
  {
    _autoDispose : false,


    /*
    ---------------------------------------------------------------------------
      SHORTCUTS
    ---------------------------------------------------------------------------
    */

    /**
     * TODOC
     *
     * @type member
     * @return {void}
     */
    preventDefault : function() {
      this.setDefaultPrevented(true);
    },


    /**
     * TODOC
     *
     * @type member
     * @return {void}
     */
    stopPropagation : function() {
      this.setPropagationStopped(true);
    }
  },




  /*
  *****************************************************************************
     DESTRUCTOR
  *****************************************************************************
  */

  destruct : function() {
    this._disposeFields("_valueOriginalTarget", "_valueTarget", "_valueRelatedTarget", "_valueCurrentTarget");
  }
});
