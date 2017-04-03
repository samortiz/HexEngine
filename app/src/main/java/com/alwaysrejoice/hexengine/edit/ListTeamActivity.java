package com.alwaysrejoice.hexengine.edit;

import android.os.Bundle;

import com.alwaysrejoice.hexengine.util.GameUtils;

public class ListTeamActivity extends ListStringBaseActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    types = GameUtils.getGame().getTeams();
    typeName = "Team";
    super.onCreate(savedInstanceState);
  }

}
