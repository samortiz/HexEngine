package com.alwaysrejoice.hexengine.play;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.v4.view.MotionEventCompat;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import com.alwaysrejoice.hexengine.dto.AI;
import com.alwaysrejoice.hexengine.dto.Ability;
import com.alwaysrejoice.hexengine.dto.Action;
import com.alwaysrejoice.hexengine.dto.BgMap;
import com.alwaysrejoice.hexengine.dto.BgTile;
import com.alwaysrejoice.hexengine.dto.Effect;
import com.alwaysrejoice.hexengine.dto.Mod;
import com.alwaysrejoice.hexengine.dto.Position;
import com.alwaysrejoice.hexengine.dto.SystemTile;
import com.alwaysrejoice.hexengine.dto.Team;
import com.alwaysrejoice.hexengine.dto.Unit;
import com.alwaysrejoice.hexengine.dto.World;
import com.alwaysrejoice.hexengine.util.GameUtils;
import com.alwaysrejoice.hexengine.util.ScriptEngine;
import com.alwaysrejoice.hexengine.util.Utils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorldView extends View {
  WorldActivity worldActivity;

  // Game drawing variables
  public static final int TILE_WIDTH = 60; // height of one tile on the map
  public static final int TILE_HEIGHT = 52; // width of one tile on the map
  public static final int HEX_SIZE = 30; // Length of one side of a hexagon
  public static final float SQRT_3 = (float) Math.sqrt(3);
  private World world;
  private int screenWidth;
  private int screenHeight;
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
  public static final int INFO_IMG_PADDING = 5;
  public static final int INFO_IMG_MAX_HEIGHT = 120;
  public static final int INFO_IMG_MAX_WIDTH = Math.round(((float)INFO_IMG_MAX_HEIGHT * 2.0f) / SQRT_3);
  public static final int INFO_FONT_SIZE_SP = 16;
  public static final int INFO_EFFECT_SIZE = 60;
  public static final int INFO_EFFECT_PADDING = 10;
  private Rect infoWindow = null;
  private Bitmap infoImg;
  private Canvas infoCanvas;
  private Bitmap exitButtonImg;
  private Rect exitButtonRect;
  private Bitmap saveButtonImg;
  private Rect saveButtonRect;
  private Bitmap endTurnButtonImg;
  private Rect endTurnButtonRect;
  TextPaint infoTextPaint;

  // End Game
  public static final int VICTORY_FONT_SIZE_SP = 200;
  TextPaint victoryTextPaint;
  boolean endGameTextDrawn = false; // When true, the next click exits the game

  // Moving
  private Unit selectedUnit = null; // currently selected unit (showing in info area)
  private List<Position> validMoves = null; // the green dots
  private Bitmap moveImg;

  // Abilities
  private Map<Position, Ability> selectedAbilities; // highlight an ability on the map
  private Map<Rect, Ability> abilityButtons = new HashMap<>(); // click on an ability in the info area
  private ScriptEngine scriptEngine;
  private Paint whiteHighlightPaint = new Paint(); // color of circle behind abilities on the map

  // AI
  List<String> myTeamIds; // contains all the teamIds that are human controlled (usually one)


  public WorldView(WorldActivity worldActivity, World world) {
    super(worldActivity);
    this.worldActivity = worldActivity;
    this.world = world;
    mapScaleDetector = new ScaleGestureDetector(worldActivity, new MapScaleListener());
    Log.d("WorldView", "Setting up world");

    // Get the screen height and width (without status bar and buttons)
    Display display = worldActivity.getWindowManager().getDefaultDisplay();
    DisplayMetrics displayMetrics = new DisplayMetrics();
    display.getMetrics(displayMetrics);
    screenWidth = displayMetrics.widthPixels;
    screenHeight = displayMetrics.heightPixels;
    int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
    if (resourceId > 0) {
      int statusBarHeight = getResources().getDimensionPixelSize(resourceId);
      screenHeight -= statusBarHeight;
    }

    // Setup some paint styles
    whiteHighlightPaint.setColor(Color.parseColor("#90FFFFFF")); // 90/FF transparent
    whiteHighlightPaint.setStyle(Paint.Style.FILL);
    infoTextPaint = new TextPaint();
    infoTextPaint.setAntiAlias(true);
    infoTextPaint.setTextSize(getFontHeightPx(INFO_FONT_SIZE_SP));
    infoTextPaint.setColor(Color.BLACK);
    victoryTextPaint = new TextPaint();
    victoryTextPaint.setAntiAlias(true);
    victoryTextPaint.setTextSize(VICTORY_FONT_SIZE_SP);
    victoryTextPaint.setColor(Color.BLACK);
    victoryTextPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

    // Load the system assets
    SystemTile.init(worldActivity.getAssets());
    // Green dot for moving
    moveImg = SystemTile.getTile(SystemTile.NAME.MOVE_DOT).getBitmap();
    // Buttons at the bottom of the info area
    exitButtonImg = SystemTile.getTile(SystemTile.NAME.EXIT).getBitmap();
    saveButtonImg = SystemTile.getTile(SystemTile.NAME.SAVE).getBitmap();
    endTurnButtonImg = SystemTile.getTile(SystemTile.NAME.LOOP).getBitmap();

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
    viewSizeX = Math.min(screenWidth, screenHeight);
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
    int infoHeight = screenHeight - viewSizeY;
    int infoWidth = screenWidth;
    int infoX = 0;
    int infoY = viewSizeY;
    infoWindow = new Rect(infoX, infoY, infoX+infoWidth, infoY+infoHeight);
    infoImg = Bitmap.createBitmap(infoWidth, infoHeight, Bitmap.Config.ARGB_8888);
    infoCanvas = new Canvas(infoImg);

    Log.d("WorldView", "height="+screenHeight+" infoHeight="+infoHeight+" INFO_IMG_MAX_HEIGHT="+INFO_IMG_MAX_HEIGHT);

    // Position the buttons on the infoCanvas
    int buttonX = INFO_LEFT_COL_PADDING;
    int buttonY = infoHeight - INFO_IMG_MAX_HEIGHT;
    exitButtonRect = new Rect(buttonX, buttonY, buttonX+INFO_IMG_MAX_WIDTH, buttonY+INFO_IMG_MAX_HEIGHT);
    buttonX += INFO_IMG_MAX_WIDTH + INFO_IMG_PADDING;
    saveButtonRect = new Rect(buttonX, buttonY, buttonX+INFO_IMG_MAX_WIDTH, buttonY+INFO_IMG_MAX_HEIGHT);
    buttonX += INFO_IMG_MAX_WIDTH + INFO_IMG_PADDING;
    endTurnButtonRect = new Rect(buttonX, buttonY, buttonX+INFO_IMG_MAX_WIDTH, buttonY+INFO_IMG_MAX_HEIGHT);

    // Setup a list of all the teams that are human controlled
    // This is indicateds by having an AI with no data in it
    myTeamIds = new ArrayList<>();
    for (Team team : world.getTeams()) {
      AI ai = GameUtils.getAiById(team.getAiId());
      if ((ai != null) && (ai.getScript() != null) && (ai.getScript().length() == 0)) {
        myTeamIds.add(team.getId());
      }
    } // for

    // Select the first Player unit
    for (Unit unit : world.getUnits()) {
      if (myTeamIds.contains(unit.getTeamId())) {
        this.selectedUnit = unit;
        drawSelectedUnitInfo();
        break;
      }
    } // for

    // Setup the scripting engine (will be shutdown when the activity is destroyed)
    scriptEngine = new ScriptEngine(world);
    if (world.getTurnCounter() == 0) {
      scriptEngine.runActions(world.getTriggers().getStartWorld(), null, null);
    }
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
    drawVictory(canvas);
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
        //Log.Log.d("pan", "right="+right+" gestureDiffX="+gestureDiffX+" diffX="+diffX);
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
        // Check for endGame
        if (endGameTextDrawn) {
          worldActivity.exit();
        }
        // Check for and handle button clicks in the info area
        if (!handleButtonClicks(x, y)) {
          if ((x < viewSizeX) && (y < viewSizeY)) {
            // It was not a button click, so treat it as a unit/bg select
            int bgX = mapX+Math.round(x * mapScaleFactor)-bgCenterX;
            int bgY = mapY+Math.round(y * mapScaleFactor)-bgCenterY;
            // Find the axial row, col coordinates from the screen x,y
            int col = Math.round(bgX * (2f / 3f) / HEX_SIZE);
            int row = Math.round((-bgX / 3f+(float) Math.sqrt(3f) / 3f * bgY) / HEX_SIZE);
            selectLocation(new Position(row, col));
          } // not a button click outside the viewport
        } // not a button click
      } //else Log.Log.d("WorldView", "not a click");
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
      //Log.Log.d("Scale", "Scaling old="+mapScaleFactor+ " new="+newScaleFactor);
      mapScaleFactor = newScaleFactor;
      //Log.Log.d("scale", "mapScaleFactor="+newScaleFactor);
      return true;
    }
  }

  /**
   * Handles and reports a button being clicked in the info area
   *  - System buttons (exit, save, end turn)
   *  - Ability buttons
   * @return true if a button click was detected and handled
   */
  private boolean handleButtonClicks(float floatX, float floatY) {
    int x = (int) floatX;
    // These Rect are in the infoWindow, so convert from screen to infoWindow coordinates
    int y = ((int) floatY) - viewSizeY;

    Log.d("WorldView", "floatX="+floatX+" floatY="+floatY+" y="+y+" exitRect="+exitButtonRect);
    if (exitButtonRect.contains(x,y)) {
      Log.d("WorldView", "Exiting Game");
      worldActivity.exit();
      return true;
    } else if (saveButtonRect.contains(x,y)) {
      GameUtils.saveWorld(world);
      return true;
    } else if (endTurnButtonRect.contains(x,y)) {
      Log.d("WorldView", "End Turn");
      endTurn();
      // TODO AI would go here
      startTurn();
      return true;
    }
    for (Rect rect : abilityButtons.keySet()) {
      if (rect.contains(x,y)) {
        drawBackground(); // in case there are other selected things already drawn
        setupSingleAbility(abilityButtons.get(rect));
      }
    }

    // No button handled the click
    return false;
  }

  /**
   * Selects the unit at the specific col, row
   */
  private void selectLocation(Position pos) {
    if ((selectedAbilities != null) && (selectedUnit != null)) {
      for (Position abilityPos : selectedAbilities.keySet()) {
        if (abilityPos.equals(pos)) {
          // The click was on a highlighted ability
          Ability ability = selectedAbilities.get(abilityPos);
          Unit targetUnit = GameUtils.getUnitAt(pos, world);
          applyAbility(ability, targetUnit);
          clearSelections();
          return;
        }
      } // for
    }
    for (Unit unit : world.getUnits()) {
      if (pos.equals(unit.getPos())) {
        // The click was selecting a unit
        drawBackground(); // in case there are other selected things already drawn
        this.selectedUnit = unit;
        drawSelectedUnitInfo();
        if (selectedUnit.getAction() >= selectedUnit.getMoveActionCost()) {
          setupUnitMove(unit);
        }
        setupSelectedUnitAbilities();
        return; // We handled the click
      }
    } // for
    if (validMoves != null) {
      for (Position movePos : validMoves) {
        if (pos.equals(movePos)) {
          // Clicked on a validMove
          moveSelectedUnit(pos);
          clearSelections();
          return; // we handled the click
        }
      } // for
    }
    // If no unit was found at that location
    for (BgMap bgMap : world.getBgMaps()) {
      if (pos.equals(bgMap.getPos())) {
        // Selected a background tile
        drawBackground(); // in case there are other selected things already drawn
        this.selectedUnit = null;
        drawSelectedBackgroundInfo(world.getBgTiles().get(bgMap.getBgTileId()));
        return;
      }
    } // for
  }

  /**
   * Clears the move and ability variables
   * and redraws the screen.
   * NOTE : this leaves the selected unit, as that will remain selected in the info area
   */
  private void clearSelections() {
    this.selectedAbilities = null;
    this.validMoves = null;
    drawBackground(); // Show the unit at the new location
    if (selectedUnit != null) {
      drawSelectedUnitInfo();
    }
  }

  /**
   * Draws the info area at the bottom for a selected background tile
   */
  private void drawSelectedBackgroundInfo(BgTile bgTile) {
    Log.d("WorldView", "drawing bg "+bgTile);
    infoCanvas.drawRGB(240, 240, 200);

    // Draw border
    Paint paint = new Paint();
    paint.setStrokeWidth(5);
    paint.setColor(Color.GREEN);
    paint.setStyle(Paint.Style.STROKE);
    infoCanvas.drawRect(infoWindow.left, infoWindow.top, infoWindow.right, infoWindow.bottom, paint);

    int centerX = infoWindow.width() / 2;
    int rowY = INFO_TOP_PADDING;
    int imgHeight = INFO_IMG_MAX_HEIGHT;
    int imgWidth = Math.round(((float)imgHeight * 2.0f) / SQRT_3);
    String desc = bgTile.getName()+" ("+bgTile.getType()+")";
    drawLeftColImgAndText(bgTile.getBitmap(), imgWidth, imgHeight, desc, rowY, centerX);
    drawBottomButtons();
  }

  /**
   * Draws the selected unit info in the info area at the bottom of the screen
   */
  private void drawSelectedUnitInfo() {
    if (selectedUnit == null) {
      Log.d("WorldView", "Warning, drawSelectedUnitInfo called with no selectedUnit");
      return;
    }
    Log.d("WorldView", "drawing unit "+selectedUnit);
    abilityButtons.clear(); // will be recalculated in this method
    // Clear the background
    infoCanvas.drawRGB(240, 240, 200);
    int centerX = infoWindow.width() / 2;
    int rowY = INFO_TOP_PADDING;
    int fontHeight = (int)getFontHeightPx(INFO_FONT_SIZE_SP);
    // Draw the left panel
    // Number of images showing up in the left panel
    int imgCount = selectedUnit.getAbilities().size();
    // Calculate the image height, we will make the images smaller if we have too many abilities and effects
    // INFO_IMG_MAX_HEIGHT : 1 for unit image, 1 for system buttons
    int spaceAvailable = infoWindow.height() -INFO_TOP_PADDING -INFO_IMG_MAX_HEIGHT*2 ;
    if (selectedUnit.getEffects().size() > 0) {
      spaceAvailable += -INFO_EFFECT_SIZE -fontHeight -INFO_EFFECT_PADDING;
    }
    // Draw the selected unit name
    drawLeftColImgAndText(selectedUnit.getBitmap(), INFO_IMG_MAX_WIDTH, INFO_IMG_MAX_HEIGHT, selectedUnit.getName(), rowY, centerX);
    // Draw the unit effects
    int effectX = INFO_LEFT_COL_PADDING + 10;
    rowY += INFO_IMG_MAX_HEIGHT;
    for (Effect effect : selectedUnit.getEffects()) {
      infoCanvas.drawBitmap(effect.getBitmap(), null, new Rect(effectX, rowY, effectX+INFO_EFFECT_SIZE, rowY+INFO_EFFECT_SIZE), null);
      drawInfoText(""+effect.getDuration(), effectX+15, rowY+fontHeight+INFO_EFFECT_PADDING, INFO_EFFECT_SIZE+INFO_EFFECT_PADDING);
      effectX += INFO_EFFECT_SIZE + INFO_EFFECT_PADDING;
    }
    if (selectedUnit.getEffects().size() > 0) {
      rowY += INFO_EFFECT_SIZE + fontHeight;
    }
    // Draw all the unit abilities
    int imgHeight = Math.min(((spaceAvailable/imgCount)-INFO_IMG_PADDING), INFO_IMG_MAX_HEIGHT);
    if (imgHeight < fontHeight) {
      Log.e("WorldView", "Too many abilities to fit on the screen!");
      // TODO : Maybe an alternate layout?
    }
    int imgWidth = Math.round(((float)imgHeight * 2.0f) / SQRT_3);
    Log.d("WorldView", "Drawing imgHeight="+imgHeight+" imgCount="+imgCount+" infoHeight="+infoWindow.height()+" spaceAvailable="+spaceAvailable);
    for (Ability ability : selectedUnit.getAbilities()) {
      drawLeftColImgAndText(ability.getBitmap(), imgWidth, imgHeight, ability.getName(), rowY, centerX);
      // Setup the button for this ability
      abilityButtons.put(new Rect(0, rowY, centerX, rowY+imgHeight), ability);
      rowY += imgHeight + INFO_IMG_PADDING;
    }
    // Draw the right column
    rowY = INFO_TOP_PADDING;
    StringBuffer infoText = new StringBuffer();
    infoText.append("Team: "+GameUtils.getTeamNameFromId(selectedUnit.getTeamId())+"\n");
    infoText.append("HP: "+ Utils.decimalFormat.format(selectedUnit.getHp())+" / "+Utils.decimalFormat.format(selectedUnit.getHpMax())+"\n");
    infoText.append("Action: "+ Utils.decimalFormat.format(selectedUnit.getAction())+" / "+Utils.decimalFormat.format(selectedUnit.getActionMax())+"\n");
    infoText.append("Attr: "+Utils.toCsv(selectedUnit.getAttr())+"\n");
    infoText.append("Move: "+selectedUnit.getMoveRange()+getRestrict(selectedUnit.getMoveRestrict())+"\n");
    infoText.append("Sight: "+selectedUnit.getSightRange()+getRestrict(selectedUnit.getSightRestrict())+"\n");
    infoText.append("Defence: "+GameUtils.damagesToString(selectedUnit.getDefence())+"\n");
    drawInfoText(infoText.toString(), centerX, rowY, centerX);

    drawBottomButtons();
  }

  /**
   * Draws an image and text in the left column of the info area
   * @param img  Icon of image to draw
   * @param imgWidth
   * @param imgHeight
   * @param text text to layout
   * @param y Y offset
   */
  private void drawLeftColImgAndText(Bitmap img, int imgWidth, int imgHeight, String text, int y, int centerX) {
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
    //Log.Log.d("WorldView", "Drawing "+text+" at x="+x+" y="+y);
  }

  /**
   * @return the height of the text given a maxWidth after which it will wrap
   */
  private int getTextHeight(String text, int maxWidth) {
    StaticLayout layout = getLayout(text, maxWidth);
    return layout.getHeight();
  }

  private float getFontHeightPx(int fontSizeSp) {
    return fontSizeSp * getResources().getDisplayMetrics().density;
  }

  /**
   * Setup a layout for drawing text on a canvas
   * @param maxWidth  max allowed width
   */
  private StaticLayout getLayout(String text, int maxWidth) {
    int textWidth = (int) infoTextPaint.measureText(text);
    StaticLayout layout = new StaticLayout(text, infoTextPaint, textWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0, false);
    return layout;
  }

  private void drawBottomButtons() {
    infoCanvas.drawBitmap(exitButtonImg, null, exitButtonRect, null);
    infoCanvas.drawBitmap(saveButtonImg, null, saveButtonRect, null);
    infoCanvas.drawBitmap(endTurnButtonImg, null, endTurnButtonRect, null);
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
    Log.d("WorldView", "Setting up moves for "+unit.getName()+" at "+ unit.getPos());
    validMoves = GameUtils.validMovePositions(unit.getPos(), unit.getMoveRange(), unit.getMoveRestrict(), world);
    for (Position pos : validMoves) {
      //Log.Log.d("WorldView", "Can move to "+pos);
      int x = bgCenterX + Math.round(HEX_SIZE * 1.5f  * pos.getCol()) - (TILE_WIDTH / 2);
      int y = bgCenterY + Math.round(HEX_SIZE * SQRT_3 * (pos.getRow() + (pos.getCol() / 2f))) - (TILE_HEIGHT/2);
      bgCanvas.drawBitmap(moveImg, x, y, null);
    }
  }

  /**
   * Moves the unit to the specified location
   */
  public void moveSelectedUnit(Position pos) {
    if (selectedUnit == null) {
      return;
    }
    if (selectedUnit.getAction() >= selectedUnit.getMoveActionCost()) {
      selectedUnit.setAction(selectedUnit.getAction() - selectedUnit.getMoveActionCost());
    }
    selectedUnit.setPos(pos);
    // Clear valid moves, need to click again for a second move
    validMoves = null;
    scriptEngine.runActions(world.getTriggers().getAbilityUsed(), null, null);
  }

  /**
   * loads and draws all the target ability locations for the unit
   */
  public void setupSelectedUnitAbilities() {
    if (selectedUnit == null) {
      return;
    }
    selectedAbilities = new HashMap<>();
    for (Ability ability : selectedUnit.getAbilities()) {
      addAbility(ability);
    }
    Log.d("WorldView", "selectedAbilities="+selectedAbilities);
    drawSelectedUnitAbilities();
  }

  private void setupSingleAbility(Ability ability) {
    if (selectedUnit == null) {
      return;
    }
    selectedAbilities = new HashMap<>();
    addAbility(ability);
    drawSelectedUnitAbilities();
  }


  private void addAbility(Ability ability) {
    Log.d("WorldView", "setting up " + selectedUnit.getName() + " ability " + ability.getName());
    int range = ability.getRange();
    Action applies = ability.getApplies();
    if (applies == null) {
      Log.d("WorldView", "Applies is null");
      return;
    }
    Mod mod = world.getMods().get(applies.getModId());
    if (mod == null) {
      Log.d("WorldView", "mod is null. modId="+applies.getModId()+" mods="+world.getMods());
      return;
    }
    if (Mod.TYPE_RULE.equals(mod.getType())) {
      for (Unit target : world.getUnits()) {
        //Log.Log.d("WorldView", "checking "+ability.getName()+" range:"+ability.getRange()+" against "+target.getName()+" at "+target.getPos()+" distance="+target.getPos().distanceTo(selectedUnit.getPos()));
        if (!selectedAbilities.containsKey(target.getPos()) && // unit is not already covered with another ability
            (target.getPos().distanceTo(selectedUnit.getPos()) <= range) && // Within range for the ability
            (ability.getActionCost() <= selectedUnit.getAction())) { // unit has enough action to perform the ability
          // Run the applies JS rule
          if (scriptEngine.runRule(applies, selectedUnit, target)) {
            selectedAbilities.put(target.getPos(), ability);
          }
        }
      } // for unit
    } else if(Mod.TYPE_RULE_LOC.equals(mod.getType())) {
      // TODO : lookup all the applicable locations (not necessarily where a unit is)
    } else {
      Log.d("WorldView", "Error in Ability:"+ability.getId()+" name="+ability.getName()+" Can't handle applies with MOD_TYPE="+mod.getType());
    }
  } // for Ability


  public ScriptEngine getScripEngine() {
    return this.scriptEngine;
  }


  public void drawSelectedUnitAbilities() {
    Log.d("WorldView", "Setting up selectedAbilities "+selectedAbilities);
    if (selectedAbilities != null) {
      for (Position pos : selectedAbilities.keySet()) {
        Ability ability = selectedAbilities.get(pos);
        int x = bgCenterX+Math.round(HEX_SIZE * 1.5f * pos.getCol())-(TILE_WIDTH / 2);
        int y = bgCenterY+Math.round(HEX_SIZE * SQRT_3 * (pos.getRow()+(pos.getCol() / 2f)))-(TILE_HEIGHT / 2);
        Rect imgPos = new Rect(x+(TILE_WIDTH/4), y+(TILE_HEIGHT/4), x+(int)(TILE_WIDTH*0.75f),  y+(int)(TILE_HEIGHT*0.75f));
        bgCanvas.drawCircle(x+TILE_WIDTH/2, y+TILE_HEIGHT/2, TILE_HEIGHT/4, whiteHighlightPaint);
        bgCanvas.drawBitmap(ability.getBitmap(), null, imgPos, null);
        //Log.Log.d("WorldView", "drawing ability="+ability.getName()+" at "+pos);
      } // for
    }
  }

  /**
   * Applies the ability (runs the script)
   * self = selectedUnit
   * target = targetUnit
   */
  public void applyAbility(Ability ability, Unit target) {
    if (selectedUnit == null) {
      // Cannot apply ability without a self
      return;
    }
    // Action cost
    selectedUnit.setAction(selectedUnit.getAction() - ability.getActionCost());
    // Execute the onStart
    scriptEngine.runActions(ability.getOnStart(), selectedUnit, target);
    // Add effect
    Effect effect = ability.getEffect();
    if (effect != null) {
      // non-stackable effects can only be applied once
      if (effect.isStackable() || !target.getEffects().contains(effect)) {
        target.getEffects().add(effect.clone());
      }
    }
    deathCheck(target);
    scriptEngine.runActions(world.getTriggers().getAbilityUsed(), selectedUnit, target);
  }

  /**
   * Called when a new turn begins
   */
  public void startTurn() {
    // Process all the effects
    // TODO : should only be for the player (the AI start turn would process effects for computer)
    for (Unit unit : world.getUnits()) {
      for (int i= unit.getEffects().size()-1; i>=0; i--) {
        Effect effect = unit.getEffects().get(i);
        scriptEngine.runActions(effect.getOnRun(), unit, unit);
        effect.setDuration(effect.getDuration() - 1);
        if (effect.getDuration() <= 0) {
          unit.getEffects().remove(i);
        }
      } // for effect
    } // for unit
    deathCheck();
    scriptEngine.runActions(world.getTriggers().getStartTurn(), null, null);

    // Refresh the UI with all the changes
    validMoves = null;
    selectedAbilities = null;
    drawBackground();
    drawSelectedUnitInfo();
  }

  /**
   * Called when ending the turn
   */
  public void endTurn() {
    Log.d("WorldView", "Ending turn");
    // Reset all the action points
    for (Unit unit : world.getUnits()) {
      unit.setAction(unit.getActionMax());
    }
    scriptEngine.runActions(world.getTriggers().getEndTurn(), null, null);
  }

  /**
   * Checks all units to see if any are dead and need to be cleaned up
   */
  public void deathCheck() {
    List<Unit> units = world.getUnits();
    for (int i=units.size(); i<=0; i--) {
      Unit unit = units.get(i);
      if (unit.getHp() <= 0) {
        units.remove(i);
        Log.d("WorldView", unit.getName()+" has died!");
      }
    } // for
  }

  /**
  * Checks this particular unit to see if it has died.
  * If it's dead it will be removed from the game
  */
  public void deathCheck(Unit unit) {
    if (unit.getHp() <= 0) {
      world.getUnits().remove(unit);
      Log.d("WorldView", unit.getName()+" has died!");
    }
  }

  public void drawVictory(Canvas canvas) {
    if (world.isVictory()) {
      drawEndGameText(canvas, "Victory!");
    } else if (world.isDefeat()) {
      drawEndGameText(canvas, "Defeated");
    }
  }

  public void drawEndGameText(Canvas canvas, String message) {
    int textWidth = (int) victoryTextPaint.measureText(message);
    StaticLayout layout = new StaticLayout(message, victoryTextPaint, textWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0, false);
    int x = (screenWidth - layout.getWidth()) / 2; // centered left to right
    int y = (viewSizeY - layout.getHeight()) / 2; // halfway up the map view
    canvas.save();
    canvas.translate(x, y);
    layout.draw(canvas);
    canvas.restore();
    this.endGameTextDrawn = true;
  }

}
