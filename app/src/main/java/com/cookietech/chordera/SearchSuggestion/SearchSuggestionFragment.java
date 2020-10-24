package com.cookietech.chordera.SearchSuggestion;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;

import com.cookietech.chordera.databinding.FragmentSearchSuggestionBinding;
import com.cookietech.chordera.fragments.ChorderaFragment;



public class SearchSuggestionFragment extends ChorderaFragment {

    FragmentSearchSuggestionBinding binding;
    Observer<String> searchKeyWordObservable;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSearchSuggestionBinding.inflate(getLayoutInflater(),container,false);
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        searchKeyWordObservable =  new Observer<String>() {
            @Override
            public void onChanged(String s) {
                Log.d("akash_debug", "onChanged: "+ s);
            }
        };
        mainViewModel.getObservableSearchKeyword().observe(fragmentLifecycleOwner,searchKeyWordObservable);
    }


    @Override
    public void onStart() {
        super.onStart();
    }



}
