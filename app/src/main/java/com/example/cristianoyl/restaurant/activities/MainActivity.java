package com.example.cristianoyl.restaurant.activities;

import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.cristianoyl.restaurant.R;
import com.example.cristianoyl.restaurant.fragments.menu.MenuFragment;
import com.example.cristianoyl.restaurant.fragments.restaurant.RestaurantFragment;
import com.example.cristianoyl.restaurant.models.Restaurant;
import com.example.cristianoyl.restaurant.utils.Constants;

public class MainActivity extends AppCompatActivity
        implements RestaurantFragment.OnRestaurantClickListener,
                    MenuFragment.OnFragmentInteractionListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RestaurantFragment restaurantFragment = RestaurantFragment.newInstance();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frame_content,restaurantFragment, Constants.FRAGMENT_RESTAURANT);
        transaction.commit();
    }

    @Override
    public void onRestaurantClicked(Restaurant restaurant) {
        Toast.makeText(MainActivity.this, "view restaurant:" + restaurant.name, Toast.LENGTH_SHORT).show();
        viewRestaurant(restaurant);
    }

    private void viewRestaurant(Restaurant restaurant){
        MenuFragment menuFragment = MenuFragment.newInstance(restaurant);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frame_content,menuFragment, Constants.FRAGMENT_MENU);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onFragmentInteraction(View view) {
        if ( view.getId() == R.id.btn_back ) {
            onBackPressed();
        }
    }
}
