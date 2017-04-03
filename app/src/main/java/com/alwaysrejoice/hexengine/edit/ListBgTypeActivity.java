package com.alwaysrejoice.hexengine.edit;

import android.os.Bundle;

import com.alwaysrejoice.hexengine.util.GameUtils;

public class ListBgTypeActivity extends ListStringBaseActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    types = GameUtils.getGame().getBgTypes();
    typeName = "Background Type";
    super.onCreate(savedInstanceState);
  }

}
