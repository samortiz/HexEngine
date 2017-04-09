package com.alwaysrejoice.hexengine.edit;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import com.alwaysrejoice.hexengine.dto.Game;
import com.alwaysrejoice.hexengine.dto.SystemTile;
import com.alwaysrejoice.hexengine.dto.TileGroup;
import com.alwaysrejoice.hexengine.dto.TileType;
import com.alwaysrejoice.hexengine.dto.TileTypeLink;
import com.alwaysrejoice.hexengine.util.GameUtils;

import java.util.ArrayList;
import java.util.List;

public class Toolbar {
  public enum Mode { MOVE, ERASE, DRAW};
  private static final int TOOLBAR_BUTTONS_PER_ROW = 5;

  EditMapView editMapView;
  Paint fillPaint = new Paint();
  Paint borderPaint = new Paint();

  // Toolbar Variables
  private int toolbarWidth;
  private int toolbarHeight;
  private int toolbarX;
  private int toolbarY;
  // This contains the system and custom menus
  private List<List<TileType>> groups;
  private Rect toolbarWindow;
  private List<ToolbarButton> toolbarButtons;
  private Bitmap selectedImg;
  private Mode mode = Mode.MOVE;
  private ToolbarButton selectedButton = null;
  private boolean selectedButtonChildrenVisible = false;

  // Default Constructor
  public Toolbar(EditMapView editMapView, int viewSizeX, int viewSizeY, int screenWidth, int screenHeight) {
    this.editMapView = editMapView;
    Log.d("toolbar", "Creating toolbar viewSizeX="+viewSizeX+" viewSizeY="+viewSizeY);

    selectedImg = SystemTile.getTile(SystemTile.NAME.SELECTED).getBitmap();
    fillPaint.setColor(Color.rgb(200, 200, 255));
    borderPaint.setColor(Color.BLACK);
    borderPaint.setStyle(Paint.Style.STROKE);
    borderPaint.setStrokeWidth(4);

    // Portrait
    toolbarHeight = screenHeight - viewSizeY;
    toolbarWidth = screenWidth;
    toolbarX = 0;
    toolbarY = viewSizeY + 1;

    toolbarWindow = new Rect(toolbarX, toolbarY, toolbarX + toolbarWidth, toolbarY + toolbarHeight);
    // Toolbar buttons
    toolbarButtons = new ArrayList<>();
    int buttonWidth = toolbarWidth / TOOLBAR_BUTTONS_PER_ROW;
    int buttonHeight = buttonWidth; // (int)Math.round((double)buttonWidth * Math.sqrt(3.0) / 2.0);
    int buttonX = -buttonWidth; // so we can pre-increment
    int buttonY = toolbarY;
    Log.d("toolbar", "toolbarWidth="+toolbarWidth+" toolbarHeight="+toolbarHeight+" buttonWidth="+buttonWidth+" buttonHeight="+buttonHeight+" toolbarY="+toolbarY);

    // Setup the System Tiles
    ArrayList<TileType> systemTiles = new ArrayList();
    systemTiles.add(SystemTile.getTile(SystemTile.NAME.HAND));
    systemTiles.add(SystemTile.getTile(SystemTile.NAME.ERASER));
    systemTiles.add(SystemTile.getTile(SystemTile.NAME.SETTINGS));
    systemTiles.add(SystemTile.getTile(SystemTile.NAME.SAVE));
    systemTiles.add(SystemTile.getTile(SystemTile.NAME.EXIT));

    Game game = GameUtils.getGame();
    // Load the rest of the game-specific tiles
    groups = new ArrayList<>();
    groups.add(systemTiles);
    for (TileGroup group : game.getTileGroups()) {
      List<TileType> thisGroup = new ArrayList();
      for (TileTypeLink link : group.getTileLinks()) {
        TileType tile = TileTypeLink.getTile(link, game);
        thisGroup.add(tile);
      }
      groups.add(thisGroup);
    }

    // Contains parent buttons whose children are too large fit using the standard layout
    List<ToolbarButton> popupTooLarge = new ArrayList<>();

    // Setup the custom buttons
    for (List<TileType> group : groups) {
      buttonX += buttonWidth;
      if (buttonX >= toolbarWidth) {
        buttonX = 0;
        buttonY += buttonHeight +1;
      }
      ToolbarButton parentButton = null;

      // Draw the buttons
      for (int i=0; i<group.size(); i++) {
        TileType tile = group.get(i);
        if (i == 0) {
          // It's a parent button
          ToolbarButton newButton = new ToolbarButton(tile.getId(), tile.getName(), tile.getTileType(), tile.getBitmap(),
              new Rect(buttonX, buttonY, buttonX+buttonWidth, buttonY+buttonHeight), null);
          toolbarButtons.add(newButton);
          parentButton = newButton;
          Log.d("toolbar", "Added parent "+newButton.getName()+" position="+newButton.getPosition());
        } else {
          // It's a child button
          int childLeft = buttonX;
          int childTop = buttonY - (buttonHeight * i);
          int childRight = buttonX + buttonWidth;
          int childBottom = childTop + buttonHeight;
          while (childTop < 0) {
            childLeft += buttonWidth;
            childTop += buttonY;
            childRight += buttonWidth;
            childBottom = childTop + buttonHeight;
          } // while
          ToolbarButton newButton = new ToolbarButton(tile.getId(), tile.getName(), tile.getTileType(), tile.getBitmap(),
              new Rect(childLeft, childTop, childRight, childBottom), parentButton);
          parentButton.addChild(newButton);
          if (childRight > screenWidth) {
            if (!popupTooLarge.contains(parentButton)) {
              popupTooLarge.add(parentButton);
            }
          }
          Log.d("toolbar", "added child " + newButton.getName() + " position=" + newButton.getPosition());
        }
      } // for tile
    } // for group

    // For each parent calculate the toolbar popup background size
    for (ToolbarButton button : toolbarButtons) {
      int buttonTop = button.getPosition().top;
      int childCount = button.getChildren().size();
      int colCount = 1;
      int popupHeight = 0;
      if (childCount > 0) {
        colCount = (int)Math.ceil((double)(childCount * buttonHeight) / (double)buttonTop);
        if (colCount == 1) {
          popupHeight = childCount * buttonHeight;
        } else {
          popupHeight = buttonTop;
        }
      }
      Rect popupPosition = new Rect(button.getPosition().left, buttonTop-popupHeight, button.getPosition().right+((colCount-1)*buttonWidth), buttonTop);
      button.setPopupPosition(popupPosition);

      if (popupTooLarge.contains(button)) {
        Log.d("toolbar", "Setting up the alternate layout for button="+button);
        int childLeft = 0;
        int childTop = 0;
        int childRight = buttonWidth;
        int childBottom = buttonHeight;
        button.getChildren().add(0, new ToolbarButton(button.getId(), button.getName(), button.getType(), button.getImg(),
            new Rect(childLeft, childTop, childRight, childBottom), button));
        // Skip the first child, since we have already set the position in the above line
        for (int i=1; i<button.getChildren().size(); i++) {
          ToolbarButton child = button.getChildren().get(i);
          childLeft += buttonWidth;
          if ((childLeft + buttonWidth) > screenWidth) {
            childLeft = 0;
            childTop += buttonHeight;
          }
          childRight = childLeft + buttonWidth;
            childBottom = childTop + buttonHeight;
          child.setPosition(new Rect(childLeft, childTop, childRight, childBottom));
        } // for
        button.setPopupPosition(new Rect(0, 0, screenWidth, childBottom));
        // If the alternate layout impinges on the toolbar, then we won't draw toolbar buttons
        // when the alternateLayout is showing (or listen for clicks on them!)
        if (childBottom > toolbarY) {
          button.setLongAlternateLayout(true);
        }
      }

      Log.d("toolbar", "childCount="+childCount+" colCount="+colCount+ " popupPosition="+popupPosition);
    } // for

    // Select the first button (move)
    selectedButton = toolbarButtons.get(0);

  } // constructor

  /**
   * Draws the toolbar on the canvas
   */
  public void drawToolbar(Canvas canvas) {
    // Background of the entire toolbar area
    canvas.drawRect(toolbarWindow, fillPaint);
    // First level of buttons (across)
    for (ToolbarButton button : toolbarButtons) {
      // Check if the selected button is a child of this button
      if (selectedButton.getParent() == button) {
        // Draw the selected button at the location of the parent
        canvas.drawBitmap(selectedButton.getImg(), null, button.getPosition(), null);
        canvas.drawBitmap(selectedImg, null, button.getPosition(), null);
      } else {
        // A child button of this button is not selected (so we need to draw the button)
        if (!(selectedButton.isLongAlternateLayout() && selectedButtonChildrenVisible)) {
          drawButton(button, canvas);
        }
      }
      if (button == selectedButton) {
        canvas.drawBitmap(selectedImg, null, button.getPosition(), null);
        if (selectedButtonChildrenVisible) {
          // Background of the popup window
          canvas.drawRect(button.getPopupPosition(), fillPaint);
          canvas.drawRect(button.getPopupPosition(), borderPaint);
          // Second-level buttons (up)
          for (ToolbarButton childButton : button.getChildren()) {
            drawButton(childButton, canvas);
          }
        }
      }
    } // for
  }

  /**
   * Draws a button on the toolbarCanvas, if the button is visible.
   * This will also highlight the button if it is selected
   */
  public void drawButton(ToolbarButton button, Canvas canvas) {
    canvas.drawBitmap(button.getImg(), null, button.getPosition(), null);
  }

  /**
   * Detects and handles toolbar events.
   * This goes through all the buttons and checks for clicks
   * @return true if a button click was detected
   */
  public boolean toolbarDetector(float x, float y) {
    boolean handledEvent = false;
    // First level of buttons (across)
    // reverse iteration so the bottom ones go first (the popup of the bottom ones may be "on top" of the first ones)
    for (int i=toolbarButtons.size()-1; i>=0; i--) {
      ToolbarButton button = toolbarButtons.get(i);
      if (buttonDetector(x, y, button)) {
        handledEvent = true;
        break;
      }
      if (selectedButton == button) {
        // Second-level buttons (up)
        for (ToolbarButton childButton : button.getChildren()) {
          if (buttonDetector(x, y, childButton)) {
            handledEvent = true;
            break;
          }
        }
        // If a child button found a click, we can exit the outer loop
        if (handledEvent) {
          break;
        }
      }
    }
    // The user clicked somewhere without hitting a button
    if (!handledEvent && selectedButtonChildrenVisible) {
      Log.d("toolbar", "clicked without hitting a button");
      selectedButtonChildrenVisible = false;
      handledEvent = true;
    }
    //Log.d("toolbar", "detector handledEvent="+handledEvent);
    return handledEvent;
  }

  /**
   * Checks and handles clicks on a button.   This will always set the selected status of the
   * button as if the user clicks outside the button, this button is not selected
   * @return true if this button was clicked
   */
  public boolean buttonDetector(float x, float y, ToolbarButton button) {
    boolean handledEvent = false;
    // If the click is on this button
    if (button.getPosition().contains((int)x, (int)y) &&
        // And it's a top level parent (and there isn't a long popup showing)
        ((button.getParent() == null)  ||
        // Or it's a visible child button
        (button.getParent() == selectedButton) && selectedButtonChildrenVisible)) {
      Log.d("toolbar", "button selected="+selectedButton.getName()+" clicked="+button.getName()+" x="+x+" y="+y+" pos="+button.getPosition());
      // If we have a main button (no parent)
      if (button.getParent() == null) {

        if (selectedButtonChildrenVisible && selectedButton.isLongAlternateLayout()) {
          // No click handling for parents when using alternate layout with long data (overlapping the toolbar)
          Log.d("toolbar", "not drawing parent button when alternateLayout is long");
          return false;
        }

        if ((selectedButton == button) && selectedButtonChildrenVisible) {
          Log.d("toolbar", "top level button already selected hiding children");
          selectedButtonChildrenVisible = false;
        } else {
          Log.d("toolbar", "showing children");
          // We have a main button that was not already selected
          // Or a main button that was selected but the children were hidden
          selectedButtonChildrenVisible = true;
        }
      } else {
        // child button selected, so hide the popup
        selectedButtonChildrenVisible = false;
        Log.d("toolbar", "child selected");
      }
      selectedButton = button;

      if (TileType.TILE_TYPE.SYSTEM.equals(button.getType()) ) {
        if (SystemTile.NAME.HAND.toString().equals(button.getName())) {
          mode = Mode.MOVE;
          Log.d("toolbar", "hand mode");
        } else if (SystemTile.NAME.ERASER.toString().equals(button.getName())) {
          mode = Mode.ERASE;
          Log.d("toolbar", "eraser mode");
        } else if (SystemTile.NAME.SETTINGS.toString().equals(button.getName())) {
          GameUtils.saveGame();
          Intent myIntent = new Intent(editMapView.getContext(), SettingsActivity.class);
          editMapView.getContext().startActivity(myIntent);
          Log.d("toolbar", "Going to settings");
        } else if (SystemTile.NAME.SAVE.toString().equals(button.getName())) {
          GameUtils.saveGame();
          Log.d("toolbar", "Saved game");
          mode = Mode.MOVE;
          selectedButton = toolbarButtons.get(0); // First button is the HAND for move mode
        } else if (SystemTile.NAME.EXIT.toString().equals(button.getName())) {
          GameUtils.saveGame();
          Intent myIntent = new Intent(editMapView.getContext(), GameListActivity.class);
          editMapView.getContext().startActivity(myIntent);
          Log.d("toolbar", "exit");
        } else {
          Log.e("toolbar", "Error! Unknown system button "+button.getName());
        }
      } else {
        mode = Mode.DRAW;
      }
      handledEvent = true;
      Log.d("toolbar", "click on button "+button.getName()+" showChildren="+selectedButtonChildrenVisible);
    }
    return handledEvent;
  }


  // Getters and Setters
  public Mode getMode() {
    return mode;
  }

  public ToolbarButton getSelectedToolbarButton() {
    return selectedButton;
  }

}
