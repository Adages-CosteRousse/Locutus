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
import fr.costerousse.locutus.models.Tree;

public class TreesAdapter extends ArrayAdapter<Tree> {
    //////////////////////////////////////////////////////////
    // Constructor
    /////////////
    public TreesAdapter(Context context, List<Tree> trees) {
        super(context, R.layout.template_tree, trees);
    }

    //////////////////////////////////////////////////////////
    // getView
    // set the text and image of each tree in the list of trees
    /////////////
    @NonNull
    @Override
    public View getView(int position, View convertView, @Nullable ViewGroup parent) {
        // Recovery of the profile corresponding to the item
        final Tree tree = getItem(position);

        // Inflater
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView = inflater.inflate(R.layout.template_tree, parent, false);

        // Retrieving graphic objects from the template
        TextView textViewTreeName = rowView.findViewById(R.id.edit_text_tree_name);

        // Processing
        if (tree != null) {
            textViewTreeName.setText(tree.getName());
        }

        return rowView;
    }
}
