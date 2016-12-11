package experimental.abondar.org.miwok;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class PhrasesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.word_list);


        List<Word> phrases = new ArrayList<>();


        phrases.add(new Word("minto wuksus", "Where are you going?"));
        phrases.add(new Word("tinnә oyaase'nә", "What is your name?"));
        phrases.add(new Word("oyaaset...", "My name is..."));
        phrases.add(new Word("michәksәs?", "How are you feeling?"));
        phrases.add(new Word("kuchi achit.", "I’m feeling good."));
        phrases.add(new Word("әәnәs'aa?", "Are you coming?"));
        phrases.add(new Word("hәә’ әәnәm.", "Yes, I’m coming."));
        phrases.add(new Word("әәnәm", "I’m coming."));
        phrases.add(new Word("yoowutis", "Let’s go."));
        phrases.add(new Word("әnni'nem", "Come here."));


        ListView rootView = (ListView) this.findViewById(R.id.rootView);

        WordAdapter wordAdapter = new WordAdapter(this, phrases);
        rootView.setAdapter(wordAdapter);
    }


}
