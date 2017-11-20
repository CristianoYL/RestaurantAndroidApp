# Introduction
This is an Android online restaurant ordering app. User may register and login into their account and starts online ordering food. The app communicates to a set of REST APIs exposed by server. A sample back-end providing this service can be found at [my other GitHub repo here.](https://github.com/CristianoYL/RestaurantAppAPI)
# Configurations
## IDE
The project is developed using **Android Studio 3.0**. It is recommended that you use **Android Studio** instead of other IDEs for simpler project import. [You may download Android Studio from the official website here](https://developer.android.com/studio/index.html).

## Dependencies
This service relies on serveral other services as well. For example, it uses [Google Maps API](https://developers.google.com/maps/documentation/android-api/) for location services and maps, [Gson](https://sites.google.com/site/gson/gson-user-guide) for data serialization, more specificly, for jsonifying data, and [Stripe](https://stripe.com/docs) for live credit card charges etc.

Since **Android Studio** uses **gradle build**, you don't need to worry about dependencies when importing the code.
# SDK
* The minimum SDK is 15
* The target and compile SDK is 26
* All details can be found in the module's ```build.gradle``` file

# User Guide
Here's a basic demo how this app works.

## Login/Register
In order to use the app, first register an account with your email and password.

<img src="https://github.com/CristianoYL/RestaurantAndroidApp/blob/master/screenshot/Screenshot_20171120-175008.png" width="200">

## Restaurant Listing
After logging in, you will see a listing of all restaurants. Basic info, such as delivery fee, promotions and minimum charges will be presented here.

<img src="https://github.com/CristianoYL/RestaurantAndroidApp/blob/master/screenshot/Screenshot_20171120-175511.png" width="200">

## Menu & Order
After selecting a restaurant, you'll be navigated to the restaurant's menu page. You may edit your order using the "-" and "+" buttons. You can see a more detailed description for each menu item by clicking on them, a view containing the picture and detail of the item will expand/fold on click.

<img src="https://github.com/CristianoYL/RestaurantAndroidApp/blob/master/screenshot/Screenshot_20171120-175606.png" width="200"> <img src="https://github.com/CristianoYL/RestaurantAndroidApp/blob/master/screenshot/Screenshot_20171120-175533.png" width="200">

## Placing Your Order
After finishing your selections, click on the price tag on the upper-right corner to proceed to check out. You'll be navigated to a order summary page where you may confirm your order, provide delivery and payment details and finally place the order.

<img src="https://github.com/CristianoYL/RestaurantAndroidApp/blob/master/screenshot/Screenshot_20171120-175629.png" width="200">

There is built-in Google Maps that locates you to help you fill in the delivery address, however, you can manually input your desired address as well, and we will provide you suggestions according to your input to make sure your address is valid.

<img src="https://github.com/CristianoYL/RestaurantAndroidApp/blob/master/screenshot/Screenshot_20171120-175700.png" width="200">

When clicking on "PAYMENT METHOD" button, you'll be prompt with a new window asking for your Card detail. If you have already added a card to your account before, you can simply select from the list.

<img src="https://github.com/CristianoYL/RestaurantAndroidApp/blob/master/screenshot/Screenshot_20171120-183507.png" width="200"> <img src="https://github.com/CristianoYL/RestaurantAndroidApp/blob/master/screenshot/Screenshot_20171120-175735.png" width="200">

If you have concerns on the security of your card info, [here's how our service makes sure it is safe.](https://github.com/CristianoYL/RestaurantAppAPI#security-of-payment)

After filling all necessary information on your order, you can place your order now!

<img src="https://github.com/CristianoYL/RestaurantAndroidApp/blob/master/screenshot/Screenshot_20171120-175818.png" width="200">

