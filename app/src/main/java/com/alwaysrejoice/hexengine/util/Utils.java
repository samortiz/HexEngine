package com.alwaysrejoice.hexengine.util;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import com.alwaysrejoice.hexengine.dto.BitmapGsonAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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

  // Custom GSON serialize/deserializer
  // This will allow my DTO to store/load Bitmaps as Base64
  public static final Gson gson = new GsonBuilder().registerTypeHierarchyAdapter(Bitmap.class, new BitmapGsonAdapter()).create();

  public static final DecimalFormat decimalFormat = new DecimalFormat("0", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
  {
    decimalFormat.setMaximumFractionDigits(10);
  }

  /**
   * Converts an object to JSON
   */
  public static String toJson(Object obj) {
    return Utils.gson.toJson(obj);
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
  public static String toCsv(List<String> list) {
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

  public static List<String> makeList(String[] strings) {
    List<String> list = new ArrayList<>(strings.length);
    for (String str : strings) {
      list.add(str);
    }
    return list;
  }

  /**
   * Setup a spinner with the supplied values
   */
  public static void setupSpinner(Spinner spinner, List<String> values) {
    ArrayAdapter<String> adapter = new ArrayAdapter<>(spinner.getContext(), android.R.layout.simple_spinner_item, values);
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    spinner.setAdapter(adapter);
  }

  /**
   * Sets the spinner to the matching value.
   */
  public static String getSpinnerValue(Spinner spinner) {
    ArrayAdapter<String> adapter = (ArrayAdapter<String>)spinner.getAdapter();
    return adapter.getItem(spinner.getSelectedItemPosition());
  }

  /**
   * Sets the selected item in the spinner to match the value.
   * If the value is not in the list, then it will set the spinner to the first item.
   */
  public static void setSpinnerValue(Spinner spinner, String value) {
    ArrayAdapter<String> adapter = (ArrayAdapter<String>)spinner.getAdapter();
    int selectedIndex = Math.max(0, adapter.getPosition(value));
    spinner.setSelection(selectedIndex);
  }

}
