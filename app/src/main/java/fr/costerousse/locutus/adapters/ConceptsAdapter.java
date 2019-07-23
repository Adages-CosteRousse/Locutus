package fr.costerousse.locutus.adapters;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import fr.costerousse.locutus.R;
import fr.costerousse.locutus.models.Concept;


public class ConceptsAdapter extends ArrayAdapter<Concept> {
	//////////////////////////////////////////////////////////
	// Constructor
	/////////////
	public ConceptsAdapter(Context context, List<Concept> concepts) {
		super(context, R.layout.template_concept, concepts);
	}
	
	//////////////////////////////////////////////////////////
	// getView
	// set the text and image of each concept in the list of concepts
	/////////////
	@NonNull
	@Override
	public View getView(int position, View convertView, @Nullable ViewGroup parent) {
		// Recovery of the profile corresponding to the item
		final Concept concept = getItem(position);
		
		// Inflater
		LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final View rowView = inflater.inflate(R.layout.template_concept, parent, false);
		
		// Retrieving graphic objects from the template
		ImageView imageViewConceptPicto = rowView.findViewById(R.id.image_view_picto);
		TextView textViewConceptName = rowView.findViewById(R.id.edit_text_concept_name);
		
		// Processing
		if (concept.getPicto()
				.startsWith("dra_")) {
			try {
				imageViewConceptPicto.setImageResource(R.drawable.class.getField(concept.getPicto())
						.getInt(R.drawable.class));
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
			}
		} else {
			imageViewConceptPicto.setImageDrawable(Drawable.createFromPath(concept.getPicto()));
		}
		
		textViewConceptName.setText(concept.getName());
		
		return rowView;
	}
}
