package com.alwaysrejoice.hexengine.util;

import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import com.alwaysrejoice.hexengine.dto.Damage;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;

/**
 * Generic Static Utils
 */
public class Utils {
  private static final DecimalFormat decimalFormat = new DecimalFormat("0", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
  {
    decimalFormat.setMaximumFractionDigits(10);
  }

  public static int toPixel(int unit, float size) {
    DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
    return (int) TypedValue.applyDimension(unit, size, metrics);
  }

  /**
   * Converts an int to a String translating 0 as ""
   */
  public static String intToString(int num) {
    if (num == 0) return "";
    return Integer.toString(num);
  }

  /**
   * Converts a double to a String translating 0 as ""
   */
  public static String doubleToString(double num) {
    if (num == 0.0) return "";
    return decimalFormat.format(num);
  }

  /**
   * Converts a String to a double treating "" as 0
   */
  public static double stringToDouble(String str) {
    if ((str == null) || ("".equals(str))) {
      return 0.0;
    }
    return Double.parseDouble(str);
  }

  /**
   * Converts a String to an int treaing "" as 0
   */
  public static int stringToInt(String str) {
    if ((str == null) || ("".equals(str))) {
      return 0;
    }
    return Integer.parseInt(str);
  }


  /**
   * Makes a comma separated list of strings
   */
  public static String toCSV(List<String> list) {
    if (list == null) return "";
    StringBuilder csv = new StringBuilder();
    for (int i=0; i<list.size(); i++) {
      if (i != 0) csv.append(", ");
      String str = list.get(i);
      if (str != null) {
        csv.append(str);
      }
    }
    return csv.toString();
  }

  public static String damageToCSV(List<Damage> list) {
    if (list == null) return "";
    StringBuilder csv = new StringBuilder();
    for (int i=0; i<list.size(); i++) {
      if (i != 0) csv.append("\n");
      Damage dmg = list.get(i);
      if (dmg != null) {
        csv.append(dmg.getDisplayText());
      }
    }
    return csv.toString();
  }



}
