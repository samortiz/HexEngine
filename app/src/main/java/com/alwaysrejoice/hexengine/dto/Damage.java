package com.alwaysrejoice.hexengine.dto;

import com.alwaysrejoice.hexengine.util.Utils;

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

  /**
   * Formatted display string for this damage
   */
  public String getDisplayText() {
    StringBuffer str = new StringBuffer();
    if ((count != 0) && (size != 0)) {
      str.append(count+" D "+size);
    };
    if (bonus > 0) {
      str.append(" + "+ Utils.doubleToString(bonus));
    }
    str.append(" of "+type);
    return str.toString();
  }
}
