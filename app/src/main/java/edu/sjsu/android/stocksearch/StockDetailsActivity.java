package edu.sjsu.android.stocksearch;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import org.json.JSONObject;

public class StockDetailsActivity extends AppCompatActivity {

    // Context variables
    private Context context;

    // Data variables
    private String stockName;
    private String histDate;

    // Zero-arg constructor
    public StockDetailsActivity() {}

    // Layout variables
    private FloatingActionButton fab;
    private ViewPager viewPager;
    private TabLayout tabs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_details);

        context = getApplicationContext();

        // Retrieve intent bundled data.
        if (getIntent().hasExtra("stockName")) {
            stockName = getIntent().getStringExtra("stockName");
            histDate = getIntent().getStringExtra("histDate");
        }

        // Default activity + UI setup
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, stockName, histDate, getSupportFragmentManager());
        viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        fab = findViewById(R.id.fab);

        // Configure action bar.
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(stockName);

        // Favorites button: favorite the stock and store its name.
        if (new SharedPreferencesJSON(context).getSharedPreferencesJSON().has(stockName)) {
            fab.setBackgroundColor(getColor(R.color.gold));
        }
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferencesJSON favJson = new SharedPreferencesJSON(context);
                JSONObject favJsonObj = favJson.getSharedPreferencesJSON();
                if (!favJsonObj.has(stockName)) {
                    favJson.addToSharedPreferencesJSON(stockName);
                    fab.setBackgroundTintList(context.getResources().getColorStateList(R.color.gold, null));
                    Snackbar.make(view, "Favorited!", Snackbar.LENGTH_SHORT).show();
                }
                else {
                    favJson.removeFromSharedPreferencesJSON(stockName);
                    fab.setBackgroundTintList(context.getResources().getColorStateList(R.color.purple_200, null));
                    Snackbar.make(view, stockName + " has been removed from favorites.", Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }
}