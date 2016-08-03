package com.zhao.seller.activity;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.zhao.seller.R;
import com.zhao.seller.httpconnection.HttpCallbackListener;
import com.zhao.seller.presenter.ShopPresenter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.text.DecimalFormat;

public class CashActivity extends AppCompatActivity {

    private TextView title;
    private Button btn_back;

    private TextView balance;
    private EditText cash;
    private double db_cash;

    private Button btn_submit;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    balance.setText((String)msg.obj);
                    break;
                case 2:
                    Toast.makeText(CashActivity.this, "提现成功", Toast.LENGTH_SHORT).show();
                    finish();
                    break;

                case -1:
                    balance.setText("00.0");
                    break;
                case -2:
                    Toast.makeText(CashActivity.this, "提现失败", Toast.LENGTH_SHORT).show();
                    break;

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cash);
        title = (TextView)findViewById(R.id.title_text);
        btn_back = (Button)findViewById(R.id.title_back);

        balance = (TextView)findViewById(R.id.cash_balance);
        cash = (EditText)findViewById(R.id.cash_cash);
        btn_submit = (Button)findViewById(R.id.cash_submit);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkCash()){
                    DecimalFormat df  = new DecimalFormat("#.00");
                    df.format(db_cash);
                    ShopPresenter sp = new ShopPresenter();
                    sp.cash(db_cash, new HttpCallbackListener() {
                        @Override
                        public void onFinish(String response) {
                            if(response.equals("success")){
                                Message msg = new Message();
                                msg.what = 2;
                                handler.sendMessage(msg);
                            }else {
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
                            Log.d("CashActivity",e.toString());
                            Message msg = new Message();
                            msg.what = -2;
                            handler.sendMessage(msg);
                        }
                    });

                }
            }
        });
        initBalance();
    }

    public void initBalance(){

        ShopPresenter sp = new ShopPresenter();
        sp.getBalance(new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {

                try {
                    JSONArray ja = new JSONArray(response);
                    if (ja.length() != 0) {
                        JSONObject jo = ja.getJSONObject(0);
                        Message msg = new Message();
                        msg.what = 1;
                        msg.obj = jo.getString("balance");
                        handler.sendMessage(msg);
                    } else {
                        Message msg = new Message();
                        msg.what = -1;
                        handler.sendMessage(msg);
                    }
                } catch (Exception e) {
                    Log.d("MyShopFragment1", e.toString());
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
                Log.d("MyShopFragment2",e.toString());
                Message msg = new Message();
                msg.what = -1;
                handler.sendMessage(msg);

            }
        });
    }

    public boolean checkCash(){
        try {
           db_cash = Double.parseDouble(cash.getText().toString());
        }catch (Exception e){
            Toast.makeText(this, "数据格式错误", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(db_cash > Double.parseDouble(balance.getText().toString())) {
            Toast.makeText(this, "账户余额不足", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


}
