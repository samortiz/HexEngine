package com.alwaysrejoice.hexengine.play;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import com.alwaysrejoice.hexengine.dto.World;
import com.alwaysrejoice.hexengine.util.WorldUtils;

public class WorldActivity extends Activity implements View.OnTouchListener {
  public static final String SELECTED_WORLD_NAME = "SELECTED_WORLD_NAME";
  WorldView worldView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Log.d("WorldActivity", "onCreate");
    Bundle bundle = getIntent().getExtras();

    String worldName = (String) bundle.get(SELECTED_WORLD_NAME);
    if (worldName == null) {
      throw new IllegalArgumentException("You must specify a world by setting SELECTED_WORLD_NAME");
    }
    World world = WorldUtils.loadWorld(worldName);

    // Show the world
    worldView = new WorldView(this, world);
    worldView.setOnTouchListener(this);
    setContentView(worldView);

  }

  // OnTouchListener method
  public boolean onTouch(View v, MotionEvent event) {
    worldView.onTouchEvent(event);
    return true;
  }

  @Override
  public void onBackPressed() {
    Log.d("WorldActivity", "Back pressed");
    WorldUtils.saveWorld();
    // default behavior
    super.onBackPressed();
  }

  @Override
  public void onDestroy() {
    worldView.getScripEngine().onDestroy();
    super.onDestroy();
  }

  public void exit() {
    Intent myIntent = new Intent(WorldActivity.this, WorldListActivity.class);
    startActivity(myIntent);
  }



}
