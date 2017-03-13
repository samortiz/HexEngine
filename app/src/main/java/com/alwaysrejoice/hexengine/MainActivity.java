package com.alwaysrejoice.hexengine;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.alwaysrejoice.hexengine.edit.ChooseFile;
import com.alwaysrejoice.hexengine.play.MapActivity;

public class MainActivity extends Activity {

  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    // Show the home screen
    setContentView(R.layout.home);
    Log.d("init", "MainActivity.onCreate");
  }

  public void showEdit(View view){
    Intent myIntent = new Intent(MainActivity.this, ChooseFile.class);
    startActivity(myIntent);
    Log.d("click", "mainActivity showEdit");
  }

  public void showPlay(View view){
    Intent myIntent = new Intent(MainActivity.this, MapActivity.class);
    startActivity(myIntent);
    Log.d("click", "mainActivity showPlay");
  }

}
