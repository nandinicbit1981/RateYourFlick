package com.vsp.at.shop.rateyourflick;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.vsp.at.shop.rateyourflick.Helper.MovieDetails;

import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;

public class DetailActivity extends ActionBarActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new DetailFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.detail, menu);    // Get the menu item.

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class DetailFragment extends Fragment {

        private static final String LOG_TAG = DetailFragment.class.getSimpleName();
        private static final String FORECAST_SHARE_HASHTAG = " #SunshineApp";
        private static MovieDetails movieDetails = new MovieDetails();
        public DetailFragment() {
            setHasOptionsMenu(true);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {


            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
            try {
                movieDetails = new ObjectMapper().readValue(getActivity().getIntent().getExtras().getString("movieDetails"), MovieDetails.class);
            } catch (IOException e) {
                e.printStackTrace();
            }

            Log.i("asd" , movieDetails.overview);
            final ImageView imageView= (ImageView)rootView.findViewById(R.id.detail_poster);
            Picasso.with(getActivity()).load("http://image.tmdb.org/t/p/w185" + movieDetails.poster_path).into(imageView);

            final TextView dateText = (TextView)rootView.findViewById(R.id.detail_date);
            final TextView detailHeading = (TextView) rootView.findViewById(R.id.detail_heading);
            final TextView movieOverview = (TextView) rootView.findViewById(R.id.movie_overview);
            final TextView detailRating = (TextView) rootView.findViewById(R.id.detail_rating);
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(!movieDetails.release_date.isEmpty()) {
                        dateText.setText(movieDetails.release_date.substring(0, movieDetails.release_date.indexOf("-")));
                    }
                    detailHeading.setText(movieDetails.original_title);
                    movieOverview.setText(movieDetails.overview);
                    detailRating.setText(String.valueOf(movieDetails.vote_average) + "/10");
                }
            });


            return rootView;
        }
    }
}