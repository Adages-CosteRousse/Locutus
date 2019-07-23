package fr.costerousse.locutus.db;


import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;


public class DatabaseClient {
	//////////////////////////////////////////////////////////
	// Fields
	/////////////
	// Instance linking to the database
	private static DatabaseClient instance;
	// Object representing the application database
	private AppDatabase appDatabase;
	
	//////////////////////////////////////////////////////////
	// CONSTRUCTOR
	/////////////
	private DatabaseClient(final Context context) {
		appDatabase = Room.databaseBuilder(context, AppDatabase.class, "MyDb")
				.addCallback(roomDatabaseCallback)
				.build();
	}
	
	//////////////////////////////////////////////////////////
	// CONSTRUCTOR
	/////////////
	public static synchronized DatabaseClient getInstance(Context context) {
		if (instance == null) {
			instance = new DatabaseClient(context);
		}
		return instance;
	}
	
	//////////////////////////////////////////////////////////
	// Getter : AppDatabase
	/////////////
	public AppDatabase getAppDatabase() {
		return appDatabase;
	}
	
	//////////////////////////////////////////////////////////
	// Object allowing to fill the database when it is created
	/////////////
	RoomDatabase.Callback roomDatabaseCallback = new RoomDatabase.Callback() {
		@Override
		public void onCreate(@NonNull SupportSQLiteDatabase db) {
			super.onCreate(db);
			
			// PROFILES
			db.execSQL("INSERT INTO profile (first_name, last_name, year_of_birth, applications_launcher, default_user_mode, tree, max_frames_per_display, scrolling_time, sound_feedback, frame_style, frame_size, highlight_intensity, frame_color) VALUES(\"Prénom1\", \"NOM1\", 2011, 0, \"scrolling\", \"none\", 4, 4, 0, \"classic\", 5, 20, \"black\")");
			db.execSQL("INSERT INTO profile (first_name, last_name, year_of_birth, applications_launcher, default_user_mode, tree, max_frames_per_display, scrolling_time, sound_feedback, frame_style, frame_size, highlight_intensity, frame_color) VALUES(\"Prénom2\", \"NOM2\", 2013, 0, \"tactile\", \"none\", 4, 4, 0, \"red_light\", 5, 20, \"pink\")");
			
			// CONCEPTS
			db.execSQL("INSERT INTO concept (name, picto, sound, picture) VALUES(\"bonjour\", \"dra_hello\", \"sound_bonjour\", \"none\")");
			db.execSQL("INSERT INTO concept (name, picto, sound, picture) VALUES(\"content\", \"dra_happy\", \"sound_content\", \"none\")");
			db.execSQL("INSERT INTO concept (name, picto, sound, picture) VALUES(\"triste\", \"dra_angry\", \"sound_triste\", \"none\")");
		}
	};
}
