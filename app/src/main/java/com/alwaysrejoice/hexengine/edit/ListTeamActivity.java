package com.alwaysrejoice.hexengine.edit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.alwaysrejoice.hexengine.R;
import com.alwaysrejoice.hexengine.util.GameUtils;

import java.util.List;

public class ListTeamActivity extends Activity {
  ListView list;
  ListStringAdapter adapter;
  List<String> types;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.list_string);

    // Unique part
    types = GameUtils.getGame().getTeams();
    String typeName = "Team";

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
    int position = (int) view.getTag();
    String type = (String) list.getItemAtPosition(position);
    adapter.removeItem(position);
    GameUtils.saveGame();
    adapter.notifyDataSetChanged();
  }

  /**
   * Called when the user clicks on "Create New"
   */
  public void create(View view) {
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
    Intent myIntent = new Intent(ListTeamActivity.this, ListEditorActivity.class);
    startActivity(myIntent);
  }

}
