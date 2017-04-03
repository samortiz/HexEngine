package com.alwaysrejoice.hexengine.edit;

import android.os.Bundle;

import com.alwaysrejoice.hexengine.util.GameUtils;

public class ListDamageActivity extends ListStringBaseActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    types = GameUtils.getGame().getDamageTypes();
    typeName = "Damage Type";
    super.onCreate(savedInstanceState);
  }

}
