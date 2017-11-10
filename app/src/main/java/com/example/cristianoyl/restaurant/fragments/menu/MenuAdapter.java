package com.example.cristianoyl.restaurant.fragments.menu;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.cristianoyl.restaurant.R;
import com.example.cristianoyl.restaurant.models.Menu;
import com.example.cristianoyl.restaurant.utils.Constants;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by CristianoYL on 8/8/17.
 */

public class MenuAdapter extends BaseAdapter {
    private Context context;
    private Menu[] menus;
    private ArrayList<String> categories;
    private ArrayList<ArrayList<Menu>> categoryMenu;
    HashMap<Menu,Integer> orderMap;
    private OnOrderChangeListener onOrderChangeListener;

    public MenuAdapter(Context context, Menu[] menus, OnOrderChangeListener onOrderChangeListener) {
        this.context = context;
        this.menus = menus;
        this.categories = new ArrayList<>();
        this.categoryMenu = new ArrayList<>();
        ArrayList<Menu> chefRecommendationList = new ArrayList<>(); // recommended dishes
        categories.add(Constants.CATEGORY_CHEF_RECOMMENDED);
        categoryMenu.add(chefRecommendationList);
        this.orderMap = new HashMap<>(menus.length);
        for ( Menu menu : this.menus ) {
            orderMap.put(menu,0);   // initialize order;
            if ( categories.contains(menu.category) ) {
                categoryMenu.get(categories.indexOf(menu.category)).add(menu);
            } else {
                ArrayList<Menu> sameCategory = new ArrayList<>();
                sameCategory.add(menu);
                categories.add(menu.category);
                categoryMenu.add(sameCategory);
            }
            if ( menu.isRecommended ) {
                chefRecommendationList.add(menu);
            }
        }
        this.onOrderChangeListener = onOrderChangeListener;
    }

    private class ViewHolder {
        TextView tvCategory;
        LinearLayout layoutMenu;

        ViewHolder(TextView tvCategory, LinearLayout layoutMenu) {
            this.tvCategory = tvCategory;
            this.layoutMenu = layoutMenu;
        }
    }

    @Override
    public int getCount() {
        return categories.size();
    }

    @Override
    public ArrayList<Menu> getItem(int position) {
        return categoryMenu.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if ( convertView != null ) {
            viewHolder = (ViewHolder) convertView.getTag();
        } else {
            convertView = LayoutInflater.from(context).inflate(R.layout.layout_menu_category,null);
            TextView tvCategory = (TextView) convertView.findViewById(R.id.tv_category);
            LinearLayout layoutMenu = (LinearLayout) convertView.findViewById(R.id.layout_menu);
            viewHolder = new ViewHolder(tvCategory,layoutMenu);
            convertView.setTag(viewHolder);
        }
        viewHolder.tvCategory.setText(categories.get(position));
        viewHolder.tvCategory.setPaintFlags(viewHolder.tvCategory.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        ViewGroup insertPoint = viewHolder.layoutMenu;
        insertPoint.removeAllViews();
        for ( int i = 0; i < getItem(position).size(); i++ ) {
            final Menu menu = getItem(position).get(i);
            //fill the category menu
            View v = LayoutInflater.from(context).inflate(R.layout.layout_menu_entry, null);
            View layoutGeneral = v.findViewById(R.id.layout_general);
            View layoutOrder = v.findViewById(R.id.layout_order_operation);
            final View layoutDetail = v.findViewById(R.id.layout_detail);
            TextView tvName = (TextView) v.findViewById(R.id.tv_name);
            TextView tvPrice = (TextView) v.findViewById(R.id.tv_price);
            TextView tv_description = (TextView) v.findViewById(R.id.tv_description);
            ImageView ivPhoto = (ImageView) v.findViewById(R.id.iv_food_image);
            Button btnRemove = (Button) v.findViewById(R.id.fab_remove);
            Button btnAdd = (Button) v.findViewById(R.id.fab_add);
            final TextView tvAmount = (TextView) v.findViewById(R.id.tv_amount);

            // fill in any details dynamically here
            tvName.setText(menu.name);
            tvPrice.setText("$" + menu.price);
            tv_description.setText(menu.description);
            tvAmount.setText(String.valueOf(orderMap.get(menu)));
            layoutGeneral.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if ( layoutDetail.getVisibility() != View.VISIBLE ) {
                        layoutDetail.setVisibility(View.VISIBLE);
                    } else {
                        layoutDetail.setVisibility(View.GONE);
                    }
                }
            });

            btnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    orderMap.put(menu,orderMap.get(menu) + 1);
                    notifyDataSetChanged();
                    tvAmount.setText(String.valueOf(orderMap.get(menu)));
                    onOrderChangeListener.addOrder(menu);
                }
            });

            btnRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int amount = orderMap.get(menu);
                    if ( amount > 0 ) {
                        orderMap.put(menu,--amount);
                        notifyDataSetChanged();
                        tvAmount.setText(String.valueOf(orderMap.get(menu)));
                        onOrderChangeListener.removeOrder(menu);
                    }
                }
            });
            // insert into main view
            insertPoint.addView(v, i, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }

        return convertView;
    }

    public interface OnOrderChangeListener{
        void addOrder(Menu menu);
        void removeOrder(Menu menu);
    }
}
