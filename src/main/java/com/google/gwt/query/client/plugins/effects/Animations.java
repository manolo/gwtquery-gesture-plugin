/*
 * Copyright 2014, The gwtquery team.
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
/*
 *
 * Copyright 2013, The gwtquery team.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.gwt.query.client.plugins.effects;

import static com.google.gwt.query.client.plugins.Effects.*;

import com.google.gwt.dom.client.Element;
import com.google.gwt.query.client.Console;
import com.google.gwt.query.client.Function;
import com.google.gwt.query.client.GQuery;
import com.google.gwt.query.client.Properties;
import com.google.gwt.query.client.impl.ConsoleBrowser;
import com.google.gwt.query.client.js.JsUtils;
import com.google.gwt.query.client.plugins.Plugin;
import static com.google.gwt.query.client.plugins.effects.Transform.getVendorPropertyName;
/**
 * Transitions and transformation plugin for gQuery.
 */
public class Animations extends Transitions {
  
  String[] css3names = new String[]{"transition", "transform", "box-shadow"};
  private static Console console = new ConsoleBrowser();

  /**
   * Object representing a CSS3 frame.
   * 
   * For instance if percent = 0 and transform = $$("top: 10, rotateZ: 90")
   * it will renderer as:
   * 0% {top: 10px; transform: rotateZ(90deg);}
   */
  public static class CssFrame {
    public CssFrame() {
    }
    public CssFrame(int i) {
      percent = i;
    }
    public int percent = 0;
    public Properties transform = $$();
  }

  /**
   * Interface for customized frame generators.
   */
  public static interface CssFrameGenerator {
    void f(CssFrame frame);
  }
  
  /**
   * Implementation of a CssFrameGenerator with two frames.
   */
  public static class CssFrames implements CssFrameGenerator {
    private Properties from, to;

    public CssFrames(Properties from, Properties to) {
      this.from = from;
      this.to = to;
    }

    public void f(CssFrame frame) {
      if (frame.percent == 0) {
        frame.transform = from;
      } else if (frame.percent == 100) {
        frame.transform = to;
      }
    }
  }
  
  static {
    
  }
  
  static {
    for (String s: new String[]{"animationName", "animationDirection", "animationDelay", "animationDuration", "animationIterationCount", "animationTimingFunction", "animationFillMode"}) {
      vendorPropNames.put(s, getVendorPropertyName(s));
    }
    console.log(vendorPropNames);
  }

  private static final String ANIMATIONPREFIX = "_a_";
  private static final String animationStart = browser.mozilla || browser.msie ? "animationstart" : (prefix + "AnimationStart");
  private static final String animationEnd = browser.mozilla || browser.msie ? "animationend" : (prefix + "AnimationEnd");
  private static final String keyframes = browser.mozilla || browser.msie ? "keyframes" : ("-" + prefix +  "-keyframes");
  private static final String animationName = vendorName("animationName");
  private static final String animationDirection = vendorName("animationDirection");
  public static final String animationDelay = vendorName("animationDelay");
  private static final String animationDuration = vendorName("animationDuration");
  private static final String animationIterationCount = vendorName("animationIterationCount");
  private static final String animationTimingFunction = vendorName("animationTimingFunction");
  private static final String animationFillMode = vendorName("animationFillMode");

  private static int animationId = 0;

  public static final Class<Animations> Animations = GQuery.registerPlugin(
      Animations.class, new Plugin<Animations>() {
        public Animations init(GQuery gq) {
          return new Animations(gq);
        }
      });
  
  private static final Animations tmpDiv = $("<div>").as(Animations);

  protected Animations(GQuery gq) {
    super(gq);
  }
  
  private static String vendorName(String prop) {
    String n = vendorProperty(prop);
    return n == null ? prop : JsUtils.hyphenize(n);
  }
  
  private static Properties fixProp(Object stringOrProperties) {
    if (stringOrProperties instanceof Properties) {
      return (Properties)stringOrProperties;
    } else {
      return $$(String.valueOf(stringOrProperties));
    }
  }
  
  private static String fixName(String name) {
    return name != null ? name : (ANIMATIONPREFIX + animationId++);
  }
  
  private static String insertStyleTag(String name, String content) {
    name = fixName(name);
    GQuery s = $("#" + name);
    if (s.isEmpty()) {
      s = $("<style></style>").id(name).appendTo(document.getHead());
    }
    s.html(content.replace("%NAME%", name));
    return name;
  }
  
  public static String insertStyle(Object stringOrProperties) {
    return insertStyle(null, stringOrProperties);
  }

  public static String insertStyle(String name, Object stringOrProperties) {
    tmpDiv.attr("style", "");
    tmpDiv.css(fixProp(stringOrProperties));
    return insertStyleTag(name, ".%NAME%{\n" + tmpDiv.attr("style") + "\n}\n");
  }
  
  public void removeStyle(String name) {
    $("#" + name, document.getHead()).remove();
    $("." + name).removeClass(name);
  }

  /**
   * Inserts a &lt;style> block in the header with the keyframes necessary for a css3 animation.
   * It reuses the block if already exists.
   *
   * @param name an unique id to use as class name to apply the animation, if null it is calculated.
   * @param cfg an object with the origin, duration, function, and count parameters
   * @param frameCreator an implementation of a Frame generator
   * @return the class name we have to use to apply the animation.
   */
  public static String keyframes(String name, Properties cfg, CssFrameGenerator frameCreator) {
    return keyframes(name, cfg.getStr("direction"), cfg.getStr("duration"), cfg.getStr("delay"),
        cfg.defined("function") ? cfg.getStr("function") : cfg.getStr("easing"),
            cfg.getStr("count"), cfg.getStr("fillmode"), frameCreator);
  }
  
  private static String cfgKeys = "direction|duration|delay|function|easing|count|fillmode";
  
  public static String keyframes(Object stringOrProperties) {
    Properties cfg = fixProp(stringOrProperties);
    Properties from = $$();
    Properties to = $$();
    for (String k : cfg.keys()) {
      if (!k.matches(cfgKeys)) {
//        from.set(k, "0");
        to.set(k, cfg.get(k));
      }
    }
    return keyframes(from, to, cfg);
  }
  
  public static String keyframes(Properties from, Properties to, Properties cfg) {
    return keyframes(null, cfg, new CssFrames(from, to));
  }

  /**
   * Inserts a &lt;style> block in the header with the keyframes necessary for a css3 animation.
   * It reuses the block if already exists.
   *
   * @param name an unique id to use as a class name to apply to the animation, if null it is calculated.
   * @param direction normal|reverse|alternate|alternate-reverse
   * @param duration animation time in ms.
   * @param delay wait for this time in ms before starting the animation.
   * @param function the easing function: linear|ease|ease-in|ease-out|cubic-bezier(n,n,n,n)
   * @param count times the animation is repeated: number|infinite
   * @param fillmode styles to apply when animation ends: none|forwards|backwards|both
   * @param frameCreator the implementation of the Frame generator to use.
   * @return the style element id which is as well the class name to apply to ane element to animate it.
   */
  public static String keyframes(String name, String direction, String duration, String delay, String function, String count, String fillmode, CssFrameGenerator frameCreator) {
    name = name != null ? name : (ANIMATIONPREFIX + animationId++);
    String css = "@" + keyframes + " %NAME%{\n";
    for (int i = 0; i < 101; i++) {
      CssFrame f = new CssFrame(i);
      frameCreator.f(f);
      if (f.transform.keys().length == 0) continue;
      // We use a temporary Transform object to consider transform properties as well.
      tmpDiv.attr("style", "");
      tmpDiv.css(f.transform);
      css += f.percent + "% {" + tmpDiv.attr("style") + "}\n";
    }
    css += "}\n.%NAME%{" + animationName + ":" + name + ";\n";
    css += animationDuration + ":" +  (duration != null ? duration : Speed.DEFAULT) + "ms;\n";
    css += animationDelay + ":" + (delay != null ? delay : "0") + "ms;\n";
    css += animationTimingFunction + ":" + (function != null ? function : "linear") + ";\n";
    css += animationIterationCount + ":" + (count != null ? count : "1") + ";\n";
    css += animationFillMode + ":" + (fillmode != null ? fillmode : "forwards") + ";\n";
    css += animationDirection + ":" + (direction != null ? direction : "normal") + ";\n";
    css += "}\n";
    return insertStyleTag(name, css);
  }

  public Animations css3Animate(final Properties from, final Properties to, Properties cfg, Function... funcs) {
    for (Element e : elements()) {
      GQAnimation a = new AnimationsAnimation(from, to, cfg, e, funcs);
      queueAnimation(a, Speed.DEFAULT);
    }
    return this;
  }
  
  @Override
  protected GQAnimation createAnimation() {
    return new AnimationsAnimation();
  }
}
