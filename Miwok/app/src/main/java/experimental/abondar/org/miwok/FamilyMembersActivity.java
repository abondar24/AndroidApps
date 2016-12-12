package experimental.abondar.org.miwok;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class FamilyMembersActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;

    private MediaPlayer.OnCompletionListener mediaListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            releaseMediaPlayer();
        }
    };

    private AudioManager audioManager;

    private AudioManager.OnAudioFocusChangeListener audioManagerListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT ||
                    focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
                mediaPlayer.pause();
                mediaPlayer.seekTo(0);
            } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                mediaPlayer.start();
            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                releaseMediaPlayer();

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.word_list);

        final List<Word> members = new ArrayList<>();

        members.add( new Word("әpә","father",
                R.drawable.family_father,R.raw.family_father));
        members.add( new Word("әṭa","mother",
                R.drawable.family_mother,R.raw.family_mother));
        members.add( new Word("angsi","son",
                R.drawable.family_son,R.raw.family_son));
        members.add( new Word("tune","daughter",
                R.drawable.family_daughter,R.raw.family_daughter));
        members.add( new Word("taachi","older brother",
                R.drawable.family_older_brother,R.raw.family_older_brother));
        members.add( new Word("chalitti","younger brother",
                R.drawable.family_younger_brother,R.raw.family_younger_brother));
        members.add( new Word("teṭe","older sister",
                R.drawable.family_older_sister,R.raw.family_older_sister));
        members.add( new Word("kolliti","younger sister",
                R.drawable.family_younger_sister,R.raw.family_younger_sister));
        members.add( new Word("ama","grandmother",
                R.drawable.family_grandmother,R.raw.family_grandmother));
        members.add( new Word("paapa","grandfather",
                R.drawable.family_grandfather,R.raw.family_grandfather));

        ListView rootView =(ListView) this.findViewById(R.id.rootView);

        WordAdapter wordAdapter = new WordAdapter(this,  members,R.color.category_family);
        rootView.setAdapter(wordAdapter);

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        rootView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                releaseMediaPlayer();

                int result = audioManager.requestAudioFocus(audioManagerListener,
                        AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
                if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                    mediaPlayer = MediaPlayer.create(FamilyMembersActivity.this, members.get(i).getAudioResourceId());
                    mediaPlayer.start();
                    mediaPlayer.setOnCompletionListener(mediaListener);
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        releaseMediaPlayer();
    }

    private void releaseMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
            audioManager.abandonAudioFocus(audioManagerListener);
        }
    }
}
