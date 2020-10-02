package com.example.android.miwok;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

public class ColorsActivity extends AppCompatActivity {
    //media player object reference needs to be global for accessing it inside the anonymous class definition
    // provided for the setOnItemClickListeners.
     private MediaPlayer mp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.word_list);


        final ArrayList<Word> words = new ArrayList<Word>();
        words.add(new Word("red","weṭeṭṭi", R.drawable.color_red, R.raw.color_red));
        words.add(new Word("green","chokokki", R.drawable.color_green, R.raw.color_green));
        words.add(new Word("brown","ṭakaakki", R.drawable.color_brown, R.raw.color_brown));
        words.add(new Word("gray","ṭopoppi", R.drawable.color_gray, R.raw.color_gray));
        words.add(new Word("black","kululli", R.drawable.color_black, R.raw.color_black));
        words.add(new Word("white","kelelli", R.drawable.color_white, R.raw.color_white));
        words.add(new Word("dusty yellow","ṭopiisә", R.drawable.color_dusty_yellow, R.raw.color_dusty_yellow));
        words.add(new Word("mustard yellow","chiwiiṭә", R.drawable.color_mustard_yellow, R.raw.color_mustard_yellow));

        //creating an adapter for holding the data of the list view of the items containing the numbers.
        //we have created a custom list item layout and used it to tell the array adapter to load data from the source accordingly.
        //we have created a custom layout for this and used it as a layout to the array adapter constructor.
        WordAdapter wordAdapter = new WordAdapter(this, words, R.color.category_colors);

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