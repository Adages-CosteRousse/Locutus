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
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
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


public class ConceptActivity extends AppCompatActivity {
	//////////////////////////////////////////////////////////
	// Fields
	/////////////
	// Intent
	public static final String CONCEPT_POSITION = "concept_position";
	private int m_conceptPosition;
	private static final int ADD_EXISTING_PICTO = 1;
	private static final int IMPORT_PICTO = 2;
	private static final int TAKE_PICTURE_FOR_PICTO = 3;
	
	private static final int ADD_EXISTING_SOUND = 11;
	private static final int IMPORT_SOUND = 12;
	private static final int CAPTURE_SOUND = 13;
	
	private static final int ADD_EXISTING_PICTURE = 21;
	private static final int IMPORT_PICTURE = 22;
	private static final int TAKE_PICTURE = 23;
	// Permissions
	private static final int REQUEST_CAMERA = 100;
	private static final int REQUEST_WRITE_EXTERNAL_STORAGE = 101;
	private static final int REQUEST_RECORD_AUDIO = 102;
	// Database & related
	private DatabaseClient mDb;
	private Concept m_concept;
	// View
	private EditText m_editTextConceptName;
	private ImageView m_imageViewPicto;
	private ImageView m_imageViewPicture;
	private Button m_buttonPlay;
	private Button m_buttonDelete;
	private Button m_buttonSave;
	private boolean m_destroyPicto = false;
	private boolean m_destroyPicture = false;
	private Uri m_soundUri;
	private String m_soundPath;
	// Sound
	private MediaPlayer m_mediaPlayerSound = null;
	
	//////////////////////////////////////////////////////////
	// ON CREATE
	/////////////
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_concept);
		
		// Check permissions
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
		
		// Retrieve current concept
		mDb = DatabaseClient.getInstance(getApplicationContext());
		m_conceptPosition = getIntent().getIntExtra(CONCEPT_POSITION, 0);
		try {
			m_concept = new GetConcepts().execute()
					.get()
					.get(m_conceptPosition);
		} catch (ExecutionException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		// Retrieve view's item
		m_editTextConceptName = findViewById(R.id.edit_text_concept_name);
		m_imageViewPicto = findViewById(R.id.image_view_picto);
		m_imageViewPicture = findViewById(R.id.image_view_picture);
		m_buttonPlay = findViewById(R.id.button_play);
		m_buttonDelete = findViewById(R.id.button_delete_concept);
		m_buttonSave = findViewById(R.id.button_save_concept);
		
		// View processing
		m_editTextConceptName.setText(m_concept.getName());
		if (m_concept.getPicto()
				.startsWith("dra_")) {
			try {
				m_imageViewPicto.setImageResource(R.drawable.class.getField(m_concept.getPicto())
						.getInt(R.drawable.class));
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
			}
		} else {
			m_imageViewPicto.setImageDrawable(Drawable.createFromPath(m_concept.getPicto()));
		}
		
		if (!m_concept.getPicture()
				.equals("none")) {
			if (m_concept.getPicture()
					.startsWith("dra_")) {
				try {
					m_imageViewPicture.setImageResource(R.drawable.class.getField(m_concept.getPicture())
							.getInt(R.drawable.class));
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (NoSuchFieldException e) {
					e.printStackTrace();
				}
			} else {
				m_imageViewPicture.setImageDrawable(Drawable.createFromPath(m_concept.getPicture()));
			}
		}
		
		m_imageViewPicto.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				AlertDialog.Builder builder = new AlertDialog.Builder(ConceptActivity.this);
				builder.setTitle(R.string.do_you_prefere)
						// EXISTING IMAGE
						.setPositiveButton(R.string.add_existing_image, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialogInterface, int i) {
								Intent intent = new Intent(ConceptActivity.this, AddExistingImageActivity.class);
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
		
		m_imageViewPicture.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				AlertDialog.Builder builder = new AlertDialog.Builder(ConceptActivity.this);
				builder.setTitle(R.string.do_you_prefere)
						// EXISTING IMAGE
						.setPositiveButton(R.string.add_existing_image, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialogInterface, int i) {
								Intent intent = new Intent(ConceptActivity.this, AddExistingImageActivity.class);
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
		
		if (m_concept.getSound()
				.startsWith("sound_")) {
			try {
				m_mediaPlayerSound = MediaPlayer.create(getApplicationContext(), R.raw.class.getField(m_concept.getSound())
						.getInt(R.raw.class));
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
			}
		} else {
			m_mediaPlayerSound = MediaPlayer.create(getApplicationContext(), Uri.fromFile(new File(m_concept.getSound())));
		}
		m_buttonPlay.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				m_mediaPlayerSound.start();
			}
		});
		
		m_buttonDelete.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				AlertDialog.Builder builder = new AlertDialog.Builder(ConceptActivity.this);
				final AlertDialog alert = builder.create();
				builder.setTitle(R.string.confirm_concept_deletion)
						.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialogInterface, int i) {
								if (!m_concept.getPicto()
										.startsWith("dra_")) {
									File file = new File(m_concept.getPicto());
									System.out.println("CreateConceptActivity : onDestroy : m_picto : " + file.delete());
								}
								if (!m_concept.getPicture()
										.startsWith("dra_")) {
									File file = new File(m_concept.getPicture());
									System.out.println("CreateConceptActivity : onDestroy : m_picture : " + file.delete());
								}
								if (!m_concept.getSound()
										.startsWith("sound_")) {
									File file = new File(m_concept.getSound());
									System.out.println("CreateConceptActivity : onDestroy : m_sound : " + file.delete());
								}
								DeleteConcept dc = new DeleteConcept();
								dc.execute();
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
		
		m_buttonSave.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (m_soundPath != null) {
					m_concept.setSound(saveSoundOnInternalFromPath(m_soundPath));
				}
				if (m_soundUri != null) {
					m_concept.setSound(saveSoundOnInternalFromUri(m_soundUri));
				}
				m_concept.setName(m_editTextConceptName.getText()
						.toString()
						.trim());
				
				m_destroyPicto = false;
				m_destroyPicture = false;
				
				SaveConcept sc = new SaveConcept();
				sc.execute();
			}
		});
	}
	
	public void setSound(View view) {
		AlertDialog.Builder builder = new AlertDialog.Builder(ConceptActivity.this);
		builder.setTitle(R.string.do_you_prefere)
				// EXISTING SOUND
				.setPositiveButton(R.string.add_existing_sound, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						Intent intent = new Intent(ConceptActivity.this, AddExistingSoundActivity.class);
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
					Intent captureSoundIntent = new Intent(ConceptActivity.this, AudioCaptureActivity.class);
					startActivityForResult(captureSoundIntent, CAPTURE_SOUND);
				}
			});
		}
		builder.create()
				.show();
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
					m_concept.setPicto(data.getStringExtra("result"));
					if (m_concept.getPicto()
							.startsWith("dra_")) {
						try {
							m_imageViewPicto.setImageResource(R.drawable.class.getField(m_concept.getPicto())
									.getInt(R.drawable.class));
						} catch (IllegalAccessException e) {
							e.printStackTrace();
						} catch (NoSuchFieldException e) {
							e.printStackTrace();
						}
					} else {
						m_imageViewPicto.setImageDrawable(Drawable.createFromPath(m_concept.getPicto()));
					}
					System.out.println("CreateConceptActivity : onActivityResult : RESULT_OK && data != null : requestCode == ADD_EXISTING_PICTO : " + m_concept.getPicto());
				} else {
					m_concept.setPicture(data.getStringExtra("result"));
					if (m_concept.getPicture()
							.startsWith("dra_")) {
						try {
							m_imageViewPicture.setImageResource(R.drawable.class.getField(m_concept.getPicture())
									.getInt(R.drawable.class));
						} catch (IllegalAccessException e) {
							e.printStackTrace();
						} catch (NoSuchFieldException e) {
							e.printStackTrace();
						}
					} else {
						m_imageViewPicture.setImageDrawable(Drawable.createFromPath(m_concept.getPicture()));
					}
					System.out.println("CreateConceptActivity : onActivityResult : RESULT_OK && data != null : requestCode == ADD_EXISTING_PICTURE : " + m_concept.getPicture());
				}
			}
			// GALLERY
			else if (requestCode == IMPORT_PICTO || requestCode == IMPORT_PICTURE) {
				System.out.println("CreateConceptActivity : onActivityResult : RESULT_OK && data != null : requestCode == IMPORT : " + data.getData());
				Uri resultUri = data.getData();
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
				if (requestCode == IMPORT_PICTO) {
					m_concept.setPicto(newFile.getAbsolutePath());
					System.out.println("CreateConceptActivity : onActivityResult : RESULT_OK && data != null : requestCode == IMPORT_PICTO : " + m_concept.getPicto());
					m_destroyPicto = true;
					m_imageViewPicto.setImageDrawable(Drawable.createFromPath(m_concept.getPicto()));
				} else {
					m_concept.setPicture(newFile.getAbsolutePath());
					System.out.println("CreateConceptActivity : onActivityResult : RESULT_OK && data != null : requestCode == IMPORT_PICTURE : " + m_concept.getPicture());
					m_destroyPicture = true;
					m_imageViewPicto.setImageDrawable(Drawable.createFromPath(m_concept.getPicture()));
				}
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
						m_concept.setPicto(saveBitmapToInternalStorage(imageBitmap));
						System.out.println("CreateConceptActivity : onActivityResult : RESULT_OK && data != null : requestCode == TAKE_PICTURE_FOR_PICTO : " + m_concept.getPicto());
					}
					m_destroyPicto = true;
					m_imageViewPicto.setImageBitmap(imageBitmap);
				} else {
					if (imageBitmap != null) {
						m_concept.setPicture(saveBitmapToInternalStorage(imageBitmap));
						System.out.println("CreateConceptActivity : onActivityResult : RESULT_OK && data != null : requestCode == TAKE_PICTURE : " + m_concept.getPicture());
					}
					m_destroyPicture = true;
					m_imageViewPicture.setImageBitmap(imageBitmap);
				}
			}
			// EXISTING SOUND
			else if (requestCode == ADD_EXISTING_SOUND) {
				m_concept.setSound(data.getStringExtra("result"));
				
				try {
					m_mediaPlayerSound = MediaPlayer.create(getApplicationContext(), R.raw.class.getField(m_concept.getSound())
							.getInt(R.raw.class));
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (NoSuchFieldException e) {
					e.printStackTrace();
				}
				m_buttonPlay.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						m_mediaPlayerSound.start();
					}
				});
				System.out.println("CreateConceptActivity : onActivityResult : RESULT_OK && data != null : requestCode == ADD_EXISTING_SOUND : " + m_concept.getSound());
			}
			// IMPORT SOUND
			else if (requestCode == IMPORT_SOUND) {
				m_soundUri = data.getData();
				
				m_mediaPlayerSound = MediaPlayer.create(getApplicationContext(), m_soundUri);
				m_buttonPlay.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						m_mediaPlayerSound.start();
					}
				});
				System.out.println("CreateConceptActivity : onActivityResult : RESULT_OK && data != null : requestCode == IMPORT_SOUND : " + m_soundUri);
			}
			// CAPTURE SOUND
			else if (requestCode == CAPTURE_SOUND) {
				m_soundPath = data.getStringExtra("result");
				
				m_mediaPlayerSound = MediaPlayer.create(getApplicationContext(), Uri.fromFile(new File(m_soundPath)));
				m_buttonPlay.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						m_mediaPlayerSound.start();
					}
				});
				System.out.println("CreateConceptActivity : onActivityResult : RESULT_OK && data != null : requestCode == CAPTURE_SOUND : " + m_soundPath);
			}
			
		} else {
			Toast.makeText(getApplicationContext(), R.string.resource_load_fail, Toast.LENGTH_LONG)
					.show();
		}
	}
	
	//////////////////////////////////////////////////////////
	// ON DESTROY
	// Destroy images if the user don't save them. It is called only is images are updated
	/////////////
	@Override
	protected void onDestroy() {
		super.onDestroy();
		System.out.println("CreateConceptActivity : onDestroy");
		if (m_destroyPicto) {
			File file = new File(m_concept.getPicto());
			System.out.println("CreateConceptActivity : onDestroy : m_picto : " + file.delete());
		}
		if (m_destroyPicture) {
			File file = new File(m_concept.getPicture());
			System.out.println("CreateConceptActivity : onDestroy : m_picture : " + file.delete());
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
	// GetConcepts
	// Allows to retrieve concepts from database
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
	// SaveConcept
	// Save the current concept asynchronously then back to MainActivity
	/////////////
	@SuppressLint("StaticFieldLeak")
	class SaveConcept extends AsyncTask<Void, Void, Concept> {
		@Override
		protected Concept doInBackground(Void... voids) {
			mDb.getAppDatabase()
					.conceptDao()
					.update(m_concept);
			return null;
		}
		
		@Override
		protected void onPostExecute(Concept concept) {
			super.onPostExecute(concept);
			Toast.makeText(getApplicationContext(), R.string.concept_saved, Toast.LENGTH_LONG)
					.show();
			finish();
		}
	}
	
	//////////////////////////////////////////////////////////
	// DeleteConcept
	// Delete the current concept asynchronously then back to MainActivity
	/////////////
	@SuppressLint("StaticFieldLeak")
	class DeleteConcept extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... voids) {
			mDb.getAppDatabase()
					.conceptDao()
					.delete(m_concept);
			return null;
		}
		
		@Override
		protected void onPostExecute(Void aVoid) {
			super.onPostExecute(aVoid);
			Toast.makeText(getApplicationContext(), R.string.concept_deleted, Toast.LENGTH_LONG)
					.show();
			Intent intent = new Intent(ConceptActivity.this, MainActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
		}
	}
	
	//////////////////////////////////////////////////////////
	// BACK
	/////////////
	public void back(View view) {
		Intent intent = new Intent(ConceptActivity.this, ConceptManagementActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}
}
