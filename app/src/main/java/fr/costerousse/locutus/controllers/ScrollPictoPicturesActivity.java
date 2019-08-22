package fr.costerousse.locutus.controllers;


import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import fr.costerousse.locutus.R;


public class ScrollPictoPicturesActivity extends AppCompatActivity {
	//////////////////////////////////////////////////////////
	// Fields
	/////////////
	// Intent
	public static final String CONCEPTS_LIST = "concepts_list";
	public static final String PROFILE_POSITION = "profile_position";
	private List<Integer> m_integers;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scroll_picto_pictures);
	}
}
