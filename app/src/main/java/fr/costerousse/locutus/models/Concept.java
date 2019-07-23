package fr.costerousse.locutus.models;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.io.Serializable;


@Entity(tableName = "concept", indices = {@Index(value = {"name", "picto", "sound", "picture"}, unique = true)})
public class Concept implements Serializable {
	//////////////////////////////////////////////////////////
	// Fields
	/////////////
	@PrimaryKey(autoGenerate = true)
	private int id;
	
	@ColumnInfo(name = "name")
	private String name;
	
	@ColumnInfo(name = "picto")
	private String picto;
	
	@ColumnInfo(name = "sound")
	private String sound;
	
	@ColumnInfo(name = "picture")
	private String picture;
	
	// Methods
	
	//////////////////////////////////////////////////////////
	// Constructor
	/////////////
	public Concept(String name, String picto, String sound) {
		this.name = name;
		this.picto = picto;
		this.sound = sound;
		this.picture = "none";
	}
	
	//////////////////////////////////////////////////////////
	// Getters & setters
	/////////////
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getPicto() {
		return picto;
	}
	
	public void setPicto(String picto) {
		this.picto = picto;
	}
	
	public String getSound() {
		return sound;
	}
	
	public void setSound(String sound) {
		this.sound = sound;
	}
	
	public String getPicture() {
		return picture;
	}
	
	public void setPicture(String picture) {
		this.picture = picture;
	}
}
