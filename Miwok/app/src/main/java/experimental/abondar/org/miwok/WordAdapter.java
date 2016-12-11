package experimental.abondar.org.miwok;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by abondar on 12/10/16.
 */
public class WordAdapter extends ArrayAdapter<Word>{

    public WordAdapter(Activity context, List<Word> words){
        super(context,0,words);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View listItemView = convertView;
        if(listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);
        }

        Word currentWord = getItem(position);

        TextView miwok = (TextView) listItemView.findViewById(R.id.miwok_tv);

        miwok.setText(currentWord.getMiwokTranslation());

        TextView defWord = (TextView) listItemView.findViewById(R.id.default_tv);

        defWord.setText(currentWord.getDefaultTranslation());

        return listItemView;
    }
}
