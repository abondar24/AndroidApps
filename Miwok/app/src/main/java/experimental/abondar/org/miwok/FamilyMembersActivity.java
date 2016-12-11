package experimental.abondar.org.miwok;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class FamilyMembersActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.word_list);

        List<Word> members = new ArrayList<>();

        members.add( new Word("әpә","father",R.drawable.family_father));
        members.add( new Word("әṭa","mother",R.drawable.family_mother));
        members.add( new Word("angsi","son",R.drawable.family_son));
        members.add( new Word("tune","daughter",R.drawable.family_daughter));
        members.add( new Word("taachi","older brother",R.drawable.family_older_brother));
        members.add( new Word("chalitti","younger brother",R.drawable.family_younger_sister));
        members.add( new Word("teṭe","older sister",R.drawable.family_older_sister));
        members.add( new Word("kolliti","younger sister",R.drawable.family_younger_sister));
        members.add( new Word("ama","grandmother",R.drawable.family_grandmother));
        members.add( new Word("paapa","grandfather",R.drawable.family_grandfather));

        ListView rootView =(ListView) this.findViewById(R.id.rootView);

        WordAdapter wordAdapter = new WordAdapter(this,  members);
        rootView.setAdapter(wordAdapter);
    }
}
