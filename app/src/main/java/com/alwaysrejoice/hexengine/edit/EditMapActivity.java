package com.alwaysrejoice.hexengine.edit;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.alwaysrejoice.hexengine.util.GameUtils;

public class EditMapActivity extends AppCompatActivity implements View.OnTouchListener {
  EditMapView editMapView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Log.d("init", "EditMapActivity.onCreate");

    // Show the edit screen
    editMapView = new EditMapView(this);
    editMapView.setOnTouchListener(this);
    setContentView(editMapView);
  }

  // OnTouchListener methods
  public boolean onTouch(View v, MotionEvent event) {
    editMapView.onTouchEvent(event);
    return true;
  }

  @Override
  public void onBackPressed() {
    Log.d("edit", "Back pressed");
    GameUtils.saveGame();
    // default behavior
    super.onBackPressed();
  }


}
