package com.alwaysrejoice.hexengine;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
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
  int TILE_SIZE_X = 60; // size of one tile on the map
  int TILE_SIZE_Y = 60;
  // Current location of view in the background
  int mapX = 100;
  int mapY = 200;

  // Panning
  private float gestureStartX = 0f; // Screen location gesture started
  private float gestureStartY = 0f;
  private int gestureDiffX = 0; // distance from gestureStartX to current location
  private int gestureDiffY = 0;

  // Scaling
  private float MIN_SCALE = 0.20f;
  private float MAX_SCALE = 3.0f;
  private ScaleGestureDetector mapScaleDetector;
  private float mapScaleFactor = 1.0f;
  private float secondFingerX;
  private float secondFingerY;

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
      for (int x = 0; x <= BACKGROUND_SIZE_X; x += TILE_SIZE_X) {
        for (int y = 0; y <= BACKGROUND_SIZE_X; y += TILE_SIZE_Y) {
          bgCanvas.drawBitmap(grass, x, y, null);
          if (rand.nextInt(10) > 8) {
            Paint paint = new Paint();
            String colorHex = "#" + Integer.toHexString(20 + rand.nextInt(200)) +
                Integer.toHexString(20 + rand.nextInt(200)) +
                Integer.toHexString(20 + rand.nextInt(200));
            //Log.d("MapView", "Color:'" + colorHex + "'");
            paint.setColor(Color.parseColor(colorHex));
            bgCanvas.drawCircle(x, y, 10, paint);
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
        if (holder.isCreating()) Log.d("surface", "Creating...");
        Log.d("surface", "Surface is not valid");
        continue;
      }
      Canvas canvas = holder.lockCanvas();
      canvas.drawRGB(0, 0, 255);

      int viewSize = Math.round(VIEW_SIZE_X * mapScaleFactor);
      int left = mapX + Math.round(gestureDiffX * mapScaleFactor);
      int top  = mapY + Math.round(gestureDiffY * mapScaleFactor);
      int right = mapX + viewSize + Math.round(gestureDiffX * mapScaleFactor);
      int bottom = mapY + viewSize + Math.round(gestureDiffY * mapScaleFactor);
      Rect subsetView = new Rect(left, top, right, bottom);
      Rect scaleView = new Rect(0, 0, VIEW_SIZE_X, VIEW_SIZE_Y);

      canvas.drawBitmap(background, subsetView, scaleView, null);

      canvas.drawBitmap(tree, VIEW_SIZE_X / 2, VIEW_SIZE_Y / 2, null); // center point
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
    float newX = event.getX(index);
    float newY = event.getY(index);

    // First finger down (start gesture)
    if (action == MotionEvent.ACTION_DOWN) {
      gestureStartX = newX;
      gestureStartY = newY;

    // Moving / dragging a finger
    } else if (action == MotionEvent.ACTION_MOVE) {
      gestureDiffX = Math.round(gestureStartX - newX);
      gestureDiffY = Math.round(gestureStartY - newY);

    // Releasing the last finger
    } else if (action == MotionEvent.ACTION_UP) {
      mapX = mapX + Math.round((gestureStartX - newX) * mapScaleFactor);
      mapY = mapY + Math.round((gestureStartY - newY) * mapScaleFactor);
      gestureDiffX = 0; // clear the diff, the mapX has moved now
      gestureDiffY = 0;
    }
    return false;
  }

  private class MapScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
    @Override
    public boolean onScale(ScaleGestureDetector detector) {
      mapScaleFactor = mapScaleFactor * (1f - (detector.getScaleFactor() - 1f));
      mapScaleFactor = Math.max(MIN_SCALE, Math.min(mapScaleFactor, MAX_SCALE));

      invalidate();
      Log.d("Scale", "Scaling to " + mapScaleFactor + " scaleFactor=" + detector.getScaleFactor());
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

