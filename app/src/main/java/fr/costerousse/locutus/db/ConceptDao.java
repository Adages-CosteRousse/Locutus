package fr.costerousse.locutus.db;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import fr.costerousse.locutus.models.Concept;
import fr.costerousse.locutus.models.Profile;


@Dao
public interface ConceptDao {
	@Query("SELECT * FROM concept")
	List<Concept> getAll();
	
	@Insert
	void insert(Concept concept);
	
	@Delete
	void delete(Concept concept);
	
	@Update
	void update(Concept concept);
}
