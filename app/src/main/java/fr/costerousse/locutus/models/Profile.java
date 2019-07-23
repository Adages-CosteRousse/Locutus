package fr.costerousse.locutus.models;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.io.Serializable;


@Entity(tableName = "profile", indices = {@Index(value = {"last_name", "first_name", "year_of_birth", "applications_launcher", "default_user_mode", "tree", "max_frames_per_display", "scrolling_time", "sound_feedback", "frame_style", "frame_size", "highlight_intensity", "frame_color"}, unique = true)})
public class Profile implements Serializable {
	//////////
	// Fields
	////
	// Register
	@PrimaryKey(autoGenerate = true)
	private int id;
	
	@ColumnInfo(name = "first_name")
	private String firstName;
	
	@ColumnInfo(name = "last_name")
	private String lastName;
	
	@ColumnInfo(name = "year_of_birth")
	private Integer yearOfBirth;
	
	// Global settings
	@ColumnInfo(name = "applications_launcher")
	private Integer applicationsLauncher;
	
	@ColumnInfo(name = "default_user_mode")
	private String defaultUserMode;
	
	@ColumnInfo(name = "tree")
	private String tree;
	
	// Scrolling settings
	@ColumnInfo(name = "max_frames_per_display")
	private Integer maxFramesPerDisplay;
	
	@ColumnInfo(name = "scrolling_time")
	private Integer scrollingTime;
	
	@ColumnInfo(name = "sound_feedback")
	private Integer soundFeedback;
	
	// Frame settings
	@ColumnInfo(name = "frame_style")
	private String frameStyle;
	
	@ColumnInfo(name = "frame_size")
	private Integer frameSize;
	
	@ColumnInfo(name = "highlight_intensity")
	private Integer highlightIntensity;
	
	@ColumnInfo(name = "frame_color")
	private String frameColor;
	
	//////////
	// Constructor(s)
	////
	public Profile(String firstName, String lastName, Integer yearOfBirth) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.yearOfBirth = yearOfBirth;
		this.applicationsLauncher = 0;
		this.defaultUserMode = "scrolling";
		this.tree = "none";
		this.maxFramesPerDisplay = 4;
		this.scrollingTime = 4;
		this.soundFeedback = 0;
		this.frameStyle = "classic";
		this.frameSize = 5;
		this.highlightIntensity = 20;
		this.frameColor = "black";
	}
	
	//////////
	// Getters & setters
	////
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getFirstName() {
		return firstName;
	}
	
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	
	public String getLastName() {
		return lastName;
	}
	
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	public Integer getYearOfBirth() {
		return yearOfBirth;
	}
	
	public void setYearOfBirth(Integer yearOfBirth) {
		this.yearOfBirth = yearOfBirth;
	}
	
	public Integer getApplicationsLauncher() {
		return applicationsLauncher;
	}
	
	public void setApplicationsLauncher(Integer applicationsLauncher) {
		this.applicationsLauncher = applicationsLauncher;
	}
	
	public String getDefaultUserMode() {
		return defaultUserMode;
	}
	
	public void setDefaultUserMode(String defaultUserMode) {
		this.defaultUserMode = defaultUserMode;
	}
	
	public String getTree() {
		return tree;
	}
	
	public void setTree(String tree) {
		this.tree = tree;
	}
	
	public Integer getMaxFramesPerDisplay() {
		return maxFramesPerDisplay;
	}
	
	public void setMaxFramesPerDisplay(Integer maxFramesPerDisplay) {
		this.maxFramesPerDisplay = maxFramesPerDisplay;
	}
	
	public Integer getScrollingTime() {
		return scrollingTime;
	}
	
	public void setScrollingTime(Integer scrollingTime) {
		this.scrollingTime = scrollingTime;
	}
	
	public Integer getSoundFeedback() {
		return soundFeedback;
	}
	
	public void setSoundFeedback(Integer soundFeedback) {
		this.soundFeedback = soundFeedback;
	}
	
	public String getFrameStyle() {
		return frameStyle;
	}
	
	public void setFrameStyle(String frameStyle) {
		this.frameStyle = frameStyle;
	}
	
	public Integer getFrameSize() {
		return frameSize;
	}
	
	public void setFrameSize(Integer frameSize) {
		this.frameSize = frameSize;
	}
	
	public Integer getHighlightIntensity() {
		return highlightIntensity;
	}
	
	public void setHighlightIntensity(Integer highlightIntensity) {
		this.highlightIntensity = highlightIntensity;
	}
	
	public String getFrameColor() {
		return frameColor;
	}
	
	public void setFrameColor(String frameColor) {
		this.frameColor = frameColor;
	}
}
