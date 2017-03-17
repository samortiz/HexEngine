package com.alwaysrejoice.hexengine.edit;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import com.alwaysrejoice.hexengine.dto.TileType;
import com.alwaysrejoice.hexengine.util.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Toolbar {
  public enum Mode { MOVE, ERASE, DRAW};
  private static final int TOOLBAR_BUTTONS_PER_ROW = 5;

  Paint paint = new Paint();

  // Toolbar Variables
  private int toolbarWidth;
  private int toolbarHeight;
  private int toolbarX;
  private int toolbarY;
  private Rect toolbarWindow;
  private List<ToolbarButton> toolbarButtons;
  private Bitmap selectedImg;
  private Mode mode = Mode.MOVE;
  private ToolbarButton selectedButton = null;
  private boolean selectedButtonChildrenVisible = false;

  // Variables from EditView
  private AssetManager assetManager;
  private HashMap<String, Bitmap> tileTypes;
  private int viewSizeX;
  private int viewSizeY;

  // Default Constructor
  public Toolbar(AssetManager assetManager, int viewSizeX, int viewSizeY, HashMap<String, Bitmap> tileTypes, int screenWidth, int screenHeight) {
    this.assetManager = assetManager;
    this.viewSizeX = viewSizeX;
    this.viewSizeY = viewSizeY;
    this.tileTypes = tileTypes;
    Log.d("toolbar", "Creating toolbar viewSizeX="+viewSizeX+" viewSizeY="+viewSizeY);

    selectedImg = Utils.loadBitmap(assetManager, "images/selected.png");

    paint.setColor(Color.rgb(200, 200, 255));

    Log.d("toolbar", "loaded img");
    // Setup the toolbar
    if (screenHeight > screenWidth) {
      // Portrait
      toolbarHeight = screenHeight - viewSizeY;
      toolbarWidth = screenWidth;
      toolbarX = 0;
      toolbarY = viewSizeY + 1;
    } else {
      // Landscape
      toolbarHeight = screenHeight;
      toolbarWidth = screenWidth - screenHeight;
      toolbarX = toolbarWidth;
      toolbarY = 0;
    }
    Log.d("toolbar", "toolbarWidth="+toolbarWidth+" height="+toolbarHeight);
    toolbarWindow = new Rect(toolbarX, toolbarY, toolbarX + toolbarWidth, toolbarY + toolbarHeight);
    // Toolbar buttons
    toolbarButtons = new ArrayList<>();
    int buttonWidth = toolbarWidth / TOOLBAR_BUTTONS_PER_ROW;
    int buttonHeight = buttonWidth;
    int buttonX = -buttonWidth; // so we can pre-increment
    int buttonY = toolbarY;
    // Setup the system buttons
    for (TileType tileType : TileType.SYSTEM_TILE_TYPES) {
      Bitmap img = tileTypes.get(tileType.getName());
      ToolbarButton parentButton = ToolbarButton.getButtonOfType(toolbarButtons, tileType.getType());
      if (parentButton == null) {
        buttonX += buttonWidth;
        if (buttonX >= toolbarWidth) {
          buttonX = 0;
          buttonY += buttonHeight +1;
        }
        ToolbarButton newButton = new ToolbarButton(tileType.getName(), tileType.getType(), img,
            new Rect(buttonX, buttonY, buttonX+buttonWidth, buttonY+buttonHeight), null);
        toolbarButtons.add(newButton);
        Log.d("toolbar", "Added parent "+newButton.getName()+" of type="+newButton.getType()+" position="+newButton.getPosition());
      } else {
        // parentButton != null
        int childCount = parentButton.getChildren().size() +1; // +1 for the child about to be added
        ToolbarButton newButton = new ToolbarButton(tileType.getName(), tileType.getType(), img,
            new Rect(buttonX, buttonY-(buttonHeight*childCount), buttonX+buttonWidth, buttonY-(buttonHeight*(childCount-1))), parentButton);
        parentButton.addChild(newButton);
        Log.d("toolbar", "added child "+newButton.getName()+" type="+newButton.getType()+" position="+newButton.getPosition());
      }
    } // for

    // Select the first button (move)
    selectedButton = toolbarButtons.get(0);

  } // constructor

  /**
   * Draws the toolbar on the canvas
   */
  public void drawToolbar(Canvas canvas) {
    canvas.drawRect(toolbarWindow, paint);
    // First level of buttons (across)
    for (ToolbarButton button : toolbarButtons) {
      drawButton(button, canvas);
      // Check if the selected button is a child of this button
      if (selectedButton.getParent() == button) {
        // Draw the selected button at the location of the parent
        canvas.drawBitmap(selectedButton.getImg(), null, button.getPosition(), null);
      }
      if (button == selectedButton) {
        // Mark this button as selected
        canvas.drawBitmap(selectedImg, null, button.getPosition(), null);
        if (selectedButtonChildrenVisible) {
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
    for (ToolbarButton button : toolbarButtons) {
      if (buttonDetector(x, y, button)) {
        handledEvent = true;
        break;
      };
      // Second-level buttons (up)
      for (ToolbarButton childButton : button.getChildren()) {
        if (buttonDetector(x, y, childButton)) {
          handledEvent = true;
          break;
        }
      }
    }
    // The user clicked somewhere without hitting a button
    if (!handledEvent) {
      selectedButtonChildrenVisible = false;
    }
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
        // And it's a top level parent
        (button.getParent() == null) ||
        // Or it's a visible child button
        (button.getParent() == selectedButton) && selectedButtonChildrenVisible) {
      Log.d("toolbar", "selected="+selectedButton.getName()+" button clicked="+button.getName());
      // If we have a main button (no parent)
      if (button.getParent() == null) {
        if (selectedButton == button) {
          Log.d("toolbar", "top level button already selected hiding children");
          selectedButtonChildrenVisible = false;
        } else {
          Log.d("toolbar", "showing children");
          // We have a main button that was not already selected
          selectedButtonChildrenVisible = true;
        }
      } else {
        // child button selected, so hide the popup
        selectedButtonChildrenVisible = false;
        Log.d("toolbar", "child selected");
      }
      selectedButton = button;

      if (TileType.SYSTEM_HAND.getName().equals(button.getName())) {
        mode = Mode.MOVE;
      } else if (TileType.SYSTEM_ERASER.getName().equals(button.getName())) {
        mode = Mode.ERASE;
      } else if (TileType.SYSTEM_SAVE.getName().equals(button.getName())) {
        // TODO
      } else if (TileType.SYSTEM_EXIT.getName().equals(button.getName())) {
        // TODO
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

  public String getToolbarButtonSelectedName() {
    if (selectedButton != null) {
      return selectedButton.getName();
    }
    return null;
  }

}
