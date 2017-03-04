package com.alwaysrejoice.hexengine;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

public class MainActivity extends Activity implements View.OnTouchListener {
  MapView renderView;

  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    renderView = new MapView(this);
    renderView.setOnTouchListener(this);
    setContentView(renderView);
  }

  // OnTouchListener methods
  public boolean onTouch(View v, MotionEvent event) {
    renderView.onTouchEvent(event);
    return true;
  }

}
