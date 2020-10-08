/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.miwok;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the content of the activity to use the activity_main.xml layout file
        setContentView(R.layout.activity_main);
       // finding reference to the numbers text view
        TextView numbersTextView = findViewById(R.id.numbers);

        //onClickListener() method Call
        numbersTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(),"Opens the list of Numbers", Toast.LENGTH_SHORT).show();

               Intent numbersIntent = new Intent(view.getContext(),NumbersActivity.class);
               startActivity(numbersIntent);
            }
        });
        //onClickListener()  method Call ending.

        // finding reference to the  colors text view
        TextView colorsTextView = findViewById(R.id.colors);

        //onClickListener() method Call
        colorsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(),"Opens the list of colors", Toast.LENGTH_SHORT).show();

                Intent colorsIntent = new Intent(view.getContext(),ColorsActivity.class);
                startActivity(colorsIntent);
            }
        });
        //onClickListener()  method Call ending.

        // finding reference to the numbers text view
        TextView familyTextView = findViewById(R.id.family);

        //onClickListener() method Call
        familyTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(),"Opens the list of family relations", Toast.LENGTH_SHORT).show();

                Intent familyIntent = new Intent(view.getContext(),FamilyActivity.class);
                startActivity(familyIntent);
            }
        });
        //onClickListener()  method Call ending.

        // finding reference to the numbers text view
        TextView phrasesTextView = findViewById(R.id.phrases);

        //onClickListener() method Call
        phrasesTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(),"Opens the list of Phrases", Toast.LENGTH_SHORT).show();

                Intent phrasesIntent = new Intent(view.getContext(),PhrasesActivity.class);
                startActivity(phrasesIntent);
            }
        });
        //onClickListener()  method Call ending.
    }
}
