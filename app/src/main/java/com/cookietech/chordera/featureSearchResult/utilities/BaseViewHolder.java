package com.cookietech.chordera.featureSearchResult.utilities;

import android.view.View;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public abstract class BaseViewHolder extends RecyclerView.ViewHolder {

    private int mCurrentPosition;

    public BaseViewHolder(View itemView) {
        super(itemView);
    }


    public void onBind(int position) {
        mCurrentPosition = position;
    }

    public void onBind( int position, List<Object> payloads) {
        mCurrentPosition = position;
    }

    public int getCurrentPosition() {
        return mCurrentPosition;
    }
}