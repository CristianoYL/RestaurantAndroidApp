//package com.example.cristianoyl.restaurant.fragments.payment;
//
//import android.support.v7.widget.RecyclerView;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//
//import com.example.cristianoyl.restaurant.R;
//import com.example.cristianoyl.restaurant.fragments.payment.CardFragment.OnCardFragmentInteractionListener;
//import com.stripe.android.model.Card;
//
//import java.util.List;
//
//public class MyCardRecyclerViewAdapter extends RecyclerView.Adapter<MyCardRecyclerViewAdapter.ViewHolder> {
//
//    private final List<Card> cardList;
//    private final OnCardFragmentInteractionListener mListener;
//
//    public MyCardRecyclerViewAdapter(List<Card> cards, OnCardFragmentInteractionListener listener) {
//        cardList = cards;
//        mListener = listener;
//    }
//
//    @Override
//    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext())
//                .inflate(R.layout.layout_card_item, parent, false);
//        return new ViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(final ViewHolder holder, int position) {
//        holder.card = cardList.get(position);
//        holder.tvCardNumber.setText(cardList.get(position).getNumber());
//        holder.tvExpirationDate.setText(holder.card.getExpMonth()+"/"+holder.card.getExpYear());
//
//        holder.view.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (null != mListener) {
//                    // Notify the active callbacks interface (the activity, if the
//                    // fragment is attached to one) that an item has been selected.
//                    mListener.onCardSelected(holder.card);
//                }
//            }
//        });
//    }
//
//    @Override
//    public int getItemCount() {
//        return cardList.size();
//    }
//
//    public class ViewHolder extends RecyclerView.ViewHolder {
//        public final View view;
//        public final TextView tvCardNumber;
//        public final TextView tvExpirationDate;
//        public Card card;
//
//        public ViewHolder(View view) {
//            super(view);
//            this.view = view;
//            tvCardNumber = view.findViewById(R.id.tv_card_number);
//            tvExpirationDate = view.findViewById(R.id.tv_expiration);
//        }
//
//        @Override
//        public String toString() {
//            return super.toString() + " <Card Ending in:" + tvCardNumber.getText() + ">";
//        }
//    }
//}
