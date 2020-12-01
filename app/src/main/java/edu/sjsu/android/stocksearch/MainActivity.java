package edu.sjsu.android.stocksearch;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class MainActivity extends AppCompatActivity {

    // Layout variables.
    private Switch autoRefreshSwitch;
    private Button clearBtn, quoteBtn;
    private ImageButton refreshBtn;
    private AutoCompleteTextView autoTextView;
    private TextView histDate;

    // Data variables.
    private String stockInput;
    private String dateInput;

    // Extra variables.
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Prepare layout items.
        autoRefreshSwitch = findViewById(R.id.autoRefreshSwitch);
        autoTextView = findViewById(R.id.editStockName);
        histDate = findViewById(R.id.editTextDate);
        clearBtn = findViewById(R.id.clearBtn);
        quoteBtn = findViewById(R.id.quoteBtn);
        refreshBtn = findViewById(R.id.refreshBtn);

        // Shared Preferences
        /*
        SharedPreferencesJSON sharedPrefs = new SharedPreferencesJSON(this);
        sharedPrefs.dropSharedPreferences();
         */

        // Prepare favorites.
        Favorites fav = new Favorites(this, MainActivity.this);
        fav.initialize();

        // Auto-complete stock input field: listen for input, and recommend stock symbols /w Tiingo API in background.
        AutoCompleteStockName autoCompleteListener = new AutoCompleteStockName(MainActivity.this, this);
        autoTextView.addTextChangedListener(autoCompleteListener);
        // When a suggestion is chosen, remove the suggestion dropdown.
        autoTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(), "Stock symbol selected.", Toast.LENGTH_SHORT).show();
                autoTextView.dismissDropDown();
            }
        });

        // Quote button: start activity to execute Tiingo request /w given input.
        quoteBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                stockInput = autoTextView.getText().toString();
                dateInput = histDate.getText().toString();
                if (stockInput.matches("")) {
                    Toast.makeText(getApplicationContext(), "Please enter a Stock Name/Symbol", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if (!autoCompleteListener.checkStockNameValid(stockInput)) {
                    Toast.makeText(getApplicationContext(), "Invalid Stock Name/Symbol", Toast.LENGTH_SHORT).show();
                }
                // Valid stock name; show the details in another activity.
                else {
                    // Check if date is valid.
                    try {
                        if (dateInput != null && !dateInput.isEmpty()) {
                            LocalDate date = LocalDate.parse(dateInput, DateTimeFormatter.ISO_LOCAL_DATE);
                        }
                        Intent intent = new Intent(MainActivity.this, StockDetailsActivity.class);
                        intent.putExtra("stockName", stockInput);
                        intent.putExtra("histDate", dateInput);
                        startActivity(intent);
                    }
                    catch (DateTimeParseException e) {
                        Toast.makeText(getApplicationContext(), "Invalid Date Format", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        // Clear button: empty the auto complete input field.
        clearBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                autoTextView.setText("");
                histDate.setText("");
            }
        });
    }
}