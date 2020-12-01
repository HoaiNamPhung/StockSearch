package edu.sjsu.android.stocksearch;

import android.util.Log;
import android.util.Pair;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Iterator;

public class JSONParser {

    /**
     * Retrieves every key-value pair in a JSON object as an array of pairs.
     * @param jsonStr The JSON formatted string.
     * @return Returns the array of key-value pairs.
     */
    public static Pair[] getKeyValuePairArray(String jsonStr) {
        JSONObject jsonObj = null;
        JSONArray jsonArr = null;
        Pair[] pairArr = {};
        // Assure given input is a JSON formatted string.
        try {
            jsonObj = new JSONObject(jsonStr);
        }
        catch (JSONException eObj) {
            try {
                jsonArr = new JSONArray(jsonStr);
                jsonObj = jsonArr.getJSONObject(0);
            }
            catch (Exception eArr) {
                eObj.printStackTrace();
                eArr.printStackTrace();
            }
        }
        // Parse the string into an array of key value pairs.
        if (jsonObj != null) {
            try {
                pairArr = new Pair[jsonObj.length()];
                Iterator<String> keys = jsonObj.keys();
                int i = 0;
                while(keys.hasNext()) {
                    String key = keys.next();
                    String value = jsonObj.getString(key);

                    // Check if market is open. If it is, set timestamp value to be "-" instead of null.
                    if (key.equals("mid")) {
                        LocalDateTime currentTimestamp = LocalDateTime.now();
                        String timestampStr = jsonObj.getString("timestamp");
                        LocalDateTime timestamp = LocalDateTime.parse(timestampStr, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
                        long minutes = ChronoUnit.MINUTES.between(timestamp, currentTimestamp);
                        // If more than 1 minute since last timestamp, market is presumably closed.
                        if (minutes < 1 && value == null) {
                            value = "-";
                        }
                    }

                    // Add to array of pairs.
                    pairArr[i] = new Pair(key, value);
                    i++;
                }
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return pairArr;
    }

    /**
     * Gets an array of values corresponding to a given key from a JSON formatted string. Assumes values will be Strings.
     * @param jsonStr The JSON formatted string.
     * @param key The key.
     * @return Returns the array of String values corresponding to the given key. If no values or bad JSON string, return empty array.
     */
    public static String[] getValueArray(String jsonStr, String key) {
        JSONArray jsonArr = null;
        JSONObject jsonObj = null;
        // Test to see if the input is a valid JSON array or object and turn it into one of them.
        try {
            jsonObj = new JSONObject(jsonStr);
        } catch (JSONException eObj) {
            try {
                jsonArr = new JSONArray(jsonStr);
            }
            catch (JSONException eArr) {
                eObj.printStackTrace();
                eArr.printStackTrace();
            }
        }
        // Use the method corresponding to whether the input was a JSON array or object.
        if (jsonArr == null) {
            if (jsonObj == null) {
                return new String[]{};
            }
            else {
                String value = getValue(jsonObj, key);
                if (value != null) {
                    return new String[]{value};
                }
                return new String[]{};
            }
        }
        else {
            return getValues(jsonArr, key);
        }

    }

    /**
     * Gets an array of values corresponding to a given key in a JSON Array. Assumes values will be Strings.
     * @param jsonArr The JSON Array.
     * @param key The key.
     * @return Returns the array of String values corresponding to the given key.
     */
    public static String[] getValues(JSONArray jsonArr, String key) {
        String[] valueArr = {};
        if (jsonArr != null) {
            try {
                valueArr = new String[jsonArr.length()];
                for (int i = 0; i < jsonArr.length(); i++) {
                    JSONObject jsonObj = jsonArr.getJSONObject(i);
                    valueArr[i] = jsonObj.getString(key);
                }
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return valueArr;
    }

    /**
     * Gets the value corresponding to a given key in a JSON Object. Assumes values will be Strings.
     * @param jsonObj The JSON Object.
     * @param key The key.
     * @return Returns the String value corresponding to the given key.
     */
    public static String getValue(JSONObject jsonObj, String key) {
        String value = null;
        if (jsonObj != null) {
            try {
                value = jsonObj.getString(key);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return value;
    }

    // Adapted from https://stackoverflow.com/a/37525989/12881694
    /**
     * Creates a connection to an HTTP website and retrieves JSON info.
     * @param url The HTTP URL that the JSON info is stored on.
     * @return Returns the JSON info as a String.
     */
    public static String getJsonFromUrl(URL url) {
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            InputStream stream = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(stream));
            StringBuffer buffer = new StringBuffer();
            String line = "";
            while ((line = reader.readLine()) != null) {
                buffer.append(line+"\n");
                Log.d("Response: ", "> " + line);
            }
            return buffer.toString();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * Retrieves the JSON object from an HTTP url.
     * @param url The HTTP URL that the JSON info is stored on.
     * @return Returns the JSON object.
     */
    public static JSONObject getJsonObjectFromUrl(URL url) {
        String jsonStr = getJsonFromUrl(url);
        try {
            JSONObject jsonObj = new JSONObject(jsonStr);
            return jsonObj;
        } catch (JSONException eObj) {
            try {
                JSONArray jsonArr = new JSONArray(jsonStr);
                JSONObject jsonObj = jsonArr.getJSONObject(0);
                return jsonObj;
            }
            catch (Exception eArr) {
                eObj.printStackTrace();
                eArr.printStackTrace();
                return null;
            }
        }
    }
}
