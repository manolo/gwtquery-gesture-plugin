package com.google.gwt.query.client.plugins.gestures;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.query.client.GQ;
import com.google.gwt.query.client.Properties;
import com.google.gwt.query.client.plugins.gestures.GestureObjects.XYZ;
import com.google.gwt.query.client.plugins.events.GqEvent;

public class TouchEvent extends GqEvent {
  protected TouchEvent() {
  }

  public final XYZ accelerationIncludingGravity() {
    return GQ.create(XYZ.class, accelerationIncludingGravityImpl());
  }

  private final native Properties accelerationIncludingGravityImpl() /*-{
    return this.accelerationIncludingGravity;
  }-*/;

  public native final JsArray<TouchEvent> touches() /*-{
    return this.touches|| [];
  }-*/;

  public native final JsArray<TouchEvent> changedTouches() /*-{
    return this.changedTouches || [];
  }-*/;

  public native final JsArray<TouchEvent> targetTouches() /*-{
    return this.targetTouches || [];
  }-*/;

  public native final int screenX() /*-{
    return this.screenX || 0;
  }-*/;

  public native final int screenY() /*-{
    return this.screenY || 0;
  }-*/;

  public native final int clientX() /*-{
    return this.clientX || 0;
  }-*/;

  public native final int clientY() /*-{
    return this.clientY || 0;
  }-*/;

  public native final double scale() /*-{
    return this.scale || 0;
  }-*/;

  public native final double rotation() /*-{
    return this.rotation || 0;
  }-*/;

  public native final int identifier() /*-{
    return this.identifier || 0;
  }-*/;
}
