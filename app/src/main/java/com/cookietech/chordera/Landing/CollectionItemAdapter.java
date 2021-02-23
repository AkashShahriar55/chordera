package com.cookietech.chordera.Landing;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ContentView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cookietech.chordera.Landing.Collection.CollectionFragment;
import com.cookietech.chordera.R;
import com.cookietech.chordera.appcomponents.ConnectionManager;
import com.cookietech.chordera.appcomponents.NavigatorTags;
import com.cookietech.chordera.architecture.MainViewModel;
import com.cookietech.chordera.models.CollectionsPOJO;

import java.util.ArrayList;

public class CollectionItemAdapter extends RecyclerView.Adapter<CollectionItemAdapter.CollectionItemViewHolder> {
    private final Context context;
    RecyclerView collectionItemRecyclerView;
    ArrayList<CollectionsPOJO> collections = new ArrayList<>();
    MainViewModel mainViewModel;

    public CollectionItemAdapter(Context context,RecyclerView collectionItemRecyclerView, MainViewModel mainViewModel) {
        this.collectionItemRecyclerView = collectionItemRecyclerView;
        this.mainViewModel = mainViewModel;
        this.context = context;
    }

    @NonNull
    @Override
    public CollectionItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.layout_collection_item, parent, false);
        int size = collectionItemRecyclerView.getHeight();
        return new CollectionItemViewHolder(listItem,size);
    }

    public void setCollections(ArrayList<CollectionsPOJO> collections) {
        this.collections = collections;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull CollectionItemViewHolder holder, int position) {
        holder.bind(collections.get(position));
    }

    @Override
    public int getItemCount() {
        return collections.size();
    }

    public class CollectionItemViewHolder extends RecyclerView.ViewHolder{

        public CardView newItemHolder;
        public ImageView background;
        public TextView collectionName;
        public CollectionItemViewHolder(@NonNull View itemView,int size) {
            super(itemView);
            newItemHolder = itemView.findViewById(R.id.cv_new_item_holder);
            background = itemView.findViewById(R.id.iv_background);
            collectionName = itemView.findViewById(R.id.collection_name);
//            newItemHolder.getLayoutParams().width = (int) (size*1.4);
//            newItemHolder.getLayoutParams().height= size;


        }

        public void bind(CollectionsPOJO collectionsPOJO) {
            collectionName.setText(collectionsPOJO.getCollection_name());
            Glide.with(itemView).load(collectionsPOJO.getImage_url())
                    .placeholder(R.drawable.placeholder_collection)
                    .error(R.drawable.placeholder_collection).thumbnail(0.5f).centerCrop().into(background);
            newItemHolder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!ConnectionManager.isOnline(context)){
                        Toast.makeText(context,"No internet connection",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Log.d("collection_debug", "onClick: "+ collectionsPOJO.getSong_id().size());
                    mainViewModel.setNavigation(NavigatorTags.COLLECTION_FRAGMENT, CollectionFragment.createArgs(collectionsPOJO));
                }
            });
        }
    }
}
