package io.github.lionisaqt.foodnow;

import android.os.AsyncTask;
import android.util.Log;

import com.yelp.fusion.client.connection.YelpFusionApi;
import com.yelp.fusion.client.models.Business;
import com.yelp.fusion.client.models.SearchResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import retrofit2.Call;

public class YelpAsyncTask extends AsyncTask<String, String, String> {
    private final String TAG = getClass().getSimpleName();

    AsyncResponse delegate = null;

    private YelpFusionApi api;
    private Map<String, Object> params;
    private String originalTermList;
    private ArrayList<Business> finalBusinessList;

    private Business businessSelection;

    YelpAsyncTask(YelpFusionApi api, Map<String, Object> params) {
        super();
        this.api = api;
        this.params = params;
        if (params.get("term") != null) {
            originalTermList = (String)params.get("term");
        }
        finalBusinessList = new ArrayList<>();
    }

    @Override
    protected String doInBackground(String... params) {
        if (this.params.get("term") != null) {
            String terms = (String)this.params.get("term");
            Log.d(TAG, "terms: " + terms);
            terms = terms.replaceAll("\\s+","");
            String[] termsArray = terms.split(",");
            for (String term : termsArray) {
                this.params.replace("term", term);
                YelpSearch();
            }
        } else {
            YelpSearch();
        }

        businessSelection = selectBusiness();

        return null;
    }

    private void YelpSearch() {
        Log.d(TAG, "looking for: " + this.params.get("term"));
        Call<SearchResponse> call = api.getBusinessSearch(this.params);

        try {
            SearchResponse response = call.execute().body();
            ArrayList<Business> businesses = response.getBusinesses();
            for (Iterator<Business> iterator = businesses.iterator(); iterator.hasNext();) {
                Business b = iterator.next();
                if (b.getRating() < Double.parseDouble(String.valueOf(this.params.get("rating")))) {
                    iterator.remove();
                } else {
                    Log.d(TAG, "adding " + b.getName());
                    finalBusinessList.add(b);
                }
            }
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    private Business selectBusiness() {
        if (finalBusinessList.size() > 0) {
            for (Business b : finalBusinessList) {
                Log.d(TAG, "candidate: " + b.getName());
            }
            return finalBusinessList.get(new Random().nextInt(finalBusinessList.size()));
        } else {
            Log.e(TAG, "Bad search, double check or have more specific parameters");
            return null;
        }
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        params.replace("term", originalTermList);
        delegate.processFinish(businessSelection, (HashMap<String, Object>)params);
    }
}
