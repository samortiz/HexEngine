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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class EditView extends View {
  // Edit Mode
  public enum Mode { MOVE, ERASE, DRAW};

  // Background drawing variables
  Background bg; // Data loaded from the user
  Bitmap backgroundImg;
  Canvas bgCanvas;
  HashMap<String, Bitmap> tileTypes = new HashMap<>();
  int viewSizeX = 200; // size of viewport on the screen
  int viewSizeY = 700;
  int backgroundSizeX = 500; // size of background map
  int backgroundSizeY = 500;
  int bgCenterX;
  int bgCenterY;
  int TILE_SIZE = 60; // size of one tile on the map
  // Current location of view in the background
  int mapX = 100;
  int mapY = 100;

  // Toolbar
  int toolbarWidth;
  int toolbarHeight;
  int toolbarX;
  int toolbarY;
  Canvas toolbarCanvas;
  Bitmap toolbarImg;
  Rect toolbarWindow;
  List<ToolbarButton> toolbarButtons;
  int TOOLBAR_BUTTONS_PER_ROW = 5;
  Bitmap toolbarSelectedImg;
  int toolbarButtonSelectedIndex = 0;
  Mode mode = Mode.MOVE; // because toolbarButtonSelectedInex == 0
  String toolbarButtonSelectedName = "";

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
      bg = gson.fromJson(JsonBackground, Background.class);
      Log.d("init", "loaded width="+bg.getWidth()+" height="+bg.getHeight());
      // System editor images
      toolbarSelectedImg = loadBitmap(inputStream, assetManager, "selected.png");
      Bitmap handImg = loadBitmap(inputStream, assetManager, "hand.png");
      Bitmap eraserImg = loadBitmap(inputStream, assetManager, "eraser.png");
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
      bgCenterX = backgroundSizeX / 2;
      bgCenterY = backgroundSizeY / 2;
      Log.d("init", "Generating background bitmap width="+backgroundSizeX+" height="+backgroundSizeY);
      backgroundImg = Bitmap.createBitmap(backgroundSizeX,backgroundSizeY, Bitmap.Config.ARGB_8888);
      bgCanvas = new Canvas(backgroundImg);

      drawBackground();

      // Setup the ViewPort
      // Full size of the screen (square)
      viewSizeX = Math.min(displayMetrics.widthPixels, displayMetrics.heightPixels);
      viewSizeY = viewSizeX;
      Log.i("init", "Edit viewSizeX="+viewSizeX+" viewSizeY="+viewSizeY);
      uiWindow = new Rect(0, 0, viewSizeX, viewSizeY);
      mapScaleFactor = Math.max(MIN_SCALE, Math.min(((float)backgroundSizeX / (float)viewSizeX), MAX_SCALE));
      mapX = (backgroundSizeX / 2) - Math.round(viewSizeX * mapScaleFactor / 2);
      mapY = (backgroundSizeY / 2) - Math.round(viewSizeY * mapScaleFactor / 2);

      // Setup the toolbar
      if (displayMetrics.heightPixels > displayMetrics.widthPixels) {
        // Portrait
        toolbarHeight = displayMetrics.heightPixels - viewSizeY;
        toolbarWidth = displayMetrics.widthPixels;
        toolbarX = 0;
        toolbarY = viewSizeY + 1;
      } else {
        // Landscape
        toolbarHeight = displayMetrics.heightPixels;
        toolbarWidth = displayMetrics.widthPixels - displayMetrics.heightPixels;
        toolbarX = toolbarWidth;
        toolbarY = 0;
      }
      toolbarImg = Bitmap.createBitmap(toolbarWidth,toolbarHeight, Bitmap.Config.ARGB_8888);
      toolbarCanvas = new Canvas(toolbarImg);
      toolbarWindow = new Rect(toolbarX, toolbarY, toolbarX + toolbarWidth, toolbarY + toolbarHeight);
      // Toolbar buttons
      toolbarButtons = new ArrayList<>();
      int buttonX = 0;
      int buttonY = 0;
      int buttonSize = toolbarWidth / TOOLBAR_BUTTONS_PER_ROW;
      // Setup the system buttons
      toolbarButtons.add(new ToolbarButton("move", handImg, new Rect(buttonX, buttonY, buttonX+buttonSize, buttonY+buttonSize)));
      buttonX += buttonSize;
      toolbarButtons.add(new ToolbarButton("eraser", eraserImg, new Rect(buttonX, buttonY, buttonX+buttonSize, buttonY+buttonSize)));
      buttonX += buttonSize;
      for (TileType tileType : bg.getTileTypes()) {
        Bitmap img = tileTypes.get(tileType.getName());
        toolbarButtons.add(new ToolbarButton(tileType.getName(), img, new Rect(buttonX, buttonY, buttonX+buttonSize, buttonY+buttonSize)));
        buttonX += buttonSize;
        if (buttonX >= toolbarWidth) {
          buttonX = 0;
          buttonY += buttonSize +1;
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

  /**
   * Draws the background onto the bgImg through bgCanvas from the bg data set
   */
  private void drawBackground() {
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
  }

  private Bitmap loadBitmap(InputStream inputStream, AssetManager assetManager, String filename) throws IOException {
    inputStream = assetManager.open(filename);
    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
    inputStream.close();
    return bitmap;
  }

  /**
   * Main draw method
   */
  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    canvas.drawRGB(0, 0, 0);
    drawMap(canvas);
    drawToolbar(canvas);
  }

  /**
   * Draws the map on the canvas
   */
  private void drawMap(Canvas canvas) {
    int viewSize = Math.round(viewSizeX * mapScaleFactor);
    int left = mapX + Math.round(gestureDiffX * mapScaleFactor + scaleDiffX);
    int top  = mapY + Math.round(gestureDiffY * mapScaleFactor + scaleDiffY);
    int right = mapX + viewSize + Math.round(gestureDiffX * mapScaleFactor + scaleDiffX);
    int bottom = mapY + viewSize + Math.round(gestureDiffY * mapScaleFactor + scaleDiffY);
    panZoomWindow.set(left, top, right, bottom);
    canvas.drawBitmap(backgroundImg, panZoomWindow, uiWindow, null);
  }

  /**
   * Draws the toolbar on the canvas
   */
  private void drawToolbar(Canvas canvas) {
    toolbarCanvas.drawRGB(200, 200, 150);
    for (int i=0; i<toolbarButtons.size(); i++) {
      ToolbarButton button = toolbarButtons.get(i);
      toolbarCanvas.drawBitmap(button.getImg(), null, button.getPosition(), null);
      if (i == toolbarButtonSelectedIndex) {
        toolbarCanvas.drawBitmap(toolbarSelectedImg, null, button.getPosition(), null);
      }
    }
    canvas.drawBitmap(toolbarImg, null, toolbarWindow, null);
  }


  /**
   * Master touch event handler for all modes
   * @param event touch event
   * @return true if the event was handled
   */
  @Override
  public boolean onTouchEvent(MotionEvent event) {
    if (mode == Mode.MOVE) {
      return moveTouchEvent(event);
    }
    return editTouchEvent(event);
  }

  /**
   * Handles touch events when in MOVE mode
   * @param event finger event to be handled
   * @return true if the event is handled false otherwise
   */
  public boolean moveTouchEvent(MotionEvent event) {
    boolean handledEvent = false;
    handledEvent = mapScaleDetector.onTouchEvent(event);
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
      handledEvent = true;
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
        handledEvent = true;
      }

    } else if (action == MotionEvent.ACTION_POINTER_DOWN) {
      Log.d("event", "ACTION_POINTER_DOWN pointerId="+pointerId);

    } else if (action == MotionEvent.ACTION_POINTER_UP) {
      Log.d("event", "ACTION_POINTER_UP pointerId="+pointerId);
      // A finger went up, probably ending a two figure gesture (zooming)
      endGesture();
      handledEvent = true;
      // Last finger went up
    } else if (action == MotionEvent.ACTION_UP) {
      Log.d("event", "ACTION_UP pointerId="+pointerId);
      endGesture();
      toolbarDetector(newX, newY);
      handledEvent = true;
    }

    if (handledEvent) {
      this.postInvalidate();
    }
    return handledEvent;
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

  /**
   * Handles touch events when in EDIT mode
   * @param event finger events
   * @return true if the event is handled
   */
  public boolean editTouchEvent(MotionEvent event) {
    boolean handledEvent = false;
    int action = MotionEventCompat.getActionMasked(event);
    int index = MotionEventCompat.getActionIndex(event);
    float newX = event.getX(index);
    float newY = event.getY(index);
    if (action == MotionEvent.ACTION_UP) {
      Log.d("edit", "edit event action up");
      handledEvent = toolbarDetector(newX, newY);
    }
    if (!handledEvent) {
      handledEvent = drawTile(newX, newY);
    }
    if (handledEvent) {
      this.postInvalidate();
    }
    return handledEvent;
  }

  /**
   * Detects and handles toolbar events
   */
  private boolean toolbarDetector(float x, float y) {
    boolean handledEvent = false;
    if ((x > toolbarX) && (x < toolbarX + toolbarWidth) &&
        (y > toolbarY) && (y < toolbarY + toolbarHeight)) {
      // Which button was clicked
      int buttonXIndex =(int)((x - toolbarX) / (toolbarWidth / TOOLBAR_BUTTONS_PER_ROW));
      int buttonYIndex = (int)((y - toolbarY) / (toolbarWidth / TOOLBAR_BUTTONS_PER_ROW));
      int buttonIndex = buttonYIndex * TOOLBAR_BUTTONS_PER_ROW + buttonXIndex;
      if (buttonIndex < toolbarButtons.size()) {
        toolbarButtonSelectedIndex = buttonIndex;
        toolbarButtonSelectedName = toolbarButtons.get(buttonIndex).getName();
        if (buttonIndex == 0) {
          mode = Mode.MOVE;
        } else if (buttonIndex == 1) {
          mode = Mode.ERASE;
        } else {
          mode = Mode.DRAW;
        }
        //Log.d("toolbar", "toolbarButtonSelected="+toolbarButtonSelectedIndex);
        handledEvent = true;
      }
    }
    return handledEvent;
  }

  private boolean drawTile(float x, float y) {
    boolean handledEvent = false;
    // If the event is on the map
    if ((x < viewSizeX) && (y < viewSizeY)) {
      // Find the axial row, col coordinates from the screen x,y
      int col = (int)((mapX + x + bgCenterX) * 4.0f / (TILE_SIZE * 3.0f));
      int row =  (int)((2.0f * (mapY + y - bgCenterY) / -TILE_SIZE) + col) / -2;
      // TODO!  The col/row is wrong, because it's not calcuating the map zoom and pan into effect
      Log.d("drawTile", "x="+x+" y="+y+" col="+col+" row="+row);
      bg.getTiles().add(new BackgroundTile(col, row, toolbarButtonSelectedName));
      drawBackground(); // this will refresh the background image (kind of costly)
      handledEvent = true;
    }
    return handledEvent;
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

