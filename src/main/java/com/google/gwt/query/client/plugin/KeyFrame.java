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

import com.google.gwt.query.client.GQ;
import com.google.gwt.query.client.GQuery;
import com.google.gwt.query.client.IsProperties;
import com.google.gwt.query.client.Properties;
import com.google.gwt.query.client.builders.JsonBuilder;
import com.google.gwt.query.client.js.JsCache;
import com.google.gwt.query.client.js.JsUtils;
import com.google.gwt.query.client.plugins.Plugin;
import com.google.gwt.query.client.plugins.effects.Transitions;

/**
 * KeyFrame CSS3 animation plugin.
 *
 * @author Manolo Carrasco
 */
public class KeyFrame extends GQuery {
  
  public static interface Frame extends JsonBuilder {
    int percent();
    Frame percent(int percent);
    IsProperties transform();
    Frame transform(IsProperties p);
  }
  
  public static interface FrameGenerator {
    void f(Frame frame);
  }
  
  
  
  private static String keyframes;
  private static String animationname;
  private static String animationorigin;
  private static String animationduration;
  private static String animationinterationcount;
  private static String animationtimingfunction;
  private static String vendor;
  static {
    vendor = Transitions.getVendorPropertyName("AnimationName").replace("AnimationName", "");
    keyframes = JsUtils.hyphenize(vendor + "Keyframes");
    animationname = JsUtils.hyphenize(vendor + "AnimationName");
    animationorigin = JsUtils.hyphenize(vendor + "AnimationOrigin");
    animationduration = JsUtils.hyphenize(vendor + "AnimationDuration");
    animationinterationcount = JsUtils.hyphenize(vendor + "AnimationIterationCount");
    animationtimingfunction = JsUtils.hyphenize(vendor + "AnimationTimingFunction");
  }
  
  public static final Class<KeyFrame> KeyFrame = registerPlugin(KeyFrame.class, new Plugin<KeyFrame>() {
    public KeyFrame init(GQuery gq) {
      return new KeyFrame(gq);
    }
  });

  public KeyFrame(GQuery gq) {
    super(gq);
  }
  
  private static GQuery $e = $("<div/>");
  private static int counter = 0;

  public String keyFrame(String name, Properties cfg, FrameGenerator frameCreator) {
    name = name != null ? name : ("__S__" + counter++);
    String css = "@" + keyframes + " " + name + " {";
    Frame f = GQ.create(Frame.class);
    for (int i = 0; i < 101; i++) {
      frameCreator.f(f.percent(i).transform(null));
      if (f.transform().<JsCache>getDataImpl().length() == 0) continue;
      $e.data("_t_", null);
      $e.attr("style", "");
      $e.as(Transitions.Transitions).css((Properties)f.transform());
      css += f.percent() + "% {" + $e.attr("style") + ";}";
    }
    css += "}." + name + " {" + animationname + ":" + name + ";";
    css += animationorigin + ":" + (cfg.defined("origin")? cfg.get("origin") : "") + ";";
    css += animationduration + ":" +  (cfg.defined("duration")? cfg.get("duration") : "400")+ ";";
    css += animationtimingfunction + ":" + (cfg.defined("function")? cfg.get("function") : "linear") + ";";
    css += animationinterationcount + ":" + (cfg.defined("count")? cfg.get("count") : "1") + ";";
    css += "}";
    
    GQuery s = $("#" + name);
    if (s.isEmpty()) {
      s = $("<style></style>").id(name).appendTo(document.getHead());
    }
    s.html(css);
    return name;
  }
}
