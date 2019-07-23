package fr.costerousse.locutus.controllers;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.IOException;

import fr.costerousse.locutus.R;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;


public class AudioCaptureActivity extends AppCompatActivity {
	private Button m_buttonStartRecording;
	private Button m_buttonStopRecording;
	private Button m_buttonPlay;
	private Button m_buttonSave;
	private MediaRecorder m_mediaRecorder;
	private MediaPlayer m_mediaPlayer;
	private String m_stringPath = null;
	private static final int RequestPersissionCode = 1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_audio_capture);
		
		m_buttonStartRecording = findViewById(R.id.button_start_recording);
		m_buttonStopRecording = findViewById(R.id.button_stop_recording);
		m_buttonPlay = findViewById(R.id.button_play);
		m_buttonSave = findViewById(R.id.button_save);
		
		m_buttonStopRecording.setEnabled(false);
		m_buttonPlay.setEnabled(false);
		m_buttonSave.setEnabled(false);
		
		m_buttonStartRecording.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				
				if (checkPermission()) {
					
					if (m_stringPath != null) {
						File file = new File(m_stringPath);
						file.delete();
					}
					
					m_stringPath = getExternalCacheDir().getAbsolutePath() + "/tempAudioRecord.3gp";
					
					MediaRecorderReady();
					
					try {
						m_mediaRecorder.prepare();
					} catch (IOException e) {
						e.printStackTrace();
					}
					m_mediaRecorder.start();
					
					m_buttonStartRecording.setEnabled(false);
					m_buttonStopRecording.setEnabled(true);
					m_buttonPlay.setEnabled(false);
					m_buttonSave.setEnabled(false);
				} else {
					requestPermission();
				}
			}
		});
		
		m_buttonStopRecording.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				m_mediaRecorder.stop();
				m_mediaRecorder.release();
				m_mediaRecorder = null;
				m_buttonStopRecording.setEnabled(false);
				m_buttonStartRecording.setEnabled(true);
				m_buttonPlay.setEnabled(true);
				m_buttonSave.setEnabled(true);
				Toast.makeText(AudioCaptureActivity.this, R.string.recording_complete, Toast.LENGTH_SHORT)
						.show();
			}
		});
		
		m_buttonPlay.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				m_mediaPlayer = new MediaPlayer();
				try {
					m_mediaPlayer.setDataSource(m_stringPath);
					m_mediaPlayer.prepareAsync();
				} catch (IOException e) {
					e.printStackTrace();
				}
				m_mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
					@Override
					public void onPrepared(MediaPlayer mediaPlayer) {
						mediaPlayer.start();
					}
				});
			}
		});
		
		m_buttonSave.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent();
				intent.putExtra("result", m_stringPath);
				setResult(AudioCaptureActivity.RESULT_OK, intent);
				finish();
			}
		});
	}
	
	private void MediaRecorderReady() {
		m_mediaRecorder = new MediaRecorder();
		m_mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		m_mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		m_mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
		m_mediaRecorder.setOutputFile(m_stringPath);
	}
	
	private void requestPermission() {
		ActivityCompat.requestPermissions(AudioCaptureActivity.this, new String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO}, RequestPersissionCode);
	}
	
	private boolean checkPermission() {
		int result = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
		int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), RECORD_AUDIO);
		
		return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
	}
}
