package fr.costerousse.locutus.controllers;


import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
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


public class WorkPictosActivity extends AppCompatActivity {
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
	private int m_padding;
	// Concepts
	private ArrayList<Concept> m_concepts = new ArrayList<>();
	//
	private MediaPlayer m_mediaPlayer = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_work_pictos);
		
		// Database
		mDb = DatabaseClient.getInstance(getApplicationContext());
		List<Concept> concepts = null;
		try {
			concepts = new GetConcepts().execute()
					.get();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		// Retrieves profile & preferences
		try {
			m_profile = new GetProfiles().execute()
					.get()
					.get(getIntent().getIntExtra("profile_position", 0));
		} catch (ExecutionException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		m_padding = m_profile.getFrameSize();
		
		
		// Retrieves the binary list of checked/unchecked IDs
		m_integers = getIntent().getIntegerArrayListExtra("concepts_list");
		System.out.println("BinList : " + m_integers);
		
		// Retrieves concepts from the IDs
		System.out.println("Concepts choisis : ");
		for (int i = 0; i < m_integers.size(); i++) {
			if (m_integers.get(i) == 1) {
				if (concepts != null) {
					m_concepts.add(concepts.get(i));
				} else {
					System.out.println("NULLPOINTER");
				}
			}
		}
		
		// Retrieves view's items
		LinearLayout linearLayoutBottom = findViewById(R.id.linear_layout_work_pictos_bottom);
		final ArrayList<ImageView> imageViews = new ArrayList<>();
		final ArrayList<LinearLayout> containers = new ArrayList<>();
		
		LinearLayout linearLayoutContainerTop1 = findViewById(R.id.linear_layout_container_top1);
		containers.add(linearLayoutContainerTop1);
		ImageView imageViewTop1 = findViewById(R.id.image_view_work_picto_top_1);
		imageViews.add(imageViewTop1);
		
		LinearLayout linearLayoutContainerTop2 = findViewById(R.id.linear_layout_container_top2);
		containers.add(linearLayoutContainerTop2);
		ImageView imageViewTop2 = findViewById(R.id.image_view_work_picto_top_2);
		imageViews.add(imageViewTop2);
		
		LinearLayout linearLayoutContainerTop3 = findViewById(R.id.linear_layout_container_top3);
		containers.add(linearLayoutContainerTop3);
		ImageView imageViewTop3 = findViewById(R.id.image_view_work_picto_top_3);
		imageViews.add(imageViewTop3);
		
		LinearLayout linearLayoutContainerTop4 = findViewById(R.id.linear_layout_container_top4);
		containers.add(linearLayoutContainerTop4);
		ImageView imageViewTop4 = findViewById(R.id.image_view_work_picto_top_4);
		imageViews.add(imageViewTop4);
		
		LinearLayout linearLayoutContainerBottom1 = findViewById(R.id.linear_layout_container_bottom1);
		containers.add(linearLayoutContainerBottom1);
		ImageView imageViewBottom1 = findViewById(R.id.image_view_work_picto_bottom_1);
		imageViews.add(imageViewBottom1);
		
		LinearLayout linearLayoutContainerBottom2 = findViewById(R.id.linear_layout_container_bottom2);
		containers.add(linearLayoutContainerBottom2);
		ImageView imageViewBottom2 = findViewById(R.id.image_view_work_picto_bottom_2);
		imageViews.add(imageViewBottom2);
		
		LinearLayout linearLayoutContainerBottom3 = findViewById(R.id.linear_layout_container_bottom3);
		containers.add(linearLayoutContainerBottom3);
		ImageView imageViewBottom3 = findViewById(R.id.image_view_work_picto_bottom_3);
		imageViews.add(imageViewBottom3);
		
		LinearLayout linearLayoutContainerBottom4 = findViewById(R.id.linear_layout_container_bottom4);
		containers.add(linearLayoutContainerBottom4);
		ImageView imageViewBottom4 = findViewById(R.id.image_view_work_picto_bottom_4);
		imageViews.add(imageViewBottom4);
		
		// Processing on view
		if (concepts != null && m_concepts.size() <= 4) {
			linearLayoutBottom.setLayoutParams(new LinearLayout.LayoutParams(0, 0, 0f));
		}
		for (int i = 0; i < m_concepts.size(); i++) {
			final int position = i;
			
			containers.get(i)
					.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1f));
			
			imageViews.get(i)
					.post(new Runnable() {
						@Override
						public void run() {
							int containerHeight = containers.get(position)
									.getHeight();
							int containerWidth = containers.get(position)
									.getWidth();
							System.out.println("container's height : " + containerHeight);
							System.out.println("container's width : " + containerWidth);
							int imageHeight = imageViews.get(position)
									.getHeight();
							int imageWidth = imageViews.get(position)
									.getWidth();
							System.out.println("image's height : " + imageHeight);
							System.out.println("image's width : " + imageWidth);
							int difHeight = containerHeight - imageHeight;
							System.out.println("difHeight : " + difHeight);
							int difWidth = containerWidth - imageWidth;
							System.out.println("difWidth : " + difWidth);
							
							if (difHeight == 0 && difWidth == 0) {
								imageViews.get(position)
										.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
							}
							
							if (difHeight > difWidth) {
								containers.get(position)
										.setPadding(100 - m_padding, ((difHeight - difWidth) / 2) + 100 - m_padding, 100 - m_padding, ((difHeight - difWidth) / 2) + 100 - m_padding);
							} else if (difWidth > difHeight) {
								containers.get(position)
										.setPadding(((difWidth - difHeight) / 2) + 100 - m_padding, 100 - m_padding, ((difWidth - difHeight) / 2) + 100 - m_padding, 100 - m_padding);
							}
							
							imageViews.get(position)
									.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
							
							imageViews.get(position)
									.setPadding(m_padding, m_padding, m_padding, m_padding);
						}
					});
			
			if (m_concepts.get(i)
					.getPicto()
					.startsWith("dra_")) {
				//ImageReader.newInstance();
				try {
					imageViews.get(i)
							.setImageResource(R.drawable.class.getField(m_concepts.get(i)
									.getPicto())
									.getInt(R.drawable.class));
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (NoSuchFieldException e) {
					e.printStackTrace();
				}
			} else {
				imageViews.get(i)
						.setImageDrawable(Drawable.createFromPath(m_concepts.get(i)
								.getPicto()));
			}
			
			imageViews.get(i)
					.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View view) {
							if (m_mediaPlayer != null) {
								m_mediaPlayer.release();
							}
							if (m_concepts.get(position)
									.getSound()
									.startsWith("sound_")) {
								try {
									m_mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.class.getField(m_concepts.get(position)
											.getSound())
											.getInt(R.raw.class));
								} catch (IllegalAccessException e) {
									e.printStackTrace();
								} catch (NoSuchFieldException e) {
									e.printStackTrace();
								}
							} else {
								m_mediaPlayer = MediaPlayer.create(getApplicationContext(), Uri.fromFile(new File(m_concepts.get(position)
										.getSound())));
							}
							if (m_mediaPlayer != null) {
								m_mediaPlayer.start();
							}
						}
					});
		}
		
		
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
