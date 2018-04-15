package com.example.george.ark;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by George on 11.01.2018.
 */

public class UserViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    View mView;
    TextView username;
    ItemClickListerner itemClickListerner;

    public void setItemClickListerner(ItemClickListerner itemClickListerner) {
        this.itemClickListerner = itemClickListerner;
    }

    public UserViewHolder(View itemView) {
        super(itemView);
        mView = itemView;
        username = (TextView) itemView.findViewById(R.id.text_email);
    }





    @Override
    public void onClick(View v) {

        itemClickListerner.onClickItem(v,getAdapterPosition());
    }
}
