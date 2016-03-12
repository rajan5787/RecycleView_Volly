package com.example.rajan.recycleview_volly;

import android.app.DownloadManager;
import android.content.ClipData;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private ListView listView;
    private CustomAdapter listAdapter;
    private ArrayList<Item> feedItems;
    private String URL_FEED = "  ";//add URL of your local host server;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView=(ListView)findViewById(R.id.list);
        feedItems=new ArrayList<>();
        listAdapter=new CustomAdapter(this,feedItems);
        listView.setAdapter(listAdapter);

        //we first check for catched request
        Cache cache=AppController.getInstance().getRequestQueue().getCache();
        Cache.Entry entry=cache.get(URL_FEED);
        if(entry!=null){
            try {
                String data=new String(entry.data,"UTF-8");
                try {
                    parseJsonFeed(new JSONObject(data));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        else{
            JsonObjectRequest request=new JsonObjectRequest(Request.Method.GET,URL_FEED,null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    VolleyLog.d(TAG, "Response: " + response.toString());
                    if (response != null) {
                        parseJsonFeed(response);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d(TAG, "Error: " + error.getMessage());
                }
            });
            AppController.getInstance().addToRequestQueue(request);
        }
    }
    public void  parseJsonFeed(JSONObject object){
        try {
            JSONArray array=object.getJSONArray("feed");

            for(int i=0;i<array.length();i++){
                JSONObject obj=(JSONObject)array.get(i);
                Item item=new Item();
                item.setId(obj.getInt("id"));
                item.setName(obj.getString("name"));
                String image=obj.isNull("image")?null:obj.getString("image");
                item.setImage(image);
                item.setStatus(obj.getString("status"));
                item.setProfilePic(obj.getString("profilePic"));
                item.setTimeStamp(obj.getString("timeStamp"));
                String feedUrl = obj.isNull("url") ? null : obj.getString("url");
                item.setUrl(feedUrl);
                feedItems.add(item);
            }
            listAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}