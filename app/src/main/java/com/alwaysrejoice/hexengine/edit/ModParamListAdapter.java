package com.alwaysrejoice.hexengine.edit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.alwaysrejoice.hexengine.R;
import com.alwaysrejoice.hexengine.dto.ModParam;

import java.util.List;

public class ModParamListAdapter extends BaseAdapter {

  private Context context;
  private List<ModParam> params;

  public ModParamListAdapter(Context context, List<ModParam> params) {
    this.context = context;
    this.params = params;
  }

  public int getCount() {
    return params.size();
  }

  public ModParam getItem(int arg0) {
    return params.get(arg0);
  }

  public long getItemId(int position) {
    return position;
  }

  public void removeItem(int position) {
    params.remove(position);
  }

  public View getView(int position, View convertView, ViewGroup parent) {
    LayoutInflater inflater = LayoutInflater.from(context);
    View row = inflater.inflate(R.layout.mod_param_list_row, parent, false);
    ModParam param = this.params.get(position);

    TextView textView = (TextView) row.findViewById(R.id.mod_param_text);
    textView.setText(param.getVar()+"("+param.getType()+")");

    ImageView deleteImg = (ImageView) row.findViewById(R.id.row_delete);
    deleteImg.setTag(position);

    ImageView editImg = (ImageView) row.findViewById(R.id.row_edit);
    editImg.setTag(position);
    return (row);
  }
}
