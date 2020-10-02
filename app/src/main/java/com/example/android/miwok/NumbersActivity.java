package com.example.android.miwok;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class NumbersActivity extends AppCompatActivity {
    //media player object reference needs to be global for accessing it inside the anonymous class definition
    // provided for the setOnItemClickListeners.
   private MediaPlayer mp ;
   private AudioManager am;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.word_list);
        am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        final ArrayList<Word> words = new ArrayList<Word>();
        words.add(new Word("one","lutti", R.drawable.number_one, R.raw.number_one));
        words.add(new Word("two","otiiko", R.drawable.number_two, R.raw.number_two));
        words.add(new Word("three","tolookosu", R.drawable.number_three, R.raw.number_three));
        words.add(new Word("four","oyyisa", R.drawable.number_four, R.raw.number_four));
        words.add(new Word("five","massokka", R.drawable.number_five, R.raw.number_five));
        words.add(new Word("six","temmokka", R.drawable.number_six, R.raw.number_six));
        words.add(new Word("seven","kenekaku", R.drawable.number_seven, R.raw.number_seven));
        words.add(new Word("eight","kawinta", R.drawable.number_eight, R.raw.number_eight));
        words.add(new Word("nine","wo'e", R.drawable.number_nine, R.raw.number_nine));
        words.add(new Word("ten","na'aacha", R.drawable.number_ten, R.raw.number_ten));

        //creating an adapter for holding the data of the list view of the items containing the numbers.
        //we have created a custom list item layout and used it to tell the array adapter to load data from the source accordingly.
        //we have created a custom layout for this and used it as a layout to the array adapter constructor.
        final WordAdapter wordAdapter = new WordAdapter(this, words, R.color.category_numbers);

        ListView listView = findViewById(R.id.list);

        //coupling list view with the adapter
        listView.setAdapter(wordAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() { //anonymous inner class for itemClickListener
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    //to play audio specific to each number or view we get a reference of the object that inflates the view
                    // i.e here word object.
                    Word word = words.get(position);

                    //create a media player
                    //supply the correct audio resource id for the audio file to play. (that belongs to the current word object.)
                    mp = MediaPlayer.create(getApplicationContext(), word.getAudioResourceId());
                    Log.i("NumbersActivity.class", "created media player");

                    int result = am.requestAudioFocus(audioFocusChangeListener,
                            AudioManager.STREAM_MUSIC,AudioManager.AUDIOFOCUS_GAIN);

                    if(result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED ){
                        Log.i("NumbersActivity.clas","audio foucus gained");
                        if(mp.isPlaying()){
                            Log.i("NumbersActivity.class","already playing the text");
                        }else{
                            mp.start();
                        }
                    }

                    //using the onCompletionListener to call the method to release the media player resources.
                    mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            releaseMediaPlayer();
                        }
                    }); // end of onCompletionListener

            } //end of onItemClick method.
        });// end of onClickListener
    }

    @Override
    protected void onStop() {
        super.onStop();
        releaseMediaPlayer();
    }

    /**
     * Clean up the media player by releasing its resources.
     */
    private void releaseMediaPlayer() {
        // If the media player is not null, then it may be currently playing a sound.
        if (mp != null) {
            // Regardless of the current state of the media player, release its resources
            // because we no longer need it.
            mp.reset();
            mp.release();
           Log.i("NumbersActivity.class", "cleaned up media player resources");
            // Set the media player back to null. For our code, we've decided that
            // setting the media player to null is an easy way to tell that the media player
            // is not configured to play an audio file at the moment.
            mp = null;
            am.abandonAudioFocus(audioFocusChangeListener);
        }
    }

    /**
     * This listener gets triggered whenever the audio focus changes.
     * (i.e., we gain or lose audio focus because of another app or device).
     */
    private AudioManager.OnAudioFocusChangeListener audioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            if(focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT ||
                    focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK){
                if(mp!= null && mp.isPlaying()){
                    mp.pause();
                    Log.i("NumbersActivity.clas", "pausing audio because of loss of focus");
                }
            } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                mp.start();
                Log.i("NumbersActivity.clas", "starting audio because of permanent gain of focus");
            }else if (focusChange == AudioManager.AUDIOFOCUS_LOSS){
                if(mp!= null && mp.isPlaying()){
                    mp.stop();
                    Log.i("NumbersActivity.clas", "stopping audio because of permanent loss of focus");
                    releaseMediaPlayer();
                }
            }
        }
    };//end of OnAudioFocusChangeListener
}