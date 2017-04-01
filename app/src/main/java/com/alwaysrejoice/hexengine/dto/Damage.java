package com.alwaysrejoice.hexengine.dto;

public class Damage {

  private String type = "";
  private int count = 0;
  private int size = 0;
  private double bonus = 0;

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public int getCount() {
    return count;
  }

  public void setCount(int count) {
    this.count = count;
  }

  public int getSize() {
    return size;
  }

  public void setSize(int size) {
    this.size = size;
  }

  public double getBonus() {
    return bonus;
  }

  public void setBonus(double bonus) {
    this.bonus = bonus;
  }

  @Override
  public String toString() {
    return "Damage{" +
        "type='" + type + '\'' +
        ", count=" + count +
        ", size=" + size +
        ", bonus=" + bonus +
        '}';
  }
}