package fr.costerousse.locutus.controllers;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import fr.costerousse.locutus.R;
import fr.costerousse.locutus.db.DatabaseClient;
import fr.costerousse.locutus.models.Concept;
import id.zelory.compressor.Compressor;


public class CreateConceptActivity extends AppCompatActivity {
	//////////////////////////////////////////////////////////
	// Fields
	/////////////
	// Permissions
	private static final int REQUEST_CAMERA = 100;
	private static final int REQUEST_WRITE_EXTERNAL_STORAGE = 101;
	private static final int REQUEST_RECORD_AUDIO = 102;
	// Intent
	private static final int ADD_EXISTING_PICTO = 1;
	private static final int IMPORT_PICTO = 2;
	private static final int TAKE_PICTURE_FOR_PICTO = 3;
	private static final int LIMIT_PICTO = 10;
	
	private static final int ADD_EXISTING_SOUND = 11;
	private static final int IMPORT_SOUND = 12;
	private static final int CAPTURE_SOUND = 13;
	private static final int LIMIT_SOUND = 20;
	
	private static final int ADD_EXISTING_PICTURE = 21;
	private static final int IMPORT_PICTURE = 22;
	private static final int TAKE_PICTURE = 23;
	
	// Database & related
	private DatabaseClient mDb;
	private List<Concept> m_concepts;
	// To save
	private boolean m_destroyPicto = false;
	private boolean m_destroyPicture = false;
	private String m_picto = null;
	private String m_sound = null;
	private String m_soundPath = null;
	private Uri m_soundUri = null;
	private String m_picture = null;
	// View
	private ImageView m_imageViewPhoto;
	private EditText m_editTextConceptName;
	private Button m_buttonAddPicto;
	private Button m_buttonAddSound;
	private Button m_buttonAddPicture;
	
	//////////////////////////////////////////////////////////
	// ON CREATE
	// Retrieve data from database and view
	/////////////
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_concept);
		System.out.println("CreateConceptActivity : onCreate");
		
		if (Build.VERSION.SDK_INT >= 23) {
			System.out.println("CreateConceptActivity : onCreate : SDK >= 23");
			if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
				System.out.println("CreateConceptActivity : onCreate : SDK >= 23 : CAMERA DENIED");
				requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
			}
			if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
				System.out.println("CreateConceptActivity : onCreate : SDK >= 23 : WRITE_EXTERNAL_STORAGE DENIED");
				requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_EXTERNAL_STORAGE);
			}
			if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
				System.out.println("CreateConceptActivity : onCreate : SDK >= 23 : RECORD_AUDIO DENIED");
				requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_RECORD_AUDIO);
			}
		}
		
		// Retrieving concepts from the database
		mDb = DatabaseClient.getInstance(getApplicationContext());
		try {
			m_concepts = new GetConcepts().execute()
					.get();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		// Retrieving view's item
		m_imageViewPhoto = findViewById(R.id.image_view_picto);
		m_editTextConceptName = findViewById(R.id.edit_text_new_concept_name);
		m_buttonAddPicto = findViewById(R.id.button_add_picto);
		m_buttonAddSound = findViewById(R.id.button_add_sound);
		m_buttonAddPicture = findViewById(R.id.button_add_picture);
		
		// Pre-building AlertDialog
		final AlertDialog.Builder builder = new AlertDialog.Builder(CreateConceptActivity.this);
		
		//////////////////////////////////////////////////////////
		// ADD PICTO onClickListener
		/////////////
		m_buttonAddPicto.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				builder.setTitle(R.string.do_you_prefere)
						// EXISTING IMAGE
						.setPositiveButton(R.string.add_existing_image, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialogInterface, int i) {
								Intent intent = new Intent(CreateConceptActivity.this, AddExistingImageActivity.class);
								startActivityForResult(intent, ADD_EXISTING_PICTO);
							}
						})
						// IMPORT FROM GALLERY
						.setNegativeButton(R.string.import_image, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialogInterface, int i) {
								Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
								startActivityForResult(intent, IMPORT_PICTO);
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
								startActivityForResult(takePictureIntent, TAKE_PICTURE_FOR_PICTO);
							}
						}
					});
				}
				builder.create()
						.show();
			}
		});
		
		//////////////////////////////////////////////////////////
		// ADD SOUND onClickListener
		/////////////
		m_buttonAddSound.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				builder.setTitle(R.string.do_you_prefere)
						// EXISTING SOUND
						.setPositiveButton(R.string.add_existing_sound, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialogInterface, int i) {
								Intent intent = new Intent(CreateConceptActivity.this, AddExistingSoundActivity.class);
								startActivityForResult(intent, ADD_EXISTING_SOUND);
							}
						})
						// IMPORT FROM EXTERNAL STORAGE
						.setNegativeButton(R.string.import_sound, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialogInterface, int i) {
								Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
								startActivityForResult(intent, IMPORT_SOUND);
							}
						});
				if (getApplicationContext().getPackageManager()
						.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
					// CAPTURE SOUND
					builder.setNeutralButton(R.string.capture_sound, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialogInterface, int i) {
							Intent captureSoundIntent = new Intent(CreateConceptActivity.this, AudioCaptureActivity.class);
							startActivityForResult(captureSoundIntent, CAPTURE_SOUND);
						}
					});
				}
				builder.create()
						.show();
			}
		});
		
		//////////////////////////////////////////////////////////
		// ADD PICTURE onClickListener
		/////////////
		m_buttonAddPicture.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				builder.setTitle(R.string.do_you_prefere)
						// EXISTING IMAGE
						.setPositiveButton(R.string.add_existing_image, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialogInterface, int i) {
								Intent intent = new Intent(CreateConceptActivity.this, AddExistingImageActivity.class);
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
		});
	}
	
	//////////////////////////////////////////////////////////
	// ON ACTIVITY RESULT
	/////////////
	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		System.out.println("CreateConceptActivity : onActivityResult");
		if (resultCode == RESULT_OK && data != null) {
			System.out.println("CreateConceptActivity : onActivityResult : RESULT_OK && data != null");
			// EXISTING IMAGE
			if (requestCode == ADD_EXISTING_PICTO || requestCode == ADD_EXISTING_PICTURE) {
				if (requestCode == ADD_EXISTING_PICTO) {
					m_picto = data.getStringExtra("result");
					if (m_picto != null && m_picto.startsWith("dra_")) {
						try {
							m_imageViewPhoto.setImageResource(R.drawable.class.getField(m_picto)
									.getInt(R.drawable.class));
						} catch (IllegalAccessException e) {
							e.printStackTrace();
						} catch (NoSuchFieldException e) {
							e.printStackTrace();
						}
					} else {
						m_imageViewPhoto.setImageDrawable(Drawable.createFromPath(m_picto));
					}
					System.out.println("CreateConceptActivity : onActivityResult : RESULT_OK && data != null : requestCode == ADD_EXISTING_PICTO : " + m_picto);
				} else {
					m_picture = data.getStringExtra("result");
					if (m_picture != null && m_picture.startsWith("dra_")) {
						try {
							m_imageViewPhoto.setImageResource(R.drawable.class.getField(m_picture)
									.getInt(R.drawable.class));
						} catch (IllegalAccessException e) {
							e.printStackTrace();
						} catch (NoSuchFieldException e) {
							e.printStackTrace();
						}
					} else {
						m_imageViewPhoto.setImageDrawable(Drawable.createFromPath(m_picture));
					}
					System.out.println("CreateConceptActivity : onActivityResult : RESULT_OK && data != null : requestCode == ADD_EXISTING_PICTURE : " + m_picture);
				}
			}
			// GALLERY
			else if (requestCode == IMPORT_PICTO || requestCode == IMPORT_PICTURE) {
				System.out.println("CreateConceptActivity : onActivityResult : RESULT_OK && data != null : requestCode == IMPORT : " + data.getData());
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
				} catch (FileNotFoundException e) {
					e.printStackTrace();
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
				if (requestCode == IMPORT_PICTO) {
					// Get file's path
					m_picto = newFile.getAbsolutePath();
					// Allows the system to destroy the file if leaves without saving
					m_destroyPicto = true;
				} else { // Don't care at this
					m_picture = newFile.getAbsolutePath();
					m_destroyPicture = true;
				}
				m_imageViewPhoto.setImageDrawable(Drawable.createFromPath(newFile.getAbsolutePath()));
			}
			// TAKE PHOTO
			else if (requestCode == TAKE_PICTURE_FOR_PICTO || requestCode == TAKE_PICTURE) {
				Bundle extras = data.getExtras();
				Bitmap imageBitmap = null;
				if (extras != null) {
					imageBitmap = (Bitmap) extras.get("data");
				}
				if (requestCode == TAKE_PICTURE_FOR_PICTO) {
					if (imageBitmap != null) {
						m_picto = saveBitmapToInternalStorage(imageBitmap);
						System.out.println("CreateConceptActivity : onActivityResult : RESULT_OK && data != null : requestCode == TAKE_PICTURE_FOR_PICTO : " + m_picto);
					}
					m_destroyPicto = true;
				} else {
					if (imageBitmap != null) {
						m_picture = saveBitmapToInternalStorage(imageBitmap);
						System.out.println("CreateConceptActivity : onActivityResult : RESULT_OK && data != null : requestCode == TAKE_PICTURE : " + m_picture);
					}
					m_destroyPicture = true;
				}
				m_imageViewPhoto.setImageBitmap(imageBitmap);
			}
			// EXISTING SOUND
			else if (requestCode == ADD_EXISTING_SOUND) {
				m_sound = data.getStringExtra("result");
				System.out.println("CreateConceptActivity : onActivityResult : RESULT_OK && data != null : requestCode == ADD_EXISTING_SOUND : " + m_sound);
			}
			// IMPORT SOUND
			else if (requestCode == IMPORT_SOUND) {
				m_soundUri = data.getData();
				System.out.println("CreateConceptActivity : onActivityResult : RESULT_OK && data != null : requestCode == IMPORT_SOUND : " + m_soundUri);
			}
			// CAPTURE SOUND
			else if (requestCode == CAPTURE_SOUND) {
				m_soundPath = data.getStringExtra("result");
				System.out.println("CreateConceptActivity : onActivityResult : RESULT_OK && data != null : requestCode == CAPTURE_SOUND : " + m_soundPath);
			}
			
			// Disable the button
			if (requestCode < LIMIT_PICTO) {
				m_buttonAddPicto.setEnabled(false);
				m_buttonAddPicto.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.primary_weak));
			} else if (requestCode < LIMIT_SOUND) {
				m_buttonAddSound.setEnabled(false);
				m_buttonAddSound.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.primary_weak));
			} else {
				m_buttonAddPicture.setEnabled(false);
				m_buttonAddPicture.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.primary_weak));
			}
			
		} else {
			Toast.makeText(getApplicationContext(), R.string.resource_load_fail, Toast.LENGTH_LONG)
					.show();
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
	// NEW CONCEPT CONFIRM
	// Tests errors (calling GetConcepts) then calls SAVE CONCEPT or TOAST error
	/////////////
	public void newConceptConfirm(View view) {
		System.out.println("CreateConceptActivity : newConceptConfirm");
		if (TextUtils.isEmpty(m_editTextConceptName.getText()
				.toString()) || m_picto == null || (m_sound == null && m_soundPath == null && m_soundUri == null)) {
			System.out.println("CreateConceptActivity : newConceptConfirm : non-filled");
			Toast.makeText(getApplicationContext(), R.string.please_fill_required_fields, Toast.LENGTH_LONG)
					.show();
		} else {
			System.out.println("CreateConceptActivity : newConceptConfirm : filled");
			boolean conceptExist = false;
			for (int i = 0; i < m_concepts.size(); i++) {
				if (m_editTextConceptName.getText()
						.toString()
						.trim()
						.equals(m_concepts.get(i)
								.getName())) {
					conceptExist = true;
					System.out.println("CreateConceptActivity : newConceptConfirm : filled : conceptExists");
				}
			}
			if (!conceptExist) {
				System.out.println("CreateConceptActivity : newConceptConfirm : filled : !conceptExists");
				m_destroyPicto = false;
				m_destroyPicture = false;
				// SAVING SOUND FILE ON INTERNAL STORAGE
				if (m_soundPath != null) {
					m_sound = saveSoundOnInternalFromPath(m_soundPath);
				}
				if (m_soundUri != null) {
					m_sound = saveSoundOnInternalFromUri(m_soundUri);
				}
				saveConcept();
			} else {
				Toast.makeText(getApplicationContext(), R.string.please_enter_unexisting_concept, Toast.LENGTH_LONG)
						.show();
			}
		}
	}
	
	private String saveSoundOnInternalFromPath(String path) {
		Uri resultUri = Uri.fromFile(new File(path));
		String soundName = m_editTextConceptName.getText()
				.toString() + ".3gp";
		File newFile = new File(getApplicationContext().getFilesDir()
				.getAbsolutePath(), soundName);
		
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
		} catch (FileNotFoundException e) {
			e.printStackTrace();
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
		return newFile.getAbsolutePath();
	}
	
	private String saveSoundOnInternalFromUri(Uri uri) {
		String soundName = m_editTextConceptName.getText()
				.toString() + ".3gp";
		File newFile = new File(getApplicationContext().getFilesDir()
				.getAbsolutePath(), soundName);
		
		InputStream inputStream = null;
		OutputStream outputStream = null;
		try {
			if (uri != null) {
				inputStream = getContentResolver().openInputStream(uri);
			}
			outputStream = new FileOutputStream(newFile);
			if (inputStream != null) {
				IOUtils.copy(inputStream, outputStream);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
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
		return newFile.getAbsolutePath();
	}
	
	//////////////////////////////////////////////////////////
	// SAVE CONCEPT
	// Saves the concept into the database (asynchronously)
	/////////////
	private void saveConcept() {
		// Retrieving information from the view
		final String sConceptName = m_editTextConceptName.getText()
				.toString()
				.trim();
		final String sConceptPicto = m_picto;
		final String sConceptSound = m_sound;
		final String sConceptPicture = m_picture;
		System.out.println("CreateConceptActivity : saveConcept : sConceptName = " + sConceptName + ", sConceptPicto = " + sConceptPicto + ", sConceptSound = " + sConceptSound + ", sConceptPicture = " + sConceptPicture);
		
		//////////////////////////////////////////////////////////
		// SaveConcept
		// Saves the concept then goes to it's activity
		/////////////
		@SuppressLint("StaticFieldLeak")
		class SaveConcept extends AsyncTask<Void, Void, Concept> {
			@Override
			protected Concept doInBackground(Void... voids) {
				Concept concept = new Concept(sConceptName, sConceptPicto, sConceptSound);
				if (sConceptPicture != null) {
					concept.setPicture(sConceptPicture);
					System.out.println("CreateConceptActivity : saveConcept : SaveConcept : sConceptPicture != null : " + sConceptPicture);
				}
				mDb.getAppDatabase()
						.conceptDao()
						.insert(concept);
				
				return concept;
			}
			
			@Override
			protected void onPostExecute(Concept concept) {
				super.onPostExecute(concept);
				Intent intent = new Intent(CreateConceptActivity.this, ConceptManagementActivity.class);
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
	// GetConcepts
	// Allows to retrieve concepts from the database
	/////////////
	@SuppressLint("StaticFieldLeak")
	class GetConcepts extends AsyncTask<Void, Void, List<Concept>> {
		@Override
		protected List<Concept> doInBackground(Void... voids) {
			return mDb.getAppDatabase()
					.conceptDao()
					.getAll();
		}
	}
	
	//////////////////////////////////////////////////////////
	// ON DESTROY
	// Destroy images if the user don't save them
	/////////////
	@Override
	protected void onDestroy() {
		super.onDestroy();
		System.out.println("CreateConceptActivity : onDestroy");
		if (m_destroyPicto) {
			File file = new File(m_picto);
			System.out.println("CreateConceptActivity : onDestroy : m_picto : " + file.delete());
		}
		if (m_destroyPicture) {
			File file = new File(m_picture);
			System.out.println("CreateConceptActivity : onDestroy : m_picture : " + file.delete());
		}
	}
	
	//////////////////////////////////////////////////////////
	// BACK (to MainActivity)
	/////////////
	public void back(View view) {
		finish();
	}
}
