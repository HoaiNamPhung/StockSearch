package edu.sjsu.android.stocksearch;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

public class SharedPreferencesJSON {

    Context context;
    public SharedPreferencesJSON(Context context) {
        this.context = context;
    }

    /**
     * Add a stock to the JSON string stored in shared preferences.
     * @param stockName The name of the stock to add.
     */
    public void addToSharedPreferencesJSON(String stockName) {
        SharedPreferences settings = context.getSharedPreferences("STOCK_SEARCH_FAVORITES", context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        // Retrieve current JSON string from preferences. If it doesn't exist, retrieve an empty JSON object.
        String favJsonStr = settings.getString("favJSON", "{}");
        if (favJsonStr.isEmpty()) {
            favJsonStr = "{}";
        }
        else if (!favJsonStr.contains(stockName)) {
            if (favJsonStr.equals("{}")) {
                favJsonStr = "{\"" + stockName + "\":\"" + stockName + "\"}";
            }
            else {
                favJsonStr = favJsonStr.substring(0, favJsonStr.length() - 1) + ",\"" + stockName + "\":\"" + stockName + "\"}";
            }
        }
        editor.putString("favJSON", favJsonStr);
        editor.commit();
    }

    /**
     * Remove a stock from the JSON string stored in shared preferences.
     * @param stockName The name of the stock to remove.
     */
    public void removeFromSharedPreferencesJSON(String stockName) {
        SharedPreferences settings = context.getSharedPreferences("STOCK_SEARCH_FAVORITES", context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        // Retrieve current JSON string from preferences. If it doesn't exist, retrieve an empty JSON object.
        String favJsonStr = settings.getString("favJSON", "{}");
        JSONObject jsonObj = null;
        try {
            jsonObj = new JSONObject(favJsonStr);
        }
        // Shouldn't fail unless the JSON string was stored incorrectly or is empty.
        catch (JSONException e) {
            e.printStackTrace();
        }
        jsonObj.remove(stockName);
        favJsonStr = jsonObj.toString();
        editor.putString("favJSON", favJsonStr);
        editor.commit();
    }

    /**
     * Retrieves the JSON string stored in shared preferences.
     * @return Returns the JSON string as a JSON object if successful.
     */
    public JSONObject getSharedPreferencesJSON() {
        SharedPreferences settings = context.getSharedPreferences("STOCK_SEARCH_FAVORITES", context.MODE_PRIVATE);
        String favJsonStr = settings.getString("favJSON", "{}");
        if (favJsonStr.isEmpty()) {
            favJsonStr = "{}";
        }
        try {
            JSONObject jsonObj = new JSONObject(favJsonStr);
            return jsonObj;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void dropSharedPreferences() {
        SharedPreferences settings = context.getSharedPreferences("STOCK_SEARCH_FAVORITES", context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.clear();
        editor.commit();

    }
}
