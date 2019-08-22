package fr.costerousse.locutus.controllers;


import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import fr.costerousse.locutus.R;


public class TreeActivity extends AppCompatActivity {
	//////////////////////////////////////////////////////////
	// Fields
	/////////////
	// Intent
	public static final String TREE_POSITION = "tree_position";
	private int m_treePosition;
	
	//////////////////////////////////////////////////////////
	// ON CREATE
	/////////////
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_trees);
	}
}
