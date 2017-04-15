package com.alwaysrejoice.hexengine.util;

import android.util.Log;

import com.alwaysrejoice.hexengine.dto.Action;
import com.alwaysrejoice.hexengine.dto.Mod;
import com.alwaysrejoice.hexengine.dto.Unit;
import com.alwaysrejoice.hexengine.dto.World;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

public class ScriptEngine {

  World world;
  Context context; // Rhino context
  Scriptable scope;

  public ScriptEngine(World world) {
    this.world = world;
    context = Context.enter();
    context.setOptimizationLevel(-1);
    scope = context.initStandardObjects();
    ScriptableObject.putProperty(scope, "world", Context.javaToJS(world, scope));
  }

  // This needs to be called before exiting!
  public void onDestroy() {
    context.exit();
  }

  public boolean runRule(Action applies, Unit self, Unit target) {
    Mod mod = world.getMods().get(applies.getModId());
    if (mod == null) {
      Log.e("Script Engine", "Error! mod is null modId="+applies.getModId()+" mods="+world.getMods());
      return false;
    }
    String script = mod.getScript();
    boolean result = false;
    ScriptableObject.putProperty(scope, "self", Context.javaToJS(self, scope));
    ScriptableObject.putProperty(scope, "target", Context.javaToJS(target, scope));

    try {
      Object resultObj = context.evaluateString(scope, script, "jsScript", 1, null);
      if (resultObj instanceof Boolean) {
        result = ((Boolean) resultObj).booleanValue();
      } else {
        Log.e("ScriptEngine", "Error! Rules must return boolean. The rule returned:"+resultObj);
      }
      Log.d("ScriptEngine", "script='"+script+"' result="+result+"\n self="+self+"\n target="+target);
    } catch (Exception e) {
      result = false;
      Log.e("ScriptEngine", "Error in script '"+script+"'", e);
    }
    return result;
  }


}
