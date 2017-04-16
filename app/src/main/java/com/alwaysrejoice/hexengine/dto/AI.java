package com.alwaysrejoice.hexengine.dto;

public class AI {

  private String id;
  private String name;
  private String script;

  public AI() {}

  public AI(String id) {
    this.id = id;
  }

  public AI(String id, String name, String script) {
    this.id = id;
    this.name = name;
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

  public String getScript() {
    return script;
  }

  public void setScript(String script) {
    this.script = script;
  }

  @Override
  public String toString() {
    return "AI{"+
        "id='"+id+'\''+
        ", name='"+name+'\''+
        ", script='"+script+'\''+
        '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof AI)) return false;
    AI ai = (AI) o;
    return id.equals(ai.id);
  }

  @Override
  public int hashCode() {
    return id.hashCode();
  }
}
