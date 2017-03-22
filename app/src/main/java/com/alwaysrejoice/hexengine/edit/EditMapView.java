package com.alwaysrejoice.hexengine.edit;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
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

import com.alwaysrejoice.hexengine.dto.BgMap;
import com.alwaysrejoice.hexengine.dto.BgTile;
import com.alwaysrejoice.hexengine.dto.Game;
import com.alwaysrejoice.hexengine.dto.SystemTile;
import com.alwaysrejoice.hexengine.util.GameUtils;

public class EditMapView extends View {
  Context context;

  // Game drawing variables
  public static final int TILE_WIDTH = 52; // width of one tile on the map
  public static final int TILE_HEIGHT = 60; // height of one tile on the map
  public static final int HEX_SIZE = 30; // Length of one side of a hexagon
  public static final float SQRT_3 = (float) Math.sqrt(3);
  private Game game; // Data loaded from the user
  private Bitmap backgroundImg;
  private Canvas bgCanvas;
  private int viewSizeX = 200; // size of viewport on the screen
  private int viewSizeY = 700;
  private int backgroundSizeX = 500; // size of background map
  private int backgroundSizeY = 500;
  private int bgCenterX;
  private int bgCenterY;
  // Current location of view in the background
  private int mapX = 100;
  private int mapY = 100;
  private Toolbar toolbar; // the toolbar with all the buttons

  // Drawing
  private Rect panZoomWindow = new Rect(0, 0, 10, 10);
  private Rect uiWindow = null;

  // Panning
  private float gestureStartX = 0f; // Screen location gesture started
  private float gestureStartY = 0f;
  private int gestureStartPointerId = -1; // unique identifier for the finger
  private float gestureDiffX = 0; // distance from gestureStartX to current location
  private float gestureDiffY = 0;

  // Zooming (scaling)
  private static final float MIN_SCALE = 0.10f;
  private static final float MAX_SCALE = 3.0f;
  private ScaleGestureDetector mapScaleDetector;
  private float mapScaleFactor = 0.5f;
  private float scaleDiffX = 0;
  private float scaleDiffY = 0;


  public EditMapView(Context context) {
    super(context);
    this.context = context;
    mapScaleDetector = new ScaleGestureDetector(context, new MapScaleListener());
    Log.d("init", "Starting");
    DisplayMetrics displayMetrics = new DisplayMetrics();
    ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
    game = GameUtils.getGame();
    SystemTile.init(context.getAssets());
    Log.d("init", "loaded width="+ game.getGameInfo().getWidth()+" height="+ game.getGameInfo().getHeight());

    // Make a background map
    backgroundSizeX = Math.round(game.getGameInfo().getWidth() * TILE_WIDTH * 0.75f);
    backgroundSizeY = game.getGameInfo().getHeight() * TILE_HEIGHT;
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
    Log.i("init", "edit viewSizeX="+viewSizeX+" viewSizeY="+viewSizeY);
    uiWindow = new Rect(0, 0, viewSizeX, viewSizeY);
    mapScaleFactor = Math.max(MIN_SCALE, Math.min(((float)backgroundSizeX / (float)viewSizeX), MAX_SCALE));
    mapX = (backgroundSizeX / 2) - Math.round(viewSizeX * mapScaleFactor / 2);
    mapY = (backgroundSizeY / 2) - Math.round(viewSizeY * mapScaleFactor / 2);

    // Create the toolbar
    toolbar = new Toolbar(this, viewSizeX, viewSizeY, displayMetrics.widthPixels, displayMetrics.heightPixels);
  }

  /**
   * Draws the background onto the bgImg through bgCanvas from the game data set
   */
  private void drawBackground() {
    bgCanvas.drawRGB(game.getGameInfo().getBackgroundColor().getRed(),
                     game.getGameInfo().getBackgroundColor().getGreen(),
                     game.getGameInfo().getBackgroundColor().getBlue());
    // Draw border
    Paint paint = new Paint();
    paint.setStrokeWidth(5);
    paint.setColor(Color.WHITE);
    paint.setStyle(Paint.Style.STROKE);
    bgCanvas.drawRect(0, 0, backgroundSizeX, backgroundSizeY, paint);

    // Draw all the background
    for (BgMap tile : game.getBgMaps()) {
      int x = bgCenterX + Math.round(HEX_SIZE * 1.5f  * tile.getCol()) - (TILE_WIDTH / 2);
      int y = bgCenterY + Math.round(HEX_SIZE * SQRT_3 * (tile.getRow() + (tile.getCol() / 2f))) - (TILE_HEIGHT/2);
      BgTile bgTile = game.getBgTiles().get(tile.getName());
      if (bgTile != null) {
        Bitmap bitmap = bgTile.getImg().getBitmap();
        if (bitmap != null) {
          bgCanvas.drawBitmap(bitmap, x, y, null);
        } else Log.e("editMapView", "Error in drawBackground, no image for BgTile with name="+bgTile.getName());
      } else Log.e("editMapView", "Error in drawBackground, no tile in BgTiles with name="+tile.getName());
    }

    // TODO : Draw the units
  }


  /**
   * Main draw method
   */
  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    canvas.drawRGB(0, 0, 0);
    drawMap(canvas);
    toolbar.drawToolbar(canvas);
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
   * Master touch event handler for all modes
   * @param event touch event
   * @return true if the event was handled
   */
  @Override
  public boolean onTouchEvent(MotionEvent event) {
    if (toolbar.getMode() == Toolbar.Mode.MOVE) {
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
      toolbar.toolbarDetector(newX, newY);
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
    int pointerId = event.getPointerId(index);
    float newX = event.getX(index);
    float newY = event.getY(index);

    if (action == MotionEvent.ACTION_DOWN) {
      handledEvent = toolbar.toolbarDetector(newX, newY);
      Log.d("editTouch", "edit clicked on "+newX+","+newY+" handledEvent="+handledEvent);
      if (!handledEvent && (gestureStartPointerId == -1)) {
        //Log.d("editTouch", "start gesture pointer=" + pointerId);
        gestureStartPointerId = pointerId;
      }
    } else if (action == MotionEvent.ACTION_UP) {
      //Log.d("editTouch", "end gesture pointer="+pointerId);
      gestureStartPointerId = -1;
    }

    // Only draw tiles if we are in a gesture
    if (gestureStartPointerId != -1) {
      if (!handledEvent) {
        //Log.d("editTouch", "gesture started, drawing tile");
        handledEvent = drawTile(newX, newY);
      }
    }

    if (handledEvent) {
      this.postInvalidate();
    }
    return handledEvent;
  }


  /**
   * Draws a tile given a click at the specified location
   * @param x,y : The UI click location
   * @return true if the tile was drawn
   */
  private boolean drawTile(float x, float y) {
    boolean handledEvent = false;
    // If the event is on the map
    if ((x < viewSizeX) && (y < viewSizeY)) {
      int bgX = mapX + Math.round(x * mapScaleFactor) - bgCenterX;
      int bgY = mapY + Math.round(y * mapScaleFactor) - bgCenterY;
      // Find the axial row, col coordinates from the screen x,y
      int col = Math.round(bgX * (2f/3f) / HEX_SIZE);
      int row = Math.round((-bgX / 3f + (float)Math.sqrt(3f)/3f * bgY) / HEX_SIZE) ;
      //Log.d("drawTile", "x="+x+" y="+y+" bgCenterX="+bgCenterX+" bgCenterY="+bgCenterY+" bgX="+bgX+" bgY="+bgY+" col="+col+" row="+row);
      // Remove any tiles that exist at that location already
      for (int i = game.getBgMaps().size()-1; i>=0; i--) {
        BgMap bgMap = game.getBgMaps().get(i);
        if ((bgMap.getCol() == col) && (bgMap.getRow() == row)) {
          game.getBgMaps().remove(i);
        }
      } // for
      if (toolbar.getMode() == Toolbar.Mode.DRAW) {
        game.getBgMaps().add(new BgMap(col, row, toolbar.getToolbarButtonSelectedName()));
      }
      // TODO : Add units if in Unit adding mode
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

  public Context getContex() {
    return context;
  }

}

