package com.alwaysrejoice.hexengine.edit;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.alwaysrejoice.hexengine.R;
import com.alwaysrejoice.hexengine.util.GameUtils;
import com.alwaysrejoice.hexengine.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class ListStringBaseActivity extends Activity {
  ListView list;
  ListStringAdapter adapter;

  // Subclasses should set these two variables in
  // onCreate but BEFORE super.onCreate is called
  List<String> types = new ArrayList<>();
  String typeName = "";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.list_string);
    EditText newInput = (EditText) findViewById(R.id.new_input);
    newInput.setHint("New "+typeName);
    TextView titleView = (TextView) findViewById(R.id.title);
    titleView.setText(typeName+"s");
    list = (ListView) findViewById(R.id.type_list_view);
    adapter = new ListStringAdapter(this, types);
    list.setAdapter(adapter);
  }

  /**
   * Called when the user clicks "Delete" on a row
   */
  public void delete(View view) {
    hideError();
    hideKeyboard();
    int position = (int) view.getTag();
    if (adapter.getCount() == 1) {
      showError("You must leave at least one item.");
      return;
    }
    String type = (String) list.getItemAtPosition(position);
    adapter.removeItem(position);
    GameUtils.saveGame();
    adapter.notifyDataSetChanged();
  }

  /**
   * Called when the user clicks on "Create New"
   */
  public void create(View view) {
    hideError();
    EditText newInput = (EditText) findViewById(R.id.new_input);
    String newType = newInput.getText().toString().trim();
    if (!"".equals(newType)) {
      types.add(newType);
      GameUtils.saveGame();
      newInput.setText("");
      adapter.notifyDataSetChanged();
    }
  }

  /**
   * Called when the user clicks Settings
   */
  public void gotoListEditor(View view) {
    Intent myIntent = new Intent(ListStringBaseActivity.this, ListEditorActivity.class);
    startActivity(myIntent);
  }

  public void showError(String errorMsg) {
    TextView errView = (TextView) findViewById(R.id.error_message);
    errView.setText(errorMsg);
    errView.setTextColor(Color.RED);
    ViewGroup.LayoutParams layoutParams = errView.getLayoutParams();
    layoutParams.height = Utils.spToPixel(24);
    errView.setLayoutParams(layoutParams);
  }

  public void hideError() {
    TextView errView = (TextView) findViewById(R.id.error_message);
    errView.setText("");
    ViewGroup.LayoutParams layoutParams = errView.getLayoutParams();
    layoutParams.height = 0;
    errView.setLayoutParams(layoutParams);
  }

  public void hideKeyboard() {
    EditText newInput = (EditText) findViewById(R.id.new_input);
    newInput.clearFocus();
    View view = this.getCurrentFocus();
    if (view != null) {
      Log.d("asdf", "clearing view");
      InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
      imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
  }

}
