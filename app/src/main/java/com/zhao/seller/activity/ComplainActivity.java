package com.zhao.seller.activity;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.zhao.seller.R;
import com.zhao.seller.custom.DownUpListView;
import com.zhao.seller.globalvariable.Globalvariable;
import com.zhao.seller.httpconnection.HttpCallbackListener;
import com.zhao.seller.model.Complain;
import com.zhao.seller.model.ComplainItemAdapter;
import com.zhao.seller.presenter.ShopPresenter;

import org.json.JSONArray;

import java.io.InputStream;
import java.util.ArrayList;

public class ComplainActivity extends BaseActivity {
    private TextView title;
    private Button back;

    private DownUpListView listView;
    private ArrayList<Complain> complains;

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                     if(complains.size() == 0) {
                         Toast.makeText(ComplainActivity.this, "无投诉", Toast.LENGTH_SHORT).show();
                     }else {
                         listView.onRefreshComplete();
                         ComplainItemAdapter complainItemAdapter = new ComplainItemAdapter(ComplainActivity.this,
                                 R.layout.listview_item_complain,complains);
                         listView.setAdapter(complainItemAdapter);
                     }
                    break;
                case -1:
                    Toast.makeText(ComplainActivity.this, "error", Toast.LENGTH_SHORT).show();
                    break;

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complain);
        title = (TextView) findViewById(R.id.title_text);
        back = (Button) findViewById(R.id.title_back);
        listView = (DownUpListView) findViewById(R.id.complain_list);
        complains = new ArrayList<Complain>();
        title.setText("投诉");
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        listView.setOnRefreshListener(new DownUpListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                init();
            }
        });
        init();
    }

    public void init(){
        complains.clear();
        listView.setAdapter(null);
        ShopPresenter sp = new ShopPresenter();
        sp.getComplainList(Globalvariable.SHOP_ID, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                try {
                    JSONArray ja = new JSONArray(response);
                    for(int i = 0; i < ja.length(); i++){
                        complains.add(new Gson().fromJson(ja.getString(i),Complain.class));
                    }
                    Message msg = new Message();
                    msg.what = 1;
                    handler.sendMessage(msg);

                }catch (Exception e){
                    Log.d("ComplainActivity",e.toString());
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
                Log.d("ComplainActivity",e.toString());
                Message msg = new Message();
                msg.what = -1;
                handler.sendMessage(msg);
            }
        });

    }
}
