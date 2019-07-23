package fr.costerousse.locutus.controllers;


import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import fr.costerousse.locutus.R;


public class HelpActivity extends AppCompatActivity {
	
	//////////////////////////////////////////////////////////
	// ON CREATE
	/////////////
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_help);
	}
	
	//////////////////////////////////////////////////////////
	// BACK (to MainActivity)
	/////////////
	public void back(View view) {
		finish();
	}
}