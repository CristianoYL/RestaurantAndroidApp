package com.example.cristianoyl.restaurant.activities;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.cristianoyl.restaurant.R;
import com.example.cristianoyl.restaurant.fragments.menu.MenuFragment;
import com.example.cristianoyl.restaurant.fragments.order.OrderFragment;
import com.example.cristianoyl.restaurant.fragments.restaurant.RestaurantFragment;
import com.example.cristianoyl.restaurant.models.Menu;
import com.example.cristianoyl.restaurant.models.Restaurant;
import com.example.cristianoyl.restaurant.stripe.CustomEphemeralKeyProvider;
import com.example.cristianoyl.restaurant.utils.Constants;
import com.stripe.android.CustomerSession;
import com.stripe.android.model.Source;
import com.stripe.android.view.PaymentMethodsActivity;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity
        implements RestaurantFragment.OnRestaurantInteractionListener,
                    MenuFragment.OnFragmentInteractionListener,
                    OrderFragment.OnOrderFragmentInteractionListener{

    private static final String TAG = "MainActivity";
    private String jwt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        if ( bundle != null ) {
            jwt = bundle.getString(Constants.ACCESS_TOKEN);
        } else if ( savedInstanceState != null ) {
            jwt = savedInstanceState.getString(Constants.ACCESS_TOKEN);
        } else {
            Log.e(TAG,"No JWT found.");
            return;
        }
        setContentView(R.layout.activity_main);
        CustomerSession.initCustomerSession(new CustomEphemeralKeyProvider(this.jwt));

        RestaurantFragment restaurantFragment = RestaurantFragment.newInstance();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frame_content,restaurantFragment, Constants.FRAGMENT_RESTAURANT);
        transaction.commit();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(Constants.ACCESS_TOKEN,this.jwt);
    }

    @Override
    protected void onDestroy() {
        CustomerSession.endCustomerSession();
        super.onDestroy();
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
        OrderFragment orderFragment = OrderFragment.newInstance(restaurant, orderMap,jwt);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frame_content,orderFragment, Constants.FRAGMENT_ORDER);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onSelectPaymentMethod() {
        Intent payIntent = PaymentMethodsActivity.newIntent(this);
        startActivityForResult(payIntent, 1000);
    }

//    /**
//     *  Send the card info to Stripe and retrieve a token back. Use the token for transaction later.
//     * @param card the card to encrypt.
//     */
//    @Override
//    public void onCardSelected(final Card card) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("Confirmation");
//        builder.setMessage("Are you sure to select the card ending in <" + card.getLast4() + "> for this order?");
//        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                Stripe stripe = new Stripe(MainActivity.this, Constants.STRIPE_PUBLISHABLE_KEY);
//                stripe.createToken(
//                        card,
//                        new TokenCallback() {
//                            public void onSuccess(Token token) {
//                                // Send token to your server
//                                Toast.makeText(MainActivity.this, "token:"+token.getId(), Toast.LENGTH_SHORT).show();
//                            }
//                            public void onError(Exception error) {
//                                // Show localized error message
//                                Toast.makeText(MainActivity.this,
//                                        "Error when tokenizing",
//                                        Toast.LENGTH_SHORT
//                                ).show();
//                            }
//                        }
//                );
//            }
//        });
//        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                // cancel, do nothing
//            }
//        });
//        builder.show();
//    }

//    /**
//     * Use the param card to add a new card for the user.
//     * @param card the new card to add for the user.
//     */
//    @Override
//    public void onAddCard(Card card) {
//        Intent payIntent = PaymentMethodsActivity.newIntent(this);
//        startActivityForResult(payIntent, 1000);
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000 && resultCode == RESULT_OK) {
            String selectedSource = data.getStringExtra(PaymentMethodsActivity.EXTRA_SELECTED_PAYMENT);
            Source source = Source.fromString(selectedSource);
            // This is the customer-selected source.
            // Note: it isn't possible for a null or non-card source to be returned at this time.
            FragmentManager fragmentManager = getSupportFragmentManager();
            OrderFragment orderFragment = (OrderFragment) fragmentManager.findFragmentByTag(Constants.FRAGMENT_ORDER);
            if ( orderFragment != null ) {
                orderFragment.notifySelectedPaymentMethod(source);
            }
        }
    }
}
