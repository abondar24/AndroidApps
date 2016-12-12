package experimental.abondar.org.miwok2;


import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;


public class NumbersFragment extends Fragment {

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


    public NumbersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.word_list, container, false);

        final List<Word> numbers = new ArrayList<>();

        numbers.add(new Word("lutti", "one",
                R.drawable.number_one, R.raw.number_one));
        numbers.add(new Word("otiiko", "two",
                R.drawable.number_two, R.raw.number_two));
        numbers.add(new Word("tolookosu", "three",
                R.drawable.number_three, R.raw.number_three));
        numbers.add(new Word("oyyisa", "four",
                R.drawable.number_four, R.raw.number_four));
        numbers.add(new Word("massokka", "five",
                R.drawable.number_five, R.raw.number_five));
        numbers.add(new Word("temmokka", "six",
                R.drawable.number_six, R.raw.number_six));
        numbers.add(new Word("kenekaku", "seven",
                R.drawable.number_seven, R.raw.number_seven));
        numbers.add(new Word("kawinta", "eight",
                R.drawable.number_eight, R.raw.number_eight));
        numbers.add(new Word("wo e", "nine",
                R.drawable.number_nine, R.raw.number_nine));
        numbers.add(new Word("na'aacha", "ten",
                R.drawable.number_ten, R.raw.number_ten));


        ListView rootList = (ListView) rootView.findViewById(R.id.rootList);

        WordAdapter wordAdapter = new WordAdapter(getActivity(), numbers, R.color.category_numbers);
        rootList.setAdapter(wordAdapter);

        audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);

        rootList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                releaseMediaPlayer();

                int result = audioManager.requestAudioFocus(audioManagerListener,
                        AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
                if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                    mediaPlayer = MediaPlayer.create(getActivity(), numbers.get(i).getAudioResourceId());
                    mediaPlayer.start();
                    mediaPlayer.setOnCompletionListener(mediaListener);
                }

            }
        });


        return rootView;
    }


    @Override
    public void onStop() {
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
