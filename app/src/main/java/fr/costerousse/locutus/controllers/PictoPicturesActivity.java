package fr.costerousse.locutus.controllers;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import fr.costerousse.locutus.R;
import fr.costerousse.locutus.adapters.PickConceptsWithPictureAdapter;
import fr.costerousse.locutus.db.DatabaseClient;
import fr.costerousse.locutus.models.Concept;
import fr.costerousse.locutus.models.Profile;
import id.zelory.compressor.Compressor;


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
	private List<Concept> m_concepts;
	private Concept m_currentConcept;
	private int m_maxFrames;
	private String m_picture;
	// View
	private PickConceptsWithPictureAdapter adapter;
	private TextView m_textViewConceptsCount;
	private ListView m_listViewConcepts;
	//
	private int m_conceptCount = 0;
	private ArrayList<Integer> m_conceptsIntegers;
	private int m_arraySize;
	//
	private static final int ADD_EXISTING_PICTURE = 21;
	private static final int IMPORT_PICTURE = 22;
	private static final int TAKE_PICTURE = 23;
	
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
		try {
			m_concepts = new PictoPicturesActivity.GetConcepts().execute()
					.get();
			m_arraySize = m_concepts.size();
		} catch (ExecutionException | InterruptedException e) {
			e.printStackTrace();
		}
		
		// Fills m_concept
		m_conceptsIntegers = new ArrayList<>();
		System.out.println("Taille mconcept = " + m_conceptsIntegers.size());
		System.out.println("m_arraySize : " + m_arraySize);
		for (int i = 0; i < m_arraySize; i++) {
			m_conceptsIntegers.add(i, 0);
		}
		
		System.out.println("Taille après = " + m_conceptsIntegers.size());
		
		// Retrieves view's items
		m_textViewConceptsCount = findViewById(R.id.text_view_pick_concepts);
		m_listViewConcepts = findViewById(R.id.list_view_pick_concepts);
		
		// Processing
		m_textViewConceptsCount.setText(String.format(getResources().getString(R.string.concept_count), m_conceptCount, m_maxFrames));
		
		// Ties adapter to listview
		adapter = new PickConceptsWithPictureAdapter(this, new ArrayList<Concept>());
		m_listViewConcepts.setAdapter(adapter);
		
		// Pre-building AlertDialog
		final AlertDialog.Builder builder = new AlertDialog.Builder(PictoPicturesActivity.this);
		
		//////////////////////////////////////////////////////////
		// SET ON ITEM CLICK LISTENER
		/////////////
		m_listViewConcepts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
				TextView text = view.findViewById(R.id.text_view_pick_concepts);
				m_currentConcept = m_concepts.get(position);
				// ADD A NEW PICTURE IF EMPTY
				if (m_concepts.get(position)
						.getPicture()
						.equals("none")) {
					builder.setTitle(R.string.do_you_prefere)
							// EXISTING IMAGE
							.setPositiveButton(R.string.add_existing_image, new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialogInterface, int i) {
									Intent intent = new Intent(PictoPicturesActivity.this, AddExistingImageActivity.class);
									startActivityForResult(intent, ADD_EXISTING_PICTURE);
								}
							})
							// IMPORT FROM GALLERY
							.setNegativeButton(R.string.import_image, new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialogInterface, int i) {
									Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
									startActivityForResult(intent, IMPORT_PICTURE);
									Toast.makeText(getApplicationContext(), R.string.care_with_big_images, Toast.LENGTH_LONG)
											.show();
								}
							});
					if (getApplicationContext().getPackageManager()
							.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
						// TAKE PICTURE
						builder.setNeutralButton(R.string.take_picture, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialogInterface, int i) {
								Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
								if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
									startActivityForResult(takePictureIntent, TAKE_PICTURE);
								}
							}
						});
					}
					builder.create()
							.show();
				}
				
				// SELECT/UNSELECT THE CONCEPT
				else {
					if (m_conceptsIntegers.get(position) == 0 && m_conceptCount < m_maxFrames) {
						text.setTextColor(getResources().getColor(R.color.white));
						text.setBackgroundColor(getResources().getColor(R.color.check));
						m_conceptsIntegers.set(position, 1);
						System.out.println(m_conceptsIntegers);
						m_conceptCount++;
						m_textViewConceptsCount.setText(String.format(getResources().getString(R.string.concept_count), m_conceptCount, m_maxFrames));
					} else if (m_conceptsIntegers.get(position) == 1) {
						text.setTextColor(getResources().getColor(R.color.primary_weak));
						text.setBackgroundColor(getResources().getColor(R.color.white));
						m_conceptsIntegers.set(position, 0);
						System.out.println(m_conceptsIntegers);
						m_conceptCount--;
						m_textViewConceptsCount.setText(String.format(getResources().getString(R.string.concept_count), m_conceptCount, m_maxFrames));
					}
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
	// Allows to retrieve m_conceptsIntegers from database then add the list to the adapter
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
			adapter.addAll(concepts);
			adapter.notifyDataSetChanged();
		}
	}
	
	//////////////////////////////////////////////////////////
	// ON ACTIVITY RESULT
	/////////////
	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK && data != null) {
			// EXISTING IMAGE
			if (requestCode == ADD_EXISTING_PICTURE) {
				m_picture = data.getStringExtra("result");
			}
			// GALLERY
			else if (requestCode == IMPORT_PICTURE) {
				// Retrieves data from intent
				Uri resultUri = data.getData();
				// Create file's name
				Date currentTime = Calendar.getInstance()
						.getTime();
				String extension = null;
				if (resultUri != null) {
					extension = MimeTypeMap.getSingleton()
							.getExtensionFromMimeType(getContentResolver().getType(resultUri));
				}
				String imageName = "gal_" + currentTime + extension;
				File newFile = new File(getApplicationContext().getFilesDir()
						.getAbsolutePath(), imageName);
				// Copy from the gallery to internal storage's file
				InputStream inputStream = null;
				OutputStream outputStream = null;
				try {
					if (resultUri != null) {
						inputStream = getContentResolver().openInputStream(resultUri);
					}
					outputStream = new FileOutputStream(newFile);
					if (inputStream != null) {
						IOUtils.copy(inputStream, outputStream);
					}
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					try {
						if (inputStream != null) {
							inputStream.close();
						}
						if (outputStream != null) {
							outputStream.close();
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				// Compressing image
				try {
					newFile = new Compressor(this).compressToFile(newFile);
				} catch (IOException e) {
					e.printStackTrace();
				}
				m_picture = newFile.getAbsolutePath();
			}
			// TAKE PHOTO
			else if (requestCode == TAKE_PICTURE) {
				Bundle extras = data.getExtras();
				Bitmap imageBitmap = null;
				if (extras != null) {
					imageBitmap = (Bitmap) extras.get("data");
				}
				if (imageBitmap != null) {
					m_picture = saveBitmapToInternalStorage(imageBitmap);
				}
			}
			savePictureInConcept();
		}
	}
	
	//////////////////////////////////////////////////////////
	// SAVE BITMAP TO INTERNAL STORAGE
	// Save the bitmap from the camera to the internal storage and return the path
	/////////////
	private String saveBitmapToInternalStorage(Bitmap bitmap) {
		System.out.println("CreateConceptActivity : saveBitmapToInternalStorage");
		ContextWrapper cw = new ContextWrapper(getApplicationContext());
		File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
		Date currentTime = Calendar.getInstance()
				.getTime();
		String imageName = "pho_" + currentTime + ".jpg";
		File myPath = new File(directory, imageName);
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(myPath);
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				if (fos != null) {
					fos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return myPath.getPath();
	}
	
	//////////////////////////////////////////////////////////
	// SAVE CONCEPT
	// Saves the concept into the database (asynchronously)
	/////////////
	private void savePictureInConcept() {
		// Retrieving information
		final String sConceptPicture = m_picture;
		
		//////////////////////////////////////////////////////////
		// SaveConcept
		// Saves the concept then goes to it's activity
		/////////////
		@SuppressLint("StaticFieldLeak")
		class SaveConcept extends AsyncTask<Void, Void, Concept> {
			@Override
			protected Concept doInBackground(Void... voids) {
				if (!sConceptPicture.equals("none")) {
					m_currentConcept.setPicture(sConceptPicture);
				}
				mDb.getAppDatabase()
						.conceptDao()
						.update(m_currentConcept);
				
				return m_currentConcept;
			}
			
			@Override
			protected void onPostExecute(Concept concept) {
				super.onPostExecute(concept);
				Intent intent = new Intent(PictoPicturesActivity.this, PictoPicturesActivity.class);
				startActivity(intent);
				finish();
				Toast.makeText(getApplicationContext(), R.string.concept_saved, Toast.LENGTH_LONG)
						.show();
			}
		}
		
		// Execute the asynchronous class tasks
		SaveConcept sc = new SaveConcept();
		sc.execute();
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
				intent.putIntegerArrayListExtra(ScrollPictoPicturesActivity.CONCEPTS_LIST, m_conceptsIntegers);
				intent.putExtra(ScrollPictoPicturesActivity.PROFILE_POSITION, m_profilePosition);
				startActivity(intent);
			} else {
				Intent intent = new Intent(PictoPicturesActivity.this, ClickPictoPicturesActivity.class);
				intent.putIntegerArrayListExtra(ClickPictoPicturesActivity.CONCEPTS_LIST, m_conceptsIntegers);
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
