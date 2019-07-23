package fr.costerousse.locutus.controllers;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutionException;

import fr.costerousse.locutus.R;
import fr.costerousse.locutus.db.DatabaseClient;
import fr.costerousse.locutus.models.Profile;


public class CreateProfileActivity extends AppCompatActivity {
	//////////////////////////////////////////////////////////
	// Fields
	/////////////
	// Database & related
	private DatabaseClient mDb;
	private List<Profile> m_profiles;
	// View
	private EditText m_editTextFirstName;
	private EditText m_editTextLastName;
	private EditText m_editTextYearOfBirth;
	
	//////////////////////////////////////////////////////////
	// ON CREATE
	// Retrieve data from database and view
	/////////////
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_profile);
		
		// Retrieving profiles from the database
		mDb = DatabaseClient.getInstance(getApplicationContext());
		try {
			m_profiles = new GetProfiles().execute()
					.get();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		// Retrieving view's items
		m_editTextFirstName = findViewById(R.id.edit_text_new_profile_first_name);
		m_editTextLastName = findViewById(R.id.edit_text_new_profile_last_name);
		m_editTextYearOfBirth = findViewById(R.id.edit_text_new_profile_year_of_birth);
	}
	
	//////////////////////////////////////////////////////////
	// NEW PROFILE CONFIRM
	// Tests errors (calling GetProfiles) then calls SAVE PROFILE or TOAST error
	/////////////
	public void newProfileConfirm(View view) {
		int currentYear = Calendar.getInstance()
				.get(Calendar.YEAR);
		if (TextUtils.isEmpty(m_editTextFirstName.getText()
				.toString()) || TextUtils.isEmpty(m_editTextLastName.getText()
				.toString()) || TextUtils.isEmpty(m_editTextYearOfBirth.getText()
				.toString())) {
			Toast.makeText(getApplicationContext(), R.string.please_fill_all_fields, Toast.LENGTH_LONG)
					.show();
		} else if (Integer.parseInt(m_editTextYearOfBirth.getText()
				.toString()
				.trim()) < 1920 || Integer.parseInt(m_editTextYearOfBirth.getText()
				.toString()
				.trim()) > currentYear) {
			Toast.makeText(getApplicationContext(), R.string.please_fill_correct_year, Toast.LENGTH_LONG)
					.show();
		} else {
			boolean profileExist = false;
			for (int i = 0; i < m_profiles.size(); i++) {
				if (m_editTextFirstName.getText()
						.toString()
						.trim()
						.equals(m_profiles.get(i)
								.getFirstName()) && m_editTextLastName.getText()
						.toString()
						.trim()
						.equals(m_profiles.get(i)
								.getLastName())) {
					profileExist = true;
				}
			}
			if (!profileExist) {
				saveProfile();
			} else {
				Toast.makeText(getApplicationContext(), R.string.please_enter_unexisting_profile, Toast.LENGTH_LONG)
						.show();
			}
		}
	}
	
	//////////////////////////////////////////////////////////
	// SAVE PROFILE
	// Saves the profile into the database (asynchronously)
	/////////////
	private void saveProfile() {
		// Retrieving information from the view
		final String sFirstName = m_editTextFirstName.getText()
				.toString()
				.trim();
		final String sLastName = m_editTextLastName.getText()
				.toString()
				.trim();
		final Integer sAge = Integer.parseInt(m_editTextYearOfBirth.getText()
				.toString()
				.trim());
		
		//////////////////////////////////////////////////////////
		// SaveProfile
		// Saves the profile then goes to it's activity
		/////////////
		@SuppressLint("StaticFieldLeak")
		class SaveProfile extends AsyncTask<Void, Void, Profile> {
			@Override
			protected Profile doInBackground(Void... voids) {
				Profile profile = new Profile(sFirstName, sLastName, sAge);
				mDb.getAppDatabase()
						.profileDao()
						.insert(profile);
				
				return profile;
			}
			
			@Override
			protected void onPostExecute(Profile profile) {
				super.onPostExecute(profile);
				Intent intent = new Intent(CreateProfileActivity.this, ProfilesActivity.class);
				startActivity(intent);
				finish();
				Toast.makeText(getApplicationContext(), R.string.profile_saved, Toast.LENGTH_LONG)
						.show();
			}
		}
		
		// Execute the asynchronous class tasks
		SaveProfile sp = new SaveProfile();
		sp.execute();
	}
	
	//////////////////////////////////////////////////////////
	// GetProfiles
	// Allows to retrieve profiles from the database
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
	// BACK (to MainActivity)
	/////////////
	public void back(View view) {
		finish();
	}
}
