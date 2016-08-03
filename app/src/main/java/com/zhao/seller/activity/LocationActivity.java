package com.zhao.seller.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.zhao.seller.R;
import com.zhao.seller.httpconnection.HttpCallbackListener;
import com.zhao.seller.model.PlaceSuggestion;
import com.zhao.seller.model.PlaceSuggestionAdapter;
import com.zhao.seller.presenter.LocationPresenter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;

public class LocationActivity extends BaseActivity {
    private Button btn_back;
    private EditText searchKey;
    private LinearLayout currentLocation;
    private ListView listView;
    private Button btn_search;
    private Location location;



    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            switch (msg.what){
                case 0:
                    currentLocation.setVisibility(View.VISIBLE);
                    listView.setAdapter(null);
                    listView.setVisibility(View.GONE);
                    Toast.makeText(LocationActivity.this,"没有搜索结果",Toast.LENGTH_SHORT).show();
                case 1:
                    PlaceSuggestionAdapter placeSuggestionAdapter = new PlaceSuggestionAdapter(LocationActivity.this,
                            R.layout.listview_item_place_suggest,placeSuggestions);
                    listView.setAdapter(placeSuggestionAdapter);
                    break;
                case 2:
                    Intent it = new Intent();
                    it.putExtra("state", true);
                    it.putExtra("name", (String)msg.obj);
                    it.putExtra("lng", location.getLongitude());
                    it.putExtra("lat", location.getLatitude());
                    setResult(RESULT_OK, it);
                    finish();
                    break;

                case -1:
                    break;
                case -2:
                    Toast.makeText(LocationActivity.this, "逆地理编码错误", Toast.LENGTH_SHORT).show();
                    break;
            }

        }
    };

    private ArrayList<PlaceSuggestion> placeSuggestions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        btn_back = (Button) findViewById(R.id.title_back);
        searchKey = (EditText)findViewById(R.id.location_searchKey);
        currentLocation = (LinearLayout)findViewById(R.id.location_current);
        listView = (ListView)findViewById(R.id.location_placeSuggestion);
        btn_search = (Button)findViewById(R.id.location_searcher);
        placeSuggestions = new ArrayList<PlaceSuggestion>();
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(searchKey.getText().toString().equals("")){
                    currentLocation.setVisibility(View.VISIBLE);
                    listView.setVisibility(View.GONE);
                }else{
                    currentLocation.setVisibility(View.GONE);
                    listView.setVisibility(View.VISIBLE);
                    placeSuggestions.clear();
                    LocationPresenter lp = new LocationPresenter();
                    lp.getPlaceSuggestionList(searchKey.getText().toString(), new HttpCallbackListener() {
                        @Override
                        public void onFinish(String response) {
                            try{
                                JSONArray ja = new JSONArray(response);
                                if(ja.length() == 0){
                                    Message msg = new Message();
                                    msg.what = 0;
                                    handler.sendMessage(msg);
                                }else{
                                    for(int i=0;i<ja.length();i++){
                                        JSONObject jo = ja.getJSONObject(i);
                                        if(jo.getJSONObject("location") == null) continue;
                                        placeSuggestions.add(new PlaceSuggestion(jo.getString("name"),
                                                jo.getString("city"),jo.getString("district"),jo.getJSONObject("location").getDouble("lng"),
                                                jo.getJSONObject("location").getDouble("lat")));
                                    }
                                    Message msg = new Message();
                                    msg.what = 1;
                                    handler.sendMessage(msg);
                                }
                            }catch (Exception e){
                                Log.d("Location",e.toString());
                                Message msg = new Message();
                                msg.what = -1;
                                handler.sendMessage(msg);

                            }


                        }

                        @Override
                        public void onFinish(InputStream in) {

                        }

                        @Override
                        public void onFinish(Bitmap bm) {

                        }

                        @Override
                        public void onError(Exception e) {
                            Log.d("Location",e.toString());
                            Message msg = new Message();
                            msg.what = -1;
                            handler.sendMessage(msg);

                        }
                    });
                }
            }
        });
        currentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LocationPresenter lp = new LocationPresenter();
                location = lp.getCurrentLocation(LocationActivity.this);
                if(location == null) {
                    Toast.makeText(LocationActivity.this, "获取位置失败",
                            Toast.LENGTH_SHORT).show();
                    Intent it = new Intent();
                    it.putExtra("state", false);
                    setResult(RESULT_OK, it);
                    finish();
                }else {
                 lp.getStringLocation(location.getLongitude(), location.getLatitude(), new HttpCallbackListener() {
                    @Override
                    public void onFinish(String response) {
                        try {
                            if(!response.equals("error")) {
                                Message msg = new Message();
                                msg.what = 2;
                                msg.obj = response;
                                handler.sendMessage(msg);
                            }else {
                                Message msg = new Message();
                                msg.what = -2;
                                handler.sendMessage(msg);
                            }
                        }catch (Exception e){
                            Log.d("Location",e.toString());
                            Message msg = new Message();
                            msg.what = -2;
                            handler.sendMessage(msg);
                        }
                    }

                    @Override
                    public void onFinish(InputStream in) {

                    }

                    @Override
                    public void onFinish(Bitmap bm) {

                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });

                }

            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent it = new Intent();
                if(placeSuggestions.get(position) == null) {
                    it.putExtra("state", false);
                    Toast.makeText(LocationActivity.this, "获取位置失败",
                            Toast.LENGTH_SHORT).show();
                }else {
                    it.putExtra("state", true);
                    it.putExtra("name",placeSuggestions.get(position).getName());
                    it.putExtra("lng", placeSuggestions.get(position).getLongitude());
                    it.putExtra("lat", placeSuggestions.get(position).getLatitude());
                }
                setResult(RESULT_OK, it);
                finish();
            }
        });

        searchKey.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().equals("")){
                    currentLocation.setVisibility(View.VISIBLE);
                    listView.setVisibility(View.GONE);
                }else{
                    currentLocation.setVisibility(View.GONE);
                    listView.setVisibility(View.VISIBLE);
                    placeSuggestions.clear();
                    LocationPresenter lp = new LocationPresenter();
                    lp.getPlaceSuggestionList(s.toString(), new HttpCallbackListener() {
                        @Override
                        public void onFinish(String response) {
                            try{
                                JSONArray ja = new JSONArray(response);
                                if(ja.length() == 0){
                                    Message msg = new Message();
                                    msg.what = 0;
                                    handler.sendMessage(msg);
                                }else{
                                    for(int i=0;i<ja.length();i++){
                                        JSONObject jo = ja.getJSONObject(i);
                                        if(jo.getJSONObject("location") == null) continue;
                                        placeSuggestions.add(new PlaceSuggestion(jo.getString("name"),
                                                jo.getString("city"),jo.getString("district"),jo.getJSONObject("location").getDouble("lng"),
                                                jo.getJSONObject("location").getDouble("lat")));
                                    }
                                    Message msg = new Message();
                                    msg.what = 1;
                                    handler.sendMessage(msg);
                                }
                            }catch (Exception e){
                                Log.d("Location",e.toString());
                                Message msg = new Message();
                                msg.what = -1;
                                handler.sendMessage(msg);

                            }


                        }

                        @Override
                        public void onFinish(InputStream in) {

                        }

                        @Override
                        public void onFinish(Bitmap bm) {

                        }

                        @Override
                        public void onError(Exception e) {
                            Log.d("Location",e.toString());
                            Message msg = new Message();
                            msg.what = -1;
                            handler.sendMessage(msg);

                        }
                    });
                }


            }
        });
    }


}
