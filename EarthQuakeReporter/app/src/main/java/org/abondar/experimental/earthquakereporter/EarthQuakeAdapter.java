package org.abondar.experimental.earthquakereporter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;


/**
 * Created by abondar on 12/18/16.
 */
public class EarthQuakeAdapter  extends ArrayAdapter<EarthQuake> {

    public EarthQuakeAdapter(Activity context, List<EarthQuake> earthQuakes){
        super(context,0,earthQuakes);


    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View listItemView = convertView;
        if(listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);
        }

        EarthQuake curEarthQuake = getItem(position);

        TextView magnitude = (TextView)listItemView.findViewById(R.id.mag_tv);

        magnitude.setText(String.valueOf(curEarthQuake.getMagnitude()));

        TextView location = (TextView) listItemView.findViewById(R.id.location_tv);

        location.setText(curEarthQuake.getLocation());

        TextView date = (TextView) listItemView.findViewById(R.id.date_tv);

        date.setText(curEarthQuake.getDate());


        return listItemView;
    }
}
