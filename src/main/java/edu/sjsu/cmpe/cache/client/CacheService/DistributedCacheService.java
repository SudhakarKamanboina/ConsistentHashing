package edu.sjsu.cmpe.cache.client.CacheService;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONArray;

/**
 * Distributed cache service
 * 
 */
public class DistributedCacheService implements CacheServiceInterface {
    private final String cacheServerUrl;

    public DistributedCacheService(String serverUrl) {
        this.cacheServerUrl = serverUrl;
    }

    /**
     * @see CacheServiceInterface#get(long)
     */
    @Override
    public String get(long key) {
        HttpResponse<JsonNode> response = null;
        try {
            response = Unirest.get(this.cacheServerUrl + "/cache/{key}")
                    .header("accept", "application/json")
                    .routeParam("key", Long.toString(key)).asJson();
        } catch (UnirestException e) {
            System.err.println(e);
        }
        String value = response.getBody().getObject().getString("value");

        return value;
    }

    /**
     * @see CacheServiceInterface#put(long,
     *      java.lang.String)
     */
    @Override
    public void put(long key, String value) {
        HttpResponse<JsonNode> response = null;
        try {
            response = Unirest
                    .put(this.cacheServerUrl + "/cache/{key}/{value}")
                    .header("accept", "application/json")
                    .routeParam("key", Long.toString(key))
                    .routeParam("value", value).asJson();
        } catch (UnirestException e) {
            System.err.println(e);
        }

        if (response.getCode() != 200) {
            System.out.println("Failed to add to the cache.");
        }
    }

    @Override
    public String getAllValues() {
        HttpResponse<JsonNode> response = null;
        try {
            response = Unirest.get(this.cacheServerUrl + "/cache")
                    .header("accept", "application/json").asJson();
        } catch (UnirestException e) {
            System.err.println(e);
        }
        JSONArray array = response.getBody().getArray();
        StringBuilder valuesBuilder = new StringBuilder().append("Values:");
        StringBuilder keyBuilder = new StringBuilder().append("Keys:");
        for(int length = 0;length < array.length();length++){
            valuesBuilder.append(" "+array.getJSONObject(length).getString("value"));
            keyBuilder.append(" "+ array.getJSONObject(length).getInt("key"));
        }


        return new StringBuilder().append(valuesBuilder.toString()+"\n"+ keyBuilder.toString()).toString();
    }
}
