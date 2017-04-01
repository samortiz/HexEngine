package com.alwaysrejoice.hexengine.edit;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.alwaysrejoice.hexengine.R;
import com.alwaysrejoice.hexengine.dto.Game;
import com.alwaysrejoice.hexengine.util.GameUtils;

import java.util.ArrayList;

/**
 * Clear Screen
 */
public class ClearActivity extends Activity {

  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.clear);
  }

  public void clearMapUnits(View view) {
    Game game = GameUtils.getGame();
    game.setUnitMaps(new ArrayList());
    GameUtils.saveGame();
    showMessage("All actions have been removed from the map");
  }

  public void clearMapBg(View view) {
    Game game = GameUtils.getGame();
    game.setBgMaps(new ArrayList());
    GameUtils.saveGame();
    showMessage("All background tiles have been removed from the map");
  }

  public void gotoSettings(View view) {
    Intent myIntent = new Intent(ClearActivity.this, SettingsActivity.class);
    startActivity(myIntent);
  }

  public void showMessage(String errorMsg) {
    TextView errView = (TextView) findViewById(R.id.message);
    errView.setText(errorMsg);
    errView.setTextColor(Color.BLACK);
  }

}
