package com.alwaysrejoice.hexengine.dto;

import java.util.HashMap;
import java.util.Map;

public class Action {

  private String modName;
  private Map<String, ModParamValue> values = new HashMap<>(); // keyed on var

  public Action() {}

  public String getModName() {
    return modName;
  }

  public void setModName(String modName) {
    this.modName = modName;
  }

  public Map<String, ModParamValue> getValues() {
    return values;
  }

  public void setValues(Map<String, ModParamValue> values) {
    this.values = values;
  }

  @Override
  public String toString() {
    return "Action{" +
        "modName='" + modName + '\'' +
        ", values=" + values +
        '}';
  }
}
