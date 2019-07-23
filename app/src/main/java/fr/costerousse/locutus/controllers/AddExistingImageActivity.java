package fr.costerousse.locutus.controllers;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import java.lang.reflect.Field;

import fr.costerousse.locutus.R;


public class AddExistingImageActivity extends AppCompatActivity {
	//////////////////////////////////////////////////////////
	// ON CREATE
	/////////////
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_existing_image);
		
		// Retrieving view's item
		LinearLayout linear = findViewById(R.id.linear_layout_existing_image);
		
		// Retrieving fields from R.drawable
		Field[] fields = R.drawable.class.getFields();
		
		// Sorting drawables by name and inserting into the (linear in) scrollView
		for (final Field field : fields) {
			try {
				if (field.getName()
						.startsWith("dra_")) {
					int currentResId = field.getInt(R.drawable.class);
					ImageButton imageButton = (ImageButton) getLayoutInflater().inflate(R.layout.template_image, null);
					imageButton.setImageResource(currentResId);
					imageButton.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View view) {
							Intent intent = new Intent();
							intent.putExtra("result", field.getName());
							setResult(AddExistingImageActivity.RESULT_OK, intent);
							finish();
						}
					});
					linear.addView(imageButton);
				}
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}
}