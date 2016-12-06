package org.abondar.experimental.uibasicsdemo;

import android.app.SearchManager;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class SearchResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        final Intent queryIntent = getIntent();
        doSearch(queryIntent);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        final Intent queryIntent = getIntent();
        doSearch(queryIntent);
    }

    private void doSearch(final Intent queryIntent){
     final String queryAction = queryIntent.getAction();
     if (!(Intent.ACTION_SEARCH.equals(queryAction))){
         Log.d("search", "intent not for search");
         return;
     }
     final String queryString = queryIntent.getStringExtra(SearchManager.QUERY);
     Log.d("search",queryString);
    }


}
