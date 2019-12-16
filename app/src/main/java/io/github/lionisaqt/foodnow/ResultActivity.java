package io.github.lionisaqt.foodnow;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.yelp.fusion.client.connection.YelpFusionApi;
import com.yelp.fusion.client.connection.YelpFusionApiFactory;
import com.yelp.fusion.client.models.Business;
import com.yelp.fusion.client.models.Category;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;

import static android.content.ContentValues.TAG;

public class ResultActivity extends AppCompatActivity implements AsyncResponse {
    private final String TAG = getClass().getSimpleName();

    private YelpFusionApi yelpFusionApi;

    private ImageView businessImage;
    private TextView businessName;
    private RatingBar businessRating;
    private TextView numReviews;
    private TextView numReviewsText;
    private TextView businessTitle;
    private TextView businessAddress;
    private TextView businessPhone;
    private TextView businessUrl;
    private TextView businessPrice;
    private TextView interpunct;

    public static boolean enableReroll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        setElements();

        YelpFusionApiFactory apiFactory = new YelpFusionApiFactory();
        try {
            yelpFusionApi = apiFactory.createAPI(getString(R.string.api_key));
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }

        if (getIntent().getSerializableExtra("business") == null) {
            reroll(findViewById(R.id.reroll_button).getRootView());
        } else {
            populateValues((Business)getIntent().getSerializableExtra("business"));
        }

        enableReroll = true;
    }

    @SuppressWarnings("unchecked")
    public void reroll(View v) {
        if (enableReroll) {
            Map<String, Object> params = (HashMap<String, Object>)getIntent().getSerializableExtra("params");
            YelpAsyncTask task = new YelpAsyncTask(yelpFusionApi, params);
            task.delegate = this;
            task.execute();
            enableReroll = false;
        }
    }

    public void back(View v) {
        Intent myIntent = new Intent(this, MainActivity.class);
        startActivity(myIntent);
    }

    private void populateValues(Business business) {
        if (business != null) {
            if (business.getImageUrl() != null) {
                businessImage.setImageResource(android.R.color.transparent);
                DownloadImageTask task = new DownloadImageTask(businessImage);
                task.execute(business.getImageUrl());
            }

            businessName.setText(business.getName());

            businessRating.setRating((float)business.getRating());

            numReviews.setText(String.valueOf(business.getReviewCount()));
            if (business.getReviewCount() != 1) { numReviewsText.setText(R.string.review_plural); }
            else { numReviewsText.setText(R.string.review_singular); }

            ArrayList<String> titles = new ArrayList<>();
            StringBuilder sb = new StringBuilder(); String prefix = "";
            for (Category category : business.getCategories()) { titles.add(category.getTitle()); }
            for (String s : titles) {
                sb.append(prefix).append(s);
                prefix = ", ";
            }
            businessTitle.setText(sb.toString());

            businessAddress.setText(business.getLocation().getAddress1());

            if (business.getDisplayPhone() == null) {
                businessPhone.setVisibility(View.GONE);
            } else {
                businessPhone.setText(business.getPhone());
                businessPhone.setVisibility(View.VISIBLE);
            }

            businessUrl.setText(Html.fromHtml("<a href=" + business.getUrl() + ">Open in Yelp</a>", Html.FROM_HTML_MODE_LEGACY));
            businessUrl.setMovementMethod(LinkMovementMethod.getInstance());

            if (business.getPrice() == null) {
                businessPrice.setVisibility(View.GONE);
                interpunct.setVisibility(View.GONE);
            } else {
                businessPrice.setVisibility(View.VISIBLE);
                interpunct.setVisibility(View.VISIBLE);
                businessPrice.setText(business.getPrice());
            }
        }
    }

    private void setElements() {
        businessImage = findViewById(R.id.restaurant_image);
        businessName = findViewById(R.id.business_name);
        businessRating = findViewById(R.id.rating);
        numReviews = findViewById(R.id.num_reviews);
        numReviewsText = findViewById(R.id.num_reviews_text);
        businessTitle = findViewById(R.id.business_title);
        businessAddress = findViewById(R.id.business_address);
        businessPhone = findViewById(R.id.business_phone);
        businessUrl = findViewById(R.id.business_url);
        businessPrice = findViewById(R.id.business_price);
        interpunct = findViewById(R.id.interpunct);
    }

    @Override
    public void processFinish(Business business, HashMap<String, Object> params) {
        populateValues(business);
    }
}

class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
    private WeakReference<ImageView> imageRef;

    DownloadImageTask(ImageView imageView) {
        this.imageRef = new WeakReference<>(imageView);
    }

    @Override
    protected Bitmap doInBackground(String... strings) {
        String url = strings[0];
        Bitmap image = null;

        try {
            InputStream in = new java.net.URL(url).openStream();
            image = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        return image;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        imageRef.get().setImageBitmap(bitmap);
        ResultActivity.enableReroll = true;
}
}