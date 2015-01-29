/*
 * Copyright 2014, Manuuel Carrasco Moñino.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.gwt.query.client.plugin;

import com.google.gwt.core.client.Duration;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.query.client.Console;
import com.google.gwt.query.client.Function;
import com.google.gwt.query.client.GQ;
import com.google.gwt.query.client.GQuery;
import com.google.gwt.query.client.Properties;
import com.google.gwt.query.client.impl.ConsoleBrowser;
import com.google.gwt.query.client.js.JsUtils;
import com.google.gwt.query.client.plugin.GestureObjects.$;
import com.google.gwt.query.client.plugin.GestureObjects.DevicePosition;
import com.google.gwt.query.client.plugin.GestureObjects.DeviceWindow;
import com.google.gwt.query.client.plugin.GestureObjects.JGestures;
import com.google.gwt.query.client.plugin.GestureObjects.JGestures.Defaults;
import com.google.gwt.query.client.plugin.GestureObjects.JGestures.Defaults.ThresholdShake;
import com.google.gwt.query.client.plugin.GestureObjects.Options;
import com.google.gwt.query.client.plugin.GestureObjects.Options.Delta;
import com.google.gwt.query.client.plugin.GestureObjects.Options.Direction;
import com.google.gwt.query.client.plugin.GestureObjects.Options.OptArgs;
import com.google.gwt.query.client.plugin.GestureObjects.Options.OptArgs.Move;
import com.google.gwt.query.client.plugin.GestureObjects.Shake;
import com.google.gwt.query.client.plugins.Plugin;
import com.google.gwt.query.client.plugins.events.EventsListener;
import com.google.gwt.query.client.plugins.events.SpecialEvent;
import com.google.gwt.query.client.plugins.events.SpecialEvent.DefaultSpecialEvent;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;

/**
 * Gesture plugin for touch events.
 *
 * It's a gwt port of the jGesture plugin (https://jgestures.codeplex.com/)
 *
 * @author Manolo Carrasco
 */
public class Gesture extends GQuery {

  private static final DeviceWindow _window;
  public static final Defaults defaults;
  public static final boolean hasGestures;

  public static final Class<Gesture> Gesture = registerPlugin(Gesture.class, new Plugin<Gesture>() {
    public Gesture init(GQuery gq) {
      return new Gesture(gq);
    }
  });

  public Gesture(GQuery gq) {
    super(gq);
  }

  /**
   * Add Gesture special events to GQuery. Once run this you don't have
   * to call GQuery.as(Gesture.Gesture) anymore.
   */
  public static void load() {
  }

  static {
    $.jGestures = GQ.create(JGestures.class);
    // $.jGestures.defaults = {};

    $.jGestures.defaults().thresholdShake()
      .requiredShakes(3) //5
      .freezeShakes(10); //50
    $.jGestures.defaults().thresholdShake().frontback()
      .sensitivity(10);
    $.jGestures.defaults().thresholdShake().leftright()
      .sensitivity(10);
    $.jGestures.defaults().thresholdShake().updown()
      .sensitivity(8); // 10

    $.jGestures.defaults().thresholdPinchopen(0.05);
    $.jGestures.defaults().thresholdPinchmove(0.05);
    $.jGestures.defaults().thresholdPinch(0.05);
    $.jGestures.defaults().thresholdPinchclose(0.05);
    $.jGestures.defaults().thresholdRotatecw(5); //deg
    $.jGestures.defaults().thresholdRotateccw(5); // deg
    // a tap becomes a swipe if x/y values changes are above this threshold
    $.jGestures.defaults().thresholdMove(20);
    $.jGestures.defaults().thresholdSwipe(60);
    // get capable user agents
    $.hasGestures = Window.Navigator.getUserAgent().matches(".*(iPad|iPhone|iPod|Mobile Safari).*");
    $.jGestures.data().hasGestures($.hasGestures);
    $.jGestures.hasGestures($.hasGestures);
    $.jGestures.events()
        .touchstart("jGestures.touchstart")
        .touchendStart("jGestures.touchend;start")
        .touchendProcessed("jGestures.touchend;processed")
        .gesturestart("jGestures.gesturestart")
        .gestureendStart("jGestures.gestureend;start")
        .gestureendProcessed("jGestures.gestureend;processed");

    // "first domevent necessary"_"touch event+counter" : "exposed as"
    // event: orientationchange
    $.special.put("orientationchange_orientationchange01", "orientationchange");
    // event: gestures
    $.special.put("gestureend_pinchopen01", "pinchopen");
    $.special.put("gestureend_pinchclose01", "pinchclose");
    $.special.put("gestureend_rotatecw01", "rotatecw");
    $.special.put("gestureend_rotateccw01", "rotateccw");
    // move events
    $.special.put("gesturechange_pinch01", "pinch");
    $.special.put("gesturechange_rotate01", "rotate");
    $.special.put("touchstart_mrotate01", "mrotate");
    $.special.put("touchstart_swipe13", "swipemove");
    // event: touches
    $.special.put("touchstart_swipe01", "swipeone");
    $.special.put("touchstart_swipe02", "swipetwo");
    $.special.put("touchstart_swipe03", "swipethree");
    $.special.put("touchstart_swipe04", "swipefour");
    $.special.put("touchstart_swipe05", "swipeup");
    $.special.put("touchstart_swipe06", "swiperightup");
    $.special.put("touchstart_swipe07", "swiperight");
    $.special.put("touchstart_swipe08", "swiperightdown");
    $.special.put("touchstart_swipe09", "swipedown");
    $.special.put("touchstart_swipe10", "swipeleftdown");
    $.special.put("touchstart_swipe11", "swipeleft");
    $.special.put("touchstart_swipe12", "swipeleftup");
    $.special.put("touchstart_tap01", "tapone");
    $.special.put("touchstart_tap02", "taptwo");
    $.special.put("touchstart_tap03", "tapthree");
    $.special.put("touchstart_tap04", "tapfour");

    $.special.put("devicemotion_shake01", "shake");
    $.special.put("devicemotion_shake02", "shakefrontback");
    $.special.put("devicemotion_shake03", "shakeleftright");
    $.special.put("devicemotion_shake04", "shakeupdown");

    for (Entry<String, String> e : $.special.entrySet()) {
      EventsListener.special.put(e.getValue(), new GestureSpecialEvent(e.getKey(), e.getValue()));
    }

    _window = GQ.create(DeviceWindow.class).load(window);;
    defaults = $.jGestures.defaults();
    hasGestures = $.jGestures.hasGestures();

    EventsListener.special.put("swipe", new DefaultSpecialEvent("swipe", "swipeone"));
    EventsListener.special.put("tap", new DefaultSpecialEvent("tap", "tapone"));
  }

  public static class GestureSpecialEvent implements SpecialEvent {
    final String sInternal_, sPublicFN_;

    public GestureSpecialEvent(String internalEventName, String publicEventName) {
      sInternal_ = internalEventName;
      sPublicFN_ = publicEventName;
    }

    @Override
    public boolean setup(Element elm) {
      // split the arguments to necessary controll arguements
      String[] _aSplit = sInternal_.split("_");
      String _sDOMEvent = _aSplit[0]; //
      // get the associated gesture event and strip the counter: necessary for distinguisihng similliar events such as tapone-tapfour
      String _sGestureEvent = _aSplit[1].substring(0, _aSplit[1].length() -2);
      GQuery _$element = $(elm);
      Properties _oDatajQueryGestures;
      Properties oObj;
      // bind the event handler on the first $.bind() for a gestureend-event, set marker
      if (_$element.data("ojQueryGestures") == null || _$element.<Properties>data("ojQueryGestures").get(_sDOMEvent) == null)  {
        // setup pseudo event
        _oDatajQueryGestures = _$element.data("ojQueryGestures") != null ? _$element.<Properties>data("ojQueryGestures") : $$();
        oObj = $$();
        // marker for:  domEvent being set on this element
        // e.g.: $.data.oGestureInternals["touchstart"] = true;
        // since they're grouped, i'm just marking the first one being added
        oObj.set(_sDOMEvent, true);

        $.extend(true,_oDatajQueryGestures,oObj);
        _$element.data("ojQueryGestures" ,_oDatajQueryGestures);
        // add gesture events
        if($.jGestures.hasGestures()) {
          switch(_sGestureEvent) {
            // event: orientationchange
            case "orientationchange":
              if (elm == window)
                _$element.on("deviceorientation", _onOrientationchange);
            break;
            // event:
            // - shake
            // - tilt
            case "shake":
            case "shakefrontback":
            case "shakeleftright":
            case "shakeupdown":
            case "tilt":
              //$.hasGyroscope = true //!window.DeviceOrientationEvent;
              //_$element.get(0).addEventListener("devicemotion", _onDevicemotion, false);
              //_$element.get(0).addEventListener("deviceorientation", _onDeviceorientation, false);
              if (elm == window)
                 _$element.on("devicemotion", _onDevicemotion);
            break;

            // event:
            // - touchstart
            // - touchmove
            // - touchend
            case "tap":
            case "swipe":
            case "swipeup":
            case "swiperightup":
            case "swiperight":
            case "swiperightdown":
            case "swipedown":
            case "swipeleftdown":
            case "swipeleft":
            case "mrotate":
              _$element.on("touchstart", _onTouchstart);
            break;

            // event: gestureend
            case "pinchopen":
            case "pinchclose" :
            case "rotatecw" :
            case "rotateccw" :
              _$element.on("gesturestart", _onGesturestart);
              _$element.on("gestureend", _onGestureend);
            break;

            // event: gesturechange
            case "pinch":
            case "rotate":
              _$element.on("gesturestart", _onGesturestart);
              _$element.on("gesturechange", _onGesturechange);
            break;
          }
        }
        // create substitute for gesture events
        else {
          switch(_sGestureEvent) {
            // event substitutes:
            // - touchstart: mousedown
            // - touchmove: none
            // - touchend: mouseup
            case "tap":
            case "swipe":
              _$element.bind("contextmenu", _onTouchstart);
              _$element.bind("mousedown", _onTouchstart);
            break;

            // no substitution
            case "orientationchange":
            case "pinchopen":
            case "pinchclose" :
            case "rotatecw" :
            case "rotateccw" :
            case "pinch":
            case "rotate":
            case "shake":
            case "tilt":

            break;
          }
        }
      }
      return false;
    }

    @Override
    public void add(Element elm, String eventType, String nameSpace, Object data, Function f) {
      // add pseudo event: properties on $.data
      GQuery _$element = $(elm);
      Properties _oDatajQueryGestures = _$element.data("ojQueryGestures");
      _oDatajQueryGestures.set(eventType, $$().set("originalType", eventType));
    }

    @Override
    public void remove(Element elm, String eventType, String nameSpace, Function f) {
      // remove pseudo event: properties on $.data
      GQuery _$element = $(elm);
      Properties _oDatajQueryGestures = _$element.data("ojQueryGestures");
      _oDatajQueryGestures.remove(eventType);
      _$element.data("ojQueryGestures", _oDatajQueryGestures);
      _$element.off("devicemotion", _onDevicemotion);
      _$element.off("gesturestart", _onGesturestart);
      _$element.off("gestureend", _onGestureend);
      _$element.off("gesturestart", _onGesturestart);
      _$element.off("gesturechange", _onGesturechange);
      _$element.off("contextmenu", _onTouchstart);
      _$element.off("mousedown", _onTouchstart);
    }


    @Override
    public boolean tearDown(Element elm) {
      // split the arguments to necessary controll arguements
      String[] _aSplit = sInternal_.split("_");
      String _sDOMEvent = _aSplit[0]; //
      // get the associated gesture event and strip the counter: necessary for distinguisihng similliar events such as tapone-tapfour
      String _sGestureEvent = _aSplit[1].substring(_aSplit[1].length() -2);
      GQuery _$element = $(elm);
      Properties _oDatajQueryGestures;
      Properties oObj;
      // bind the event handler on the first $.bind() for a gestureend-event, set marker
      if (_$element.data("ojQueryGestures") == null || _$element.<Properties>data("ojQueryGestures").get(_sDOMEvent) == null)  {
        // setup pseudo event
        _oDatajQueryGestures = _$element.data("ojQueryGestures") != null ? _$element.<Properties>data("ojQueryGestures") : $$();
        oObj = $$();
        // remove marker for:  domEvent being set on this element
        oObj.remove(_sDOMEvent);

        $.extend(true,_oDatajQueryGestures,oObj);

        _$element.data("ojQueryGestures" ,_oDatajQueryGestures);

        // remove gesture events
        if($.jGestures.hasGestures()) {
          switch(_sGestureEvent) {
            // event: orientationchange
            case "orientationchange":
              _$element.off("deviceorientation", _onOrientationchange);
            break;
            // event:
            // - shake
            // - tilt
            case "shake":
            case "shakefrontback":
            case "shakeleftright":
            case "shakeupdown":
            case "tilt":
              //$.hasGyroscope = true //!window.DeviceOrientationEvent;
              //_$element.get(0).addEventListener("devicemotion", _onDevicemotion, false);
              //_$element.get(0).addEventListener("deviceorientation", _onDeviceorientation, false);
              _$element.off("devicemotion", _onDevicemotion);
            break;

            // event:
            // - touchstart
            // - touchmove
            // - touchend
            case "tap":
            case "swipe":
            case "swipeup":
            case "swiperightup":
            case "swiperight":
            case "swiperightdown":
            case "swipedown":
            case "swipeleftdown":
            case "swipeleft":
            case "mrotate":
              _$element.off("touchstart", _onTouchstart);
              _$element.off("touchmove", _onTouchmove);
              _$element.off("touchend", _onTouchend);
            break;

            // event: gestureend
            case "pinchopen":
            case "pinchclose" :
            case "rotatecw" :
            case "rotateccw" :
              _$element.off("gesturestart", _onGesturestart);
              _$element.off("gestureend", _onGestureend);
            break;

            // event: gesturechange
            case "pinch":
            case "rotate":
              _$element.off("gesturestart", _onGesturestart);
              _$element.off("gesturechange", _onGesturechange);
            break;
          }
        }
        // create substitute for gesture events
        else {
          switch(_sGestureEvent) {
            // event substitutes:
            // - touchstart: mousedown
            // - touchmove: none
            // - touchend: mouseup
            case "tap":
            case "swipe":
            case "mrotate":

               _$element.unbind("mousedown", _onTouchstart);
               _$element.unbind("mousemove", _onTouchstart);
               _$element.unbind("mouseup", _onTouchstart);
               _$element.unbind("contextmenu", _onTouchstart);
            break;

            // no substitution
            case "orientationchange":
            case "pinchopen":
            case "pinchclose" :
            case "rotatecw" :
            case "rotateccw" :
            case "pinch":
            case "rotate":
            case "shake":
            case "tilt":

            break;
          }
        }
      }
      return false;
    }

    @Override
    public boolean hasHandlers(Element e) {
      // TODO Auto-generated method stub
      return false;
    }
  }

  static Options _createOptions(OptArgs oOptions_) {
    Options rOptions = GQ.create(Options.class);
    long _iNow = new Date().getTime();
    List<Direction> _oDirection = new ArrayList<Direction>(rOptions.direction());
    List<Delta> _oDelta = new ArrayList<Delta>(rOptions.delta());

    // calculate touch differences
    if (oOptions_.touches() > 0) {
      // store delta values
      _oDelta.add(GQ.create(Delta.class)
          .lastX(oOptions_.deltaX())
          .lastY(oOptions_.deltaY())
          .moved(0)
          .startX(oOptions_.screenX()- oOptions_.startMove().screenX())
          .startY(oOptions_.screenY()- oOptions_.startMove().screenY())
          );

      _oDirection.add(GQ.create(Direction.class)
          .vector(oOptions_.vector())
          .orientiation(_window.orientation())
          .lastX(((_oDelta.get(0).lastX() > 0) ? +1 : ( (_oDelta.get(0).lastX() < 0) ? -1 : 0 ) ))
          .lastY(((_oDelta.get(0).lastY() > 0) ? +1 : ( (_oDelta.get(0).lastY() < 0) ? -1 : 0 ) ))
          .startX(((_oDelta.get(0).startX() > 0) ? +1 : ( (_oDelta.get(0).startX() < 0) ? -1 : 0 )))
          .startY(((_oDelta.get(0).startY() > 0) ? +1 : ( (_oDelta.get(0).startY() < 0) ? -1 : 0 )))
       );

      // calculate distance traveled using the pythagorean theorem
      _oDelta.get(0).moved(Math.sqrt(Math.pow(Math.abs(_oDelta.get(0).startX()), 2) + Math.pow(Math.abs(_oDelta.get(0).startY()), 2)));

      // determine direction name
      double x = _oDelta.get(0).lastX(),
             y = _oDelta.get(0).lastY();
      String direction = null;

      if (x == 0 || y/x <= -2 || y/x >= 2) {
        if (y > 0)
          direction = "down";
        else if (y < 0)
          direction = "up";
      }
      else if (y == 0 || x/y <= -2 || x/y >= 2) {
        if (x > 0)
          direction = "right";
        else if (x < 0)
          direction = "left";
      }
      else if (0.5 < y/x && y/x < 2) {
        if (y > 0)
          direction = "rightdown";
        else if (y < 0)
          direction = "leftup";
      }
      else if (-2 < y/x && y/x < -0.5) {
        if (y > 0)
          direction = "leftdown";
        else if (y < 0)
          direction = "rightup";
      }

      rOptions.directionName(direction);
    }

    return rOptions
        .type(oOptions_.type())
//        .originalEvent(oOptions_.event())
        .delta(_oDelta)
        .direction(_oDirection)
        .screenX(oOptions_.screenX())
        .screenY(oOptions_.screenY())
        .duration(oOptions_.duration() > 0 ? oOptions_.duration() : oOptions_.startMove().timestamp() > 0 ? _iNow - oOptions_.timestamp() : 0)
        .rotation(oOptions_.rotation())
        .scale(oOptions_.scale())
        .description(oOptions_.description() != null ? oOptions_.description() : (
            oOptions_.type() + ":" + oOptions_.touches() + ":" +
            (_oDelta.get(0).lastX() != 0 ? (_oDelta.get(0).lastX() > 0 ? "right" : "left") : "steady") + ":" +
            (_oDelta.get(0).lastY() != 0 ? (_oDelta.get(0).lastY() > 0 ? "down" : "up") : "steady")
         ));
  }

  static Function _onOrientationchange = new Function() {
    private int _lastOrientiation = _window.orientation();
    public boolean f(Event event_) {
      if (_window.orientation() != _lastOrientiation) {
        _lastOrientiation = _window.orientation();
        // window.orientation: -90,0,90,180
        String[] _aDict = new String[] {"landscape:clockwise:","portrait:default:","landscape:counterclockwise:","portrait:upsidedown:"};
        $(window).trigger("orientationchange", _createOptions(GQ.create(OptArgs.class)
            .direction(_window.orientation())
            .description("orientiationchange:" +  _aDict[(_window.orientation()/90) + 1] + _window.orientation())));
      }
      return false;
    };
  };

  static Function _onDevicemotion = new Function() {
    public boolean f(Event e) {
      TouchEvent event_ = e.cast();

//      String _sType;
      GQuery _$element = $(event_.getCurrentEventTarget());
      //var _bHasGyroscope = $.hasGyroscope;

      // skip custom notification: devicemotion is triggered every 0.05s regardlesse of any gesture

      // get options
      Properties _oDatajQueryGestures = _$element.data("ojQueryGestures");

      ThresholdShake _oThreshold = $.jGestures.defaults().thresholdShake();

      // get last position or set initital values
      DevicePosition _oLastDevicePosition = _oDatajQueryGestures.get("oDeviceMotionLastDevicePosition");
      if (_oLastDevicePosition == null) {
        _oLastDevicePosition = GQ.create(DevicePosition.class);
      }

      // cache current values
      DevicePosition _oCurrentDevicePosition = GQ.create(DevicePosition.class);
      _oCurrentDevicePosition.accelerationIncludingGravity()
          .x(event_.accelerationIncludingGravity().x())
          .y(event_.accelerationIncludingGravity().y())
          .z(event_.accelerationIncludingGravity().z());
      _oCurrentDevicePosition.shake()
          .eventCount(_oLastDevicePosition.shake().eventCount())
          .intervalsPassed(_oLastDevicePosition.shake().intervalsPassed())
          .intervalsFreeze(_oLastDevicePosition.shake().intervalsFreeze());
      _oCurrentDevicePosition.shakeleftright()
          .eventCount(_oLastDevicePosition.shakeleftright().eventCount())
          .intervalsPassed(_oLastDevicePosition.shakeleftright().intervalsPassed())
          .intervalsFreeze(_oLastDevicePosition.shakeleftright().intervalsFreeze());
      _oCurrentDevicePosition.shakefrontback()
          .eventCount(_oLastDevicePosition.shakefrontback().eventCount())
          .intervalsPassed(_oLastDevicePosition.shakefrontback().intervalsPassed())
          .intervalsFreeze(_oLastDevicePosition.shakefrontback().intervalsFreeze());
      _oCurrentDevicePosition.shakeupdown()
          .eventCount(_oLastDevicePosition.shakeupdown().eventCount())
          .intervalsPassed(_oLastDevicePosition.shakeupdown().intervalsPassed())
          .intervalsFreeze(_oLastDevicePosition.shakeupdown().intervalsFreeze());

      // options
      JsArrayString _aType;
      Properties _oObj;


      // trigger events for all bound pseudo events on this element
      for (String _sType : _oDatajQueryGestures.keys()) {
        // get current pseudo event


        // trigger bound events on this element
        switch(_sType) {

          case "shake":
          case "shakeleftright":
          case "shakefrontback":
          case "shakeupdown":

            // options
            _aType = JsArrayString.createArray().cast();

            _aType.push(_sType);

            // freeze shake - prevent multiple shake events on one  shaking motion (user won't stop shaking immediately)
            Shake _oCurrentDevicePosition_sType = GQ.create(Shake.class).load(_oCurrentDevicePosition.get(_sType));
            _oCurrentDevicePosition_sType.intervalsFreeze(1 + _oCurrentDevicePosition_sType.intervalsFreeze());
            if (_oCurrentDevicePosition_sType.intervalsFreeze() > _oThreshold.freezeShakes() && _oCurrentDevicePosition_sType.intervalsFreeze() < (2*_oThreshold.freezeShakes()) ) { break; }

            // set control values
            _oCurrentDevicePosition_sType.intervalsFreeze(0);
            _oCurrentDevicePosition_sType.intervalsPassed(1 + _oCurrentDevicePosition_sType.intervalsPassed());

            // check for shaking motions: massive acceleration changes in every direction
            if ( ( _sType == "shake" ||_sType == "shakeleftright" ) && ( _oCurrentDevicePosition.accelerationIncludingGravity().x() > _oThreshold.leftright().sensitivity()  || _oCurrentDevicePosition.accelerationIncludingGravity().x() < (-1* _oThreshold.leftright().sensitivity()) ) ) {
              _aType.push("leftright");
              _aType.push("x-axis");
            }

            if ( ( _sType == "shake" ||_sType == "shakefrontback" ) && (_oCurrentDevicePosition.accelerationIncludingGravity().y() > _oThreshold.frontback().sensitivity()  || _oCurrentDevicePosition.accelerationIncludingGravity().y() < (-1 * _oThreshold.frontback().sensitivity()) ) ) {
              _aType.push("frontback");
              _aType.push("y-axis");
            }

            if ( ( _sType == "shake" ||_sType == "shakeupdown" ) && ( _oCurrentDevicePosition.accelerationIncludingGravity().z()-9.81 > _oThreshold.updown().sensitivity()  || _oCurrentDevicePosition.accelerationIncludingGravity().z()-9.81 < (-1 * _oThreshold.updown().sensitivity()) ) ) {
              _aType.push("updown");
              _aType.push("z-axis");
            }

            // at least one successful shaking event
            if (_aType.length() > 1) {
              // minimum number of shaking motions during  the defined "time" (messured by events - device event interval: 0.05s)
              _oCurrentDevicePosition_sType.eventCount(1 + _oCurrentDevicePosition_sType.eventCount());
              if (_oCurrentDevicePosition_sType.eventCount() == _oThreshold.requiredShakes() && (_oCurrentDevicePosition_sType.intervalsPassed()) < _oThreshold.freezeShakes() ) {
                // send event
                _$element.trigger(_sType, _createOptions( GQ.create(OptArgs.class).type(_sType).description(join(_aType,":")).event(event_).duration(_oCurrentDevicePosition_sType.intervalsPassed()*5)));

                // reset
                _oCurrentDevicePosition_sType.eventCount(0);
                _oCurrentDevicePosition_sType.intervalsPassed(0);
                // freeze shake
                _oCurrentDevicePosition_sType.intervalsFreeze(_oThreshold.freezeShakes()+1);
              }
              // too slow, reset
              else if (_oCurrentDevicePosition_sType.eventCount() == _oThreshold.requiredShakes() && _oCurrentDevicePosition_sType.intervalsPassed() > _oThreshold.freezeShakes() ) {
                _oCurrentDevicePosition_sType.eventCount(0);
                _oCurrentDevicePosition_sType.intervalsPassed(0);
              }
            }
          break;

        }

        // refresh pseudo events
        _oObj = $$();
        _oObj.set("oDeviceMotionLastDevicePosition", _oCurrentDevicePosition);
        _$element.data("ojQueryGestures",$.extend(true,_oDatajQueryGestures,_oObj));

      }
      return false;
    }
  };

  static List<GQuery> started = new ArrayList<GQuery>();

  static Function _onTouchstart = new Function() {
    public boolean f(Event e) {
      TouchEvent event_ = e.cast();

      // ignore bubbled handlers
      // if ( event_.currentTarget !== event_.target ) { return; }

      GQuery _$element = $(event_.getCurrentEventTarget());
      started.add(_$element);
      // var _$element = jQuery(event_.target);

      // set the necessary touch events
      if($.hasGestures) {
        _$element.bind("touchmove", _onTouchmove);
        _$element.bind("touchend", _onTouchend);
      }
      // event substitution
      else {
        _$element.bind("mousemove", _onTouchmove);
        _$element.bind("mouseup", _onTouchend);
        _$element.bind("mouseleave", _onTouchend);
      }

      // get stored pseudo event
      Properties _oDatajQueryGestures = _$element.data("ojQueryGestures");

      // var _oEventData = _oDatajQueryGestures[_sType];
      TouchEvent _eventBase = (event_.touches().length() > 0) ? event_.touches().get(0) : event_;
      TouchEvent _eventSec = (event_.touches().length() > 1) ? event_.touches().get(1) : null;
      // store current values for calculating relative values (changes between touchmoveevents)
      Properties _oObj = $$();

      int _iLastFingers = _oDatajQueryGestures.getInt("fingers");
      int _iFingers = Math.max(event_.touches().length(), 1);
      if (!$.hasGestures) {
        _iFingers = _iFingers > 1 ? _iFingers : (_iLastFingers + _iFingers);
      }
      _oObj.set("oLastSwipemove", GQ.create(Move.class).identifier(_eventBase.identifier()).screenX(_eventBase.screenX()).screenY(_eventBase.screenY()).timestamp(Duration.currentTimeMillis()).getDataImpl());
      _oObj.set("oStartTouch", GQ.create(Move.class).identifier(_eventBase.identifier()).screenX(_eventBase.screenX()).screenY(_eventBase.screenY()).timestamp(Duration.currentTimeMillis()).getDataImpl());
      _oObj.set("oMovement", GQ.create(Move.class).identifier(_eventBase.identifier()).screenX(_eventBase.screenX()).screenY(_eventBase.screenY()).top(_eventBase.screenY()).right(_eventBase.screenX()).bottom(_eventBase.screenY()).left(_eventBase.screenX()).getDataImpl());
      _oObj.set("oMovementSec", _eventSec == null ? null : GQ.create(Move.class).identifier(_eventSec.identifier()).screenX(_eventSec.screenX()).screenY(_eventSec.screenY()).top(_eventSec.screenY()).right(_eventSec.screenX()).bottom(_eventSec.screenY()).left(_eventSec.screenX()).getDataImpl());

      _oDatajQueryGestures.setNumber("fingers", _iFingers);
      _oDatajQueryGestures.setNumber("identifier", _eventBase.identifier());

      _$element.data("ojQueryGestures",$.extend(true,_oDatajQueryGestures,_oObj));

      // trigger custom notification
      _$element.trigger($.jGestures.events().touchstart(), event_, _iFingers);
      return (_iFingers > 1 && e.getType() == "contextmenu") ? false : true;
    }
  };



  static Function _onTouchmove = new Function() {
    public boolean f(Event e) {
      TouchEvent event_ = e.cast();
      TouchEvent _eventBase = getEventBase(event_);
      if (_eventBase == null) {
        return false;
      }

      GQuery _$element = $(event_.getCurrentEventTarget());

      // get stored pseudo event
      Properties _oDatajQueryGestures = _$element.data("ojQueryGestures");

      boolean _bHasTouches = event_.touches().length() > 0;
      int _iScreenX = (_bHasTouches) ? event_.changedTouches().get(0).screenX() : event_.screenX();
      int _iScreenY = (_bHasTouches) ? event_.changedTouches().get(0).screenY() : event_.screenY();

      //relative to the last event
      Move _oEventData = GQ.create(Move.class).load(_oDatajQueryGestures.getJavaScriptObject("oLastSwipemove"));
      int _iDeltaX = _iScreenX - _oEventData.screenX()   ;
      int _iDeltaY = _iScreenY - _oEventData.screenY();

      Move _oMovement = GQ.create(Move.class).load(_oDatajQueryGestures.getJavaScriptObject("oMovement"));
      _oMovement.left(Math.min(_oMovement.left(), _eventBase.screenX()));
      _oMovement.top(Math.min(_oMovement.top(), _eventBase.screenY()));
      _oMovement.right(Math.max(_oMovement.right(), _eventBase.screenX()));
      _oMovement.bottom(Math.max(_oMovement.bottom(), _eventBase.screenY()));
      if (_oMovement.direction() == 0) {
        if (_oMovement.screenX() - _oMovement.left() > 50) {
          _oMovement.direction(-1);
        } else if (_oMovement.right() - _oMovement.screenX()  > 50) {
          _oMovement.direction(1);
        }
      }
      if (_oMovement.direction() != 0 && _oMovement.rotation() == 0) {
        if (_oMovement.right() - _oMovement.left() > 150 && _oMovement.bottom() - _oMovement.top() > 150) {
          _oMovement.rotation(_oMovement.direction());
        }
      }

      Options _oDetails;

      // there's a swipemove set (not the first occurance), trigger event
      if (_oDatajQueryGestures.getJavaScriptObject("oLastSwipemove") != null) {
        // check
          _oDetails = _createOptions(GQ.create(OptArgs.class).type("swipemove").touches(_bHasTouches ? event_.touches().length() : 1).screenX(_iScreenX).screenY(_iScreenY).deltaX(_iDeltaX).deltaY(_iDeltaY).startMove(_oEventData).event(event_).timestamp(_oEventData.timestamp()));
          if (!trigger(_$element, event_, _oDetails.type(), _oDetails)) {
            return false;
          }
      }
      // store the new values
      Properties _oObj = $$();
//      TouchEvent _eventBase = (event_.touches().length() > 0) ? event_.touches().get(0) : event_;
      _oObj.set("oLastSwipemove", GQ.create(Move.class).screenX(_eventBase.screenX()).screenY(_eventBase.screenY()).timestamp(Duration.currentTimeMillis()).getDataImpl());
      _$element.data("ojQueryGestures",$.extend(true,_oDatajQueryGestures,_oObj));
      return true;
    }
  };

  private static TouchEvent getEventBase(TouchEvent e) {
    float identifier = $(e).<Properties>data("ojQueryGestures").getFloat("identifier");
    if (e.changedTouches().length() == 0) {
      return e;
    }
    for (int i = 0; i < e.changedTouches().length(); i++) {
      if (e.changedTouches().get(i).identifier() == identifier) {
        return e.changedTouches().get(i);
      }
    }
    return null;
  }

  static boolean moved(double x1, double y1, double x2, double y2, double threshold) {
    return Math.sqrt( Math.pow(x2-x1, 2) + Math.pow(y2-y1, 2)) >= threshold;
  }

  static Function _onTouchend = new Function() {
    public boolean f(Event e) {

      boolean ret = true;
      TouchEvent event_ = e.cast();
      TouchEvent _eventBase = getEventBase(event_);
      if (_eventBase == null) {
        return ret;
      }

      // ignore bubbled handlers
      // if ( event_.currentTarget !== event_.target ) { return; }

      GQuery _$element = $(event_.getCurrentEventTarget());

      boolean _bHasTouches = hasGestures;
      int _iTouches = (_bHasTouches) ? event_.changedTouches().length() : 1;
      int _iScreenX = (_bHasTouches) ? _eventBase.screenX() : event_.screenX();
      int _iScreenY = (_bHasTouches) ? _eventBase.screenY() : event_.screenY();

      // trigger custom notification
      _$element.trigger($.jGestures.events().touchendStart(),event_);

      // var _$element = jQuery(event_.target);
      // remove events
      if($.hasGestures) {
        for (GQuery g: started) {
          g.unbind("touchmove", _onTouchmove);
          g.unbind("touchend", _onTouchend);
        }
      }
      // event substitution
      else {
        for (GQuery g: started) {
          g.unbind("mousemove", _onTouchmove);
          g.unbind("mouseup mouseleave", _onTouchend);
        }
      }
      started.clear();

      // get all bound pseudo events
      Properties _oDatajQueryGestures = _$element.data("ojQueryGestures");

      _iTouches = _oDatajQueryGestures.getInt("fingers");
      if (_iTouches == 0) {
        return ret;
      }
      _oDatajQueryGestures.set("fingers", 0);
      Move oStartTouch = GQ.create(Move.class).load(_oDatajQueryGestures.getJavaScriptObject("oStartTouch"));

      // if the current change on the x/y position is above the defined threshold for moving an element set the moved flag
      // to distinguish between a moving gesture and a shaking finger trying to tap
      boolean _bHasMoved = moved(oStartTouch.screenX(), oStartTouch.screenY(), _iScreenX, _iScreenY, $.jGestures.defaults().thresholdMove());

      // if the current change on the x/y position is above the defined threshold for swiping set the moved flag
      // to indicate we're dealing with a swipe gesture
      boolean _bHasSwipeGesture = moved(oStartTouch.screenX(), oStartTouch.screenY(), _iScreenX, _iScreenY, $.jGestures.defaults().thresholdSwipe());

      Move _oMovement = GQ.create(Move.class).load(_oDatajQueryGestures.getJavaScriptObject("oMovement"));

      Move _oEventData ;

      // calculate distances in relation to the touchstart position not the last touchmove event!
      int _iDeltaX;
      int _iDeltaY;
      Options _oDetails;

      String[] _aDict = new String[]{"","one","two","three","four"};

      // swipe marker
      boolean _bIsSwipe;

      // get current pseudo event
      _oEventData = oStartTouch;

      // calculate distances in relation to the touchstart position not the last touchmove event!
      _iDeltaX = _iScreenX - _oEventData.screenX() ;
      _iDeltaY = _iScreenY - _oEventData.screenY();
      _oDetails = _createOptions(GQ.create(OptArgs.class).type("swipe").touches(_iTouches).screenY(_iScreenY).screenX(_iScreenX).deltaY(_iDeltaY).deltaX(_iDeltaX).startMove(_oEventData).event(event_).timestamp(_oEventData.timestamp()));

      // trigger events for all bound pseudo events on this element
      for (String _sType : _oDatajQueryGestures.keys()) {
        if (!$.special.containsValue(_sType) || JsUtils.isDefaultPrevented(event_)) continue;

        // swipe marker
        _bIsSwipe = false;
        // trigger bound events on this element
        switch(_sType) {
          case "mrotate":
            if (_oMovement.rotation() != 0) {
              _oDetails.type("mrotate").rotation(_oMovement.rotation()).description("rotation:" + (_oMovement.rotation() < 0 ? "counterclockwise" : "clockwise"));
              ret |= trigger(_$element, event_, _sType, _oDetails);
            }
            break;
          case "swipe":
          case "swipeone":
          case "swipeup":
          case "swiperightup":
          case "swiperight":
          case "swiperightdown":
          case "swipedown":
          case "swipeleftdown":
          case "swipeleft":
          case "swipeleftup":
            if (_iTouches > 1) {
              break;
            }
          case "swipetwo":
          case "swipethree":
          case "swipefour":
            if(_bHasTouches == false && _iTouches >= 1 && _bHasMoved == false){
              // trigger tap!
              break;
            }
            if (_bHasTouches==false || ( _iTouches >= 1  && _bHasMoved == true && _bHasSwipeGesture==true)) {
              _bIsSwipe = true;
              // trigger simple swipe
              _oDetails.type("swipe" + _aDict[_iTouches]);

              if (_oDetails.type() == _sType) {
                ret |= trigger(_$element, event_, _sType, _oDetails);
                break;
              }

              // trigger directional swipe
              if (_oDetails.directionName() != null) {
                _oDetails.type("swipe" + _oDetails.directionName());
                if (_oDetails.type() == _sType) {
                  ret |= trigger(_$element, event_, _sType, _oDetails);
                }
              }
            }
          break;

          case "tap":
          case "tapone":
          case "taptwo":
          case "tapthree":
          case "tapfour":

            if (( /* _bHasTouches && */ _bHasMoved != true && _bIsSwipe != true) && (_aDict[_iTouches] == _sType.substring(3)) ) {
              _oDetails.description("tap" + _aDict[_iTouches]);
              _oDetails.type("tap" + _aDict[_iTouches]);

              if (_oDetails.type() == _sType) {
                ret |= trigger(_$element, event_, _sType, _oDetails);
              }
            }
            break;

        }

        // refresh pseudo events
        Properties _oObj = $$();
//        _oObj[_sType] = false;
//        _oObj.hasTouchmoved = false;
        _$element.data("ojQueryGestures",$.extend(true,_oDatajQueryGestures,_oObj));
      }
      _$element.trigger($.jGestures.events().touchendProcessed(),event_);
      return ret;
    }
  };

  static Function _onGesturestart = new Function() {
    public boolean f(Event event_) {

      // ignore bubbled handlers
      // if ( event_.currentTarget !== event_.target ) { return; }

      GQuery _$element = $(event_.getCurrentEventTarget());
      // var _$element = jQuery(event_.target);

      // trigger custom notification
      _$element.trigger($.jGestures.events().gesturestart(),event_);


      // get stored pseudo event
      Properties _oDatajQueryGestures = _$element.data("ojQueryGestures");

      // var _oEventData = _oDatajQueryGestures[_sType];
      // store current values for calculating relative values (changes between touchmoveevents)
      Properties _oObj = $$();
      _oObj.set("oStartTouch", GQ.create(Move.class).timestamp(Duration.currentTimeMillis()).getDataImpl());
      _$element.data("ojQueryGestures",$.extend(true,_oDatajQueryGestures,_oObj));
      return true;
    }
  };

  static Function _onGesturechange = new Function() {
    public boolean f(Event e) {

      TouchEvent event_ = e.cast();

      // ignore bubbled handlers
      // if ( event_.currentTarget !== event_.target ) { return; }

      GQuery _$element = $(event_.getCurrentEventTarget());
      // var _$element = jQuery(event_.target);
      double _iDelta; int _iDirection; String _sDesc; Options _oDetails;
      // get all pseudo events
      Properties _oDatajQueryGestures = _$element.data("ojQueryGestures");
      Move oStartTouch = GQ.create(Move.class).load(_oDatajQueryGestures.getJavaScriptObject("oStartTouch"));

      // trigger events for all bound pseudo events on this element
      for (String _sType : _oDatajQueryGestures.keys()) {

        // trigger a specific bound event
        switch(_sType) {

          case "pinch":
            _iDelta = event_.scale();
            if ( ( ( _iDelta < 1 ) && (_iDelta % 1) < (1 - $.jGestures.defaults().thresholdPinchclose()) ) || ( ( _iDelta > 1 ) && (_iDelta % 1) > ($.jGestures.defaults().thresholdPinchopen()) ) ) {
              _iDirection = (_iDelta < 1 ) ? -1 : +1 ;
              _oDetails = _createOptions(GQ.create(OptArgs.class).type("pinch").scale(_iDelta).startMove(oStartTouch).event(event_).timestamp(oStartTouch.timestamp()).vector(_iDirection).description("pinch:" + _iDirection + ":" + (_iDelta < 1 ? "close" : "open")));
              _$element.trigger(_oDetails.type(), _oDetails);
            }
          break;

          case "rotate":
            _iDelta = event_.rotation();
            if ( ( ( _iDelta < 1 ) &&  ( -1*(_iDelta) > $.jGestures.defaults().thresholdRotateccw() ) ) || ( ( _iDelta > 1 ) && (_iDelta  > $.jGestures.defaults().thresholdRotatecw()) ) ) {
              _iDirection = (_iDelta < 1 ) ? -1 : +1 ;
              _oDetails = _createOptions(GQ.create(OptArgs.class).type("rotate").rotation(_iDelta).startMove(oStartTouch).event(event_).timestamp(oStartTouch.timestamp()).vector(_iDirection).description("rotate:" + _iDirection + ":" + (_iDelta < 1 ? "counterclockwise" : "clockwise")));
              _$element.trigger(_oDetails.type(), _oDetails);
            }
          break;

        }
      }
      return true;
    };
  };

  static Function _onGestureend = new Function() {
    public boolean f(Event e) {
      TouchEvent event_ = e.cast();

      // ignore bubbled handlers
      // if ( event_.currentTarget !== event_.target ) { return; }

      GQuery _$element = $(event_.getCurrentEventTarget());
      // var _$element = jQuery(event_.target);

      // trigger custom notification
      _$element.trigger($.jGestures.events().gestureendStart(),event_);

      double _iDelta;
      Properties _oDatajQueryGestures = _$element.data("ojQueryGestures");
      Move oStartTouch = GQ.create(Move.class).load(_oDatajQueryGestures.getJavaScriptObject("oStartTouch"));

      // trigger handler for every bound event
      for (String _sType : _oDatajQueryGestures.keys()) {

        switch(_sType) {

          case "pinchclose":
            _iDelta = event_.scale();
            if (( _iDelta < 1 ) && (_iDelta % 1) < (1 - $.jGestures.defaults().thresholdPinchclose())) {
              _$element.trigger("pinchclose", _createOptions (GQ.create(OptArgs.class).type("pinchclose").scale(_iDelta).vector(-1).startMove(oStartTouch).event(event_).timestamp(oStartTouch.timestamp()).description("pinch:-1:close")));
            }
          break;

          case "pinchopen":
            _iDelta = event_.scale();
            if ( ( _iDelta > 1 ) && (_iDelta % 1) > ($.jGestures.defaults().thresholdPinchopen())) {
              _$element.trigger("pinchopen", _createOptions (GQ.create(OptArgs.class).type("pinchopen").scale(_iDelta).vector(+1).startMove(oStartTouch).event(event_).timestamp(oStartTouch.timestamp()).description("pinch:+1:open")));
            }
          break;

          case "rotatecw":
            _iDelta = event_.rotation();
            if ( ( _iDelta > 1 ) && (_iDelta  > $.jGestures.defaults().thresholdRotatecw()) ) {
              _$element.trigger("rotatecw", _createOptions (GQ.create(OptArgs.class).type("rotatecw").rotation(_iDelta).vector(+1).startMove(oStartTouch).event(event_).timestamp(oStartTouch.timestamp()).description("rotate:+1:clockwise")));
            }
          break;

          case "rotateccw":
            _iDelta = event_.rotation();
            if ( ( _iDelta < 1 ) &&  ( -1*(_iDelta) > $.jGestures.defaults().thresholdRotateccw()) ) {
              _$element.trigger("rotateccw", _createOptions (GQ.create(OptArgs.class).type("rotateccw").rotation(_iDelta).vector(+1).startMove(oStartTouch).event(event_).timestamp(oStartTouch.timestamp()).description("rotate:-1:counterclockwise")));
              }
          break;

        }
      }
      _$element.trigger($.jGestures.events().gestureendProcessed(),event_);
      return true;
    }
  };

  static native String join(JsArrayString arr, String ch) /*-{
    return arr.join(ch);
  }-*/;

  /**
   * Make the app go full-screen whenever the user touches the screen, useful in modern mobile
   * devices. We cannot put the screen in full mode without an user click.
   *
   * Note: that the classic hack trying to move the scroll does not work fine, and have many
   * problems with 100% sizes.
   */
  public Gesture fullScreen() {
    return fullScreen(false);
  }

  /**
   * Go full-screen once.
   */
  public Gesture fullScreenOnce() {
    return fullScreen(true);
  }

  private Gesture fullScreen(final boolean once) {
    EventsListener.getInstance(body).bind(-1, null, hasGestures ? "touchstart" : "mousedown", null, new Function() {public void f() {
        if (!isFullScreen()) goFullScreen();
    }}, once ? 1 : -1);
    return this;
  }

  public Gesture fullScreenNow() {
    goFullScreen();
    return this;
  }

  /**
   * Configure the view port with default values to avoid the user resizing the screen
   */
  public Gesture viewPortDefault() {
    return viewPort(null);
  }

  /**
   * Configure the viewport with the provided content;
   */
  public Gesture viewPort(String content) {
    String s = content != null ? content : "user-scalable=no, initial-scale=1, maximum-scale=1, minimum-scale=1";
    GQuery v = $("meta[name='viewport']", document.getHead());
    if (v.isEmpty()) {
      v = $("<meta name='viewport'></meta>").appendTo(document.getHead());
    }
    v.attr("content", s);
    return this;
  }

  private static boolean trigger(GQuery elm, Event evt, String eventName, Object... datas) {
    elm.data(EventsListener.EVENT_DATA, datas);
    EventsListener.getInstance(elm.get(0)).dispatchEvent(evt, eventName);
    elm.removeData(EventsListener.EVENT_DATA);
    return !JsUtils.isDefaultPrevented(evt);
  }

  private static boolean trigger(GQuery elm, String eventName, Object... datas) {
    elm.data(EventsListener.EVENT_DATA, datas);
    NativeEvent evt = document.createHtmlEvent(eventName, true, true);
    elm.get(0).dispatchEvent(evt);
    elm.removeData(EventsListener.EVENT_DATA);
    return !JsUtils.isDefaultPrevented(evt);
  }

  public native void goFullScreen() /*-{
    var e = $doc.documentElement;
    var fnc = e.requestFullscreen || e.mozRequestFullScreen || e.webkitRequestFullScreen || e.msRequestFullscreen;
    fnc.call(e);
  }-*/;

  public native boolean isFullScreen() /*-{
    return $doc.fullscreenElement && !$doc.mozFullScreenElement && !$doc.webkitFullscreenElement && !$doc.msFullscreenElement
  }-*/;
}
