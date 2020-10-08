package com.example.android.miwok;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class PhrasesActivity extends AppCompatActivity {
    //media player object reference needs to be global for accessing it inside the anonymous class definition
    // provided for the setOnItemClickListeners.
    private MediaPlayer mp ;
    private AudioManager am;
    private AudioFocusRequest audioFocusRequest = null;
    private AudioAttributes audioAttributes = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.word_list);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

       final ArrayList<Word> words = new ArrayList<Word>();
        words.add(new Word("Where are you going?","minto wuksus", R.raw.phrase_where_are_you_going));
        words.add(new Word("What is your name?","tinnә oyaase'nә", R.raw.phrase_what_is_your_name));
        words.add(new Word("My name is...","oyaaset...", R.raw.phrase_my_name_is));
        words.add(new Word("How are you feeling?","michәksәs?",R.raw.phrase_how_are_you_feeling));
        words.add(new Word("I’m feeling good.","kuchi achit", R.raw.phrase_im_feeling_good));
        words.add(new Word("Are you coming?","әәnәs'aa?", R.raw.phrase_are_you_coming));
        words.add(new Word("Yes, I’m coming.","hәә’ әәnәm", R.raw.phrase_yes_im_coming));
        words.add(new Word("I’m coming.","әәnәm", R.raw.phrase_im_coming));
        words.add(new Word("Let’s go.","yoowutis", R.raw.phrase_lets_go));
        words.add(new Word("Come here.","әnni'nem", R.raw.phrase_come_here));

        //creating an adapter for holding the data of the list view of the items containing the numbers.
        //we have created a custom list item layout and used it to tell the array adapter to load data from the source accordingly.
        //we have created a custom layout for this and used it as a layout to the array adapter constructor.
        WordAdapter wordAdapter = new WordAdapter(this, words, R.color.category_phrases);

        ListView listView = findViewById(R.id.list);

        //coupling list view with the adapter
        listView.setAdapter(wordAdapter);

        //onItemClickListener for the individual items
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() { //anonymous inner class for itemClickListener
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int result;
                //to play audio specific to each number or view we get a reference of the object that inflates the view
                // i.e here word object.
                Word word = words.get(position);

                //create a media player
                //supply the correct audio resource id for the audio file to play. (that belongs to the current word object.)
                mp = MediaPlayer.create(getApplicationContext(), word.getAudioResourceId());
                Log.i("PhrasesActivity.class", "created media player");

                ////////////////////  building audio attributes to use in building a AudioFocusRequest object.
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {  //21
                    audioAttributes = new AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                            .build();
                }
                ///// building a AudioFocusRequest object to use for requesting audio focus.
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {  //26
                    audioFocusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                            .setOnAudioFocusChangeListener(audioFocusChangeListener)
                            .setAudioAttributes(audioAttributes)
                            .setAcceptsDelayedFocusGain(false)
                            .setWillPauseWhenDucked(true)
                            .build();

                    //requesting  the audio focus for playing the file.
                    result = am.requestAudioFocus(audioFocusRequest);
                }else {
                    //requesting for audio focus in older versions below oreo.
                    result = am.requestAudioFocus(audioFocusChangeListener,
                            AudioManager.STREAM_MUSIC,AudioManager.AUDIOFOCUS_GAIN);
                }
                // final Object mFocusLock = new Object();

                if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                    Log.i("PhrasesActivity.class", "audio focus gained");
                    if (mp.isPlaying()) {
                        Log.i("PhrasesActivity.class", "already playing the text");
                    } else {
                        mp.start();
                    }
                }

                //using the onCompletionListener to call the method to release the media player resources.
                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        if(!mp.isPlaying()){
                            releaseMediaPlayer(mp);
                        }
                    }
                }); // end of onCompletionListener
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("onCreate", "app activity is destroyed.");
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
            Log.i("PhrasesActivity", "musicPlayer resources cleared");

            // Set the media player back to null. For our code, we've decided that
            // setting the media player to null is an easy way to tell that the media player
            // is not configured to play an audio file at the moment.
            mp = null;
            if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O){
                am.abandonAudioFocusRequest(audioFocusRequest);
            }
            else  am.abandonAudioFocus(audioFocusChangeListener);
            Log.i("music player","the music player focus is abandoned");
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
                    Log.i("PhrasesActivity.class", "pausing audio because of loss of focus");
                }
            } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                mp.start();
                Log.i("PhrasesActivity.class", "starting audio because of permanent gain of focus");
            }else if (focusChange == AudioManager.AUDIOFOCUS_LOSS){
                if(mp!= null && mp.isPlaying()){
                    mp.stop();
                    Log.i("PhrasesActivity.class", "stopping audio because of permanent loss of focus");
                    releaseMediaPlayer(mp);
                }
            }
        }
    };//end of OnAudioFocusChangeListener
}