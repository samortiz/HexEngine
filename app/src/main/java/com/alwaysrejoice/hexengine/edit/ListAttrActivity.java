package com.alwaysrejoice.hexengine.edit;

import android.os.Bundle;

import com.alwaysrejoice.hexengine.util.GameUtils;

public class ListAttrActivity extends ListStringBaseActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    types = GameUtils.getGame().getAttr();
    typeName = "Attribute";
    super.onCreate(savedInstanceState);
  }

}
