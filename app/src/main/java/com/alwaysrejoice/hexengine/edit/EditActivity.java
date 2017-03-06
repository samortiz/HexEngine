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
    // Show the edit screen
    editView = new EditView(this);
    editView.setOnTouchListener(this);
    setContentView(editView);

  }

  // OnTouchListener methods
  public boolean onTouch(View v, MotionEvent event) {
    editView.onTouchEvent(event);
    return true;
  }


}
