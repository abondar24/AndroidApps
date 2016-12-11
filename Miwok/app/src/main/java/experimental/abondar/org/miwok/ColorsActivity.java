package experimental.abondar.org.miwok;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class ColorsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.word_list);


        List<Word> colors = new ArrayList<>();


        colors.add( new Word("weṭeṭṭi","red",R.drawable.color_red));
        colors.add( new Word("chokokki","green",R.drawable.color_green));
        colors.add( new Word("ṭakaakki","brown",R.drawable.color_brown));
        colors.add( new Word("ṭopoppi","gray",R.drawable.color_gray));
        colors.add( new Word("kululli","black",R.drawable.color_black));
        colors.add( new Word("kelelli","white",R.drawable.color_white));
        colors.add( new Word("ṭopiisә","yellow",R.drawable.color_dusty_yellow));
        colors.add( new Word("chiwiiṭә","mustard yellow",R.drawable.color_mustard_yellow));



        ListView rootView =(ListView) this.findViewById(R.id.rootView);

        WordAdapter wordAdapter = new WordAdapter(this,  colors);
        rootView.setAdapter(wordAdapter);
    }
}
