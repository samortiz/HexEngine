package com.alwaysrejoice.hexengine.edit;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import com.alwaysrejoice.hexengine.R;
import com.alwaysrejoice.hexengine.dto.AI;
import com.alwaysrejoice.hexengine.dto.Game;
import com.alwaysrejoice.hexengine.dto.Team;
import com.alwaysrejoice.hexengine.util.GameUtils;
import com.alwaysrejoice.hexengine.util.Utils;
import java.util.ArrayList;
import java.util.List;

public class TeamEditActivity extends Activity {
  public static final String SELECTED_TEAM_ID = "SELECTED_TEAM_ID";

  Team team; // The team we are currently editing

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Log.d("teamEdit", "onCreate");
    setContentView(R.layout.team_edit);
    Bundle bundle = getIntent().getExtras();
    Game game = GameUtils.getGame();

    List<String> aiNames = new ArrayList<>();
    for (AI ai : game.getAis()) {
      aiNames.add(ai.getName());
    }

    Utils.setupSpinner((Spinner)findViewById(R.id.ai_spinner), aiNames);

    // Load the team we are editing
    String teamId = (String) bundle.get(TeamEditActivity.SELECTED_TEAM_ID);
    if ((teamId != null) && !"".equals(teamId)) {
      team = GameUtils.getTeamById(teamId);
      Log.d("teamEdit", "begin editing selected team "+team.getName());
    }

    if (team == null) {
      team = new Team(Utils.generateUniqueId());
      game.getTeams().add(team);
    }
    setUiFromTeam();
  }

  /**
   * Called when the user clicks "Save"
   */
  public void save(View view) {
    loadTeamFromUi();
    GameUtils.saveGame();
    Log.d("teamEdit", "saving Team name="+team.getName());
    Intent myIntent = new Intent(TeamEditActivity.this, TeamListActivity.class);
    startActivity(myIntent);
  }

  /**
   * Loads the information from the UI input components into the team
   */
  private void loadTeamFromUi() {
    EditText nameInput = (EditText) findViewById(R.id.team_name);
    team.setName(nameInput.getText().toString().trim());

    String aiName = Utils.getSpinnerValue((Spinner) findViewById(R.id.ai_spinner));
    team.setAiId(GameUtils.getAiIdFromName(aiName));
  }

  /**
   * Updates the UI to match the data in the team
   */
  private void setUiFromTeam() {
    EditText nameInput = (EditText) findViewById(R.id.team_name);
    nameInput.setText(team.getName());

    Utils.setSpinnerValue((Spinner) findViewById(R.id.ai_spinner), GameUtils.getAiNameFromId(team.getAiId()));
  }

  public void showError(String errorMsg) {
    TextView errView = (TextView) findViewById(R.id.error_message);
    errView.setText(errorMsg);
    errView.setTextColor(Color.RED);
  }

}
