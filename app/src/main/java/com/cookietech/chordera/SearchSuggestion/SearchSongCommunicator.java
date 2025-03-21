package com.cookietech.chordera.SearchSuggestion;

import java.io.Serializable;

public interface  SearchSongCommunicator extends Serializable {
    void onSearchedSongSelected();

    void onBackButtonClicked();
}
