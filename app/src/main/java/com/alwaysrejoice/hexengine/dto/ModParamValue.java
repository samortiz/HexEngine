package com.alwaysrejoice.hexengine.dto;

public class ModParamValue {

  private String var;
  private ModParam.TYPE type;

  private int valueInt = 0;
  private double valueDouble = 0.0;
  private String valueString = "";
  private boolean valueBoolean = false;
  private Damage valueDamage = new Damage();

  public ModParamValue() {}

  // Creates a paramValue with default values
  public ModParamValue(String var, ModParam.TYPE type) {
    this.var = var;
    this.type = type;
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

  public int getValueInt() {
    return valueInt;
  }

  public void setValueInt(int valueInt) {
    this.valueInt = valueInt;
  }

  public double getValueDouble() {
    return valueDouble;
  }

  public void setValueDouble(double valueDouble) {
    this.valueDouble = valueDouble;
  }

  public String getValueString() {
    return valueString;
  }

  public void setValueString(String valueString) {
    this.valueString = valueString;
  }

  public boolean getValueBoolean() {
    return valueBoolean;
  }

  public void setValueBoolean(boolean valueBoolean) {
    this.valueBoolean = valueBoolean;
  }

  public Damage getValueDamage() {
    return valueDamage;
  }

  public void setValueDamage(Damage valueDamage) {
    this.valueDamage = valueDamage;
  }

  @Override
  public String toString() {
    return "ModParamValue{" +
        "var='" + var + '\'' +
        ", type=" + type +
        ", valueInt=" + valueInt +
        ", valueDouble=" + valueDouble +
        ", valueString='" + valueString + '\'' +
        ", valueBoolean=" + valueBoolean +
        ", valueDamage=" + valueDamage +
        '}';
  }
}
