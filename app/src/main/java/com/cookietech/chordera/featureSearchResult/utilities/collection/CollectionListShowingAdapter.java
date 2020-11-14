package com.cookietech.chordera.featureSearchResult.utilities.collection;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.cookietech.chordera.R;
import com.cookietech.chordera.appcomponents.CookieTechFragmentManager;
import com.cookietech.chordera.databinding.FragmentSearchResultRecyclerViewBinding;
import com.cookietech.chordera.featureSearchResult.utilities.BaseViewHolder;
import com.cookietech.chordera.models.Collection;

import java.util.ArrayList;
import java.util.List;

public class CollectionListShowingAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private FragmentListener fragmentListener;

    private static final int VIEW_TYPE_LOADING = 0;
    private static final int VIEW_TYPE_NORMAL = 1;
    private boolean isLoaderVisible = false;
    private FragmentSearchResultRecyclerViewBinding binding;
    private List<Collection> collectionList;
    CookieTechFragmentManager cookieTechFragmentManager;

    public CollectionListShowingAdapter(ArrayList<Collection> collectionList, FragmentSearchResultRecyclerViewBinding fragmentSearchResultRecyclerViewBinding) {
        this.binding = fragmentSearchResultRecyclerViewBinding;
        this.collectionList = collectionList;
        cookieTechFragmentManager = CookieTechFragmentManager.getInstance();
    }



    // Create new views (invoked by the layout manager)
    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_NORMAL:
                return new ViewHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.collection_row_view, parent, false));
            case VIEW_TYPE_LOADING:
                return new ProgressHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading, parent, false));
            default:
                return null;
        }
    }


    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        holder.onBind(position);
    }


    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position, @NonNull List<Object> payloads) {
        if(!payloads.isEmpty()){
            holder.onBind(position, payloads);
        }else{
            super.onBindViewHolder(holder,position,payloads);
        }
    }



    @Override
    public int getItemViewType(int position) {
        if (isLoaderVisible) {
            return position == collectionList.size() - 1 ? VIEW_TYPE_LOADING : VIEW_TYPE_NORMAL;
        } else {
            return VIEW_TYPE_NORMAL;
        }
    }

    @Override
    public int getItemCount() {
        return collectionList == null ? 0 : collectionList.size();
    }

    public void addItems(List<Collection> collections) {
        collectionList.addAll(collections);
        notifyDataSetChanged();
    }

    public void addLoading() {
        isLoaderVisible = true;
        collectionList.add(new Collection());
        notifyItemInserted(collectionList.size() - 1);
    }

    public void removeLoading() {
        isLoaderVisible = false;
        int position = collectionList.size() - 1;
        Collection item = getItem(position);
        if (item != null) {
            collectionList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clear() {
        collectionList.clear();
        notifyDataSetChanged();
    }

    Collection getItem(int position) {
        return collectionList.get(position);
    }
    public ArrayList<Collection> getData() {
        return (ArrayList<Collection>) this.collectionList;
    }
    public class ViewHolder extends BaseViewHolder {
        public TextView name, view;
        ConstraintLayout rowLayout;
        public ViewHolder(View v) {
            super(v);
            name = v.findViewById(R.id.txt_collection_name);
            view = v.findViewById(R.id.views_count);
            rowLayout = v.findViewById(R.id.rowLayout);

            ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) rowLayout.getLayoutParams();
            params.height = (int) (binding.recyclerView.getWidth()/7.2);
            rowLayout.setLayoutParams(params);
            //width/height = 7.2    ratio was calculated from xd design

            rowLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fragmentListener.onItemClick("avoid rafa");
                }
            });
        }

        protected void clear() {

        }

        public void onBind(int position) {
            super.onBind(position);
            Collection item = collectionList.get(position);

            name.setText(item.getName());
            view.setText(item.getView());
        }
        public void onBind(int position, List<Object> payloads)
        {
            if (payloads.isEmpty()){
                //
                super.onBind(position, payloads);
                Collection item = collectionList.get(position);

                name.setText(item.getName());
                view.setText(item.getView());
            }
            else {
                Bundle o = (Bundle) payloads.get(0);
                for (String key : o.keySet()) {
                    if(key.equals("name")){
                        //Toast.makeText(name.getContext(), "Collection "+position+" : Tittle Changed", Toast.LENGTH_SHORT).show();;
                        name.setText(collectionList.get(position).getName());
                    }
                    if(key.equals("view")){
                       // Toast.makeText(itemView.getContext(), "Collection "+position+" : View Changed", Toast.LENGTH_SHORT).show();;
                        view.setText(collectionList.get(position).getView());
                    }
                }
            }
        }
    }

    public class ProgressHolder extends BaseViewHolder {

        @Override
        protected void clear() {
        }
        public ProgressHolder(View v) {
            super(v);
        }
    }

    public void onNewData(ArrayList<Collection> newData) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new CollectionDiffUtilCallback(newData, (ArrayList<Collection>) collectionList));
        diffResult.dispatchUpdatesTo(this);
        this.collectionList.clear();
        this.collectionList.addAll(newData);
    }

    public interface FragmentListener
    {
        void onItemClick(String id);
    }

    public void setOnclickItemListener(FragmentListener listener)
    {
        fragmentListener = listener;
    }
}
