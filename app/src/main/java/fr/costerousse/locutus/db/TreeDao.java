package fr.costerousse.locutus.db;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import fr.costerousse.locutus.models.Tree;


@Dao
public interface TreeDao {
    @Query("SELECT * FROM tree")
    List<Tree> getAll();

    @Insert
    void insert(Tree tree);

    @Delete
    void delete(Tree tree);

    @Update
    void update(Tree tree);
}
