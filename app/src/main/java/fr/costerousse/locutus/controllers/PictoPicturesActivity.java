package fr.costerousse.locutus.controllers;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import fr.costerousse.locutus.R;
import fr.costerousse.locutus.adapters.PickConceptsAdapter;
import fr.costerousse.locutus.db.DatabaseClient;
import fr.costerousse.locutus.models.Concept;
import fr.costerousse.locutus.models.Profile;


public class PictoPicturesActivity extends AppCompatActivity {
	//////////////////////////////////////////////////////////
	// Fields
	/////////////
	// Intent
	public static final String PROFILE_POSITION = "profile_position";
	private int m_profilePosition;
	// Database & related
	private DatabaseClient mDb;
	private Profile m_profile;
	private int m_maxFrames;
	// View
	private PickConceptsAdapter adapter;
	private TextView m_textViewConceptsCount;
	private ListView m_listViewConcepts;
	//
	private int m_conceptCount = 0;
	private ArrayList<Integer> m_concepts;
	private int m_arraySize;
	
	//////////////////////////////////////////////////////////
	// ON CREATE
	/////////////
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_picto_pictures);
		
		// Retrieves database & related
		mDb = DatabaseClient.getInstance(getApplicationContext());
		m_profilePosition = getIntent().getIntExtra(PROFILE_POSITION, 0);
		
		// GetProfiles
		System.out.println("profile_position : " + m_profilePosition);
		try {
			m_profile = new PictoPicturesActivity.GetProfiles().execute()
					.get()
					.get(m_profilePosition);
		} catch (ExecutionException | InterruptedException e) {
			e.printStackTrace();
		}
		if (m_profile.getMaxFramesPerDisplay() > 4) {
			m_maxFrames = 4;
		} else {
			m_maxFrames = m_profile.getMaxFramesPerDisplay();
		}
		
		// GetConcepts and execute it's tasks
		List<Concept> tempList = null;
		try {
			tempList = new PictoPicturesActivity.GetConcepts().execute()
					.get();
			m_arraySize = tempList.size();
		} catch (ExecutionException | InterruptedException e) {
			e.printStackTrace();
		}
		
		// Fills m_concept
		m_concepts = new ArrayList<>();
		System.out.println("Taille mconcept = " + m_concepts.size());
		for (int i = 0; i < m_arraySize; i++) {
			System.out.println("m_arraySize : " + m_arraySize);
			System.out.println("ALORS : " + tempList.get(i)
					.getPicture());
			m_concepts.add(i, 0);
		}
		
		System.out.println("Taille après = " + m_concepts.size());
		
		// Retrieves view's items
		m_textViewConceptsCount = findViewById(R.id.text_view_pick_concepts);
		m_listViewConcepts = findViewById(R.id.list_view_pick_concepts);
		
		// Processing
		m_textViewConceptsCount.setText(String.format(getResources().getString(R.string.concept_count), m_conceptCount, m_maxFrames));
		
		// Ties adapter to listview
		adapter = new PickConceptsAdapter(this, new ArrayList<Concept>());
		m_listViewConcepts.setAdapter(adapter);
		
		//////////////////////////////////////////////////////////
		// SET ON ITEM CLICK LISTENER
		/////////////
		m_listViewConcepts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
				TextView text = view.findViewById(R.id.text_view_pick_concepts);
				if (m_concepts.get(position) == 0 && m_conceptCount < m_maxFrames) {
					System.out.println("Position : " + position + " id : " + l);
					text.setTextColor(getResources().getColor(R.color.white));
					text.setBackgroundColor(getResources().getColor(R.color.check));
					m_concepts.set(position, 1);
					System.out.println(m_concepts);
					m_conceptCount++;
					m_textViewConceptsCount.setText(String.format(getResources().getString(R.string.concept_count), m_conceptCount, m_maxFrames));
				} else if (m_concepts.get(position) == 1) {
					System.out.println("Position : " + position + " id : " + l);
					text.setTextColor(getResources().getColor(R.color.primary_weak));
					text.setBackgroundColor(getResources().getColor(R.color.white));
					m_concepts.set(position, 0);
					
					System.out.println(m_concepts);
					m_conceptCount--;
					m_textViewConceptsCount.setText(String.format(getResources().getString(R.string.concept_count), m_conceptCount, m_maxFrames));
				}
			}
		});
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
	// GetConcepts
	// Allows to retrieve m_concepts from database then add the list to the adapter
	/////////////
	@SuppressLint("StaticFieldLeak")
	class GetConcepts extends AsyncTask<Void, Void, List<Concept>> {
		@Override
		protected List<Concept> doInBackground(Void... voids) {
			return mDb.getAppDatabase()
					.conceptDao()
					.getAll();
		}
		
		@Override
		protected void onPostExecute(List<Concept> concepts) {
			adapter.clear();
			System.out.println("m_arraySize : " + m_arraySize);
			for (int i = 0; i < m_arraySize; i++) {
				System.out.println(concepts.get(i)
						.getName() + " : " + concepts.get(i)
						.getPicture());
				if (!concepts.get(i)
						.getPicture()
						.equals("none")) {
					adapter.add(concepts.get(i));
				}
			}
			adapter.notifyDataSetChanged();
		}
	}
	
	//////////////////////////////////////////////////////////
	// GOTO WORK PICTOS
	/////////////
	public void gotoWorkPictoPictures(View view) {
		if (m_conceptCount < 1) {
			Toast.makeText(this, R.string.at_least_one_concept, Toast.LENGTH_SHORT)
					.show();
		} else {
			if (m_profile.getDefaultUserMode()
					.equals("scrolling")) {
				Intent intent = new Intent(PictoPicturesActivity.this, ScrollPictoPicturesActivity.class);
				intent.putIntegerArrayListExtra(ScrollPictoPicturesActivity.CONCEPTS_LIST, m_concepts);
				intent.putExtra(ScrollPictoPicturesActivity.PROFILE_POSITION, m_profilePosition);
				startActivity(intent);
			} else {
				Intent intent = new Intent(PictoPicturesActivity.this, ClickPictoPicturesActivity.class);
				intent.putIntegerArrayListExtra(ClickPictoPicturesActivity.CONCEPTS_LIST, m_concepts);
				intent.putExtra(ClickPictoPicturesActivity.PROFILE_POSITION, m_profilePosition);
				startActivity(intent);
			}
		}
	}
	
	//////////////////////////////////////////////////////////
	// BACK (to MainActivity)
	/////////////
	public void back(View view) {
		finish();
	}
}
