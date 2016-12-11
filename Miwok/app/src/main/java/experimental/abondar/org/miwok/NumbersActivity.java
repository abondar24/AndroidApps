package experimental.abondar.org.miwok;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class NumbersActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.word_list);

        List<Word> numbers = new ArrayList<>();

        numbers.add( new Word("lutti","one",R.drawable.number_one));
        numbers.add( new Word("otiiko","two",R.drawable.number_two));
        numbers.add( new Word("tolookosu","three",R.drawable.number_three));
        numbers.add( new Word("oyyisa","four",R.drawable.number_four));
        numbers.add( new Word("massokka","five",R.drawable.number_five));
        numbers.add( new Word("temmokka","six",R.drawable.number_six));
        numbers.add( new Word("kenekaku","seven",R.drawable.number_seven));
        numbers.add( new Word("kawinta","eight",R.drawable.number_eight));
        numbers.add( new Word("wo e","nine",R.drawable.number_nine));
        numbers.add( new Word("na'aacha","ten",R.drawable.number_ten));



        ListView rootView =(ListView) this.findViewById(R.id.rootView);

        WordAdapter wordAdapter = new WordAdapter(this,  numbers);
        rootView.setAdapter(wordAdapter);
    }
}
