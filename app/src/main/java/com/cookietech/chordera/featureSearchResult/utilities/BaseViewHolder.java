package com.cookietech.chordera.featureSearchResult.utilities;

import android.view.View;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public abstract class BaseViewHolder extends RecyclerView.ViewHolder {

    private int mCurrentPosition;

    public BaseViewHolder(View itemView) {
        super(itemView);
    }

    protected abstract void clear();

    public void onBind(int position) {
        mCurrentPosition = position;
        clear();
    }

    public void onBind( int position, List<Object> payloads) {
        mCurrentPosition = position;
        clear();
    }

    public int getCurrentPosition() {
        return mCurrentPosition;
    }
}