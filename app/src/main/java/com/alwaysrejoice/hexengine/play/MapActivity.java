package com.alwaysrejoice.hexengine.play;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class MapActivity extends AppCompatActivity implements View.OnTouchListener {
  MapView mapView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Log.d("init", "EditMapActivity.onCreate");
    // Show the edit screen
    mapView = new MapView(this);
    mapView.setOnTouchListener(this);
    setContentView(mapView);

  }

  // OnTouchListener methods
  public boolean onTouch(View v, MotionEvent event) {
    mapView.onTouchEvent(event);
    return true;
  }


}
