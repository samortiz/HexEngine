package com.alwaysrejoice.hexengine.edit;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import com.alwaysrejoice.hexengine.R;
import com.alwaysrejoice.hexengine.dto.AI;
import com.alwaysrejoice.hexengine.dto.Game;
import com.alwaysrejoice.hexengine.util.GameUtils;
import com.alwaysrejoice.hexengine.util.Utils;

public class AiEditActivity extends Activity {
  public static final String SELECTED_AI_ID = "SELECTED_AI_ID";

  AI ai; // The ai we are currently editing

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Log.d("aiEdit", "onCreate");
    setContentView(R.layout.ai_edit);
    Bundle bundle = getIntent().getExtras();
    Game game = GameUtils.getGame();

    // Load the ai we are editing
    String aiId = (String) bundle.get(AiEditActivity.SELECTED_AI_ID);
    if ((aiId != null) && !"".equals(aiId)) {
      ai = GameUtils.getAiById(aiId);
      Log.d("aiEdit", "begin editing selected ai "+ai.getName());
    }

    if (ai == null) {
      ai = new AI(Utils.generateUniqueId());
      game.getAis().add(ai);
    }
    setUiFromAI();
  }

  /**
   * Called when the user clicks "Save"
   */
  public void save(View view) {
    loadAIFromUi();
    GameUtils.saveGame();
    Log.d("aiEdit", "saving AI name="+ai.getName());
    Intent myIntent = new Intent(AiEditActivity.this, AiListActivity.class);
    startActivity(myIntent);
  }

  /**
   * Loads the information from the UI input components into the ai
   */
  private void loadAIFromUi() {
    EditText nameInput = (EditText) findViewById(R.id.ai_name);
    ai.setName(nameInput.getText().toString().trim());

    EditText scriptInput = (EditText) findViewById(R.id.ai_script);
    ai.setScript(scriptInput.getText().toString().trim());
  }

  /**
   * Updates the UI to match the data in the ai
   */
  private void setUiFromAI() {
    EditText nameInput = (EditText) findViewById(R.id.ai_name);
    nameInput.setText(ai.getName());

    EditText scriptInput = (EditText) findViewById(R.id.ai_script);
    scriptInput.setText(ai.getScript());
  }

  public void showError(String errorMsg) {
    TextView errView = (TextView) findViewById(R.id.error_message);
    errView.setText(errorMsg);
    errView.setTextColor(Color.RED);
  }

}
