package fr.costerousse.locutus.controllers;


import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;
import java.util.concurrent.ExecutionException;

import fr.costerousse.locutus.R;
import fr.costerousse.locutus.db.DatabaseClient;
import fr.costerousse.locutus.models.Profile;


public class PreferencesActivity extends AppCompatActivity {
	//////////////////////////////////////////////////////////
	// Fields
	/////////////
	// Intent
	public static final String PROFILE_POSITION = "profile_position";
	private int m_profilePosition;
	// Database & related
	private DatabaseClient mDb;
	private Profile m_profile;
	// View : global settings
	private CheckBox m_checkBoxApplicationsLauncher;
	private CheckBox m_checkBoxDefaultUser;
	private RadioButton m_radioButtonScrolling;
	private RadioButton m_radioButtonTactile;
	private TextView m_textViewProfileTree;
	private Button m_buttonSelectTreeOrChange;
	// View : scrolling settings
	private NumberPicker m_numberPickerFramesPerDisplay;
	private NumberPicker m_numberPickerScrollingTime;
	private CheckBox m_checkBoxSoundFeedback;
	// View : frames settings
	private RadioButton m_radioButtonClassic;
	private RadioButton m_radioButtonHighlighting;
	private RadioButton m_radioButtonRedLight;
	private RadioButton m_radioButtonCheckerboard;
	private TextView m_textViewSizeOrIntensity;
	private NumberPicker m_numberPickerSizeOrIntensity;
	private TextView m_textViewColor;
	private RadioGroup m_radioGroupColors;
	private RadioButton m_radioButtonBlack;
	private RadioButton m_radioButtonCyan;
	private RadioButton m_radioButtonGreen;
	private RadioButton m_radioButtonPink;
	private RadioButton m_radioButtonYellow;
	// View : confirm button
	private Button m_buttonConfirm;
	private Button m_buttonDeleteProfile;
	
	//////////////////////////////////////////////////////////
	// ON CREATE
	/////////////
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_preferences);
		
		// SharedPreferences
		final SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("preferences", MODE_PRIVATE);
		
		// Retrieves the current profile from the database
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
		
		// Retrieving view's items
		m_checkBoxApplicationsLauncher = findViewById(R.id.checkbox_applications_launcher);
		m_checkBoxDefaultUser = findViewById(R.id.checkbox_default_user);
		m_radioButtonScrolling = findViewById(R.id.radio_button_scrolling);
		m_radioButtonTactile = findViewById(R.id.radio_button_tactile);
		m_textViewProfileTree = findViewById(R.id.text_view_profile_tree);
		m_buttonSelectTreeOrChange = findViewById(R.id.button_select_tree_or_change);
		m_numberPickerFramesPerDisplay = findViewById(R.id.number_picker_frames_per_display);
		m_numberPickerScrollingTime = findViewById(R.id.number_picker_scrolling_time);
		m_checkBoxSoundFeedback = findViewById(R.id.checkbox_sound_feedback);
		m_radioButtonClassic = findViewById(R.id.radio_button_classic);
		m_radioButtonHighlighting = findViewById(R.id.radio_button_highlighting);
		m_radioButtonRedLight = findViewById(R.id.radio_button_red_light);
		m_radioButtonCheckerboard = findViewById(R.id.radio_button_checkerboard);
		m_textViewSizeOrIntensity = findViewById(R.id.text_view_size_or_intensity);
		m_numberPickerSizeOrIntensity = findViewById(R.id.number_picker_size_or_intensity);
		m_textViewColor = findViewById(R.id.text_view_color);
		m_radioGroupColors = findViewById(R.id.radio_group_colors);
		m_radioButtonBlack = findViewById(R.id.radio_button_black);
		m_radioButtonCyan = findViewById(R.id.radio_button_cyan);
		m_radioButtonGreen = findViewById(R.id.radio_button_green);
		m_radioButtonPink = findViewById(R.id.radio_button_pink);
		m_radioButtonYellow = findViewById(R.id.radio_button_yellow);
		m_buttonConfirm = findViewById(R.id.button_confirm);
		m_buttonDeleteProfile = findViewById(R.id.button_delete_profile);
		
		//////////////////////////////////////////////////////////
		// Modification of the view according to database's preferences
		/////////////
		// APPLICATION LAUNCHER
		if (m_profile.getApplicationsLauncher() == 1) {
			m_checkBoxApplicationsLauncher.setChecked(true);
		}
		
		// DEFAULT PROFILE
		if (sharedPreferences.getInt("default_profile", -1) == m_profilePosition) {
			m_checkBoxDefaultUser.setChecked(true);
		}
		
		// DEFAULT USER MODE
		if (m_profile.getDefaultUserMode()
				.equals("scrolling")) {
			m_radioButtonScrolling.setChecked(true);
		} else {
			m_radioButtonTactile.setChecked(true);
		}
		
		// TREE
		if (m_profile.getTree()
				.equals("none")) {
			m_buttonSelectTreeOrChange.setText(R.string.choose_tree);
		} else {
			m_textViewProfileTree.setText(m_profile.getTree());
			m_buttonSelectTreeOrChange.setText(R.string.change_tree);
		}
		
		// NUMBER OF FRAMES PER DISPLAY
		m_numberPickerFramesPerDisplay.setMinValue(2);
		m_numberPickerFramesPerDisplay.setMaxValue(8);
		m_numberPickerFramesPerDisplay.setValue(m_profile.getMaxFramesPerDisplay());
		
		// SCROLLING TIME
		m_numberPickerScrollingTime.setMinValue(2);
		m_numberPickerScrollingTime.setMaxValue(20);
		m_numberPickerScrollingTime.setValue(m_profile.getScrollingTime());
		
		// SOUND FEEDBACK
		if (m_profile.getSoundFeedback() == 1) {
			m_checkBoxSoundFeedback.setChecked(true);
		}
		
		// FRAME STYLE
		m_numberPickerSizeOrIntensity.setMinValue(5);
		m_numberPickerSizeOrIntensity.setMaxValue(100);
		switch (m_profile.getFrameStyle()) {
			case "classic":
				m_radioButtonClassic.setChecked(true);
				// SIZE
				m_textViewSizeOrIntensity.setVisibility(View.VISIBLE);
				m_textViewSizeOrIntensity.setText(R.string.size);
				m_numberPickerSizeOrIntensity.setVisibility(View.VISIBLE);
				m_numberPickerSizeOrIntensity.setValue(m_profile.getFrameSize());
				// COLOR
				m_textViewColor.setVisibility(View.VISIBLE);
				m_radioGroupColors.setVisibility(View.VISIBLE);
				switch (m_profile.getFrameColor()) {
					case "black":
						m_radioButtonBlack.setChecked(true);
						break;
					case "cyan":
						m_radioButtonCyan.setChecked(true);
						break;
					case "green":
						m_radioButtonGreen.setChecked(true);
						break;
					case "pink":
						m_radioButtonPink.setChecked(true);
						break;
					case "yellow":
						m_radioButtonYellow.setChecked(true);
						break;
				}
				break;
			case "highlighting":
				m_radioButtonHighlighting.setChecked(true);
				// INTENSITY
				m_textViewSizeOrIntensity.setVisibility(View.VISIBLE);
				m_textViewSizeOrIntensity.setText(R.string.intensity);
				m_numberPickerSizeOrIntensity.setVisibility(View.VISIBLE);
				m_numberPickerSizeOrIntensity.setValue(m_profile.getHighlightIntensity());
				// HIDE COLOR
				m_textViewColor.setVisibility(View.GONE);
				m_radioGroupColors.setVisibility(View.GONE);
				break;
			case "redLight":
				m_radioButtonRedLight.setChecked(true);
				// HIDE SIZE/INTENSITY & COLOR
				m_textViewSizeOrIntensity.setVisibility(View.GONE);
				m_numberPickerSizeOrIntensity.setVisibility(View.GONE);
				m_textViewColor.setVisibility(View.GONE);
				m_radioGroupColors.setVisibility(View.GONE);
				break;
			case "checkerboard":
				m_radioButtonCheckerboard.setChecked(true);
				// FRAME SIZE
				m_textViewSizeOrIntensity.setVisibility(View.VISIBLE);
				m_textViewSizeOrIntensity.setText(R.string.size);
				m_numberPickerSizeOrIntensity.setVisibility(View.VISIBLE);
				m_numberPickerSizeOrIntensity.setValue(m_profile.getFrameSize());
				// HIDE COLOR
				m_textViewColor.setVisibility(View.GONE);
				m_radioGroupColors.setVisibility(View.GONE);
				break;
		}
		
		//////////////////////////////////////////////////////////
		// DELETE PROFILE onClickListener
		// Set or disable (-1) this profile as the default profile then calls savePreferences()
		/////////////
		m_buttonDeleteProfile.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				AlertDialog.Builder builder = new AlertDialog.Builder(PreferencesActivity.this);
				final AlertDialog alert = builder.create();
				builder.setTitle(R.string.confirm_deletion)
						.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialogInterface, int i) {
								DeleteProfile dp = new DeleteProfile();
								dp.execute();
							}
						})
						.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialogInterface, int i) {
								alert.cancel();
							}
						})
						.create()
						.show();
			}
		});
		
		//////////////////////////////////////////////////////////
		// CONFIRM onClickListener
		// Set or disable (-1) this profile as the default profile then calls savePreferences()
		/////////////
		m_buttonConfirm.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (m_checkBoxDefaultUser.isChecked()) {
					sharedPreferences.edit()
							.putInt("default_profile", m_profilePosition)
							.apply();
				} else {
					if (sharedPreferences.getInt("default_profile", -1) == m_profilePosition) {
						sharedPreferences.edit()
								.putInt("default_profile", -1)
								.apply();
					}
				}
				savePreferences();
				finish();
			}
		});
	}
	
	//////////////////////////////////////////////////////////
	// CLASSIC CLICKED
	// Changes the view to show the size and color selector
	/////////////
	public void classicClicked(View view) {
		// Modification de la vue
		m_radioButtonClassic.setChecked(true);
		m_textViewSizeOrIntensity.setVisibility(View.VISIBLE);
		m_textViewSizeOrIntensity.setText(R.string.size);
		m_numberPickerSizeOrIntensity.setVisibility(View.VISIBLE);
		m_numberPickerSizeOrIntensity.setValue(m_profile.getFrameSize());
		m_textViewColor.setVisibility(View.VISIBLE);
		m_radioGroupColors.setVisibility(View.VISIBLE);
		switch (m_profile.getFrameColor()) {
			case "black":
				m_radioButtonBlack.setChecked(true);
				break;
			case "cyan":
				m_radioButtonCyan.setChecked(true);
				break;
			case "green":
				m_radioButtonGreen.setChecked(true);
				break;
			case "pink":
				m_radioButtonPink.setChecked(true);
				break;
			case "yellow":
				m_radioButtonYellow.setChecked(true);
				break;
		}
	}
	
	//////////////////////////////////////////////////////////
	// HIGHLIGHTING CLICKED
	// Changes the view to show the intensity and hide the color selector
	/////////////
	public void highlightingClicked(View view) {
		m_radioButtonHighlighting.setChecked(true);
		m_textViewSizeOrIntensity.setVisibility(View.VISIBLE);
		m_textViewSizeOrIntensity.setText(R.string.intensity);
		m_numberPickerSizeOrIntensity.setVisibility(View.VISIBLE);
		m_numberPickerSizeOrIntensity.setValue(m_profile.getHighlightIntensity());
		m_textViewColor.setVisibility(View.GONE);
		m_radioGroupColors.setVisibility(View.GONE);
	}
	
	//////////////////////////////////////////////////////////
	// REDLIGHT CLICKED
	// Changes the view to hide the size/intensity and color selector
	/////////////
	public void redLightClicked(View view) {
		m_radioButtonRedLight.setChecked(true);
		m_textViewSizeOrIntensity.setVisibility(View.GONE);
		m_numberPickerSizeOrIntensity.setVisibility(View.GONE);
		m_textViewColor.setVisibility(View.GONE);
		m_radioGroupColors.setVisibility(View.GONE);
	}
	
	//////////////////////////////////////////////////////////
	// CHECKERBOARD CLICKED
	// Changes the view to show the size/intensity and hide color selector
	/////////////
	public void checkerboardClicked(View view) {
		m_radioButtonCheckerboard.setChecked(true);
		m_textViewSizeOrIntensity.setVisibility(View.VISIBLE);
		m_textViewSizeOrIntensity.setText(R.string.size);
		m_numberPickerSizeOrIntensity.setVisibility(View.VISIBLE);
		m_numberPickerSizeOrIntensity.setValue(m_profile.getFrameSize());
		m_textViewColor.setVisibility(View.GONE);
		m_radioGroupColors.setVisibility(View.GONE);
	}
	
	//////////////////////////////////////////////////////////
	// GO TO TREE CHOICE
	// Goes to the tree's choice
	/////////////
	public void gotoTreeChoice(View view) {
		Intent intent = new Intent(PreferencesActivity.this, TreeChoiceActivity.class);
		startActivity(intent);
	}
	
	//////////////////////////////////////////////////////////
	// BACK (to UserProfileActivity)
	/////////////
	public void back(View view) {
		finish();
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
	// SAVE PREFERENCES
	// Retrieves data from the view then check conditions to store it into the database (asynchronously)
	/////////////
	private void savePreferences() {
		// Fields
		final int sApplicationsLauncher;
		final String sDefaultProfileMode;
		final int sSoundFeedback;
		final String sFrameStyle;
		final int sFrameSize;
		final String sFrameColor;
		final int sHighlightIntensity;
		
		//////////////////////////////////////////////////////////
		// Checks conditions and initializes fields
		/////////////
		// APPLICATION LAUNCHER
		if (m_checkBoxApplicationsLauncher.isChecked()) {
			sApplicationsLauncher = 1;
		} else {
			sApplicationsLauncher = 0;
		}
		// USER DEFAULT MODE
		if (m_radioButtonScrolling.isChecked()) {
			sDefaultProfileMode = "scrolling";
		} else {
			sDefaultProfileMode = "tactile";
		}
		// MAX FRAMES PER DISPLAY
		final Integer sMaxFramesPerDisplay = m_numberPickerFramesPerDisplay.getValue();
		// SCROLLING TIME
		final Integer sScrollingTime = m_numberPickerScrollingTime.getValue();
		// SOUND FEEDBACK
		if (m_checkBoxSoundFeedback.isChecked()) {
			sSoundFeedback = 1;
		} else {
			sSoundFeedback = 0;
		}
		// FRAME STYLE
		if (m_radioButtonHighlighting.isChecked()) {
			sFrameStyle = "highlighting";
			sHighlightIntensity = m_numberPickerSizeOrIntensity.getValue();
			// WARN
			sFrameSize = m_profile.getFrameSize();
			sFrameColor = m_profile.getFrameColor();
		} else if (m_radioButtonRedLight.isChecked()) {
			sFrameStyle = "redLight";
			// WARN
			sFrameSize = m_profile.getFrameSize();
			sFrameColor = m_profile.getFrameColor();
			sHighlightIntensity = m_profile.getHighlightIntensity();
		} else if (m_radioButtonCheckerboard.isChecked()) {
			sFrameStyle = "checkerboard";
			sFrameSize = m_numberPickerSizeOrIntensity.getValue();
			// WARN
			sFrameColor = m_profile.getFrameColor();
			sHighlightIntensity = m_profile.getHighlightIntensity();
		} else {
			sFrameStyle = "classic";
			sFrameSize = m_numberPickerSizeOrIntensity.getValue();
			if (m_radioButtonCyan.isChecked()) {
				sFrameColor = "cyan";
			} else if (m_radioButtonGreen.isChecked()) {
				sFrameColor = "green";
			} else if (m_radioButtonPink.isChecked()) {
				sFrameColor = "pink";
			} else if (m_radioButtonYellow.isChecked()) {
				sFrameColor = "yellow";
			} else {
				sFrameColor = "black";
			}
			// WARN
			sHighlightIntensity = m_profile.getHighlightIntensity();
		}
		
		//////////////////////////////////////////////////////////
		// SavePreferences
		// Stores preferences from method's fields into database
		/////////////
		@SuppressLint("StaticFieldLeak")
		class SavePreferences extends AsyncTask<Void, Void, Profile> {
			@Override
			protected Profile doInBackground(Void... voids) {
				m_profile.setApplicationsLauncher(sApplicationsLauncher);
				m_profile.setDefaultUserMode(sDefaultProfileMode);
				m_profile.setMaxFramesPerDisplay(sMaxFramesPerDisplay);
				m_profile.setScrollingTime(sScrollingTime);
				m_profile.setSoundFeedback(sSoundFeedback);
				m_profile.setFrameStyle(sFrameStyle);
				switch (sFrameStyle) {
					case "classic":
						m_profile.setFrameSize(sFrameSize);
						m_profile.setFrameColor(sFrameColor);
						break;
					case "checkerboard":
						m_profile.setFrameSize(sFrameSize);
						break;
					case "highlighting":
						m_profile.setHighlightIntensity(sHighlightIntensity);
						break;
					case "redLight":
						break;
				}
				
				mDb.getAppDatabase()
						.profileDao()
						.update(m_profile);
				
				return m_profile;
			}
			
			@Override
			protected void onPostExecute(Profile profile) {
				super.onPostExecute(profile);
				Toast.makeText(getApplicationContext(), R.string.profile_saved, Toast.LENGTH_LONG)
						.show();
			}
		}
		
		// Execute the asynchronous class tasks
		SavePreferences sp = new SavePreferences();
		sp.execute();
	}
	
	//////////////////////////////////////////////////////////
	// DeleteProfile
	// Delete the current profile asynchronously then back to MainActivity
	/////////////
	@SuppressLint("StaticFieldLeak")
	class DeleteProfile extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... voids) {
			mDb.getAppDatabase()
					.profileDao()
					.delete(m_profile);
			return null;
		}
		
		@Override
		protected void onPostExecute(Void aVoid) {
			super.onPostExecute(aVoid);
			Toast.makeText(getApplicationContext(), R.string.profile_deleted, Toast.LENGTH_LONG)
					.show();
			Intent intent = new Intent(PreferencesActivity.this, MainActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
		}
	}
	
}
