package edu.sjsu.android.stocksearch;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class HistoricalDataFragment extends Fragment {

    // Data variables
    private String stockName;
    private String histDate;
    private LocalDate startDate;
    private String resampleFreq;

    // Layout variables
    private ProgressBar progBar;

    // RecyclerView variables
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private List<Pair> pairs = new ArrayList<>();

    // Context variables
    private Context context;

    public static HistoricalDataFragment newInstance(String stockName, String histDate) {
        HistoricalDataFragment fragment = new HistoricalDataFragment();
        Bundle bundle = new Bundle();
        bundle.putString("stockName", stockName);
        bundle.putString("histDate", histDate);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity().getApplicationContext();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Get the view.
        View root = inflater.inflate(R.layout.fragment_recycler_view_historical, container, false);

        // Create the recycler view.
        recyclerView = (RecyclerView) root.findViewById(R.id.histRecyclerView);
        recyclerView.setHasFixedSize(true);

        // LinearLayout manager
        layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);

        // Grab Tiingo info place it into UI in another thread.
        queryJSON();

        // Return.
        return root;
    }

    /**
     * Queries Tiingo's online JSON stock database for stock info.
     */
    public void queryJSON() {
        String token = context.getResources().getString(R.string.token);
        Bundle bundle = this.getArguments();
        stockName = bundle.getString("stockName");
        histDate = bundle.getString("histDate");
        if (histDate == null || histDate.isEmpty()) {
            startDate = LocalDate.parse("2020-11-30");
        }
        else {
            startDate = LocalDate.parse(histDate);
        }
        resampleFreq = "daily";
        String url = "https://api.tiingo.com/tiingo/daily/" + stockName + "/prices?startDate=" + startDate + "&resampleFreq=" + resampleFreq + "&token=" + token;
        new JsonTask().execute(url);
    }

    // Inner class for querying JSON in background thread.
    private class JsonTask extends AsyncTask<String, Long, Pair[]> {

        protected void onPreExecute() {
            super.onPreExecute();
            // Show a loading progress bar on UI thread while we wait for background task.
            Activity activity = getActivity();
            progBar = getActivity().findViewById(R.id.progressBar);
            progBar.setVisibility(View.VISIBLE);
        }

        protected Pair[] doInBackground(String... params) {
            String jsonStr = null;
            try {
                URL url = new URL(params[0]);
                jsonStr = JSONParser.getJsonFromUrl(url);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            // Parse json info and return it within an array.
            return JSONParser.getKeyValuePairArray(jsonStr);
        }

        @Override
        protected void onProgressUpdate(Long... value) {
        }

        @Override
        protected void onPostExecute(Pair[] result) {
            super.onPostExecute(result);

            // Remove the loading progress bar from UI thread on background task finish.
            progBar.setVisibility(View.GONE);

            // Add rows to recycler view based on number of JSON data rows.
            for (int i = 0; i < result.length; i++) {
                pairs.add(result[i]);
            }

            // Initialize adapter and adapt pairs data to our recyclerView.
            mAdapter = new PairRecyclerViewAdapter(pairs);
            recyclerView.setAdapter(mAdapter);
        }
    }
}
