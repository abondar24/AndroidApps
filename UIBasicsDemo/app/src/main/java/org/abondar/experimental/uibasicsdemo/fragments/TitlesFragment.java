package org.abondar.experimental.uibasicsdemo.fragments;

import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * Created by abondar on 12/3/16.
 */
public class TitlesFragment extends ListFragment {
    private FragmentsActivity fragmentsActivity = null;

    int curCheckPos = 0;

    @Override
    public void onAttach(Activity fragmentsActivity) {
        Log.v(FragmentsActivity.TAG, "TitlesFragment onAttach; activity id" + fragmentsActivity);
        super.onAttach(fragmentsActivity);
        this.fragmentsActivity = (FragmentsActivity) fragmentsActivity;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.v(FragmentsActivity.TAG, "TitlesFragment onActivityCreated, savedState contains:");
        if (savedInstanceState != null) {
            for (String key : savedInstanceState.keySet()) {
                Log.v(FragmentsActivity.TAG, "   " + key);
            }
        } else {
            Log.v(FragmentsActivity.TAG, "  savedInstanceState is null");
        }
        super.onActivityCreated(savedInstanceState);


        //Populate list with titles
        setListAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, Champions.CHAMPS));

        if (savedInstanceState != null) {
            curCheckPos = savedInstanceState.getInt("curChoice", 0);
        }

        ListView lv = getListView();
        lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        lv.setSelection(curCheckPos);

        fragmentsActivity.showDetails(curCheckPos);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.v(FragmentsActivity.TAG, "TitleSFragment onSaveInstanceState");
        super.onSaveInstanceState(outState);
        outState.putInt("curChoice", curCheckPos);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Log.v(FragmentsActivity.TAG, "TitlesFragment onListItemClick. pos =" + position);
        fragmentsActivity.showDetails(position);
        curCheckPos = position;
    }

    @Override
    public void onDetach() {
        Log.v(FragmentsActivity.TAG, "Titles Fragment onDetach");
        super.onDetach();
        fragmentsActivity = null;
    }
}
