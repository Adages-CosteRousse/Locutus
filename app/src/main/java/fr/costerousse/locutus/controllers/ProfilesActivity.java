package fr.costerousse.locutus.controllers;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import fr.costerousse.locutus.R;
import fr.costerousse.locutus.adapters.ProfilesAdapter;
import fr.costerousse.locutus.db.DatabaseClient;
import fr.costerousse.locutus.models.Profile;


public class ProfilesActivity extends AppCompatActivity {
	//////////////////////////////////////////////////////////
	// Fields
	/////////////
	private DatabaseClient mDb;
	private ProfilesAdapter adapter;
	private ListView m_listViewProfiles;
	
	//////////////////////////////////////////////////////////
	// ON CREATE
	/////////////
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profiles);
		
		// Retrieves database
		mDb = DatabaseClient.getInstance(getApplicationContext());
		
		// Retrieves view's item
		m_listViewProfiles = findViewById(R.id.list_view_profiles);
		
		// Ties adapter to listView
		adapter = new ProfilesAdapter(this, new ArrayList<Profile>());
		m_listViewProfiles.setAdapter(adapter);
		
		//////////////////////////////////////////////////////////
		// SET ON ITEM CLICK LISTENER
		/////////////
		m_listViewProfiles.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
				Intent intent = new Intent(ProfilesActivity.this, UserProfileActivity.class);
				intent.putExtra(UserProfileActivity.PROFILE_POSITION, position);
				finish();
				startActivity(intent);
			}
		});
		
		// Create GetProfiles and execute it's tasks
		new GetProfiles().execute();
	}
	
	//////////////////////////////////////////////////////////
	// GetProfiles
	// Allows to retrieve profiles from database then add the list to the adapter
	/////////////
	@SuppressLint("StaticFieldLeak")
	class GetProfiles extends AsyncTask<Void, Void, List<Profile>> {
		@Override
		protected List<Profile> doInBackground(Void... voids) {
			return mDb.getAppDatabase()
					.profileDao()
					.getAll();
		}
		
		@Override
		protected void onPostExecute(List<Profile> profiles) {
			adapter.clear();
			adapter.addAll(profiles);
			adapter.notifyDataSetChanged();
		}
	}
	
	//////////////////////////////////////////////////////////
	// GO TO CREATE PROFILE
	/////////////
	public void gotoCreateProfile(View view) {
		Intent intent = new Intent(ProfilesActivity.this, CreateProfileActivity.class);
		startActivity(intent);
		finish();
	}
	
	//////////////////////////////////////////////////////////
	// BACK (to MainActivity)
	/////////////
	public void back(View view) {
		finish();
	}
}
