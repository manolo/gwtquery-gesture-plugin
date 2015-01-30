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
package com.google.gwt.query.client.plugins.effects;

import static com.google.gwt.query.client.GQuery.$;
import static com.google.gwt.query.client.GQuery.document;

import com.google.gwt.dom.client.Element;
import com.google.gwt.query.client.Function;
import com.google.gwt.query.client.Properties;
import com.google.gwt.query.client.js.JsObjectArray;
import com.google.gwt.query.client.plugins.Effects.GQAnimation;
import com.google.gwt.query.client.plugins.effects.Animations.CssFrameGenerator;
import com.google.gwt.query.client.plugins.effects.Animations.CssFrames;
import com.google.gwt.query.client.plugins.effects.TransitionsAnimation.TransitionsClipAnimation;
import com.google.gwt.user.client.Timer;

import java.util.HashMap;
import java.util.Map;

/**
 * Animation effects on any numeric CSS3 property or transformation
 * using CSS3 animations.
 */
public class AnimationsAnimation extends TransitionsClipAnimation {
  
  private static Map<Properties, JsObjectArray<Fx>> effectsCache = new HashMap<Properties, JsObjectArray<Fx>>();
  
  private Animations g;
  private String id;
  private Properties to;
  private Properties cfg;
  private CssFrameGenerator cssFrames;

  public AnimationsAnimation() {
  }
  
  public AnimationsAnimation(Properties from, Properties to, Properties cfg, Element elem, Function...funcs) {
    setEasing(easing).setProperties(to).setElement(e).setCallback(funcs);
    setCallback(funcs);
    this.to = to;
    this.cfg = to;
    cssFrames = new CssFrames(from, to);
  }
  
  @Override
  public GQAnimation setElement(Element element) {
    g = $(element).as(Animations.Animations);
    return super.setElement(element);
  }
  
  @Override
  public void onStart() {
    if ((effects = effectsCache.get(prps)) == null) {
      super.onStart();
      effectsCache.put(prps, effects);
    }
  }
  
  private void clean() {
    // Remove animation class
    g.removeClass(id);
    // We don't need the style element anymore
    $("#" + id, document.getHead()).remove();
  }
  
  @Override
  public void onCancel() {
    clean();
    super.onCancel();
  }
  
  @Override
  public void onComplete() {
    // Set final css properties
    g.css(to);
    clean();
    super.onComplete();
  }
  
  public void run(int duration) {
    CssFrameGenerator frameGenerator;
    
    if (cssFrames == null) {
      // Compute effects
      onStart();
      // create initial properties from effects
      final Properties from = getFxProperties(true);
      // create final properties from effects
      to = getFxProperties(false);
      frameGenerator = new CssFrames(from, to);
      cfg = prps;
      cfg.set("duration", duration).set("function", easing).set("delay", delay);
    } else {
      frameGenerator = cssFrames;
    }
    
    // Generate style element, it returns the class-name to add to the lement
    id = Animations.keyframes(null, cfg, frameGenerator);
    
    // Add animation class
    g.addClass(id);
    
    // Wait until transition has finished
    new Timer() {
      public void run() {
//        onComplete();
      }
    }.schedule(delay + duration);
  }
}
