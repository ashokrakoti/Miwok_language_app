package com.example.android.miwok;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

public class WordAdapter extends ArrayAdapter<Word> {
   //constructor
    int colorId;
    public WordAdapter(@NonNull Context context, ArrayList<Word> pWords, int colorId) {
        super(context, 0, pWords);
        this.colorId = colorId;
    }

    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View listItem = convertView;
        if(listItem == null){
            //if it is null we wanted a new view top be created because there are no views to recycle.
            listItem = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }

        Word local_word = getItem(position);

        ImageView imageView = listItem.findViewById(R.id.image_view);

        // Check if an image is provided for this word or not
        if (local_word.hasImage()) {
            // If an image is available, display the provided image based on the resource ID
            imageView.setImageResource(local_word.getImageResourceId());
            // Make sure the view is visible
            imageView.setVisibility(View.VISIBLE);
        } else {
            // Otherwise hide the ImageView (set visibility to GONE)
            imageView.setVisibility(View.GONE);
        }

        TextView miwokView = listItem.findViewById(R.id.miwok_text_view);
        miwokView.setText(local_word.getmMiwokTranslation());

        TextView defaultview = listItem.findViewById(R.id.default_text_view);
        defaultview.setText(local_word.getmDefaultTranslation());

        // Set the theme color for the list item
        View textContainer = listItem.findViewById(R.id.text_view_layout);
        // Find the color that the resource ID maps to
        int color = ContextCompat.getColor(getContext(), colorId);
        // Set the background color of the text container View
        textContainer.setBackgroundColor(color);

        /*ImageView playButton = listItem.findViewById(R.id.play);
        playButton.setImageResource(R.drawable.sharp_play_arrow_white_24dp);
        playButton.setBackgroundColor(color);*/

        return listItem;

    }
}
