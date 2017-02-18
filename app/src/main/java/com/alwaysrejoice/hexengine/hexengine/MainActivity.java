package com.alwaysrejoice.hexengine.hexengine;

import android.app.Activity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class MainActivity extends Activity  implements View.OnTouchListener,  GestureDetector.OnGestureListener {
    MapView renderView;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        renderView = new MapView(this);
        renderView.setOnTouchListener(this);

        setContentView(renderView);
    }

    protected void onResume() {
        super.onResume();
        renderView.resume();
    }

    protected void onPause() {
        super.onPause();
        renderView.pause();
    }

    // OnTouchListener methods
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                renderView.touchUp(event.getX(),event.getY());
                break;

        }
        renderView.onTouchEvent(event);
        return true;
    }

    // GestureDetector Methods
    public boolean onDown(MotionEvent e) {
        return true;
    }

    public void onShowPress(MotionEvent e) {

    }

    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        renderView.pan(distanceX, distanceY);
        return true;
    }

    public void onLongPress(MotionEvent e) {

    }

    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }
}
