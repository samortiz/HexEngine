package com.alwaysrejoice.hexengine.dto;

public class Team {

  private String id;
  private String name;
  private String aiId;

  public Team() {
  }

  public Team(String id) {
    this.id = id;
  }

  public Team(String id, String name, String aiId) {
    this.id = id;
    this.name = name;
    this.aiId = aiId;
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

  public String getAiId() {
    return aiId;
  }

  public void setAiId(String aiId) {
    this.aiId = aiId;
  }

  @Override
  public String toString() {
    return "Team{"+
        "id='"+id+'\''+
        ", name='"+name+'\''+
        ", aiId='"+aiId+'\''+
        '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Team)) return false;
    Team team = (Team) o;
    return id != null ? id.equals(team.id) : team.id == null;
  }

  @Override
  public int hashCode() {
    return id != null ? id.hashCode() : 0;
  }
}
