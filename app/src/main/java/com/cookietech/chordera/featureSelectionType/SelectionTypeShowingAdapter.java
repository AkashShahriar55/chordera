package com.cookietech.chordera.featureSelectionType;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.cookietech.chordera.R;
import com.cookietech.chordera.appcomponents.NavigatorTags;
import com.cookietech.chordera.architecture.MainViewModel;
import com.cookietech.chordera.databinding.FragmentSelectionTypeBinding;
import com.cookietech.chordera.models.SelectionType;


import java.util.ArrayList;
import java.util.List;

public class SelectionTypeShowingAdapter extends RecyclerView.Adapter<SelectionTypeShowingAdapter.ViewHolder> {
    FragmentSelectionTypeBinding binding;
    private List<SelectionType> selectionTypeList;
    MainViewModel mainViewModel;

    public SelectionTypeShowingAdapter(ArrayList<SelectionType> selectionTypeList, FragmentSelectionTypeBinding fragmentSelectionTypeBinding,MainViewModel mainViewModel) {
        this.binding = fragmentSelectionTypeBinding;
        this.selectionTypeList = selectionTypeList;
        this.mainViewModel = mainViewModel;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.selection_type_row_view, parent, false);
        return new SelectionTypeShowingAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final SelectionType selectionType = selectionTypeList.get(position);
        holder.setPosition(position);
        holder.seletionName.setText(selectionType.getSelectionName());
    }




    @Override
    public int getItemCount() {
        return selectionTypeList == null ? 0 : selectionTypeList.size();
    }

    public void addItems(List<SelectionType> selectionTypes) {
        selectionTypeList.clear();
        selectionTypeList.addAll(selectionTypes);
        notifyDataSetChanged();
    }



    SelectionType getItem(int position) {
        return selectionTypeList.get(position);
    }

    public ArrayList<SelectionType> getData() {
        return (ArrayList<SelectionType>) this.selectionTypeList;
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView seletionName;
        public ConstraintLayout rowLayout;
        private int position;

        public ViewHolder(View v) {
            super(v);

            seletionName = v.findViewById(R.id.selection_type);
            rowLayout = v.findViewById(R.id.rowLayout);
            ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) rowLayout.getLayoutParams();
            //Log.e("ratio h/w", String.valueOf(binding.recyclerView.getWidth()/params.height));
            params.height = (int) (binding.recyclerView.getWidth()/7.2);
            rowLayout.setLayoutParams(params);
            //width/height = 7.2    ratio was calculated from xd design
            rowLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mainViewModel.setSelectedTab(selectionTypeList.get(position));
                    mainViewModel.setNavigation(NavigatorTags.CHORD_DISPLAY_FRAGMENT,1);
                }
            });
        }

        public void setPosition(int position) {
            this.position = position;
        }
    }




}

