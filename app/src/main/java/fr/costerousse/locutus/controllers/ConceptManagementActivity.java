package fr.costerousse.locutus.controllers;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import fr.costerousse.locutus.R;
import fr.costerousse.locutus.adapters.ConceptsAdapter;
import fr.costerousse.locutus.db.DatabaseClient;
import fr.costerousse.locutus.models.Concept;


public class ConceptManagementActivity extends AppCompatActivity {
	//////////////////////////////////////////////////////////
	// Fields
	/////////////
	private DatabaseClient mDb;
	private ConceptsAdapter m_adapter;
	private ListView m_listViewConcepts;
	// Database
	private List<Concept> m_concepts;
	
	//////////////////////////////////////////////////////////
	// ON CREATE
	/////////////
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_concept_management);
		
		// Retrieves database
		mDb = DatabaseClient.getInstance(getApplicationContext());
		// Create GetConcepts and execute it's tasks
		try {
			m_concepts = new GetConcepts().execute()
					.get();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		// Retrieves view's item
		m_listViewConcepts = findViewById(R.id.list_view_concepts);
		
		// Ties m_adapter to listView
		m_adapter = new ConceptsAdapter(this, new ArrayList<Concept>());
		m_listViewConcepts.setAdapter(m_adapter);
		
		//////////////////////////////////////////////////////////
		// SET ON ITEM CLICK LISTENER
		/////////////
		m_listViewConcepts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
				MediaPlayer sound = null;
				if (m_concepts.get(position)
						.getSound()
						.startsWith("sound_")) {
					try {
						sound = MediaPlayer.create(getApplicationContext(), R.raw.class.getField(m_concepts.get(position)
								.getSound())
								.getInt(R.raw.class));
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (NoSuchFieldException e) {
						e.printStackTrace();
					}
				} else {
					sound = MediaPlayer.create(getApplicationContext(), Uri.fromFile(new File(m_concepts.get(position)
							.getSound())));
				}
				if (sound != null) {
					sound.start();
				}
			}
		});
		
		//////////////////////////////////////////////////////////
		// SET ON ITEM LONG CLICK LISTENER
		/////////////
		m_listViewConcepts.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
				Intent intent = new Intent(ConceptManagementActivity.this, ConceptActivity.class);
				intent.putExtra(ConceptActivity.CONCEPT_POSITION, position);
				finish();
				startActivity(intent);
				
				return false;
			}
		});
		
		
	}
	
	//////////////////////////////////////////////////////////
	// GetConcepts
	// Allows to retrieve concepts from database then add the list to the m_adapter
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
			m_adapter.clear();
			m_adapter.addAll(concepts);
			m_adapter.notifyDataSetChanged();
		}
	}
	
	//////////////////////////////////////////////////////////
	// GO TO CREATE CONCEPT
	/////////////
	public void gotoCreateConcept(View view) {
		Intent intent = new Intent(ConceptManagementActivity.this, CreateConceptActivity.class);
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
