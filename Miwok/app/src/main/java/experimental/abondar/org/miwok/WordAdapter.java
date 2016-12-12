package experimental.abondar.org.miwok;

import android.app.Activity;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by abondar on 12/10/16.
 */
public class WordAdapter extends ArrayAdapter<Word>{

    private int backgroundColor;

    public WordAdapter(Activity context, List<Word> words, int backgroundColor){
        super(context,0,words);
        this.backgroundColor = backgroundColor;

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

        ImageView pic = (ImageView) listItemView.findViewById(R.id.image);
        if (currentWord.hasImage()) {

            pic.setImageResource(currentWord.getImageResourceId());
        } else {
            pic.setVisibility(View.GONE);
        }


        View textContainer = listItemView.findViewById(R.id.text_container);

       int color = ContextCompat.getColor(getContext(), backgroundColor);
       textContainer.setBackgroundColor(color);

        return listItemView;
    }
}
