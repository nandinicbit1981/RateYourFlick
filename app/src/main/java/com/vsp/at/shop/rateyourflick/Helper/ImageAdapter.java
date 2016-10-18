package com.vsp.at.shop.rateyourflick.Helper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.vsp.at.shop.rateyourflick.R;

import java.util.List;

public class ImageAdapter extends ArrayAdapter<MovieDetails> {
    public static Context mContext;

    public static List<MovieDetails> images;

    public ImageAdapter(Context context, List<MovieDetails> movieDetailsList) {
        super(context, 0, movieDetailsList);
        mContext = context;
    }

    @Override
    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {

        MovieDetails item = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.grid_item_view_mv, parent, false);
        }
       // new MovieServiceTask().execute("");
        ImageView iconView = (ImageView) convertView.findViewById(R.id.flavor_image);
        Picasso.with(mContext).load("http://image.tmdb.org/t/p/w185" + item.poster_path).into(iconView);
        return iconView;
    }

}