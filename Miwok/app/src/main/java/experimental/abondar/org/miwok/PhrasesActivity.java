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

public class PhrasesActivity extends AppCompatActivity {

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


        final List<Word> phrases = new ArrayList<>();


        phrases.add(new Word("minto wuksus", "Where are you going?",
                R.raw.phrase_where_are_you_going));
        phrases.add(new Word("tinnә oyaase'nә", "What is your name?",
                R.raw.phrase_what_is_your_name));
        phrases.add(new Word("oyaaset...", "My name is...",
                R.raw.phrase_my_name_is));
        phrases.add(new Word("michәksәs?", "How are you feeling?",
                R.raw.phrase_how_are_you_feeling));
        phrases.add(new Word("kuchi achit.", "I’m feeling good.",
                R.raw.phrase_im_feeling_good));
        phrases.add(new Word("әәnәs'aa?", "Are you coming?",
                R.raw.phrase_are_you_coming));
        phrases.add(new Word("hәә’ әәnәm.", "Yes, I’m coming.",
                R.raw.phrase_yes_im_coming));
        phrases.add(new Word("әәnәm", "I’m coming.",
                R.raw.phrase_im_coming));
        phrases.add(new Word("yoowutis", "Let’s go.",
                R.raw.phrase_lets_go));
        phrases.add(new Word("әnni'nem", "Come here.",
                R.raw.phrase_come_here));


        ListView rootView = (ListView) this.findViewById(R.id.rootView);

        WordAdapter wordAdapter = new WordAdapter(this, phrases, R.color.category_phrases);
        rootView.setAdapter(wordAdapter);

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        rootView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                releaseMediaPlayer();

                int result = audioManager.requestAudioFocus(audioManagerListener,
                        AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
                if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                    mediaPlayer = MediaPlayer.create(PhrasesActivity.this, phrases.get(i).getAudioResourceId());
                    mediaPlayer.start();
                    mediaPlayer.setOnCompletionListener(mediaListener);
                }
            }
        });
    }

    @Override
    protected void onStop() {
        releaseMediaPlayer();
        super.onStop();
    }

    private void releaseMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
            audioManager.abandonAudioFocus(audioManagerListener);
        }
    }
}
