//package com.example.cristianoyl.restaurant.fragments.payment;
//
//import android.content.Context;
//import android.os.Bundle;
//import android.support.v4.app.Fragment;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.Toast;
//
//import com.example.cristianoyl.restaurant.R;
//import com.stripe.android.model.Card;
//import com.stripe.android.view.CardInputWidget;
//
//import java.util.ArrayList;
//
///**
// * A fragment representing a list of Items.
// * <p/>
// * Activities containing this fragment MUST implement the {@link OnCardFragmentInteractionListener}
// * interface.
// */
//public class CardFragment extends Fragment {
//
//    private static final String ARG_USER_ID = "user_id";
//    private int mUserID = 1;
//    private OnCardFragmentInteractionListener mListener;
//
//    CardInputWidget cardInputWidget;
//    View addCardView;
//
//    /**
//     * Mandatory empty constructor for the fragment manager to instantiate the
//     * fragment (e.g. upon screen orientation changes).
//     */
//    public CardFragment() {
//    }
//
//    public static CardFragment newInstance(int userID) {
//        CardFragment fragment = new CardFragment();
//        Bundle args = new Bundle();
//        args.putInt(ARG_USER_ID, userID);
//        fragment.setArguments(args);
//        return fragment;
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        if (getArguments() != null) {
//            mUserID = getArguments().getInt(ARG_USER_ID);
//        }
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_card_list, container, false);
//
//        cardInputWidget = view.findViewById(R.id.card_input);
//        addCardView = view.findViewById(R.id.layout_add_card);
//
//        Button btnBack = view.findViewById(R.id.btn_back);
//        btnBack.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mListener.onBackButtonPressed();
//            }
//        });
//
//        Button btnAddCard = view.findViewById(R.id.btn_add_card);
//        btnAddCard.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                if ( cardInputWidget.getVisibility() == View.VISIBLE ) {
////                    Card cardToSave = cardInputWidget.getCard();
////                    if ( cardToSave == null ) {
////                        Toast.makeText(getContext(), "Invalid Card Information!", Toast.LENGTH_SHORT).show();
////                    } else {
////                        cardInputWidget.setVisibility(View.GONE);
////                        addCardView.setBackgroundResource(0);   // remove background
////                        mListener.onAddCard(cardToSave);
////                    }
////                } else {
////                    cardInputWidget.setVisibility(View.VISIBLE);
////
////                    addCardView.setBackgroundResource(R.drawable.edge_primary_rounded);
////                }
//                mListener.onAddCard(null);
//            }
//        });
//
//        ArrayList<Card> cardList = new ArrayList<>();
//        // dummy data for testing
//        Card testCard1 = new Card("4242424242424242",6,21,"312");
//        Card testCard2 = new Card("2424242424242424",11,22,"012");
//        for ( int i = 0; i < 10; i ++ ) {
//            cardList.add(testCard1);
//            cardList.add(testCard2);
//        }
//        // Set the adapter
//        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
//        Context context = view.getContext();
//        recyclerView.setLayoutManager(new LinearLayoutManager(context));
//        recyclerView.setAdapter(new MyCardRecyclerViewAdapter(cardList, mListener));
//        return view;
//    }
//
//
//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnCardFragmentInteractionListener) {
//            mListener = (OnCardFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnCardFragmentInteractionListener");
//        }
//    }
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mListener = null;
//    }
//
//    /**
//     * This interface must be implemented by activities that contain this
//     * fragment to allow an interaction in this fragment to be communicated
//     * to the activity and potentially other fragments contained in that
//     * activity.
//     * <p/>
//     * See the Android Training lesson <a href=
//     * "http://developer.android.com/training/basics/fragments/communicating.html"
//     * >Communicating with Other Fragments</a> for more information.
//     */
//    public interface OnCardFragmentInteractionListener {
//        void onCardSelected(Card card);
//        void onAddCard(Card card);
//        void onBackButtonPressed();
//    }
//}
