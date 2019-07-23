package fr.costerousse.locutus.controllers;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutionException;

import fr.costerousse.locutus.R;
import fr.costerousse.locutus.db.DatabaseClient;
import fr.costerousse.locutus.models.Profile;


public class UserProfileActivity extends AppCompatActivity {
	//////////////////////////////////////////////////////////
	// Fields
	/////////////
	// Intent
	public static final String PROFILE_POSITION = "profile_position";
	public static final String GOTO_TREE = "goto_tree";
	private int m_profilePosition;
	// Database & related
	private DatabaseClient mDb;
	private Profile m_profile;
	// View
	private TextView m_textViewProfileName;
	
	//////////////////////////////////////////////////////////
	// ON CREATE
	/////////////
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_profile);
		
		// Retrieves the current profile
		mDb = DatabaseClient.getInstance(getApplicationContext());
		m_profilePosition = getIntent().getIntExtra(PROFILE_POSITION, 0);
		try {
			m_profile = new GetProfiles().execute()
					.get()
					.get(m_profilePosition);
		} catch (ExecutionException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		// IF DEFAULT PROFILE & FROM MainActivity : GOTO TREE
		SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("preferences", MODE_PRIVATE);
		if (getIntent().getBooleanExtra(GOTO_TREE, false)) {
			System.out.println("default_profile position : " + sharedPreferences.getInt("default_profile", 0));
			Intent intent = new Intent(UserProfileActivity.this, TreeActivity.class);
			intent.putExtra(UserProfileActivity.PROFILE_POSITION, m_profilePosition);
			startActivity(intent);
		}
		
		// Retrieves view's item
		m_textViewProfileName = findViewById(R.id.text_view_profile_name);
		
		// Modifies view's item
		int currentYear = Calendar.getInstance()
				.get(Calendar.YEAR);
		String profileTitle = getString(R.string.profile_title, m_profile.getFirstName(), m_profile.getLastName(), currentYear - m_profile.getYearOfBirth());
		m_textViewProfileName.setText(profileTitle);
	}
	
	//////////////////////////////////////////////////////////
	// GetProfiles
	// Allows to retrieve profiles from database
	/////////////
	@SuppressLint("StaticFieldLeak")
	class GetProfiles extends AsyncTask<Void, Void, List<Profile>> {
		@Override
		protected List<Profile> doInBackground(Void... voids) {
			return mDb.getAppDatabase()
					.profileDao()
					.getAll();
		}
	}
	
	//////////////////////////////////////////////////////////
	// GO TO PICTO PICTURES
	/////////////
	public void gotoPictoPictures(View view) {
		Intent intent = new Intent(UserProfileActivity.this, PictoPicturesActivity.class);
		intent.putExtra(PictosActivity.PROFILE_POSITION, m_profilePosition);
		startActivity(intent);
	}
	
	//////////////////////////////////////////////////////////
	// GO TO PICTOS
	/////////////
	public void gotoPictos(View view) {
		Intent intent = new Intent(UserProfileActivity.this, PictosActivity.class);
		intent.putExtra(PictosActivity.PROFILE_POSITION, m_profilePosition);
		startActivity(intent);
	}
	
	//////////////////////////////////////////////////////////
	// GO TO TREES
	/////////////
	public void gotoTrees(View view) {
		Intent intent = new Intent(UserProfileActivity.this, TreeActivity.class);
		intent.putExtra(PictosActivity.PROFILE_POSITION, m_profilePosition);
		startActivity(intent);
	}
	
	//////////////////////////////////////////////////////////
	// GO TO PREFERENCES
	/////////////
	public void gotoPreferences(View view) {
		Intent intent = new Intent(UserProfileActivity.this, PreferencesActivity.class);
		intent.putExtra(PreferencesActivity.PROFILE_POSITION, m_profilePosition);
		startActivity(intent);
	}
	
	//////////////////////////////////////////////////////////
	// BACK (to MainActivity)
	/////////////
	public void back(View view) {
		finish();
	}
}
