package org.abondar.experimental.androidbasics;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by abondar on 12/21/16.
 */
public class Palette extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle icicle) {
        View v = inflater.inflate(R.layout.palette, container, false);
        return v;
    }
}
