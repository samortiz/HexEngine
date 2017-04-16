package com.alwaysrejoice.hexengine.edit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.alwaysrejoice.hexengine.R;

/**
 * List of String editors
 */
public class ListEditorActivity extends Activity {

  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.list_editor);
  }

  public void gotoBgTypeList(View view) {
    Intent myIntent = new Intent(ListEditorActivity.this, ListBgTypeActivity.class);
    startActivity(myIntent);
  }

  public void gotoDamageList(View view) {
    Intent myIntent = new Intent(ListEditorActivity.this, ListDamageActivity.class);
    startActivity(myIntent);
  }

  public void gotoAttrList(View view) {
    Intent myIntent = new Intent(ListEditorActivity.this, ListAttrActivity.class);
    startActivity(myIntent);
  }

  public void gotoSettings(View view) {
    Intent myIntent = new Intent(ListEditorActivity.this, SettingsActivity.class);
    startActivity(myIntent);
  }

}
