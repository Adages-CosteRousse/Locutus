package fr.costerousse.locutus.db;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import fr.costerousse.locutus.models.Profile;


@Dao
public interface ProfileDao {
	@Query("SELECT * FROM profile")
	List<Profile> getAll();
	
	@Insert
	void insert(Profile profile);
	
	@Delete
	void delete(Profile profile);
	
	@Update
	void update(Profile profile);
}
