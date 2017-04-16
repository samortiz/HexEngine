package com.alwaysrejoice.hexengine.dto;

import java.util.ArrayList;
import java.util.List;

public class Triggers {

  public List<Action> startWorld = new ArrayList<>();
  public List<Action> startTurn = new ArrayList<>();
  public List<Action> endTurn = new ArrayList<>();
  public List<Action> abilityUsed = new ArrayList<>();


  public List<Action> getStartWorld() {
    return startWorld;
  }

  public void setStartWorld(List<Action> startWorld) {
    this.startWorld = startWorld;
  }

  public List<Action> getStartTurn() {
    return startTurn;
  }

  public void setStartTurn(List<Action> startTurn) {
    this.startTurn = startTurn;
  }

  public List<Action> getEndTurn() {
    return endTurn;
  }

  public void setEndTurn(List<Action> endTurn) {
    this.endTurn = endTurn;
  }

  public List<Action> getAbilityUsed() {
    return abilityUsed;
  }

  public void setAbilityUsed(List<Action> abilityUsed) {
    this.abilityUsed = abilityUsed;
  }

  @Override
  public String toString() {
    return "Triggers{"+
        "startWorld="+startWorld+
        ", startTurn="+startTurn+
        ", endTurn="+endTurn+
        ", abilityUsed="+abilityUsed+
        '}';
  }

}

