package fr.costerousse.locutus.db;


import androidx.room.Database;
import androidx.room.RoomDatabase;

import fr.costerousse.locutus.models.Concept;
import fr.costerousse.locutus.models.Profile;
import fr.costerousse.locutus.models.Tree;


@Database(version = 1, entities = {Profile.class, Concept.class, Tree.class}, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
	public abstract ProfileDao profileDao();
	
	public abstract ConceptDao conceptDao();
	
	public abstract TreeDao treeDao();
}
