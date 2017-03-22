package com.alwaysrejoice.hexengine.edit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.alwaysrejoice.hexengine.R;
import com.alwaysrejoice.hexengine.dto.Game;
import com.alwaysrejoice.hexengine.util.GameUtils;

import java.util.ArrayList;

/**
 * Displays and handles events for the image picker screen
 */
public class ImagePickerActivity extends Activity {
  ListView list;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.image_picker);

    TableLayout table = (TableLayout) findViewById(R.id.image_table);
    for (int i = 0; i < 10; i++) {
      TableRow row = new TableRow(this);
      row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT));
      TextView tv = new TextView(this);
      tv.setText("Testing " + i);
      row.addView(tv);
      table.addView(row, i);
    } // for
  }

}
