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


public class PickConceptsAdapter extends ArrayAdapter<Concept> {
	//////////////////////////////////////////////////////////
	// Constructor
	/////////////
	public PickConceptsAdapter(Context context, List<Concept> concepts) {
		super(context, R.layout.template_pick_concepts, concepts);
	}
	
	//////////////////////////////////////////////////////////
	// getView
	// set the text of each profile in the list of profiles
	/////////////
	@NonNull
	@Override
	public View getView(int position, View convertView, @Nullable ViewGroup parent) {
		// Retrieves the current concept
		final Concept concept = getItem(position);
		
		// Inflater
		LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		ViewHolder holder;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.template_pick_concepts, parent, false);
			ImageView imageView = convertView.findViewById(R.id.image_view_pick_concepts);
			TextView textView = convertView.findViewById(R.id.text_view_pick_concepts);
			convertView.setTag(new ViewHolder(imageView, textView));
		}
		holder = (ViewHolder) convertView.getTag();
		
		// Processing on view
		if (concept.getPicto()
				.startsWith("dra_")) {
			try {
				holder.m_imageView.setImageResource(R.drawable.class.getField(concept.getPicto())
						.getInt(R.drawable.class));
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
			}
		} else {
			holder.m_imageView.setImageDrawable(Drawable.createFromPath(concept.getPicto()));
		}
		holder.m_textView.setText(concept.getName());
		
		return convertView;
	}
	
	// Allows to save each
	private static class ViewHolder {
		final ImageView m_imageView;
		final TextView m_textView;
		
		private ViewHolder(ImageView imageView, TextView textView) {
			this.m_imageView = imageView;
			this.m_textView = textView;
		}
	}
}
