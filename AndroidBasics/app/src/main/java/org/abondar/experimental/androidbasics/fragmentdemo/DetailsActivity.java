package org.abondar.experimental.androidbasics.fragmentdemo;

import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import org.abondar.experimental.androidbasics.R;

public class DetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(FragmentsActivity.TAG, "DetailsActivity onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            finish();
            return;
        }

        if (getIntent() !=null){
            DetailsFragment details = DetailsFragment.newInstance(getIntent().getExtras());

            getFragmentManager().beginTransaction().add(android.R.id.content,details).commit();
        }
    }
}
