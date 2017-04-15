package com.alwaysrejoice.hexengine.dto;

import java.util.HashMap;
import java.util.Map;

public class Action {

  private String id;
  private String modId;
  private Map<String, ModParamValue> paramValues = new HashMap<>(); // keyed on var

  public Action() {}

  public Action(String id) {
    this.id = id;
  }

  public String getModId() {
    return modId;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public void setModId(String modId) {
    this.modId = modId;
  }

  public Map<String, ModParamValue> getParamValues() {
    return paramValues;
  }

  public void setParamValues(Map<String, ModParamValue> paramValues) {
    this.paramValues = paramValues;
  }

  @Override
  public String toString() {
    return "Action{" +
        "id='" + id + '\'' +
        ", modId='" + modId + '\'' +
        ", paramValues=" +paramValues+
        '}';
  }
}
