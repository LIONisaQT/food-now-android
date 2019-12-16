package io.github.lionisaqt.foodnow;

import com.yelp.fusion.client.models.Business;

import java.util.HashMap;

public interface AsyncResponse {
    void processFinish(Business business, HashMap<String, Object> params);
}
