package com.zhao.seller.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.zhao.seller.R;
import com.zhao.seller.custom.DownUpListView;
import com.zhao.seller.globalvariable.Globalvariable;
import com.zhao.seller.httpconnection.HttpCallbackListener;
import com.zhao.seller.model.Food;
import com.zhao.seller.model.FoodItemAdapter;
import com.zhao.seller.presenter.ShopPresenter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;

public class FoodActivity extends BaseActivity {
    private ArrayList<Food> fooditems;
    private TextView title;
    private Button btn_back;
    private DownUpListView listView;
    private Button btn_add;
    private ProgressBar progressBar;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    progressBar.setVisibility(View.GONE);
                    listView.setVisibility(View.VISIBLE);
                    listView.onRefreshComplete();
                    FoodItemAdapter foodItemAdapter = new FoodItemAdapter(FoodActivity.this, R.layout.listview_item_food, fooditems);
                    listView.setAdapter(foodItemAdapter);
                    Log.d("FoodActivity", "success init");
                    break;
                case -1:
                    Toast.makeText(FoodActivity.this, "请检查你网络", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    Log.d("FoodActivity", "error init");
                    break;
                case -2:
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(FoodActivity.this, "本店没有菜单", Toast.LENGTH_SHORT).show();
                    break;

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food);
        progressBar = (ProgressBar)findViewById(R.id.progress_bar);
        title = (TextView)findViewById(R.id.title_text);
        btn_back = (Button) findViewById(R.id.title_back);
        listView = (DownUpListView)findViewById(R.id.food_list);
        btn_add = (Button)findViewById(R.id.food_btn_add);
        title.setText("商铺餐饮");
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(FoodActivity.this, EditFoodActivity.class);
                it.putExtra("foodId",0);
                startActivity(it);
            }
        });
        listView.setOnRefreshListener(new DownUpListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                init();
            }
        });
        fooditems = new ArrayList<Food>();

    }

    public void init() {
        listView.setAdapter(null);
        fooditems.clear();
        progressBar.setVisibility(View.VISIBLE);
        listView.setVisibility(View.GONE);
        ShopPresenter sp = new ShopPresenter();
        sp.getFoodList(Globalvariable.SHOP_ID, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {

                try {
                    JSONArray res = new JSONArray(response);
                    if (res.length() != 0) {
                        Log.d("Foodlist", "get Data length:" + res.length());
                        for (int i = 0; i < res.length(); i++) {
                            JSONObject jb = res.getJSONObject(i);
                            Log.d("Foodlist Data:", jb.toString());
                            fooditems.add(new Gson().fromJson(jb.toString(),Food.class));
                        }
                        Log.d("Foodlist", "fooditems add success!");
                        Message msg = new Message();
                        msg.what = 1;
                        handler.sendMessage(msg);
                    } else {
                        Message msg = new Message();
                        msg.what = -2;
                        handler.sendMessage(msg);
                    }
                } catch (Exception e) {
                    Log.d("Foodlist", e.toString());
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
                Log.d("foodlist", e.toString());
                Message msg = new Message();
                msg.what = -1;
                handler.sendMessage(msg);
            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();
        init();
    }
}
