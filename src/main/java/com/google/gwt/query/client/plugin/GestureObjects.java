package com.google.gwt.query.client.plugin;

import com.google.gwt.query.client.builders.JsonBuilder;
import com.google.gwt.user.client.Event;

import java.util.List;

public interface GestureObjects extends JsonBuilder {

  public interface DeviceWindow extends JsonBuilder {
    int orientation();
  }

  public interface DataGestures extends JsonBuilder {

  }

  public interface JGestures extends JsonBuilder {
    public interface Defaults extends JsonBuilder {
      public interface ThresholdShake extends JsonBuilder {
        public interface Sensitivity extends JsonBuilder {
          int sensitivity();
          Sensitivity sensitivity(int i);
        }
        int requiresShakes();
        int freezeShakes();
        int requiredShakes();
        Sensitivity frontback();
        Sensitivity leftright();
        Sensitivity updown();
        ThresholdShake requiresShakes(int i);
        ThresholdShake freezeShakes(int i);
        ThresholdShake requiredShakes(int i);
      }
      ThresholdShake thresholdShake();
      Defaults thresholdPinchopen(double d);
      Defaults thresholdPinchmove(double d);
      Defaults thresholdPinch(double d);
      Defaults thresholdPinchclose(double d);
      Defaults thresholdRotatecw(int i);
      Defaults thresholdRotateccw(int i);
      Defaults thresholdMove(int i);
      Defaults thresholdSwipe(int i);
      double thresholdPinchopen();
      double thresholdPinchmove();
      double thresholdPinch();
      double thresholdPinchclose();
      int thresholdRotatecw();
      int thresholdRotateccw();
      int thresholdMove();
      int thresholdSwipe();
    }
    public interface Data extends JsonBuilder {
      boolean hasGestures();
      Data hasGestures(boolean b);
    }
    public interface Events extends JsonBuilder {
      String touchstart();
      Events touchstart(String s);
      String touchendStart();
      Events touchendStart(String s);
      String touchendProcessed();
      Events touchendProcessed(String s);
      String gesturestart();
      Events gesturestart(String s);
      String gestureendStart();
      Events gestureendStart(String s);
      String gestureendProcessed();
      Events gestureendProcessed(String s);
    }
    Defaults defaults();
    Data data();
    Events events();
    boolean hasGestures();
    void hasGestures(boolean b);
  }

  public static class GOptions {
    public static class GOptArgs {
      public static class GMove {
        public int startX, startY, screenX, screenY;
        public double timestamp;
      }
      public String type, description;
      public int touches, deltaY, deltaX, vector, screenX, screenY, rotation, scale;
      public GMove startMove;
      public double duration, timestamp;
    }
    public static class GDelta {
      public double moved;
      int lastX, lastY, startX, startY;
    }
    public static class GDirection {
      public double moved;
      int lastX, lastY, startX, startY;
    }
  }

  public interface Options extends JsonBuilder {
    public interface OptArgs extends JsonBuilder {
      public interface Move extends JsonBuilder {
        int startX();
        int startY();
        double timestamp();
        int screenX();
        int screenY();
        Move timestamp(double d);
        Move startX(int i);
        Move startY(int i);
        Move screenX(int i);
        Move screenY(int i);
        int direction();
        Move direction(int i);
        int identifier();
        Move identifier(int i);
        int left();
        int top();
        int bottom();
        int right();
        Move left(int i);
        Move top(int i);
        Move bottom(int i);
        Move right(int i);
        Move rotation(int i);
        int rotation();
      }

      String type();
      int touches();
      int deltaY();
      int deltaX();
      Move startMove();
      TouchEvent event();
      double duration();
      double timestamp();
      int vector();
      int screenX();
      int screenY();
      OptArgs type(String s);
      OptArgs touches(int i);
      OptArgs deltaY(int i);
      OptArgs deltaX(int i);
      OptArgs startMove(Move m);
      OptArgs event(TouchEvent e);
      OptArgs duration(double d);
      OptArgs timestamp(double d);
      OptArgs vector(int i);
      OptArgs screenX(int i);
      OptArgs screenY(int i);
      double rotation();
      OptArgs rotation(double i);
      double scale();
      OptArgs scale(double i);
      String description();
      OptArgs description(String s);
      Move orientiation();
      OptArgs orientiation(Move i);
    }
    public interface Delta extends JsonBuilder {
      int lastX();
      int lastY();
      double moved();
      int startX();
      int startY();
      Delta lastX(int i);
      Delta lastY(int i);
      Delta moved(double d);
      Delta startX(int i);
      Delta startY(int i);
    }
    public interface Direction extends JsonBuilder {
      int lastX();
      int lastY();
      int startX();
      int startY();
      int vector();
      int orientation();
      Direction lastX(int i);
      Direction lastY(int i);
      Direction startX(int i);
      Direction startY(int i);
      Direction vector(int i);
      Direction orientiation(int i);
      String name();
      Direction name(String s);
    }

    String type();
//    Event originalEvent();
    List<Delta> delta();
    List<Direction> direction();
    int rotation();
    int scale();
    double duration();
    String description();
    Options type(String s);
//    Options originalEvent(Event e);
    Options delta(List<Delta> l);
    Options direction(List<Direction> l);
    Options rotation(double i);
    Options scale(double i);
    Options description(String s);
    Options duration(double d);
    Options directionName(String direction);
    String directionName();
    int screenX();
    int screenY();
    Options screenX(int i);
    Options screenY(int i);
  }

  public interface XYZ extends JsonBuilder {
    int x();
    XYZ x(int i);
    int y();
    XYZ y(int i);
    int z();
    XYZ z(int i);
  }
  public interface Shake extends JsonBuilder {
    int eventCount();
    int intervalsPassed();
    int intervalsFreeze();
    Shake eventCount(int i);
    Shake intervalsPassed(int i);
    Shake intervalsFreeze(int i);
  }

  public interface DevicePosition extends JsonBuilder {
    XYZ accelerationIncludingGravity();
    Shake shake();
    Shake shakeleftright();
    Shake shakefrontback();
    Shake shakeupdown();
  }

  public interface DataGQueryGestures extends JsonBuilder {
    DevicePosition oDeviceMotionLastDevicePosition();
  }


}
