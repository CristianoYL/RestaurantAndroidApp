package com.example.cristianoyl.restaurant.fragments.menu;

import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cristianoyl.restaurant.R;
import com.example.cristianoyl.restaurant.models.Menu;

import java.util.ArrayList;

/**
 * Created by CristianoYL on 8/8/17.
 */

public class MenuEntryAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Menu> categoryMenu;

    public MenuEntryAdapter(Context context, ArrayList<Menu> categoryMenu) {
        this.context = context;
        this.categoryMenu = categoryMenu;
        for ( int i = 0; i < categoryMenu.size() ; i++ ) {
            Log.d("TEST","menu:"+ categoryMenu.get(i).name);
        }
    }

    private class ViewHolder {
        View layoutGeneral, layoutOrder, layoutDetail;
        TextView tvName, tvPrice, tv_description,tvAmount;
        ImageView ivPhoto;
        Button fabRemove, fabAdd;

        ViewHolder(View layoutGeneral, View layoutOrder, View layoutDetail, TextView tvName,
                          TextView tvPrice, TextView tv_description, ImageView ivPhoto,
                          Button fabRemove, Button fabAdd,
                          TextView tvAmount) {
            this.layoutGeneral = layoutGeneral;
            this.layoutOrder = layoutOrder;
            this.layoutDetail = layoutDetail;
            this.tvName = tvName;
            this.tvPrice = tvPrice;
            this.tv_description = tv_description;
            this.ivPhoto = ivPhoto;
            this.fabRemove = fabRemove;
            this.fabAdd = fabAdd;
            this.tvAmount = tvAmount;
        }
    }

    @Override
    public int getCount() {
        return categoryMenu.size();
    }

    @Override
    public Menu getItem(int position) {
        return categoryMenu.get(position);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if ( convertView != null ) {
            viewHolder = (ViewHolder) convertView.getTag();
        } else {
            convertView = LayoutInflater.from(context).inflate(R.layout.layout_menu_entry,null);
            View layoutGeneral = convertView.findViewById(R.id.layout_general);
            View layoutOrder = convertView.findViewById(R.id.layout_order);
            View layoutDetail = convertView.findViewById(R.id.layout_detail);
            TextView tvName = (TextView) convertView.findViewById(R.id.tv_name);
            TextView tvPrice = (TextView) convertView.findViewById(R.id.tv_price);
            TextView tv_description = (TextView) convertView.findViewById(R.id.tv_description);
            ImageView ivPhoto = (ImageView) convertView.findViewById(R.id.iv_food);
            Button fabRemove = (Button)
                    convertView.findViewById(R.id.fab_remove);
            Button fabAdd = (Button) convertView.findViewById(R.id.fab_add);
            TextView tvAmount = (TextView) convertView.findViewById(R.id.tv_amount);
            viewHolder = new ViewHolder(layoutGeneral,layoutOrder,layoutDetail,tvName,tvPrice,
                    tv_description,ivPhoto,fabRemove,fabAdd,tvAmount);
            convertView.setTag(viewHolder);
        }
        viewHolder.tvName.setText(getItem(position).name);
        viewHolder.tvPrice.setText("$" + getItem(position).price);
        viewHolder.tv_description.setText(getItem(position).description);
        viewHolder.tvAmount.setText("0");
        return convertView;
    }
}
