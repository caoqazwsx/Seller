package com.zhao.seller.activity;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.zhao.seller.R;
import com.zhao.seller.globalvariable.Globalvariable;
import com.zhao.seller.httpconnection.HttpCallbackListener;
import com.zhao.seller.model.Food;
import com.zhao.seller.model.FoodItemAdapter;
import com.zhao.seller.model.FoodSalesItemAdapter;
import com.zhao.seller.presenter.ShopPresenter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;

public class SalesCountActivity extends BaseActivity {
    private ArrayList<Food> fooditems;
    private TextView title;
    private Button btn_back;
    private ListView listView;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    //progressBar.setVisibility(View.GONE);
                    FoodSalesItemAdapter foodSalesItemAdapter = new  FoodSalesItemAdapter(SalesCountActivity.this, R.layout.listview_item_sales_food, fooditems);
                    listView.setAdapter(foodSalesItemAdapter);
                    Log.d("FoodFragment", "success init");
                    break;
                case -1:
                    Toast.makeText(SalesCountActivity.this, "请检查你网络", Toast.LENGTH_SHORT).show();
                    //progressBar.setVisibility(View.GONE);
                    Log.d("FoodFragment", "error init");
                    break;
                case -2:
                    // progressBar.setVisibility(View.GONE);
                    Toast.makeText(SalesCountActivity.this, "本店没有菜单", Toast.LENGTH_SHORT).show();
                    break;

            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_count);
        title = (TextView)findViewById(R.id.title_text);
        btn_back = (Button) findViewById(R.id.title_back);
        listView = (ListView)findViewById(R.id.sales_count_list);
        title.setText("销量统计");
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        fooditems = new ArrayList<Food>();
        init();
    }
    public void init() {
//        listView.setAdapter(null);
//        fooditems.clear();
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
}
