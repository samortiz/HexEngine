package com.alwaysrejoice.hexengine.util;

import android.content.res.Resources;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.alwaysrejoice.hexengine.dto.Damage;

import java.nio.ByteBuffer;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

/**
 * Generic Static Utils
 */
public class Utils {
  public static final Random random = new Random();

  private static final DecimalFormat decimalFormat = new DecimalFormat("0", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
  {
    decimalFormat.setMaximumFractionDigits(10);
  }

  /**
   * Converts SP to pixels
   */
  public static int spToPixel(float size) {
    DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
    return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, size, metrics);
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

  /**
   * Method for Setting the Height of the ListView dynamically.
   * Hack to fix the issue of not showing all the items of the ListView
   * when placed inside a ScrollView  (by Arshu on StackOverflow)
   **/
  public static void setListViewHeight(ListView listView) {
    ListAdapter listAdapter = listView.getAdapter();
    if (listAdapter == null) return;
    int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
    int totalHeight = 0;
    View view = null;
    for (int i = 0; i < listAdapter.getCount(); i++) {
      view = listAdapter.getView(i, view, listView);
      if (i == 0) {
        view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, RelativeLayout.LayoutParams.WRAP_CONTENT));
      }
      view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
      totalHeight += view.getMeasuredHeight();
    }
    ViewGroup.LayoutParams params = listView.getLayoutParams();
    params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
    listView.setLayoutParams(params);
  }

  /**
   * Generates a globally unique ID, useful for identifying objects without collisions
   */
  public static String generateUniqueId() {
    long randomLong = random.nextLong();
    ByteBuffer buff = ByteBuffer.allocate(8);
    buff.putLong(randomLong);
    return Base64.encodeToString(buff.array(), Base64.NO_PADDING).trim();
  }

  public static List<String> makeList(String a, String b) {
    List<String> list = new ArrayList<>(2);
    list.add(a);
    list.add(b);
    return list;
  }

}
