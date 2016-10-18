package com.vsp.at.shop.rateyourflick;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.vsp.at.shop.rateyourflick.Helper.ImageAdapter;
import com.vsp.at.shop.rateyourflick.Helper.MovieDetails;
import com.vsp.at.shop.rateyourflick.Helper.MovieInfo;

import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    final String LOG = MainActivityFragment.class.getSimpleName();
    public static ImageAdapter movieAdapter;
    public static List<MovieDetails> movieList = new ArrayList();
    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        movieAdapter= new ImageAdapter(getActivity(), movieList);
        GridView viewById = (GridView) rootView.findViewById(R.id.movies_grid);


        viewById.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                // Toast.makeText(getActivity(), weatherAdapter.getItem(i), Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                try {
                    intent.putExtra("movieDetails", new ObjectMapper().writeValueAsString(movieList.get(i)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                startActivity(intent);

            }
        });

        viewById.setAdapter(movieAdapter);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        new MovieServiceTask().execute("nun");
    }



    public class MovieServiceTask extends AsyncTask<String, Void, List<MovieDetails>> {
        final String LOG = MovieServiceTask.class.getSimpleName();

        @Override
        protected List<MovieDetails> doInBackground(String... params) {
            // These two need to be declared outside the try/cat≈ch
            // so that they can be closed in the finally block.

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String movieStr = null;

            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are available at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast

                String sort = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("Sort By", "Popularity");

                Uri.Builder builder = new Uri.Builder();
                builder.scheme("https")
                        .authority("api.themoviedb.org")
                        .appendPath("3")
                        .appendPath("discover")
                        .appendPath("movie")
                        .appendQueryParameter("sort_by", sort + ".desc")
                        .appendQueryParameter("api_key", "YOUR_KEY")
                        .fragment("section-name");
                String myUrl = builder.build().toString();
                URL url = new URL(myUrl);

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    movieStr = null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    movieStr = null;
                    return null;
                }
                movieStr = buffer.toString();
                Log.i(LOG, movieStr);


                ObjectMapper mapper = new ObjectMapper();
                MovieInfo movieInfo = mapper.readValue(movieStr, MovieInfo.class);
                return movieInfo.results;

            } catch (IOException e) {
                Log.e(LOG, e.getMessage());
                // If the code didn't successfully get the weather data, there's no point in attempting
                // to parse it.
                movieStr = null;
                return null;
            } catch (Exception e) {
                Log.e(LOG, e.getMessage());
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG, "Error closing stream", e);
                    }
                }
            }
            return null;
        }

        private String[] getMovieList(String movieStr) {
            String movieString = movieStr;
            JSONObject movieJson = null;
            try {
                movieJson = new JSONObject(movieStr);
                JSONArray movieArray = movieJson.getJSONArray("results");
                String[] posters = new String[movieArray.length()];
                for(int i=0;i<movieArray.length();i++) {
                    JSONObject jsonObject = movieArray.getJSONObject(i);
                    posters[i] = "http://image.tmdb.org/t/p/w185" + jsonObject.get("poster_path").toString();
                }
                return posters;

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;

        }

        @Override
        public void onPostExecute(final List<MovieDetails> array){
            movieList.clear();
            movieList.addAll(array);
            movieAdapter.notifyDataSetChanged();

        }

    }


}
