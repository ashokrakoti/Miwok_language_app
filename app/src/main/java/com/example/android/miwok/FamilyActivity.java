package com.example.android.miwok;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

public class FamilyActivity extends AppCompatActivity {
    //media player object reference needs to be global for accessing it inside the anonymous class definition
    // provided for the setOnItemClickListeners.
     private MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.word_list);


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
                //to play audio specific to each number or view we get a reference of the object that inflates the view
                // i.e here word object.
                Word word = words.get(position);

                //create a media player
                //supply the correct audio resource id for the audio file to play. (that belongs to the current word object.)
                mp = MediaPlayer.create(getApplicationContext(), word.getAudioResourceId());
                mp.start();
                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        releaseMediaPlayer();
                    }
                });
            }
        });

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
            mp.release();

            // Set the media player back to null. For our code, we've decided that
            // setting the media player to null is an easy way to tell that the media player
            // is not configured to play an audio file at the moment.
            mp = null;
        }
    }
}