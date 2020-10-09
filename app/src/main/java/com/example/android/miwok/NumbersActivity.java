package com.example.android.miwok;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;

public class NumbersActivity extends AppCompatActivity {
    //media player object reference needs to be global for accessing it inside the anonymous class definition
    // provided for the setOnItemClickListeners.
    private MediaPlayer mp ;

   //handles the media playing audio focus.
    private AudioManager am;

   //these are two new attributes created for building the audio focus request object.
    AudioFocusRequest audioFocusRequest = null;
    AudioAttributes audioAttributes = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.word_list);


        //setting up the up button action using the default app bar. We are not using a toolbar here.
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!= null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        //initialising the audio manager
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

        //setting on item click listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int result;

                //to play audio specific to each number or view we get a reference of the object that inflates the view
                // i.e here word object.
                Word word = words.get(position);

                //building audio focus attributes for requesting audio manager.
                //needed only for api levels more than 21 - lollipop.
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {  //21

                    //building audio focus attributes. needed to build audioFocusRequest.
                    buildAudioFocusAttributes();
                }

                // building a AudioFocusRequest object to use for requesting audio focus.
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {  //level26

                    //building audio focus request for apis more than or equal to oreo.
                    buildAudioFocusRequest();

                    //requesting  the audio focus for playing the file.
                    result = am.requestAudioFocus(audioFocusRequest);
                }else {
                    //requesting for audio focus in older versions below oreo.
                    result = am.requestAudioFocus(audioFocusChangeListener,
                            AudioManager.STREAM_MUSIC,AudioManager.AUDIOFOCUS_GAIN);
                }

                if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                    Log.i("NumbersActivity.class", "audio focus gained");

                    //create a media player
                    //supply the correct audio resource id for the audio file to play. (that belongs to the current word object.)
                    mp = MediaPlayer.create(getApplicationContext(), word.getAudioResourceId());
                    Log.i("NumbersActivity.class", "created media player");
                    mp.start();

                    //using the onCompletionListener to call the method to release the media player resources.
                    mp.setOnCompletionListener(onCompletionListener);
                }
            }// end of onItemClick() method.
        });//end of setOnItemClickListener().
    }// end of onCreate() method.

    @Override
    protected void onStop() {
        super.onStop();
        releaseMediaPlayer(mp);
        Toast.makeText(getApplicationContext(), "music player killed", Toast.LENGTH_SHORT).show();
    }

    /**
     * Clean up the media player by releasing its resources.
     */
    private void releaseMediaPlayer(MediaPlayer mp) {
        // If the media player is not null, then it may be currently playing a sound.
        if (mp != null) {
            // Regardless of the current state of the media player, release its resources
            // because we no longer need it.
            mp.release();
           Log.i("NumbersActivity.class", "cleaned up media player resources");
            // Set the media player back to null. For our code, we've decided that
            // setting the media player to null is an easy way to tell that the media player
            // is not configured to play an audio file at the moment.
            mp = null;
            if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O){
                am.abandonAudioFocusRequest(audioFocusRequest);
            }
            else  am.abandonAudioFocus(audioFocusChangeListener);
            Log.i("NumbersActivity","the music player focus is abandoned");
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
                    Log.i("NumbersActivity.class", "pausing audio because of loss of focus");
                }
            } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                mp.start();
                Log.i("NumbersActivity.class", "starting audio because of permanent gain of focus");
            }else if (focusChange == AudioManager.AUDIOFOCUS_LOSS){
                if(mp!= null && mp.isPlaying()){
                    mp.stop();
                    Log.i("NumbersActivity.class", "stopping audio because of permanent loss of focus");
                    releaseMediaPlayer(mp);
                }
            }
        }
    };//end of OnAudioFocusChangeListener

    /**
     * This listener gets triggered when the {@link MediaPlayer} has completed
     * playing the audio file.
     */
    private MediaPlayer.OnCompletionListener onCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            if(!mp.isPlaying()){
                mp.reset();
                releaseMediaPlayer(mp);
            }
        }
    };

    //building audio focus attributes
    public void buildAudioFocusAttributes(){
        //  building audio attributes to use in building a AudioFocusRequest object.
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {  //21
            audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build();
        }
    }

    //building audio focus request object.
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void buildAudioFocusRequest(){
        audioFocusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                .setOnAudioFocusChangeListener(audioFocusChangeListener)
                .setAudioAttributes(audioAttributes)
                .setAcceptsDelayedFocusGain(false)
                .setWillPauseWhenDucked(true)
                .build();
    }
}