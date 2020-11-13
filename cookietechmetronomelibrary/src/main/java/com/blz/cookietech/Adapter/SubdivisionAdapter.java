package com.blz.cookietech.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.blz.cookietech.cookietechmetronomelibrary.R;

public class SubdivisionAdapter extends RecyclerView.Adapter<SubdivisionAdapter.SubdivisionViewHolder> {

    private static final String TAG = "SubdivisionAdapter";

    private int recyclerViewHeight;

    private RecyclerView subDivisionRecyclerView;
    private int rightTimeSignature;
    private Context context;
    private static int icon_count = 1;

    private final int[][] drawables = new int[][]{{R.mipmap.sub_a_1,R.mipmap.sub_a_2,R.mipmap.sub_a_3,R.mipmap.sub_a_4},{R.mipmap.sub_b_1,R.mipmap.sub_b_2,R.mipmap.sub_b_3,R.mipmap.sub_b_4},{R.mipmap.sub_c_1,R.mipmap.sub_c_2,R.mipmap.sub_c_3,R.mipmap.sub_c_4},{R.mipmap.sub_d_1,R.mipmap.sub_d_2,R.mipmap.sub_d_3,R.mipmap.sub_d_4}};




    public SubdivisionAdapter(Context context, RecyclerView recyclerView, int rightTimeSignature){
        this.context = context;
        this.subDivisionRecyclerView = recyclerView;
        this.rightTimeSignature = rightTimeSignature;

    }


    @NonNull
    @Override
    public SubdivisionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.subdivision_item,parent,false);
        recyclerViewHeight = subDivisionRecyclerView.getHeight();
        return new SubdivisionViewHolder(view,recyclerViewHeight);
    }

    @Override
    public void onBindViewHolder(@NonNull SubdivisionViewHolder holder, int position) {

        //holder.rootViewGroup.setMinHeight(recyclerViewHeight/3);

        if(position ==0 || position == 5){
            holder.subdivision_icon.setImageResource(0);
        }else{
            int drawableId = drawables[rightTimeSignature-1][position-1];
            holder.subdivision_icon.setImageResource(drawableId);
            holder.rootViewGroup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("akash_debug", "onClick: ");
                    setSelectedItemToMiddle(holder.rootViewGroup);
                }
            });
        }



    }

    private void setSelectedItemToMiddle(View view) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int viewLocation = location[1];
        int[] subDivisionRecyclerViewLocation = new int[2];
        subDivisionRecyclerView.getLocationOnScreen(subDivisionRecyclerViewLocation);
        int recyclerCenterLocation = subDivisionRecyclerViewLocation[1] + (recyclerViewHeight/2);
        int viewCenter = location[1] + (view.getHeight()/2);
        int scroll = recyclerCenterLocation - viewCenter;


        subDivisionRecyclerView.postOnAnimation(new Runnable() {
            @Override
            public void run() {
                Log.d("akash_debug_test_1", "run: scroll" + scroll);
                subDivisionRecyclerView.smoothScrollBy(0, -scroll);
            }
        });
    }

    @Override
    public int getItemCount() {
        return 6;
    }

    public class SubdivisionViewHolder extends RecyclerView.ViewHolder{

        ConstraintLayout rootViewGroup;
        ImageView subdivision_icon;

        public SubdivisionViewHolder(@NonNull View itemView, int recyclerViewHeight) {
            super(itemView);
            rootViewGroup = itemView.findViewById(R.id.rootViewGroup);
            rootViewGroup.setMinHeight(recyclerViewHeight/3);
            subdivision_icon = itemView.findViewById(R.id.subdivision_icon);
        }
    }


    public void setRightTimeSignature(int rightTimeSignature) {
        this.rightTimeSignature = rightTimeSignature;
        notifyDataSetChanged();
    }

}
