package org.abondar.experimental.uibasicsdemo.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import org.abondar.experimental.uibasicsdemo.R;

/**
 * Created by abondar on 12/3/16.
 */
public class DetailsFragment extends Fragment {
    private int mIndex = 0;

    public static DetailsFragment newInstance(int index) {
        Log.v(FragmentsActivity.TAG, "in DetailsFragment newInstance(" + index + ")");

        DetailsFragment df = new DetailsFragment();

        //Supply index input as an argument
        Bundle args = new Bundle();
        args.putInt("index", index);
        df.setArguments(args);
        return df;
    }

    public static DetailsFragment newInstance(Bundle bundle) {
        int index = bundle.getInt("index", 0);
        return newInstance(index);
    }

    @Override
    public void onCreate(Bundle bundle) {
        Log.v(FragmentsActivity.TAG, "in DetailsFragment onCreate. Bundle contains");

        if (bundle != null) {
            for (String key : bundle.keySet()) {
                Log.v(FragmentsActivity.TAG, "  " + key);
            }
        } else {
            Log.v(FragmentsActivity.TAG, "  myBundle is null");
        }

        super.onCreate(bundle);

        mIndex = getArguments().getInt("index", 0);
    }

    public int getShowIndex() {
        return mIndex;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.v(FragmentsActivity.TAG, "in DetailsFragment onCreateView. container =" + container);

        View v = inflater.inflate(R.layout.details, container, false);
        TextView text1 = (TextView) v.findViewById(R.id.text1);
        text1.setText(Champions.DESCR[mIndex]);
        return v;

    }
}
