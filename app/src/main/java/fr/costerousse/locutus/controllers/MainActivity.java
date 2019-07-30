/* Copyright
Copyright 2019 Adages-Costerousse

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */


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
