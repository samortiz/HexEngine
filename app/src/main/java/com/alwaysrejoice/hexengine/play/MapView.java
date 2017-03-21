package com.alwaysrejoice.hexengine.play;

import android.content.Context;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.View;

public class MapView extends View {

  public MapView(Context context) {
    super(context);
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    canvas.drawRGB(50, 80, 230);
  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    return false;
  }

}

