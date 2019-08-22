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
import java.util.concurrent.ExecutionException;

import fr.costerousse.locutus.R;
import fr.costerousse.locutus.adapters.TreesAdapter;
import fr.costerousse.locutus.db.DatabaseClient;
import fr.costerousse.locutus.models.Tree;


public class TreeManagementActivity extends AppCompatActivity {
	//////////////////////////////////////////////////////////
	// Fields
	/////////////
	private DatabaseClient mDb;
	private TreesAdapter m_adapter;
	private ListView m_listViewTrees;
	// Database
	private List<Tree> m_trees;
	
	//////////////////////////////////////////////////////////
	// ON CREATE
	/////////////
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tree_management);

		// Retrieves database
		mDb = DatabaseClient.getInstance(getApplicationContext());
		// Create GetConcepts and execute it's tasks
		try {
			m_trees = new GetTrees().execute()
					.get();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Retrieves view's item
		m_listViewTrees = findViewById(R.id.list_view_trees);

		// Ties m_adapter to listView
		m_adapter = new TreesAdapter(this, new ArrayList<Tree>());
		m_listViewTrees.setAdapter(m_adapter);

		//////////////////////////////////////////////////////////
		// SET ON ITEM CLICK LISTENER
		/////////////
		m_listViewTrees.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
				Intent intent = new Intent(TreeManagementActivity.this, TreeActivity.class);
				intent.putExtra(TreeActivity.TREE_POSITION, position);
				finish();
				startActivity(intent);
			}
		});
	}

	//////////////////////////////////////////////////////////
	// GetTrees
	// Allows to retrieve trees from database then add the list to the m_adapter
	/////////////
	@SuppressLint("StaticFieldLeak")
	class GetTrees extends AsyncTask<Void, Void, List<Tree>> {
		@Override
		protected List<Tree> doInBackground(Void... voids) {
			return mDb.getAppDatabase()
					.treeDao()
					.getAll();
		}

		@Override
		protected void onPostExecute(List<Tree> trees) {
			m_adapter.clear();
			m_adapter.addAll(trees);
			m_adapter.notifyDataSetChanged();
		}
	}

	//////////////////////////////////////////////////////////
	// GO TO CREATE TREE
	/////////////
	public void gotoCreateTree(View view) {
		Intent intent = new Intent(TreeManagementActivity.this, CreateTreeActivity.class);
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
