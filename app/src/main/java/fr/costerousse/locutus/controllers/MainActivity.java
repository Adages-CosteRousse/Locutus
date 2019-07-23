package fr.costerousse.locutus.controllers;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import fr.costerousse.locutus.R;


public class MainActivity extends AppCompatActivity {
	// Fields
	// Empty
	
	// Methods
	
	//////////////////////////////////////////////////////////
	// ON CREATE
	// If a default profile exists, goes to it's activity
	/////////////
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// Check SharedPreferences
		SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("preferences", MODE_PRIVATE);
		if (sharedPreferences.contains("default_profile") && sharedPreferences.getInt("default_profile", -1) != -1) {
			Intent intent = new Intent(MainActivity.this, UserProfileActivity.class);
			intent.putExtra(UserProfileActivity.PROFILE_POSITION, sharedPreferences.getInt("default_profile", 0));
			intent.putExtra(UserProfileActivity.GOTO_TREE, true);
			startActivity(intent);
		}
	}
	
	//////////////////////////////////////////////////////////
	// GO TO PROFILES
	// Goes to the profiles manager
	/////////////
	public void gotoProfiles(View view) {
		Intent intent = new Intent(MainActivity.this, ProfilesActivity.class);
		startActivity(intent);
	}
	
	//////////////////////////////////////////////////////////
	// GO TO CONCEPT MANAGEMENT
	// Goes to the concepts manager
	/////////////
	public void gotoConceptManagement(View view) {
		Intent intent = new Intent(MainActivity.this, ConceptManagementActivity.class);
		startActivity(intent);
	}
	
	//////////////////////////////////////////////////////////
	// GO TO TREE MANAGEMENT
	// Goes to the trees manager
	/////////////
	public void gotoTreeManagement(View view) {
		Intent intent = new Intent(MainActivity.this, TreeManagementActivity.class);
		startActivity(intent);
	}
	
	//////////////////////////////////////////////////////////
	// GO TO HELP
	// Goes to the help activity
	/////////////
	public void gotoHelp(View view) {
		Intent intent = new Intent(MainActivity.this, HelpActivity.class);
		startActivity(intent);
	}
	
	//////////////////////////////////////////////////////////
	// EXIT (app)
	/////////////
	public void exit(View view) {
		finish();
	}
}
