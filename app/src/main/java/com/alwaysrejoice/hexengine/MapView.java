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
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

public class MapView extends SurfaceView implements Runnable, SurfaceHolder.Callback {
  // Core screen drawing
  Thread renderThread = null;
  SurfaceHolder holder;
  volatile boolean running = false;

  // Background drawing variables
  Bitmap grass;
  Bitmap tree;
  Bitmap background;
  int VIEW_SIZE_X = 1000; // size of viewport on the screen
  int VIEW_SIZE_Y = 1000;
  int BACKGROUND_SIZE_X = 2500; // size of background map
  int BACKGROUND_SIZE_Y = 2500;
  int TILE_SIZE = 60; // size of one tile on the map
  // Current location of view in the background
  int mapX = 100;
  int mapY = 100;

  // Panning
  private float gestureStartX = 0f; // Screen location gesture started
  private float gestureStartY = 0f;
  private int gestureStartPointerId = -1; // unique identifier for the finger
  private float gestureDiffX = 0; // distance from gestureStartX to current location
  private float gestureDiffY = 0;

  // Zooming (scaling)
  private float MIN_SCALE = 0.10f;
  private float MAX_SCALE = 3.0f;
  private ScaleGestureDetector mapScaleDetector;
  private float mapScaleFactor = 1.0f;
  private float scaleDiffX = 0;
  private float scaleDiffY = 0;

  public MapView(Context context) {
    super(context);
    holder = getHolder();
    holder.addCallback(this);
    mapScaleDetector = new ScaleGestureDetector(context, new MapScaleListener());


    InputStream inputStream = null;

    try {
      AssetManager assetManager = context.getAssets();
      inputStream = assetManager.open("grass.png");
      grass = BitmapFactory.decodeStream(inputStream);
      inputStream.close();
      Log.d("BitmapText", "grass.png format: " + grass.getConfig());

      inputStream = assetManager.open("tree.png");
      BitmapFactory.Options options = new BitmapFactory.Options();
      tree = BitmapFactory.decodeStream(inputStream, null, options);
      Log.d("BitmapText", "tree.png format: " + tree.getConfig());

      Random rand = new Random();

      // Make a background map
      background = Bitmap.createBitmap(BACKGROUND_SIZE_X, BACKGROUND_SIZE_Y, Bitmap.Config.ARGB_8888);
      Canvas bgCanvas = new Canvas(background);
      bgCanvas.drawRGB(100, 200, 200);
      boolean odd = true;
      for (int y = 0; y <= BACKGROUND_SIZE_X-TILE_SIZE; y += (TILE_SIZE /2)) {
        odd = !odd; // toggle for each row
        for (int x = odd? 0 : Math.round(TILE_SIZE *0.75f); x <= BACKGROUND_SIZE_X-TILE_SIZE; x += Math.round(TILE_SIZE * 1.5f)) {
          if (rand.nextInt(10) < 8) {
            bgCanvas.drawBitmap(grass, x, y, null);
          } else {
            bgCanvas.drawBitmap(tree, x, y, null);
          }
        }
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

  public void resume() {
    Log.d("MapView", "resume");
    running = true;
    renderThread = new Thread(this);
    renderThread.start();
  }

  public void run() {
    Log.d("MapView", "running");
    while (running) {
      if (!holder.getSurface().isValid()) {
        Log.d("surface", "Surface is not valid");
        continue;
      }
      // Why do I need this sleep?  It seems to hang without it
      try {
        Thread.sleep(10);
      } catch (InterruptedException e) {
        Log.d("Thread", "woken");
      }
      Canvas canvas = holder.lockCanvas();
      canvas.drawRGB(0, 0, 255);
      int viewSize = Math.round(VIEW_SIZE_X * mapScaleFactor);
      int left = mapX + Math.round(gestureDiffX * mapScaleFactor + scaleDiffX);
      int top  = mapY + Math.round(gestureDiffY * mapScaleFactor + scaleDiffY);
      int right = mapX + viewSize + Math.round(gestureDiffX * mapScaleFactor + scaleDiffX);
      int bottom = mapY + viewSize + Math.round(gestureDiffY * mapScaleFactor + scaleDiffY);
      Rect subsetView = new Rect(left, top, right, bottom);
      Rect scaleView = new Rect(0, 0, VIEW_SIZE_X, VIEW_SIZE_Y);
      canvas.drawBitmap(background, subsetView, scaleView, null);
      holder.unlockCanvasAndPost(canvas);
    }
  }

  public void pause() {
    Log.d("MapView", "pause");
    running = false;
    while (true) {
      try {
        renderThread.join();
        return;
      } catch (InterruptedException e) {
        Log.d("pause", "interrupted while trying to join");
        // retry
      }
    }
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
      endGesture(event);

    // Last finger went up
    } else if (action == MotionEvent.ACTION_UP) {
      Log.d("event", "ACTION_UP pointerId="+pointerId);
      endGesture(event);
    }

    return false;
  }

  private void endGesture(MotionEvent event) {
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

      invalidate();
      return true;
    }
  }


  @Override
  public void surfaceCreated(SurfaceHolder holder) {
    Log.d("SurfaceHolder.CallBack", "Created");
  }

  @Override
  public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    Log.d("SurfaceHolder.CallBack", "changed");
  }

  @Override
  public void surfaceDestroyed(SurfaceHolder holder) {
    Log.d("SurfaceHolder.CallBack", "destroyed");
  }

}

