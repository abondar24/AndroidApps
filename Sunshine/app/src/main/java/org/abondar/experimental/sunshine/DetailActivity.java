package org.abondar.experimental.sunshine;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null){
            Bundle args = new Bundle();
            args.putParcelable(DetailFragment.DETAIL_URI,getIntent().getData());
            args.putBoolean(DetailFragment.DETAIL_TRANSITION_ANIMATION,true);

            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.weather_detail_container, fragment)
                    .commit();

            supportPostponeEnterTransition();
        }
    }



}
