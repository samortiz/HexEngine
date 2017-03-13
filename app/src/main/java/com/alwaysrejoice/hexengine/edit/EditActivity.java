package com.alwaysrejoice.hexengine.edit;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class EditActivity extends AppCompatActivity implements View.OnTouchListener {
  EditView editView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Log.d("init", "EditActivity.onCreate");

    // Get the name of the selected game that was passed in with this intent
    String gameName = getIntent().getStringExtra("GAME_NAME");

    // Show the edit screen
    editView = new EditView(this, gameName);
    editView.setOnTouchListener(this);
    setContentView(editView);
  }

  // OnTouchListener methods
  public boolean onTouch(View v, MotionEvent event) {
    editView.onTouchEvent(event);
    return true;
  }

}
