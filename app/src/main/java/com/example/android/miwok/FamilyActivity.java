package com.example.android.miwok;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

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

import java.util.ArrayList;

public class FamilyActivity extends AppCompatActivity {

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

        //creating the words list
        final ArrayList<Word> words = createWordsList();

        //creating an adapter for holding the data of the list view of the items containing the numbers.
        //we have created a custom list item layout and used it to tell the array adapter to load data from the source accordingly.
        //we have created a custom layout for this and used it as a layout to the array adapter constructor.
        WordAdapter wordAdapter = new WordAdapter(this, words, R.color.category_family);

        ListView listView = findViewById(R.id.list);

        //coupling list view with the adapter
        listView.setAdapter(wordAdapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() { //anonymous inner class for itemClickListener
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
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {  //26

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
                    Log.i("ColorsActivity.class", "audio focus gained");
                    //create a media player
                    //supply the correct audio resource id for the audio file to play. (that belongs to the current word object.)
                    mp = MediaPlayer.create(getApplicationContext(), word.getAudioResourceId());
                    Log.i("NumbersActivity.class", "created media player");

                    //start playing the audio
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
            Log.i("FamilyActivity", "musicPlayer resources cleared");

            // Set the media player back to null. For our code, we've decided that
            // setting the media player to null is an easy way to tell that the media player
            // is not configured to play an audio file at the moment.
            mp = null;
            if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O){
                am.abandonAudioFocusRequest(audioFocusRequest);
            }
            else  am.abandonAudioFocus(audioFocusChangeListener);
            Log.i("FamilyActivity.class","the music player focus is abandoned");
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
                    Log.i("FamilyActivity.class", "pausing audio because of loss of focus");
                }
            } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                mp.start();
                Log.i("FamilyActivity.class", "starting audio because of permanent gain of focus");
            }else if (focusChange == AudioManager.AUDIOFOCUS_LOSS){
                if(mp!= null && mp.isPlaying()){
                    mp.stop();
                    Log.i("FamilyActivity.class", "stopping audio because of permanent loss of focus");
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

    ////building audio focus attributes
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void buildAudioFocusAttributes(){
        //  building audio attributes to use in building a AudioFocusRequest object.
            audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build();
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


    /* creating the array list of Word objects for displaying in  list view.
     */
    private ArrayList<Word> createWordsList(){
        final ArrayList<Word> words = new ArrayList<Word>();
        words.add(new Word("father","әpә", R.drawable.family_father, R.raw.family_father));
        words.add(new Word("mother","әṭa", R.drawable.family_mother, R.raw.family_mother));
        words.add(new Word("son","angsi", R.drawable.family_son, R.raw.family_son));
        words.add(new Word("daughter","tune", R.drawable.family_daughter, R.raw.family_daughter));
        words.add(new Word("older brother","taachi", R.drawable.family_older_brother, R.raw.family_older_brother));
        words.add(new Word("younger brother","chalitti", R.drawable.family_younger_brother, R.raw.family_younger_brother));
        words.add(new Word("older sister","teṭe", R.drawable.family_older_sister, R.raw.family_older_sister));
        words.add(new Word("younger sister","kolliti", R.drawable.family_younger_sister, R.raw.family_younger_sister));
        words.add(new Word("grandmother","ama", R.drawable.family_grandmother, R.raw.family_grandmother));
        words.add(new Word("grandfather","paapa", R.drawable.family_grandfather, R.raw.family_grandfather));

        return words;
    }
}