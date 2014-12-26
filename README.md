

## Introduction
A gwtQuery plugin for adding gesture events.

## Usage

1. You only have to drop the .jar file in your classpath, or add this dependency to your project:
   
   ```
        <dependency>
            <groupId>com.googlecode.gwtquery.plugins</groupId>
            <artifactId>gesture-plugin</artifactId>
            <version>1.0-SNAPSHOT</version>
            <scope>provided</scope>
        </dependency>
   ```
2. Then use it as any other gQuery plugin through the `as()` method
   ```
   // Observe attribute changes in all elements matching the selector
   $(selector)
     .as(Gesture.Gesture)
     .on("", new Function() {
         public boolean f(List<MutationRecord> mutations) {
           console.log(mutations.get(0).type());
         }
     });

   ```
3. gQuery Gesture plugin is a port of the jGesture plugin at https://jgestures.codeplex.com/


## Mobile compatibility

- iOS: 
  orientationchange swipemove swipeone swipetwo swipethree swipefour swipeup swiperightup swiperight swiperightdown swipedown swipeleftdown swipeleft swipeleftup tapone taptwo tapthree tapfour shake shakefrontback shakeleftright shakeupdown pinchopen pinchclose rotatecw rotateccw pinch rotate

- Android: 
  orientationchange swipemove swipeone swipetwo swipethree swipefour swipeup swiperightup swiperight swiperightdown swipedown swipeleftdown swipeleft swipeleftup tapone taptwo tapthree tapfour shake shakefrontback shakeleftright shakeupdown
