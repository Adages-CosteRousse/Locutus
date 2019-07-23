package fr.costerousse.locutus.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import fr.costerousse.locutus.R;
import fr.costerousse.locutus.models.Profile;


public class ProfilesAdapter extends ArrayAdapter<Profile> {
	//////////////////////////////////////////////////////////
	// Constructor
	/////////////
	public ProfilesAdapter(Context context, List<Profile> profiles) {
		super(context, R.layout.template_profile, profiles);
	}
	
	//////////////////////////////////////////////////////////
	// getView
	// set the text of each profile in the list of profiles
	/////////////
	@NonNull
	@Override
	public View getView(int position, View view, @Nullable ViewGroup parent) {
		// Recovery of the profile corresponding to the item
		final Profile profile = getItem(position);
		
		// Inflater
		LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final View rowView = inflater.inflate(R.layout.template_profile, parent, false);
		
		// Retrieving graphic objects from the template
		TextView textViewFirstName = rowView.findViewById(R.id.text_select_profile_first_name);
		TextView textViewLastName = rowView.findViewById(R.id.text_select_profile_last_name);
		
		// Processing
		textViewFirstName.setText(profile.getFirstName());
		textViewLastName.setText(profile.getLastName());
		
		return rowView;
	}
}
