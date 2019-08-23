package fr.costerousse.locutus.controllers;


import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import fr.costerousse.locutus.R;
import fr.costerousse.locutus.db.DatabaseClient;
import fr.costerousse.locutus.models.Concept;
import fr.costerousse.locutus.models.Profile;


public class ScrollPictoPicturesActivity extends AppCompatActivity {
	//////////////////////////////////////////////////////////
	// Fields
	/////////////
	// Intent
	public static final String CONCEPTS_LIST = "concepts_list";
	public static final String PROFILE_POSITION = "profile_position";
	private List<Integer> m_integers;
	// Database & related
	private DatabaseClient mDb;
	private Profile m_profile;
	private int m_padding = 0;
	private int m_gniddap = 0;
	// Concepts
	private ArrayList<Concept> m_concepts = new ArrayList<>();
	//
	private MediaPlayer m_mediaPlayer = null;
	private boolean m_play = true;
	private boolean m_pause = false;
	// Scrolling
	private int m_currentConcept = 0;
	private boolean m_scroll = true;
	private Thread m_thread;
	
	//////////////////////////////////////////////////////////
	// ON CREATE
	/////////////
	@SuppressLint("ClickableViewAccessibility")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_work_picto_pictures);
		
		// Database
		mDb = DatabaseClient.getInstance(getApplicationContext());
		List<Concept> concepts = null;
		try {
			concepts = new ScrollPictoPicturesActivity.GetConcepts().execute()
					.get();
		} catch (ExecutionException | InterruptedException e) {
			e.printStackTrace();
		}
		
		// Retrieves profile & preferences
		try {
			m_profile = new ScrollPictoPicturesActivity.GetProfiles().execute()
					.get()
					.get(getIntent().getIntExtra("profile_position", 0));
		} catch (ExecutionException | InterruptedException e) {
			e.printStackTrace();
		}
		m_padding = m_profile.getFrameSize();
		m_gniddap = 100 - m_padding;
		
		// Retrieves the binary list of checked/unchecked IDs
		m_integers = getIntent().getIntegerArrayListExtra("concepts_list");
		
		// Retrieves concepts from the IDs
		if (m_integers != null) {
			for (int i = 0; i < m_integers.size(); i++) {
				if (m_integers.get(i) == 1) {
					if (concepts != null) {
						m_concepts.add(concepts.get(i));
					} else {
						System.out.println("NULLPOINTER");
					}
				}
			}
		}
		
		// Retrieves view's items
		LinearLayout linearLayoutWork = findViewById(R.id.linear_layout_work_picto_pictures);
		final ArrayList<LinearLayout> containers = new ArrayList<>();
		final ArrayList<ImageView> foregrounds = new ArrayList<>();
		final ArrayList<ImageView> imageViews = new ArrayList<>();
		final ArrayList<ImageView> backgrounds = new ArrayList<>();
		
		// Image 1
		LinearLayout linearLayoutContainerTop1 = findViewById(R.id.linear_layout_container_top1);
		containers.add(linearLayoutContainerTop1);
		ImageView foregroundTop1 = findViewById(R.id.image_view_foreground_top1);
		foregrounds.add(foregroundTop1);
		ImageView imageViewTop1 = findViewById(R.id.image_view_work_picto_top1);
		imageViews.add(imageViewTop1);
		ImageView backgroundTop1 = findViewById(R.id.image_view_background_top1);
		backgrounds.add(backgroundTop1);
		
		// Image 2
		LinearLayout linearLayoutContainerTop2 = findViewById(R.id.linear_layout_container_top2);
		containers.add(linearLayoutContainerTop2);
		ImageView foregroundTop2 = findViewById(R.id.image_view_foreground_top2);
		foregrounds.add(foregroundTop2);
		ImageView imageViewTop2 = findViewById(R.id.image_view_work_picto_top2);
		imageViews.add(imageViewTop2);
		ImageView backgroundTop2 = findViewById(R.id.image_view_background_top2);
		backgrounds.add(backgroundTop2);
		
		// Image 3
		LinearLayout linearLayoutContainerTop3 = findViewById(R.id.linear_layout_container_top3);
		containers.add(linearLayoutContainerTop3);
		ImageView foregroundTop3 = findViewById(R.id.image_view_foreground_top3);
		foregrounds.add(foregroundTop3);
		ImageView imageViewTop3 = findViewById(R.id.image_view_work_picto_top3);
		imageViews.add(imageViewTop3);
		ImageView backgroundTop3 = findViewById(R.id.image_view_background_top3);
		backgrounds.add(backgroundTop3);
		
		// Image 4
		LinearLayout linearLayoutContainerTop4 = findViewById(R.id.linear_layout_container_top4);
		containers.add(linearLayoutContainerTop4);
		ImageView foregroundTop4 = findViewById(R.id.image_view_foreground_top4);
		foregrounds.add(foregroundTop4);
		ImageView imageViewTop4 = findViewById(R.id.image_view_work_picto_top4);
		imageViews.add(imageViewTop4);
		ImageView backgroundTop4 = findViewById(R.id.image_view_background_top4);
		backgrounds.add(backgroundTop4);
		
		// Image 5
		LinearLayout linearLayoutContainerBottom1 = findViewById(R.id.linear_layout_container_bottom1);
		containers.add(linearLayoutContainerBottom1);
		ImageView foregroundBottom1 = findViewById(R.id.image_view_foreground_bottom1);
		foregrounds.add(foregroundBottom1);
		ImageView imageViewBottom1 = findViewById(R.id.image_view_work_picto_bottom1);
		imageViews.add(imageViewBottom1);
		ImageView backgroundBottom1 = findViewById(R.id.image_view_background_bottom1);
		backgrounds.add(backgroundBottom1);
		
		// Image 6
		LinearLayout linearLayoutContainerBottom2 = findViewById(R.id.linear_layout_container_bottom2);
		containers.add(linearLayoutContainerBottom2);
		ImageView foregroundBottom2 = findViewById(R.id.image_view_foreground_bottom2);
		foregrounds.add(foregroundBottom2);
		ImageView imageViewBottom2 = findViewById(R.id.image_view_work_picto_bottom2);
		imageViews.add(imageViewBottom2);
		ImageView backgroundBottom2 = findViewById(R.id.image_view_background_bottom2);
		backgrounds.add(backgroundBottom2);
		
		// Image 7
		LinearLayout linearLayoutContainerBottom3 = findViewById(R.id.linear_layout_container_bottom3);
		containers.add(linearLayoutContainerBottom3);
		ImageView foregroundBottom3 = findViewById(R.id.image_view_foreground_bottom3);
		foregrounds.add(foregroundBottom3);
		ImageView imageViewBottom3 = findViewById(R.id.image_view_work_picto_bottom3);
		imageViews.add(imageViewBottom3);
		ImageView backgroundBottom3 = findViewById(R.id.image_view_background_bottom3);
		backgrounds.add(backgroundBottom3);
		
		// Image 8
		LinearLayout linearLayoutContainerBottom4 = findViewById(R.id.linear_layout_container_bottom4);
		containers.add(linearLayoutContainerBottom4);
		ImageView foregroundBottom4 = findViewById(R.id.image_view_foreground_bottom4);
		foregrounds.add(foregroundBottom4);
		ImageView imageViewBottom4 = findViewById(R.id.image_view_work_picto_bottom4);
		imageViews.add(imageViewBottom4);
		ImageView backgroundBottom4 = findViewById(R.id.image_view_background_bottom4);
		backgrounds.add(backgroundBottom4);
		
		// Processing on view
		for (int i = 0; i < m_concepts.size(); i++) {
			final int position = i;
			
			containers.get(i)
					.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1f));
			containers.get(i + 4)
					.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1f));
			
			imageViews.get(i)
					.post(new Runnable() {
						@Override
						public void run() {
							// CONTAINER
							int containerHeight = containers.get(position)
									.getHeight();
							int picContainerHeight = containers.get(position + 4)
									.getHeight();
							int containerWidth = containers.get(position)
									.getWidth();
							int picContainerWidth = containers.get(position + 4)
									.getWidth();
							
							// IMAGE
							int imageHeight = imageViews.get(position)
									.getHeight();
							int picImageHeight = imageViews.get(position + 4)
									.getHeight();
							int imageWidth = imageViews.get(position)
									.getWidth();
							int picImageWidth = imageViews.get(position + 4)
									.getWidth();
							
							// DIF FOR PADDING
							int difHeight = containerHeight - imageHeight;
							int difWidth = containerWidth - imageWidth;
							int difPicHeight = picContainerHeight - picImageHeight;
							int difPicWidth = picContainerWidth - picImageWidth;
							
							if (difHeight * imageWidth / imageHeight > difWidth * imageHeight / imageWidth) {
								int upDownPadding = ((containerHeight - (containerWidth * imageHeight / imageWidth)) / 2) + (m_gniddap * imageHeight / imageWidth);
								containers.get(position)
										.setPadding(m_gniddap, upDownPadding, m_gniddap, upDownPadding);
							} else if (difHeight * imageWidth / imageHeight < difWidth * imageHeight / imageWidth) {
								int sidesPadding = ((containerWidth - (containerHeight * imageWidth / imageHeight)) / 2) + (m_gniddap * imageWidth / imageHeight);
								containers.get(position)
										.setPadding(sidesPadding, m_gniddap, sidesPadding, m_gniddap);
							} else {
								containers.get(position)
										.setPadding(m_gniddap * imageHeight / imageWidth, m_gniddap * imageWidth / imageHeight, m_gniddap * imageHeight / imageWidth, m_gniddap * imageWidth / imageHeight);
							}
							if (difPicHeight * picImageWidth / picImageHeight > difPicWidth * picImageHeight / picImageWidth) {
								int picUpDownPadding = ((picContainerHeight - (picContainerWidth * picImageHeight / picImageWidth)) / 2) + (m_gniddap * picImageHeight / picImageWidth);
								containers.get(position + 4)
										.setPadding(m_gniddap, picUpDownPadding, m_gniddap, picUpDownPadding);
							} else if (difPicHeight * picImageWidth / picImageHeight < difPicWidth * picImageHeight / picImageWidth) {
								int sidesPicPadding = ((picContainerWidth - (picContainerHeight * picImageWidth / picImageHeight)) / 2) + (m_gniddap * picImageWidth / picImageHeight);
								containers.get(position + 4)
										.setPadding(sidesPicPadding, m_gniddap, sidesPicPadding, m_gniddap);
							} else {
								containers.get(position + 4)
										.setPadding(m_gniddap * picImageHeight / picImageWidth, m_gniddap * picImageWidth / picImageHeight, m_gniddap * picImageHeight / picImageWidth, m_gniddap * picImageWidth / picImageHeight);
							}
							imageViews.get(position)
									.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
							imageViews.get(position + 4)
									.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
							
							// FOREGROUND & BACKGROUND
							switch (m_profile.getFrameStyle()) {
								case "classic":
									imageViews.get(position)
											.setPadding(m_padding, m_padding, m_padding, m_padding);
									imageViews.get(position + 4)
											.setPadding(m_padding, m_padding, m_padding, m_padding);
									switch (m_profile.getFrameColor()) {
										case "black":
											backgrounds.get(position)
													.setBackgroundColor(Color.BLACK);
											backgrounds.get(position + 4)
													.setBackgroundColor(Color.BLACK);
											break;
										case "cyan":
											backgrounds.get(position)
													.setBackgroundColor(Color.CYAN);
											backgrounds.get(position + 4)
													.setBackgroundColor(Color.CYAN);
											break;
										case "green":
											backgrounds.get(position)
													.setBackgroundColor(Color.GREEN);
											backgrounds.get(position + 4)
													.setBackgroundColor(Color.GREEN);
											break;
										case "pink":
											backgrounds.get(position)
													.setBackgroundColor(getResources().getColor(R.color.pink));
											backgrounds.get(position + 4)
													.setBackgroundColor(getResources().getColor(R.color.pink));
											break;
										case "yellow":
											backgrounds.get(position)
													.setBackgroundColor(getResources().getColor(R.color.yellow));
											backgrounds.get(position + 4)
													.setBackgroundColor(getResources().getColor(R.color.yellow));
											break;
										default:
											backgrounds.get(position)
													.setBackgroundColor(Color.RED);
											backgrounds.get(position + 4)
													.setBackgroundColor(Color.RED);
									}
									break;
								case "highlighting":
									foregrounds.get(position)
											.setBackgroundColor(getResources().getColor(R.color.yellow));
									foregrounds.get(position + 4)
											.setBackgroundColor(getResources().getColor(R.color.yellow));
									foregrounds.get(position)
											.getBackground()
											.setAlpha(m_profile.getHighlightIntensity() * 2);
									foregrounds.get(position + 4)
											.getBackground()
											.setAlpha(m_profile.getHighlightIntensity() * 2);
									foregrounds.get(position)
											.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
									foregrounds.get(position + 4)
											.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
									break;
								case "redLight":
									foregrounds.get(position)
											.setBackground(getResources().getDrawable(R.drawable.img_red_light));
									foregrounds.get(position + 4)
											.setBackground(getResources().getDrawable(R.drawable.img_red_light));
									foregrounds.get(position)
											.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
									foregrounds.get(position + 4)
											.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
									break;
								case "checkerboard":
									imageViews.get(position)
											.setPadding(m_padding, m_padding, m_padding, m_padding);
									imageViews.get(position + 4)
											.setPadding(m_padding, m_padding, m_padding, m_padding);
									backgrounds.get(position)
											.setBackground(getResources().getDrawable(R.drawable.img_checkerboard));
									backgrounds.get(position + 4)
											.setBackground(getResources().getDrawable(R.drawable.img_checkerboard));
									break;
							}
						}
					});
			
			if (m_concepts.get(i)
					.getPicto()
					.startsWith("dra_")) {
				try {
					imageViews.get(i)
							.setImageResource(R.drawable.class.getField(m_concepts.get(i)
									.getPicto())
									.getInt(R.drawable.class));
				} catch (IllegalAccessException | NoSuchFieldException e) {
					e.printStackTrace();
				}
			} else {
				imageViews.get(i)
						.setImageDrawable(Drawable.createFromPath(m_concepts.get(i)
								.getPicto()));
			}
			if (m_concepts.get(i)
					.getPicture()
					.startsWith("dra_")) {
				try {
					imageViews.get(i + 4)
							.setImageResource(R.drawable.class.getField(m_concepts.get(i)
									.getPicture())
									.getInt(R.drawable.class));
				} catch (IllegalAccessException | NoSuchFieldException e) {
					e.printStackTrace();
				}
			} else {
				imageViews.get(i + 4)
						.setImageDrawable(Drawable.createFromPath(m_concepts.get(i)
								.getPicture()));
			}
			
			linearLayoutWork.setOnTouchListener(new View.OnTouchListener() {
				@Override
				public boolean onTouch(View view, MotionEvent motionEvent) {
					if (m_play) {
						if (m_mediaPlayer != null) {
							m_mediaPlayer.release();
						}
						if (m_concepts.get(m_currentConcept)
								.getSound()
								.startsWith("sound_")) {
							try {
								m_mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.class.getField(m_concepts.get(m_currentConcept)
										.getSound())
										.getInt(R.raw.class));
							} catch (IllegalAccessException | NoSuchFieldException e) {
								e.printStackTrace();
							}
						} else {
							m_mediaPlayer = MediaPlayer.create(getApplicationContext(), Uri.fromFile(new File(m_concepts.get(m_currentConcept)
									.getSound())));
						}
						if (m_mediaPlayer != null) {
							m_mediaPlayer.start();
						}
						m_play = false;
					}
					
					if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
						m_play = true;
					}
					
					return true;
				}
			});
		}
		
		// SCROLLING
		m_thread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				while (m_scroll) {
					if (!m_pause) {
						// PLAY SOUND
						if (m_concepts.get(m_currentConcept)
								.getSound()
								.startsWith("sound_")) {
							try {
								m_mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.class.getField(m_concepts.get(m_currentConcept)
										.getSound())
										.getInt(R.raw.class));
								m_mediaPlayer.setVolume(0.4f, 0.4f);
							} catch (IllegalAccessException | NoSuchFieldException e) {
								e.printStackTrace();
							}
						} else {
							m_mediaPlayer = MediaPlayer.create(getApplicationContext(), Uri.fromFile(new File(m_concepts.get(m_currentConcept)
									.getSound())));
						}
						if (m_mediaPlayer != null) {
							m_mediaPlayer.start();
							m_mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
								@Override
								public void onCompletion(MediaPlayer mediaPlayer) {
									m_mediaPlayer.release();
								}
							});
						}
						
						// UPDATE UI -> MAKES FRAME VISIBLE
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								System.out.println(m_currentConcept);
								if (m_profile.getFrameStyle()
										.equals("classic") || m_profile.getFrameStyle()
										.equals("checkerboard")) {
									backgrounds.get(m_currentConcept)
											.setVisibility(View.VISIBLE);
									backgrounds.get(m_currentConcept + 4)
											.setVisibility(View.VISIBLE);
								} else {
									foregrounds.get(m_currentConcept)
											.setVisibility(View.VISIBLE);
									foregrounds.get(m_currentConcept + 4)
											.setVisibility(View.VISIBLE);
								}
							}
						});
						
						// SLEEP
						try {
							Thread.sleep(m_profile.getScrollingTime() * 1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						
						// RE-UPDATE UI -> MAKES FRAME INVISIBLE
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								System.out.println("blablabla");
								backgrounds.get(m_currentConcept)
										.setVisibility(View.INVISIBLE);
								backgrounds.get(m_currentConcept + 4)
										.setVisibility(View.INVISIBLE);
								foregrounds.get(m_currentConcept)
										.setVisibility(View.INVISIBLE);
								foregrounds.get(m_currentConcept + 4)
										.setVisibility(View.INVISIBLE);
								
								// INCREMENT CONCEPT TO PLAY
								if (m_currentConcept == m_concepts.size() - 1) {
									m_currentConcept = 0;
								} else {
									m_currentConcept++;
								}
							}
						});
						
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					} else {
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
				
				Thread.currentThread()
						.interrupt();
			}
		});
		
		m_thread.start();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		m_pause = false;
		System.out.println(m_thread.isAlive());
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		m_mediaPlayer.release();
		m_pause = true;
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		m_scroll = false;
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
	}
}
