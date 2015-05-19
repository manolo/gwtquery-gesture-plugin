

## Introduction
A gwtQuery plugin for adding gesture events.

## Demo
Open the [demo](http://manolo.github.io/gwtquery-gesture-demo/index.html) page with either a mobile device or in your desktop, try to tap with one or more fingers on the screen, 
to move the image, shake your device and change the orientiation.


## Usage

1. You only have to drop the .jar file in your classpath, or add this dependency to your project:
   
   ```
        <dependency>
            <groupId>com.googlecode.gwtquery.plugins</groupId>
            <artifactId>gestures-plugin</artifactId>
            <version>1.0-SNAPSHOT</version>
            <scope>provided</scope>
        </dependency>
   ```
2. Then use it as any other gQuery plugin through the `as()` method
   ```
   // Observe attribute changes in all elements matching the selector
   $(selector)
     .as(Gesture.Gesture)
     .on("tap", new Function() {
         public boolean f(Event ev) {
           Options o = arguments(0);
           console.log(o.description());
           return true;
         }
     });

   ```
3. gQuery Gesture plugin is a gwt port of the jGesture plugin at https://jgestures.codeplex.com/


## Mobile compatibility

- iOS: 
  orientationchange swipemove swipeone swipetwo swipethree swipefour swipeup swiperightup swiperight swiperightdown swipedown swipeleftdown swipeleft swipeleftup tapone taptwo tapthree tapfour shake shakefrontback shakeleftright shakeupdown pinchopen pinchclose rotatecw rotateccw pinch rotate

- Android: 
  orientationchange swipemove swipeone swipetwo swipethree swipefour swipeup swiperightup swiperight swiperightdown swipedown swipeleftdown swipeleft swipeleftup tapone taptwo tapthree tapfour shake shakefrontback shakeleftright shakeupdown
