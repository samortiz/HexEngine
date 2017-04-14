package com.alwaysrejoice.hexengine.play;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v4.view.MotionEventCompat;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import com.alwaysrejoice.hexengine.dto.Ability;
import com.alwaysrejoice.hexengine.dto.BgMap;
import com.alwaysrejoice.hexengine.dto.BgTile;
import com.alwaysrejoice.hexengine.dto.Position;
import com.alwaysrejoice.hexengine.dto.SystemTile;
import com.alwaysrejoice.hexengine.dto.Unit;
import com.alwaysrejoice.hexengine.dto.World;
import com.alwaysrejoice.hexengine.util.GameUtils;
import com.alwaysrejoice.hexengine.util.Utils;

import java.util.List;

import static android.R.attr.centerX;

public class WorldView extends View {
  Context context;

  // Game drawing variables
  public static final int TILE_WIDTH = 60; // height of one tile on the map
  public static final int TILE_HEIGHT = 52; // width of one tile on the map
  public static final int HEX_SIZE = 30; // Length of one side of a hexagon
  public static final float SQRT_3 = (float) Math.sqrt(3);
  private World world;
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

  // Drawing
  private Rect panZoomWindow = new Rect(0, 0, 10, 10);
  private Rect uiWindow = null;

  // Clicks
  private float clickPointerId = -1;
  private float clickX = 0f;
  private float clickY = 0f;
  private static final float MAX_CLICK_DISTANCE = 50;

  // Panning
  private float gestureStartX = 0f; // Screen location gesture started
  private float gestureStartY = 0f;
  private int gestureStartPointerId = -1; // unique identifier for the finger
  private float gestureDiffX = 0; // distance from gestureStartX to current location
  private float gestureDiffY = 0;

  // Zooming (scaling)
  private static final float GLOBAL_MIN_SCALE = 0.10f;
  private static final float GLOBAL_MAX_SCALE = 3.0f;
  private float maxScale = GLOBAL_MAX_SCALE;
  private ScaleGestureDetector mapScaleDetector;
  private float mapScaleFactor = 0.5f;
  private float scaleDiffX = 0;
  private float scaleDiffY = 0;

  // Drawing the info area
  public static final int INFO_TOP_PADDING = 10;
  public static final int INFO_LEFT_COL_PADDING = 10;
  public static final int INFO_IMG_PADDING = 10;
  public static final int INFO_IMG_MAX_HEIGHT = 100;
  public static final int FONT_SIZE_SP = 16;
  private Rect infoWindow = null;
  private Bitmap infoImg;
  private Canvas infoCanvas;

  // Moving
  private Unit selectedUnit = null;
  Bitmap moveImg;


  public WorldView(Context context, World world) {
    super(context);
    this.world = world;
    this.context = context;
    mapScaleDetector = new ScaleGestureDetector(context, new MapScaleListener());
    Log.d("WorldView", "Setting up world");
    DisplayMetrics displayMetrics = new DisplayMetrics();
    ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

    SystemTile.init(context.getAssets());
    moveImg = SystemTile.getTile(SystemTile.NAME.MOVE).getBitmap();

    // Make a background map
    backgroundSizeY = GameUtils.getBackgroundSize(world.getGameInfo().getSize());
    backgroundSizeX = backgroundSizeY; // make a square background map
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

    float maxScaleX = (float)backgroundSizeX / (float)viewSizeX;
    float maxScaleY = (float)backgroundSizeY / (float)viewSizeY;
    maxScale = Math.min(Math.min(maxScaleX, maxScaleY), GLOBAL_MAX_SCALE);
    mapScaleFactor = maxScale;
    mapX = (backgroundSizeX / 2) - Math.round(viewSizeX * mapScaleFactor / 2);
    mapY = (backgroundSizeY / 2) - Math.round(viewSizeY * mapScaleFactor / 2);

    // Setup the info area
    int infoHeight = displayMetrics.heightPixels - viewSizeY;
    int infoWidth = displayMetrics.widthPixels;
    int infoX = 0;
    int infoY = viewSizeY;
    infoWindow = new Rect(infoX, infoY, infoX+infoWidth, infoY+infoHeight);
    infoImg = Bitmap.createBitmap(infoWidth, infoHeight, Bitmap.Config.ARGB_8888);
    infoCanvas = new Canvas(infoImg);

    // Select the first Player unit
    for (Unit unit : world.getUnits()) {
      if ("Player".equals(unit.getTeam())) {
        drawSelectedUnitInfo(unit);
        break;
      }
    } // for

  }

  /**
   * Draws the background onto the bgImg through bgCanvas from the game data set
   */
  private void drawBackground() {
    bgCanvas.drawRGB(world.getGameInfo().getBackgroundColor().getRed(),
        world.getGameInfo().getBackgroundColor().getGreen(),
        world.getGameInfo().getBackgroundColor().getBlue());
    // Draw border
    Paint paint = new Paint();
    paint.setStrokeWidth(1);
    paint.setColor(Color.BLACK);
    paint.setStyle(Paint.Style.STROKE);
    bgCanvas.drawRect(0, 0, backgroundSizeX, backgroundSizeY, paint);

    // Draw all the background
    for (BgMap tile : world.getBgMaps()) {
      int x = bgCenterX + Math.round(HEX_SIZE * 1.5f  * tile.getCol()) - (TILE_WIDTH / 2);
      int y = bgCenterY + Math.round(HEX_SIZE * SQRT_3 * (tile.getRow() + (tile.getCol() / 2f))) - (TILE_HEIGHT/2);
      BgTile bgTile = world.getBgTiles().get(tile.getBgTileId());
      if (bgTile != null) {
        Bitmap bitmap = bgTile.getBitmap();
        if (bitmap != null) {
          bgCanvas.drawBitmap(bitmap, x, y, null);
        } else Log.e("WorldView", "Error in drawBackground, no image for BgTile with id="+bgTile.getId());
      } else Log.e("WorldView", "Error in drawBackground, no tile in BgTiles with id="+tile.getBgTileId());
    } // for

    for (Unit unit : world.getUnits()) {
      Position pos = unit.getPos();
      int x = bgCenterX + Math.round(HEX_SIZE * 1.5f  * pos.getCol()) - (TILE_WIDTH / 2);
      int y = bgCenterY + Math.round(HEX_SIZE * SQRT_3 * (pos.getRow() + (pos.getCol() / 2f))) - (TILE_HEIGHT/2);
      Bitmap bitmap = unit.getBitmap();
      if (bitmap != null) {
        bgCanvas.drawBitmap(bitmap, x, y, null);
      } else Log.e("WorldView", "Error in drawBackground, no image for unit with id="+unit.getId());
    } // for
  }


  /**
   * Main draw method
   */
  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    canvas.drawRGB(0, 0, 0);
    drawMap(canvas);
    canvas.drawBitmap(infoImg, null, infoWindow, null);
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
    boolean handledEvent = false;
    handledEvent = mapScaleDetector.onTouchEvent(event);
    int action = MotionEventCompat.getActionMasked(event);
    int index = MotionEventCompat.getActionIndex(event);
    int pointerId = event.getPointerId(index);
    float newX = event.getX(index);
    float newY = event.getY(index);

    // First time, get the initial finger (can happen at other times beside ACTION_DOWN)
    // we start a new gesture on action_down, or on the second finger going up, which ends the
    // two finger gesture ands starts a single finger gesture.
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
      clickPointerId = pointerId;
      clickX = newX;
      clickY = newY;

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
        if (Math.sqrt(diffX * diffX + diffY * diffY) > MAX_CLICK_DISTANCE) {
          clickPointerId = -1;
        }
      }

    } else if (action == MotionEvent.ACTION_POINTER_DOWN) {
      Log.d("event", "ACTION_POINTER_DOWN pointerId="+pointerId);
      // putting a second finger down kills the click
      clickPointerId = -1;

    } else if (action == MotionEvent.ACTION_POINTER_UP) {
      Log.d("event", "ACTION_POINTER_UP pointerId="+pointerId);
      // A finger went up, probably ending a two figure gesture (zooming)
      endGesture();
      handledEvent = true;

    } else if (action == MotionEvent.ACTION_UP) {
      // Last finger went up
      Log.d("event", "ACTION_UP pointerId="+pointerId);
      endGesture();
      clickListener(newX, newY, pointerId);
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
   * Calculates if a click happened
   *  - The distance from start to end wasn't too far
   *  - No other fingers were doing funny stuff
   */
  private void clickListener(float x, float y, int pointerId) {
    if (pointerId == clickPointerId) {
      float dX = clickX - x;
      float dY = clickY - y;
      if (Math.sqrt(dX * dX + dY * dY) <= MAX_CLICK_DISTANCE) {
        Log.d("WorldView", "click dX=" + dX + " dY=" + dY + " pointerId=" + pointerId);
        if ((x < viewSizeX) && (y < viewSizeY)) {
          int bgX = mapX + Math.round(x * mapScaleFactor) - bgCenterX;
          int bgY = mapY + Math.round(y * mapScaleFactor) - bgCenterY;
          // Find the axial row, col coordinates from the screen x,y
          int col = Math.round(bgX * (2f / 3f) / HEX_SIZE);
          int row = Math.round((-bgX / 3f + (float) Math.sqrt(3f) / 3f * bgY) / HEX_SIZE);
          selectUnit(col, row);
        } //else Log.d("WorldView", "no click dX="+dX+" dY="+dY+" pointerId="+pointerId);
      } //else Log.d("WorldView", "not a click");
    }
  }

  /**
   * This handles scale events (pinching zoom)
   */
  private class MapScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
    @Override
    public boolean onScale(ScaleGestureDetector detector) {
      float newScaleFactor = mapScaleFactor * (1f - (detector.getScaleFactor() - 1f));
      newScaleFactor = Math.max(GLOBAL_MIN_SCALE, Math.min(newScaleFactor, maxScale));
      // Pan while zooming to maintain the relative position on the screen of the center of the gesture
      float relativeX = detector.getFocusX() / viewSizeX; //  percent of view port
      float relativeY = detector.getFocusY() / viewSizeY;
      float diffX = ((viewSizeX * mapScaleFactor) * relativeX) - ((viewSizeX * newScaleFactor) * relativeX);
      float diffY = ((viewSizeY * mapScaleFactor) * relativeY) - ((viewSizeY * newScaleFactor) * relativeY);
      scaleDiffX += diffX;
      scaleDiffY += diffY;
      //Log.d("Scale", "Scaling old="+mapScaleFactor+ " new="+newScaleFactor);
      mapScaleFactor = newScaleFactor;
      //Log.d("scale", "mapScaleFactor="+newScaleFactor);
      return true;
    }
  }

  /**
   * Selects the unit at the specific col, row
   */
  private void selectUnit(int col, int row) {
    for (Unit unit : world.getUnits()) {
      if (unit.getPos().equals(row, col)) {
        drawBackground(); // in case there is other selected bits already drawn
        this.selectedUnit = unit;
        drawSelectedUnitInfo(unit);
        setupUnitMove(unit);
        break;
      }
    } // for
  }

  /**
   * Draws the selected unit info in the info area at the bottom of the screen
   */
  private void drawSelectedUnitInfo(Unit unit) {
    Log.d("WorldView", "drawing unit "+unit);
    infoCanvas.drawRGB(240, 240, 200);

    // Draw border
    Paint paint = new Paint();
    paint.setStrokeWidth(5);
    paint.setColor(Color.GREEN);
    paint.setStyle(Paint.Style.STROKE);
    infoCanvas.drawRect(infoWindow.left, infoWindow.top, infoWindow.right, infoWindow.bottom, paint);

    int centerX = infoWindow.width() / 2;
    int rowY = INFO_TOP_PADDING;
    // Number of images showing up in the left panel
    int imgCount = 1 + unit.getAbilities().size() + unit.getEffects().size();
    int imgHeight = Math.min((infoWindow.height() / imgCount), INFO_IMG_MAX_HEIGHT);
    int imgWidth = Math.round(((float)imgHeight * 2.0f) / SQRT_3);

    Log.d("WorldView", "Drawing imgHeight="+imgHeight+" imgWidth="+imgWidth);

    drawLeftColImgAndText(unit.getBitmap(), imgWidth, imgHeight, unit.getName(), rowY);

    for (Ability ability : unit.getAbilities()) {
      rowY += imgHeight + INFO_IMG_PADDING;
      drawLeftColImgAndText(ability.getBitmap(), imgWidth, imgHeight, ability.getName(), rowY);
    }

    rowY = INFO_TOP_PADDING;
    StringBuffer infoText = new StringBuffer();
    infoText.append("Team: "+unit.getTeam()+"\n");
    infoText.append("HP: "+ Utils.decimalFormat.format(unit.getHp())+" / "+Utils.decimalFormat.format(unit.getHpMax())+"\n");
    infoText.append("Action: "+ Utils.decimalFormat.format(unit.getAction())+" / "+Utils.decimalFormat.format(unit.getActionMax())+"\n");
    infoText.append("Attr: "+Utils.toCsv(unit.getAttr())+"\n");
    infoText.append("Move: "+unit.getMoveRange()+getRestrict(unit.getMoveRestrict())+"\n");
    infoText.append("Sight: "+unit.getSightRange()+getRestrict(unit.getSightRestrict())+"\n");
    infoText.append("Defence: "+GameUtils.damagesToString(unit.getDefence())+"\n");
    drawInfoText(infoText.toString(), centerX, rowY, centerX);
  }

  /**
   * Draws an image and text in the left column of the info area
   * @param img  Icon of image to draw
   * @param imgWidth
   * @param imgHeight
   * @param text text to layout
   * @param y Y offset
   */
  private void drawLeftColImgAndText(Bitmap img, int imgWidth, int imgHeight, String text, int y) {
    infoCanvas.drawBitmap(img, null, new Rect(INFO_LEFT_COL_PADDING, y, INFO_LEFT_COL_PADDING+imgWidth, y+imgHeight), null);
    int textYOffset = (imgHeight - getTextHeight(text, (centerX - imgWidth - INFO_IMG_PADDING))) / 2;
    drawInfoText(text, INFO_LEFT_COL_PADDING + imgWidth + INFO_IMG_PADDING,
                 y + textYOffset, (centerX - imgWidth - INFO_IMG_PADDING));
  }

  /**
   * Draw text at x,y on the infoCanvas
   * @param maxWidth after this width the text will wrap
   */
  private void drawInfoText(String text, int x, int y, int maxWidth) {
    StaticLayout staticLayout = getLayout(text, maxWidth);
    infoCanvas.save();
    infoCanvas.translate(x, y);
    staticLayout.draw(infoCanvas);
    infoCanvas.restore();
    //Log.d("WorldView", "Drawing "+text+" at x="+x+" y="+y);
  }

  /**
   * @return the height of the text given a maxWidth after which it will wrap
   */
  private int getTextHeight(String text, int maxWidth) {
    StaticLayout layout = getLayout(text, maxWidth);
    return layout.getHeight();
  }

  /**
   * Setup a layout for drawing text on a canvas
   * @param maxWidth  max allowed width
   */
  private StaticLayout getLayout(String text, int maxWidth) {
    TextPaint textPaint = new TextPaint();
    textPaint.setAntiAlias(true);
    textPaint.setTextSize(FONT_SIZE_SP * getResources().getDisplayMetrics().density);
    textPaint.setColor(Color.BLACK);
    int textWidth = (int) textPaint.measureText(text);
    StaticLayout layout = new StaticLayout(text, textPaint, textWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0, false);
    return layout;
  }

  /**
   * Draws restriction list
   */
  private String getRestrict(List<String> restrict) {
    if ((restrict == null) || (restrict.size() == 0)) {
      return "";
    }
    return " not "+Utils.toCsv(restrict);
  }

  /**
   * Sets up the unit for moving (showing where the unit can move to)
   */
  private void setupUnitMove(Unit unit) {
    Log.d("WorldView", "Moving unit at "+ unit.getPos());
    List<Position> movePositions = GameUtils.getValidPositions(unit.getPos(), unit.getMoveRange(), unit.getMoveRestrict(), world);
    movePositions.remove(unit.getPos()); // can't move where you already are
    for (Position pos : movePositions) {
      //Log.d("WorldView", "Can move to "+pos);
      int x = bgCenterX + Math.round(HEX_SIZE * 1.5f  * pos.getCol()) - (TILE_WIDTH / 2);
      int y = bgCenterY + Math.round(HEX_SIZE * SQRT_3 * (pos.getRow() + (pos.getCol() / 2f))) - (TILE_HEIGHT/2);
      bgCanvas.drawBitmap(moveImg, x, y, null);
    }
  }

}

