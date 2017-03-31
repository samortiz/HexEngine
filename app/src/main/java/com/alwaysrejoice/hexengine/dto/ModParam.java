package com.alwaysrejoice.hexengine.dto;

import java.util.ArrayList;
import java.util.List;

public class ModParam {

  public static enum TYPE {String, Number, Integer, Boolean, Damage};

  public static List<String> getTypesAsString() {
    ArrayList<String> strTypes = new ArrayList();
    for (TYPE t : TYPE.values()) {
      strTypes.add(t.name());
    }
    return strTypes;
  }

  private String var; // variable name in scripts
  private TYPE type;

  public ModParam() {}

  public ModParam(String var, TYPE type) {
    this.var = var;
    this.type = type;
  }

  public String getVar() {
    return var;
  }

  public void setVar(String var) {
    this.var = var;
  }

  public TYPE getType() {
    return type;
  }

  public void setType(TYPE type) {
    this.type = type;
  }

  @Override
  public String toString() {
    return "ModParam{" +
        ", var='" + var + '\'' +
        ", type=" + type +
        '}';
  }
}
