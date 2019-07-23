package fr.costerousse.locutus.controllers;


import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.lang.reflect.Field;

import fr.costerousse.locutus.R;


public class AddExistingSoundActivity extends AppCompatActivity {
	//////////////////////////////////////////////////////////
	// ON CREATE
	/////////////
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_existing_sound);
		
		// Retrieving view's item
		LinearLayout linear = findViewById(R.id.linear_layout_existing_sound);
		
		// Retrieving fields from R.raw
		Field[] fields = R.raw.class.getFields();
		
		// Sorting raws by name and inserting into the (linear in) scrollView
		for (final Field field : fields) {
			try {
				if (field.getName()
						.startsWith("sound_")) {
					final int currentResId = field.getInt(R.raw.class);
					LinearLayout linearLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.template_sound, null);
					Button buttonSelect = linearLayout.findViewById(R.id.button_select);
					buttonSelect.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View view) {
							Intent intent = new Intent();
							intent.putExtra("result", field.getName());
							setResult(AddExistingSoundActivity.RESULT_OK, intent);
							finish();
						}
					});
					Button buttonPlay = linearLayout.findViewById(R.id.button_play);
					final MediaPlayer sound = MediaPlayer.create(getApplicationContext(), currentResId);
					buttonPlay.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View view) {
							sound.start();
						}
					});
					TextView textView = linearLayout.findViewById(R.id.text_view_sound);
					textView.setText(field.getName());
					
					linear.addView(linearLayout);
				}
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}
}
