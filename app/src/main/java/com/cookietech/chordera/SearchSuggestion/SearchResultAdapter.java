package com.cookietech.chordera.SearchSuggestion;

import android.content.Context;
import android.graphics.ImageDecoder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.cookietech.chordera.Landing.NewItemAdapter;
import com.cookietech.chordera.R;
import com.cookietech.chordera.appcomponents.ConnectionManager;
import com.cookietech.chordera.architecture.MainViewModel;
import com.cookietech.chordera.models.SearchData;

import java.util.ArrayList;

public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.SearchResultViewHolder> {

    private ArrayList<SearchData> searchData = new ArrayList<>();
    private MainViewModel mainViewModel;
    private Context context;

    public SearchResultAdapter(Context context,MainViewModel mainViewModel) {
        this.context = context;
        this.mainViewModel = mainViewModel;
    }

    public void setSearchData(ArrayList<SearchData> searchData) {
        this.searchData = searchData;

        Log.d("search_result", "setSearchData: "+ this.searchData.size());
    }

    @NonNull
    @Override
    public SearchResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.song_row_view, parent, false);
        return new SearchResultViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchResultViewHolder holder, int position) {
            Log.d("search_result", "onBindViewHolder: " + position);
            SearchData data = searchData.get(position);
            holder.bind(data);
    }

    @Override
    public int getItemCount() {
        Log.d("search_result", "reset: ");
        return searchData.size();
    }

    public void reset() {
        Log.d("search_result", "reset: ");
        searchData.clear();
        notifyDataSetChanged();
    }

    public class SearchResultViewHolder extends RecyclerView.ViewHolder{

        public TextView tvViews;
        public TextView tvSongName;
        public TextView tvArtistName;

        public SearchResultViewHolder(@NonNull View itemView) {
            super(itemView);
            tvViews = itemView.findViewById(R.id.views_count);
            tvSongName = itemView.findViewById(R.id.txt_song_tittle);
            tvArtistName = itemView.findViewById(R.id.txt_artist);
        }


        public void bind(SearchData data){
            tvSongName.setText(data.getSong_name());
            tvArtistName.setText(data.getArtist_name());
            tvViews.setText(String.valueOf(data.getViews()));
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!ConnectionManager.isOnline(context)){
                        Toast.makeText(context,"No internet connection",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    mainViewModel.downloadSearchedDataAndNavigate(data);
                }
            });
        }
    }
}
