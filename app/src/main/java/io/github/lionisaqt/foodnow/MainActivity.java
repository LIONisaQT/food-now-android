package io.github.lionisaqt.foodnow;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.yelp.fusion.client.connection.YelpFusionApi;
import com.yelp.fusion.client.connection.YelpFusionApiFactory;
import com.yelp.fusion.client.models.Business;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements AsyncResponse {
    private final String TAG = getClass().getSimpleName();

    private YelpFusionApi yelpFusionApi;

    private LocationManager locationManager;
    private MyLocationListener locationListener;

    private TextInputEditText categories;
    private TextInputEditText locationText;
    private SeekBar distanceSlider;
    private TextView distanceText;
    private RatingBar minRating;
    private Button submitButton;
    private Switch openNowSwitch;
    private ArrayList<Switch> attributeSwitches;

    private ArrayList<String> priceRange;
    private ArrayList<String> attributes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setElements();
        onDistanceChanged();
        setSubmitButtonText();
        setAttributeSwitchToggles();

        YelpFusionApiFactory apiFactory = new YelpFusionApiFactory();
        try {
            yelpFusionApi = apiFactory.createAPI(getString(R.string.api_key));
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }

        priceRange = new ArrayList<>();
        attributes = new ArrayList<>();

        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        locationListener = new MyLocationListener();
    }

    private void setElements() {
        categories = findViewById(R.id.categories);
        locationText = findViewById(R.id.location);
        distanceSlider = findViewById(R.id.distance_slider);
        distanceText = findViewById(R.id.distance_text);
        minRating = findViewById(R.id.min_rating);
        openNowSwitch = findViewById(R.id.open_now);

        attributeSwitches = new ArrayList<>();
        Switch hotAndNewSwitch = findViewById(R.id.hot_and_new);
        attributeSwitches.add(hotAndNewSwitch);
        Switch requestAQuoteSwitch = findViewById(R.id.request_a_quote);
        attributeSwitches.add(requestAQuoteSwitch);
        Switch reservationSwitch = findViewById(R.id.reservation);
        attributeSwitches.add(reservationSwitch);
        Switch waitlistReservationSwitch = findViewById(R.id.waitlist_reservation);
        attributeSwitches.add(waitlistReservationSwitch);
        Switch cashbackSwitch = findViewById(R.id.cashback);
        attributeSwitches.add(cashbackSwitch);
        Switch dealsSwitch = findViewById(R.id.deals);
        attributeSwitches.add(dealsSwitch);
        Switch genderNeutralRestroomsSwitch = findViewById(R.id.gender_neutral_restrooms);
        attributeSwitches.add(genderNeutralRestroomsSwitch);
        Switch openToAllSwitch = findViewById(R.id.open_to_all);
        attributeSwitches.add(openToAllSwitch);
        Switch wheelchairAccessibleSwitch = findViewById(R.id.wheelchair_accessible);
        attributeSwitches.add(wheelchairAccessibleSwitch);

        submitButton = findViewById(R.id.submit_button);
    }

    public void submit(View v) {
        if (locationText.getText().toString().length() <= 0) {
            Toast.makeText(this, "Location cannot be empty!", Toast.LENGTH_SHORT).show();
            locationText.requestFocus();
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(locationText, InputMethodManager.SHOW_IMPLICIT);
            return;
        }

        HashMap<String, Object> params = new HashMap<>();

        String categories = this.categories.getText().toString();
        if (categories.length() > 0) {
            params.put("term", categories);
        } else {
            params.put("term", "food");
        }
        if (priceRange.size() > 0) {
            params.put("price", arrayListToString(priceRange));
        }
        if (attributes.size() > 0) {
            params.put("attributes", arrayListToString(attributes));
        }
        if (openNowSwitch.isChecked()) {
            params.put("open_now", openNowSwitch.isChecked());
        }
        if (locationText.getText().toString().contains(",")) {
            String[] coordinates = locationText.getText().toString().split(",");
            params.put("latitude", coordinates[0]);
            params.put("longitude", coordinates[1]);
        }
        else {
            params.put("location", locationText.getText().toString());
        }
        params.put("radius", (int)(Float.valueOf(distanceText.getText().toString()) * 1600));
        params.put("rating", minRating.getRating());

        YelpAsyncTask task = new YelpAsyncTask(yelpFusionApi, params);
        task.delegate = this;
        task.execute();
    }

    public void onPriceToggled(View v) {
        String price = v.getTag().toString();
        if (priceRange.contains(price)) priceRange.remove(price);
        else priceRange.add(price);
    }

    public void onDistanceChanged() {
        distanceSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            float progress;
            String distance;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                this.progress = progress;
                if (progress <= 1f) this.progress = 1f;
                distance = String.valueOf(this.progress/2f);
                distanceText.setText(distance);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                distanceSlider.setProgress((int)progress);
                distance = String.valueOf(progress/2f);
                distanceText.setText(distance);
            }
        });
    }

    private void setAttributeSwitchToggles() {
        for (final Switch attributeSwitch : attributeSwitches) {
            attributeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) attributes.add(buttonView.getTag().toString());
                    else attributes.remove(buttonView.getTag().toString());
                }
            });
        }
    }

    public void onLocationClicked(View v) {
        Location location = getLocation();

        if (location != null) {
            String coordinates = location.getLatitude() + ", " + location.getLongitude();
            locationText.setText(coordinates);
            if (getCurrentFocus() != null && getCurrentFocus().getId() == R.id.location) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
            }
        } else {
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                Toast.makeText(this, R.string.location_toast_wait, Toast.LENGTH_LONG).show();
            }
        }
    }

    private Location getLocation() {
        Location location = null;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    0);
        } else {
            boolean gpsEnabled = false;

            try {
                gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }

            if (gpsEnabled) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, locationListener);
                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (location == null) {
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if (location == null) {
                        Log.e(TAG, "Could not grab location");
                    }
                }
            } else {
                new AlertDialog.Builder(this)
                        .setMessage(R.string.location_alert_message)
                        .setPositiveButton(R.string.location_alert_positive, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                MainActivity.this.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                            }
                        })
                        .setNegativeButton(R.string.location_alert_negative, null)
                        .show();
            }
        }
        return location;
    }

    private void setSubmitButtonText() {
        submitButton.setText(new Random().nextFloat() >= 0.5 ?
                R.string.submit_button_text_1 :
                R.string.submit_button_text_2);
    }

    private String arrayListToString(ArrayList<String> arrayList) {
        return arrayList.toString().substring(1, priceRange.toString().length() - 1);
    }

    @Override
    public void processFinish(Business business, HashMap<String, Object> params) {
        Intent myIntent = new Intent(this, ResultActivity.class);
        myIntent.putExtra("params", params);
        myIntent.putExtra("business", business);
        startActivity(myIntent);
    }
}
