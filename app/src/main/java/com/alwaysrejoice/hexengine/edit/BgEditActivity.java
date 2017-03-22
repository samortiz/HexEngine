package com.alwaysrejoice.hexengine.edit;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.alwaysrejoice.hexengine.R;
import com.alwaysrejoice.hexengine.dto.BgTile;
import com.alwaysrejoice.hexengine.dto.Game;
import com.alwaysrejoice.hexengine.dto.Image;
import com.alwaysrejoice.hexengine.util.FileUtils;
import com.alwaysrejoice.hexengine.util.GameUtils;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.Map;

public class BgEditActivity extends Activity {

  /**
   * ATTENTION: This was auto-generated to implement the App Indexing API.
   * See https://g.co/AppIndexing/AndroidStudio for more information.
   */
  private GoogleApiClient client;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Log.d("bgEdit", "onCreate");
    setContentView(R.layout.bg_edit);
    // Get the name of the selected game that was passed in with this intent
    String bgName = getIntent().getStringExtra("BG_NAME");
    // ATTENTION: This was auto-generated to implement the App Indexing API.
    // See https://g.co/AppIndexing/AndroidStudio for more information.
    client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
  }

  /**
   * Called when the user clicks "Save"
   */
  public void save(View view) {
    EditText nameInput = (EditText) findViewById(R.id.bg_name);
    EditText typeInput = (EditText) findViewById(R.id.bg_type);
    String name = nameInput.getText().toString();
    String type = typeInput.getText().toString();

    Bitmap bitmap = FileUtils.loadBitmap(name + ".png");
    Image image = new Image(name + ".png", bitmap);

    Game game = GameUtils.getGame();
    Map<String, BgTile> bgTiles = game.getBgTiles();
    BgTile bgTile = bgTiles.get(name);
    if (bgTile != null) {
      Log.i("bgEditActivity", "Name=" + name + " already exists as a bgTile, overwriting");
      // TODO : Validation error for the user
    }
    bgTile = new BgTile(name, type, image);
    bgTiles.put(name, bgTile);
    Log.d("bgEdit", "saving bgTile name="+name+" type="+type);
    // TOOD : Save the game

    Intent myIntent = new Intent(BgEditActivity.this, BgListActivity.class);
    startActivity(myIntent);
  }


  /**
   * ATTENTION: This was auto-generated to implement the App Indexing API.
   * See https://g.co/AppIndexing/AndroidStudio for more information.
   */
  public Action getIndexApiAction() {
    Thing object = new Thing.Builder()
        .setName("BgEdit Page") // TODO: Define a title for the content shown.
        // TODO: Make sure this auto-generated URL is correct.
        .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
        .build();
    return new Action.Builder(Action.TYPE_VIEW)
        .setObject(object)
        .setActionStatus(Action.STATUS_TYPE_COMPLETED)
        .build();
  }

  @Override
  public void onStart() {
    super.onStart();

    // ATTENTION: This was auto-generated to implement the App Indexing API.
    // See https://g.co/AppIndexing/AndroidStudio for more information.
    client.connect();
    AppIndex.AppIndexApi.start(client, getIndexApiAction());
  }

  @Override
  public void onStop() {
    super.onStop();

    // ATTENTION: This was auto-generated to implement the App Indexing API.
    // See https://g.co/AppIndexing/AndroidStudio for more information.
    AppIndex.AppIndexApi.end(client, getIndexApiAction());
    client.disconnect();
  }
}
