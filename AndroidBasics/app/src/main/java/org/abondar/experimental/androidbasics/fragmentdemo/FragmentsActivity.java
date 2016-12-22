package org.abondar.experimental.androidbasics.fragmentdemo;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import org.abondar.experimental.androidbasics.R;


public class FragmentsActivity extends FragmentActivity {

    public static String TAG = "Fragment demo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragments);
    }


    public boolean isMultiPane() {
        return getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
    }

    public void showDetails(int index) {
        Log.v(TAG, "showDetails(" + index + ")");

        if (isMultiPane()) {
            DetailsFragment details = (DetailsFragment) getFragmentManager().findFragmentById(R.id.details);

            if ((details == null) || (details.getShowIndex() != index)) {
                //Show selection on another fragment
                details = DetailsFragment.newInstance(index);

                //Do replacement
                Log.v(TAG,"about to run FragmentTransaction...");
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.replace(R.id.details, details);
                ft.commit();
            }
        } else {
           // Otherwise launch a new activity to display fragment with selected text
            Intent intent = new Intent();
            intent.setClass(this, DetailsActivity.class);
            intent.putExtra("index",index);
            startActivity(intent);
        }
    }
}
