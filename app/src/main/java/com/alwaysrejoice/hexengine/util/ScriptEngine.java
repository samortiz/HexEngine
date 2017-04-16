package com.alwaysrejoice.hexengine.util;

import android.util.Log;

import com.alwaysrejoice.hexengine.dto.Action;
import com.alwaysrejoice.hexengine.dto.Mod;
import com.alwaysrejoice.hexengine.dto.ModParam;
import com.alwaysrejoice.hexengine.dto.ModParamValue;
import com.alwaysrejoice.hexengine.dto.Unit;
import com.alwaysrejoice.hexengine.dto.World;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import java.util.List;
import java.util.Map;

public class ScriptEngine {

  private World world;
  private Context context; // Rhino context
  private Scriptable scope;
  private ScriptTools tools;

  public ScriptEngine(World world) {
    this.world = world;
    this.tools = new ScriptTools(world);
    context = Context.enter();
    context.setOptimizationLevel(-1);
    scope = context.initStandardObjects();
    ScriptableObject.putProperty(scope, "world", Context.javaToJS(world, scope));
    ScriptableObject.putProperty(scope, "tools", Context.javaToJS(tools, scope));
  }

  // This needs to be called before exiting!
  public void onDestroy() {
    Log.d("ScriptEngine", "Shutting down script engine");
    context.exit();
  }

  /**
   * Executes a list of actions
   */
  public void runActions(List<Action> actions, Unit self, Unit target) {
    if (actions == null) {
      return;
    }
    for (Action action : actions) {
      executeScript(action, self, target);
    }
  }

  /**
   * Runs a RULE script
   */
  public boolean runRule(Action applies, Unit self, Unit target) {
    boolean result = false;
    Object resultObj = executeScript(applies, self, target);
    if (resultObj instanceof Boolean) {
      result = ((Boolean) resultObj).booleanValue();
    } else {
      Log.e("ScriptEngine", "Error! Rules must return a boolean. modId="+applies.getModId()+" returned:"+resultObj);
    }
    return result;
  }

  /**
   * Executes a script and returns the object that the JS returned
   */
  public Object executeScript(Action action, Unit self, Unit target) {
    Object retVal = null;
    Mod mod = world.getMods().get(action.getModId());
    if (mod == null) {
      Log.e("Script Engine", "Error in executeScript! mod is null modId="+action.getModId()+" mods="+world.getMods());
      return null;
    }
    String script = mod.getScript();
    // Setup the JS context for this script
    ScriptableObject.putProperty(scope, "self", Context.javaToJS(self, scope));
    ScriptableObject.putProperty(scope, "target", Context.javaToJS(target, scope));

    // Add the parameters to the JS context
    Map<String, ModParamValue> paramValues = action.getParamValues();
    if (mod.getParams() != null) {
      for (ModParam param : mod.getParams()) {
        ModParamValue paramValue = paramValues.get(param.getVar());
        if (paramValue == null) {
          Log.d("ScriptEngine", "Warning: mod="+mod.getName()+" param "+param.getVar()+" is null");
          continue;
        }
        Object value = null;
        if (param.getType() == ModParam.TYPE.String) {
          value = paramValue.getValueString();
        } else if (param.getType() == ModParam.TYPE.Number) {
          value = new Double(paramValue.getValueDouble());
        } else if (param.getType() == ModParam.TYPE.Integer) {
          value = new Integer(paramValue.getValueInt());
        } else if (param.getType() == ModParam.TYPE.Boolean) {
          value = new Boolean(paramValue.getValueBoolean());
        } else if (param.getType() == ModParam.TYPE.Damage) {
          value = paramValue.getValueDamage();
        }
        if (value != null) {
          ScriptableObject.putProperty(scope, param.getVar(), Context.javaToJS(value, scope));
          Log.d("ScriptEngine", "Adding param "+param.getVar()+" to context with value="+value);
        }
      } // for
    }

    try {
      retVal = context.evaluateString(scope, script, "jsScript", 1, null);
    } catch (Exception e) {
      Log.e("ScriptEngine", "Error in script '"+script+"'", e);
    }
    Log.d("ScriptEngine", "script='"+script+"' retVal="+retVal+"\n self="+self+"\n target="+target);
    return retVal;
  }


  /**
   * @return tools available to the JS scripts
   */
  public ScriptTools getTools() {
    return this.tools;
  }

}
