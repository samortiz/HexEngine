package com.alwaysrejoice.hexengine.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;

import java.security.InvalidParameterException;
import java.util.List;

public class DialogMultiSelect implements OnMultiChoiceClickListener, DialogInterface.OnClickListener {

  private Context context;
  private String title;
  private List<String> data;
  private List<String> selected; // Stores state both in and out

  private AlertDialog.Builder builder;
  // Stores state before the user clicks "OK"
  private boolean[] selectedArray;
  DialogMultiSelectListener listener;

  public DialogMultiSelect(Context context, String title, List<String> data, List<String> selected) {
    this.context = context;
    this.title = title;
    this.data = data;
    this.selected = selected;
    if (data == null)
      throw new InvalidParameterException("Data cannot be null. You can pass an empty list.");
    if (selected == null)
      throw new InvalidParameterException("Selected list cannot be null, pass an empty list if nothing is selected");
  }

  /**
   * Called to show the dialog popup
   */
  public void showDialog(DialogMultiSelectListener listener) {
    this.listener = listener;
    // Setup the data
    String[] dataArray = data.toArray(new String[data.size()]);
    selectedArray = new boolean[dataArray.length];
    for (int i = 0; i < selectedArray.length; i++) {
      selectedArray[i] = selected.contains(data.get(i));
    }
    // Setup the dialog
    builder = new AlertDialog.Builder(context);
    builder.setTitle(title);
    builder.setMultiChoiceItems(dataArray, selectedArray, this);
    builder.setPositiveButton("OK", this);
    builder.setNegativeButton("Cancel", null);
    builder.create().show();
  }

  /**
   * Called on every click of an item
   */
  @Override
  public void onClick(DialogInterface dialog, int which, boolean isChecked) {
    selectedArray[which] = isChecked;
  }

  /**
   * Called when the OK button is clicked (updates the user-specified model)
   */
  @Override
  public void onClick(DialogInterface dialog, int which) {
    // Update the selected values
    selected.clear(); // do not re-create the array or the calling code won't have access to the List
    for (int i=0; i<selectedArray.length; i++) {
      if (selectedArray[i]) {
        selected.add(data.get(i));
      }
    } // for
    listener.onOK();
    //Log.d("dialogMultiSelect", "clicked OK selected="+selected);
  }

}
