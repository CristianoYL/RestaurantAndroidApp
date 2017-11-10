package com.example.cristianoyl.restaurant.fragments.restaurant;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cristianoyl.restaurant.R;
import com.example.cristianoyl.restaurant.models.Restaurant;

import java.util.List;

public class MyRestaurantRecyclerViewAdapter extends RecyclerView.Adapter<MyRestaurantRecyclerViewAdapter.ViewHolder> {

    private final List<Restaurant> mValues;
    private final RestaurantFragment.OnRestaurantInteractionListener mListener;

    public MyRestaurantRecyclerViewAdapter(List<Restaurant> items, RestaurantFragment.OnRestaurantInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.listitem_restaurant, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Restaurant restaurant = mValues.get(position);
        holder.restaurant = restaurant;
        holder.tvName.setText(restaurant.name);
        holder.tvPromo.setText(restaurant.promo);
        String info = "$" + restaurant.fee +" Delivery  $" + restaurant.limit + " Minimum";
        holder.tvInfo.setText(info);
        //TODO: set the logo
//        holder.ivLogo.setImageBitmap();

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onRestaurantClicked(holder.restaurant);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView tvName;
        public final TextView tvPromo;
        public final TextView tvInfo;
        public final ImageView ivLogo;
        public Restaurant restaurant;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            tvName = (TextView) view.findViewById(R.id.tv_name);
            tvPromo = (TextView) view.findViewById(R.id.tv_promo);
            tvInfo = (TextView) view.findViewById(R.id.tv_info);
            ivLogo = (ImageView) view.findViewById(R.id.iv_logo);
        }
    }
}
