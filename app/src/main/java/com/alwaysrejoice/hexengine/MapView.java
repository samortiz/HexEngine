package com.alwaysrejoice.hexengine;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Random;

public class MapView extends View {
  // Background drawing variables
  Bitmap background;
  HashMap<String, Bitmap> backgroundTiles = new HashMap<>();
  int VIEW_SIZE_X = 1000; // size of viewport on the screen
  int VIEW_SIZE_Y = 1000;
  int BACKGROUND_SIZE_X = 2500; // size of background map
  int BACKGROUND_SIZE_Y = 2500;
  int TILE_SIZE = 60; // size of one tile on the map
  // Current location of view in the background
  int mapX = 100;
  int mapY = 100;

  // Drawing
  Rect panZoomWindow = new Rect(0, 0, 10, 10);
  Rect uiWindow = new Rect(0, 0, VIEW_SIZE_X, VIEW_SIZE_Y);

  // Panning
  private float gestureStartX = 0f; // Screen location gesture started
  private float gestureStartY = 0f;
  private int gestureStartPointerId = -1; // unique identifier for the finger
  private float gestureDiffX = 0; // distance from gestureStartX to current location
  private float gestureDiffY = 0;

  // Zooming (scaling)
  private static float MIN_SCALE = 0.10f;
  private static float MAX_SCALE = 3.0f;
  private ScaleGestureDetector mapScaleDetector;
  private float mapScaleFactor = 1.0f;
  private float scaleDiffX = 0;
  private float scaleDiffY = 0;

  public MapView(Context context) {
    super(context);
    mapScaleDetector = new ScaleGestureDetector(context, new MapScaleListener());
    Random rand = new Random();
    InputStream inputStream = null;

    try {
      String[] tileNames = {"grass.png", "grass2.png", "tree.png", "tree2.png"};
      AssetManager assetManager = context.getAssets();
      for (String tileName : tileNames) {
        inputStream = assetManager.open(tileName);
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
        inputStream.close();
        backgroundTiles.put(tileName, bitmap);
      }

      // Make a background map
      background = Bitmap.createBitmap(BACKGROUND_SIZE_X, BACKGROUND_SIZE_Y, Bitmap.Config.ARGB_8888);
      Canvas bgCanvas = new Canvas(background);
      bgCanvas.drawRGB(100, 200, 200);
      boolean odd = true;
      for (int y = 0; y <= BACKGROUND_SIZE_X-TILE_SIZE; y += (TILE_SIZE /2)) {
        odd = !odd; // toggle for each row
        for (int x = odd? 0 : Math.round(TILE_SIZE *0.75f); x <= BACKGROUND_SIZE_X-TILE_SIZE; x += Math.round(TILE_SIZE * 1.5f)) {
          // Choose a random image
          String tileName = tileNames[rand.nextInt(tileNames.length)];
          Bitmap bitmap = backgroundTiles.get(tileName);
          if (bitmap != null) {
            Log.d("init", "drawing tileName="+tileName);
            bgCanvas.drawBitmap(bitmap, x, y, null);
          } else {
            Log.d("init", "Error loading tileName="+tileName);
          }
        } // for
      }

    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (inputStream != null) {
        try {
          inputStream.close();
        } catch (IOException e) {
          Log.d("ERROR", "Error closing stream");
        }
      }
    }
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    canvas.drawRGB(0, 0, 255);
    int viewSize = Math.round(VIEW_SIZE_X * mapScaleFactor);
    int left = mapX + Math.round(gestureDiffX * mapScaleFactor + scaleDiffX);
    int top  = mapY + Math.round(gestureDiffY * mapScaleFactor + scaleDiffY);
    int right = mapX + viewSize + Math.round(gestureDiffX * mapScaleFactor + scaleDiffX);
    int bottom = mapY + viewSize + Math.round(gestureDiffY * mapScaleFactor + scaleDiffY);
    panZoomWindow.set(left, top, right, bottom);
    canvas.drawBitmap(background, panZoomWindow, uiWindow, null);
  }


  @Override
  public boolean onTouchEvent(MotionEvent event) {
    mapScaleDetector.onTouchEvent(event);
    int action = MotionEventCompat.getActionMasked(event);
    int index = MotionEventCompat.getActionIndex(event);
    int pointerId = event.getPointerId(index);
    float newX = event.getX(index);
    float newY = event.getY(index);

    // First time, get the intitial finger (can happen at other times beside ACTION_DOWN)
    if (gestureStartPointerId == -1) {
      Log.d("touch", "start pointer "+pointerId);
      gestureStartPointerId = pointerId;
      gestureStartX = newX;
      gestureStartY = newY;
    }
    // First finger down (start gesture)
    if (action == MotionEvent.ACTION_DOWN) {
      Log.d("event", "ACTION_DOWN pointerId="+pointerId);
      // Don't need to do anything all the startup has been taken care of already

    // Moving / dragging a finger
    } else if (action == MotionEvent.ACTION_MOVE) {
      if (event.getPointerCount() == 1) {
        float diffX = gestureStartX - newX;
        float diffY = gestureStartY - newY;
        // Bounds checking on the pan
        int viewSize = Math.round(VIEW_SIZE_X * mapScaleFactor);
        int left = mapX + Math.round(diffX * mapScaleFactor + scaleDiffX);
        if (left < 0) {
          diffX = (-mapX -scaleDiffX) / mapScaleFactor;
        }
        int top  = mapY + Math.round(diffY * mapScaleFactor + scaleDiffY);
        if (top < 0) {
          diffY = (-mapY - scaleDiffY) / mapScaleFactor;
        }
        int right = mapX + viewSize + Math.round(gestureDiffX * mapScaleFactor + scaleDiffX);
        if ((right >= BACKGROUND_SIZE_X) && (diffX > 0)) {
          diffX = (BACKGROUND_SIZE_X - mapX - viewSize - scaleDiffX) / mapScaleFactor;
        }
        int bottom = mapY + viewSize + Math.round(gestureDiffY * mapScaleFactor + scaleDiffY);
        if ((bottom >=  BACKGROUND_SIZE_Y) && (diffY > 0)) {
          diffY = (BACKGROUND_SIZE_Y - mapY - viewSize - scaleDiffY) / mapScaleFactor;
        }
        //Log.d("pan", "right="+right+" gestureDiffX="+gestureDiffX+" diffX="+diffX);
        gestureDiffX = diffX;
        gestureDiffY = diffY;
      }

    } else if (action == MotionEvent.ACTION_POINTER_DOWN) {
      Log.d("event", "ACTION_POINTER_DOWN pointerId="+pointerId);

    } else if (action == MotionEvent.ACTION_POINTER_UP) {
      Log.d("event", "ACTION_POINTER_UP pointerId="+pointerId);
      // A finger went up, probably ending a two figure gesture (zooming)
      endGesture();

    // Last finger went up
    } else if (action == MotionEvent.ACTION_UP) {
      Log.d("event", "ACTION_UP pointerId="+pointerId);
      endGesture();
    }

    this.postInvalidate();
    return false;
  }

  private void endGesture() {
    // Apply the pan and zoom diffs
    mapX =  mapX + Math.round(gestureDiffX * mapScaleFactor + scaleDiffX);
    mapY =  mapY + Math.round(gestureDiffY * mapScaleFactor + scaleDiffY);
    gestureDiffX = 0; // clear the panning diff
    gestureDiffY = 0;
    scaleDiffX = 0; // clear the zooming diff
    scaleDiffY = 0;
    gestureStartPointerId = -1;
  }

  private class MapScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
    @Override
    public boolean onScale(ScaleGestureDetector detector) {
      float newScaleFactor = mapScaleFactor * (1f - (detector.getScaleFactor() - 1f));
      newScaleFactor = Math.max(MIN_SCALE, Math.min(newScaleFactor, MAX_SCALE));
      // Pan while zooming to maintain the relative position on the screen of the center of the gesture
      float relativeX = detector.getFocusX() / VIEW_SIZE_X; //  percent of view port
      float relativeY = detector.getFocusY() / VIEW_SIZE_Y;
      float diffX = ((VIEW_SIZE_X * mapScaleFactor) * relativeX) - ((VIEW_SIZE_X * newScaleFactor) * relativeX);
      float diffY = ((VIEW_SIZE_Y * mapScaleFactor) * relativeY) - ((VIEW_SIZE_Y * newScaleFactor) * relativeY);
      scaleDiffX += diffX;
      scaleDiffY += diffY;
      //Log.d("Scale", "Scaling old="+mapScaleFactor+ " new="+newScaleFactor);
      mapScaleFactor = newScaleFactor;
      return true;
    }
  }

}

