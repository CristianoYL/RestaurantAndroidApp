package com.example.cristianoyl.restaurant.activities;

import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.cristianoyl.restaurant.R;
import com.example.cristianoyl.restaurant.fragments.card.CardFragment;
import com.example.cristianoyl.restaurant.fragments.card.dummy.DummyContent;
import com.example.cristianoyl.restaurant.fragments.menu.MenuFragment;
import com.example.cristianoyl.restaurant.fragments.order.OrderFragment;
import com.example.cristianoyl.restaurant.fragments.restaurant.RestaurantFragment;
import com.example.cristianoyl.restaurant.models.Menu;
import com.example.cristianoyl.restaurant.models.Restaurant;
import com.example.cristianoyl.restaurant.utils.Constants;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity
        implements RestaurantFragment.OnRestaurantClickListener,
                    MenuFragment.OnFragmentInteractionListener,
                    OrderFragment.OnOrderFragmentInteractionListener,
                    CardFragment.OnCardFragmentInteractionListener {

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
    public void onViewOrder(Restaurant restaurant, HashMap<Menu,Integer> orderMap) {
        viewOrderPage(restaurant, orderMap);
    }

    @Override
    public void onBackButtonPressed() {
        onBackPressed();
    }


    private void viewOrderPage(Restaurant restaurant, HashMap<Menu,Integer> orderMap){
        OrderFragment orderFragment = OrderFragment.newInstance(restaurant, orderMap);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frame_content,orderFragment, Constants.FRAGMENT_ORDER);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onChooseCard() {
        CardFragment cardFragment = CardFragment.newInstance(0);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frame_content,cardFragment, Constants.FRAGMENT_CARD);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onCardSelected(DummyContent.DummyItem item) {

    }

    @Override
    public void onCreateCard(DummyContent.DummyItem item) {

    }
}
