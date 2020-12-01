package edu.sjsu.android.stocksearch;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Favorites extends AppCompatActivity {

    // Context variables
    private Context context;
    private Activity mainActivity;
    private SharedPreferencesJSON favJSON;

    // RecyclerView variables
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private List<String[]> stocks = new ArrayList<>();

    // Layout variables
    private ProgressBar favProgBar;
    private ImageButton refreshBtn;
    private Switch autoRefreshSwitch;

    public Favorites(Context context, Activity activity) {
        this.context = context;
        this.mainActivity = activity;
        this.favJSON = new SharedPreferencesJSON(context);
    }

    public void initialize() {
        // Create the recycler view.
        recyclerView = (RecyclerView) mainActivity.findViewById(R.id.favRecyclerView);

        // LinearLayout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // Initializing layout resources
        refreshBtn = (ImageButton) mainActivity.findViewById(R.id.refreshBtn);
        autoRefreshSwitch = (Switch) mainActivity.findViewById(R.id.autoRefreshSwitch);

        // Implementation of swiping elements out of the list.
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                String[] stock = stocks.remove(viewHolder.getAdapterPosition());
                String stockName = stock[0];
                mAdapter.notifyItemRemoved(viewHolder.getAdapterPosition());
                // Remove from shared preferences.
                favJSON.removeFromSharedPreferencesJSON(stockName);
                Toast.makeText(context, stockName + " has been removed from favorites.", Toast.LENGTH_SHORT).show();
            }
        };
        ItemTouchHelper itemTouch = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouch.attachToRecyclerView(recyclerView);

        new JsonTask().execute();

        // Initialize adapter and adapt favorite stocks to our recyclerView.
        mAdapter = new FavoritesRecyclerViewAdapter(stocks);
        recyclerView.setAdapter(mAdapter);

        // Refresh button: refresh the recycler view of favorites.
        refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAdapter.notifyDataSetChanged();
                Toast.makeText(context, "Favorites refreshed!", Toast.LENGTH_SHORT).show();
            }
        });

        // Auto refresh button: refresh the recycler view of favorites every 10 seconds if on.
        autoRefreshSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                final Handler handler = new Handler();
                final int delay = 10000;    // 10 seconds


                handler.postDelayed(new Runnable() {
                    public void run() {
                        if (autoRefreshSwitch.isChecked()) {
                            mAdapter.notifyDataSetChanged();
                            Toast.makeText(context, "Favorites refreshed!", Toast.LENGTH_SHORT).show();
                            handler.postDelayed(this, delay);
                        } else {
                            handler.removeCallbacksAndMessages(null);
                        }
                    }
                }, delay);
            }
        });
    }


    /**
     * Returns the percent change.
     *
     * @param prev The previous value.
     * @param curr The current value since the previous value.
     * @return The change in value in percent.
     */
    public double getDelta(double prev, double curr) {
        return (curr - prev) / prev * 100;
    }

// Inner class for querying JSON in background thread.
private class JsonTask extends AsyncTask<Void, Long, Void> {

    protected void onPreExecute() {
        super.onPreExecute();

        // Show a loading progress bar.
        favProgBar = mainActivity.findViewById(R.id.favProgBar);
        favProgBar.setVisibility(View.VISIBLE);
    }

    protected Void doInBackground(Void... params) {
        String token = context.getResources().getString(R.string.token);
        JSONObject jsonObjPref = favJSON.getSharedPreferencesJSON();

        // Iterate through the JSONObject and query every stock name within in Tiingo.
        Iterator<String> keys = jsonObjPref.keys();
        int i = 0;
        while (keys.hasNext()) {
            String stockName = keys.next();

            // Query Tiingo.
            JSONObject jsonObj = null;
            try {
                URL url = new URL("https://api.tiingo.com/iex/?tickers=" + stockName + "&token=" + token);
                jsonObj = JSONParser.getJsonObjectFromUrl(url);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            // Retrieve the json info we need for favorites.
            try {
                String prevClosePrice = jsonObj.getString("prevClose");
                String latestPrice = jsonObj.getString("last");
                double deltaPrice = getDelta(Double.parseDouble(prevClosePrice), Double.parseDouble(latestPrice));
                String stockChange = deltaPrice + "%";
                if (deltaPrice > 0) {
                    stockChange = "+" + stockChange;
                }

                // Add it to the list of favorite stocks as an array.
                String[] stock = {stockName, latestPrice, stockChange};
                stocks.add(stock);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            i++;
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Long... value) {
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        // Remove the loading progress bar with no background threads left to do.
        favProgBar.setVisibility(View.GONE);
    }
}
}
