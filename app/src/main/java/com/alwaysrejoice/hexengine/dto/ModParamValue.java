package com.alwaysrejoice.hexengine.dto;

public class ModParamValue {

  private String var;
  private ModParam.TYPE type;
  private Object value; // can be String, Integer, Double, Boolean, Damage etc..

  public ModParamValue() {}

  // Creates a paramValue with default values
  public ModParamValue(String var, ModParam.TYPE type) {
    this.var = var;
    this.type = type;
    if (type == ModParam.TYPE.String) {
      this.value = "";
    } else if (type == ModParam.TYPE.Number) {
      this.value = null;
    } else if (type == ModParam.TYPE.Integer) {
      this.value = null;
    } else if (type == ModParam.TYPE.Boolean) {
      this.value = new Boolean(false);
    } else if (type == ModParam.TYPE.Damage) {
      this.value = new Damage();
    }
  }

  public String getVar() {
    return var;
  }

  public void setVar(String var) {
    this.var = var;
  }

  public ModParam.TYPE getType() {
    return type;
  }

  public void setType(ModParam.TYPE type) {
    this.type = type;
  }

  public Object getValue() {
    return value;
  }

  public void setValue(Object value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return "ModParamValue{" +
        "var='" + var + '\'' +
        ", type=" + type +
        ", value=" + value +
        '}';
  }
}
