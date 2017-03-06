package com.alwaysrejoice.hexengine.edit;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v4.view.MotionEventCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import com.alwaysrejoice.hexengine.dto.Background;
import com.alwaysrejoice.hexengine.dto.BackgroundTile;
import com.alwaysrejoice.hexengine.dto.TileType;
import com.google.gson.Gson;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Random;

public class EditView extends View {
  // Background drawing variables
  Bitmap backgroundImg;
  HashMap<String, Bitmap> tileTypes = new HashMap<>();
  int viewSizeX = 200; // size of viewport on the screen
  int viewSizeY = 700;
  int backgroundSizeX = 500; // size of background map
  int backgroundSizeY = 500;
  int TILE_SIZE = 60; // size of one tile on the map
  // Current location of view in the background
  int mapX = 100;
  int mapY = 100;

  // Drawing
  Rect panZoomWindow = new Rect(0, 0, 10, 10);
  Rect uiWindow = null;

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
  private float mapScaleFactor = 0.5f;
  private float scaleDiffX = 0;
  private float scaleDiffY = 0;

  public EditView(Context context) {
    super(context);
    mapScaleDetector = new ScaleGestureDetector(context, new MapScaleListener());
    Log.d("init", "Starting");
    InputStream inputStream = null;
    Random rand = new Random();
    Gson gson = new Gson();
    DisplayMetrics displayMetrics = new DisplayMetrics();
    ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

    try {
      AssetManager assetManager = context.getAssets();

      // Load the main background file
      inputStream = assetManager.open("background.json");
      String JsonBackground = IOUtils.toString(inputStream, "UTF-8");
      inputStream.close();
      Background bg = gson.fromJson(JsonBackground, Background.class);
      Log.d("init", "loaded width="+bg.getWidth()+" height="+bg.getHeight());

      // Load all the tileType images
      for (TileType type: bg.getTileTypes()) {
        inputStream = assetManager.open(type.getFileName());
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
        inputStream.close();
        tileTypes.put(type.getName(), bitmap);
      }

      // Make a background map
      backgroundSizeX = (bg.getWidth() * TILE_SIZE * 3) / 4;
      backgroundSizeY = bg.getHeight() * TILE_SIZE;
      int bgCenterX = backgroundSizeX / 2;
      int bgCenterY = backgroundSizeY / 2;
      Log.d("init", "Generating background bitmap width="+backgroundSizeX+" height="+backgroundSizeY);
      backgroundImg = Bitmap.createBitmap(backgroundSizeX,backgroundSizeY, Bitmap.Config.ARGB_8888);
      Canvas bgCanvas = new Canvas(backgroundImg);
      bgCanvas.drawRGB(bg.getBackgroundColor().getRed(), bg.getBackgroundColor().getGreen(), bg.getBackgroundColor().getBlue());
      // Draw border
      Paint paint = new Paint();
      paint.setStrokeWidth(5);
      paint.setColor(Color.GREEN);
      paint.setStyle(Paint.Style.STROKE);
      bgCanvas.drawRect(0, 0, backgroundSizeX, backgroundSizeY, paint);

      // Draw all the tiles
      for (BackgroundTile tile : bg.getTiles()) {
        int x = bgCenterX + Math.round(tile.getCol() * 0.75f * TILE_SIZE);
        int y = bgCenterY + Math.round(((-tile.getCol() - (2 * tile.getRow())) / 2.0f) * TILE_SIZE * -1);
        Bitmap bitmap = tileTypes.get(tile.getName());
        if (bitmap == null) {
          Log.d("error", "Error! Unknown tile : "+tile.getName());
        } else {
          bgCanvas.drawBitmap(bitmap, x, y, null);
        }
      }

      // Setup the ViewPort
      // Full size of the screen (square)
      viewSizeX = Math.min(displayMetrics.widthPixels, displayMetrics.heightPixels);
      viewSizeY = viewSizeX;
      Log.i("init", "Edit viewSizeX="+viewSizeX+" viewSizeY="+viewSizeY);
      uiWindow = new Rect(0, 0, viewSizeX, viewSizeY);
      mapScaleFactor = Math.max(MIN_SCALE, Math.min(((float)backgroundSizeX / (float)viewSizeX), MAX_SCALE));
      mapX = (backgroundSizeX / 2) - Math.round(viewSizeX * mapScaleFactor / 2);
      mapY = (backgroundSizeY / 2) - Math.round(viewSizeY * mapScaleFactor / 2);

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
    int viewSize = Math.round(viewSizeX * mapScaleFactor);
    int left = mapX + Math.round(gestureDiffX * mapScaleFactor + scaleDiffX);
    int top  = mapY + Math.round(gestureDiffY * mapScaleFactor + scaleDiffY);
    int right = mapX + viewSize + Math.round(gestureDiffX * mapScaleFactor + scaleDiffX);
    int bottom = mapY + viewSize + Math.round(gestureDiffY * mapScaleFactor + scaleDiffY);
    panZoomWindow.set(left, top, right, bottom);
    canvas.drawBitmap(backgroundImg, panZoomWindow, uiWindow, null);
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
        int viewSize = Math.round(viewSizeX * mapScaleFactor);
        int left = mapX + Math.round(diffX * mapScaleFactor + scaleDiffX);
        if (left < 0) {
          diffX = (-mapX -scaleDiffX) / mapScaleFactor;
        }
        int top  = mapY + Math.round(diffY * mapScaleFactor + scaleDiffY);
        if (top < 0) {
          diffY = (-mapY - scaleDiffY) / mapScaleFactor;
        }
        int right = mapX + viewSize + Math.round(gestureDiffX * mapScaleFactor + scaleDiffX);
        if ((right >= backgroundSizeX) && (diffX > 0)) {
          diffX = (backgroundSizeX - mapX - viewSize - scaleDiffX) / mapScaleFactor;
        }
        int bottom = mapY + viewSize + Math.round(gestureDiffY * mapScaleFactor + scaleDiffY);
        if ((bottom >=  backgroundSizeY) && (diffY > 0)) {
          diffY = (backgroundSizeY - mapY - viewSize - scaleDiffY) / mapScaleFactor;
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
      if ((viewSizeX * newScaleFactor) >= backgroundSizeX) {
        newScaleFactor = (float)backgroundSizeX / (float)viewSizeX;
      }
      // Pan while zooming to maintain the relative position on the screen of the center of the gesture
      float relativeX = detector.getFocusX() / viewSizeX; //  percent of view port
      float relativeY = detector.getFocusY() / viewSizeY;
      float diffX = ((viewSizeX * mapScaleFactor) * relativeX) - ((viewSizeX * newScaleFactor) * relativeX);
      float diffY = ((viewSizeY * mapScaleFactor) * relativeY) - ((viewSizeY * newScaleFactor) * relativeY);
      scaleDiffX += diffX;
      scaleDiffY += diffY;
      //Log.d("Scale", "Scaling old="+mapScaleFactor+ " new="+newScaleFactor);
      mapScaleFactor = newScaleFactor;
      Log.d("scale", "mapScaleFactor="+newScaleFactor);
      return true;
    }
  }

}

