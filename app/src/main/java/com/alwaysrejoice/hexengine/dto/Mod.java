package com.alwaysrejoice.hexengine.dto;

import java.util.ArrayList;
import java.util.List;

public class Mod implements Comparable {

  public static final String TYPE_MOD="mod";
  public static final String TYPE_RULE="rule";
  public static final String TYPE_MOD_LOC="modloc";
  public static final String TYPE_RULE_LOCK="ruleloc";

  private String id;
  private String name;
  private String type;
  private List<ModParam> params = new ArrayList();
  private String script;

  public Mod() {}

  public Mod(String id) {
    this.id = id;
  }

  public Mod(String id, String name, String type, List<ModParam> params, String script) {
    this.id = id;
    this.name = name;
    this.type = type;
    this.params = params;
    this.script = script;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public List<ModParam> getParams() {
    return params;
  }

  public void setParams(List<ModParam> params) {
    this.params = params;
  }

  public String getScript() {
    return script;
  }

  public void setScript(String script) {
    this.script = script;
  }

  @Override
  public String toString() {
    return "Mod{" +
        "id='" + id + '\'' +
        ", name='" + name + '\'' +
        ", type='" + type + '\'' +
        ", params=" + params +
        ", script='" + script + '\'' +
        '}';
  }

  @Override
  public int compareTo(Object o) {
    return this.getName().toLowerCase().compareTo(((Mod)o).getName().toLowerCase());
  }

}
