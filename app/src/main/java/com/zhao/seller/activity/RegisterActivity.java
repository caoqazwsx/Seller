package com.zhao.seller.activity;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.zhao.seller.R;
import com.zhao.seller.httpconnection.HttpCallbackListener;
import com.zhao.seller.presenter.LoginPresenter;

import java.io.InputStream;

public class RegisterActivity extends BaseActivity {

    private TextView titleText;
    private Button btn_back;

    private EditText account;
    private EditText password;
    private EditText name;
    private EditText idNumber;
    private Button btn_submit;

    private   Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                case -1:
                    Toast.makeText(RegisterActivity.this, (String) msg.obj, Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        btn_back = (Button)findViewById(R.id.title_back);
        titleText = (TextView)findViewById(R.id.title_text);
        account = (EditText)findViewById(R.id.register_account);
        password = (EditText)findViewById(R.id.register_password);
        name = (EditText)findViewById(R.id.register_name);
        idNumber = (EditText)findViewById(R.id.register_id_number);
        btn_submit = (Button)findViewById(R.id.register_submit);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        titleText.setText("注册");

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             if(checkInput()){
                 LoginPresenter lp = new LoginPresenter();
                 lp.register(account.getText().toString(), password.getText().toString(), name.getText().toString(), idNumber.getText().toString(),
                         new HttpCallbackListener() {
                             @Override
                             public void onFinish(String response) {
                                 if(response.equals("success")){
                                     Message msg = new Message();
                                     msg.what = 1;
                                     handler.sendMessage(msg);
                                 }
                                 else{
                                     Message msg = new Message();
                                     msg.what = -1;
                                     msg.obj = response;
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
                                 Log.d("register",e.toString());
                                 Message msg = new Message();
                                 msg.what = -1;
                                 msg.obj = "error";
                                 handler.sendMessage(msg);

                             }
                         });

             }
            }
        });
    }

    private Boolean checkInput(){
        if (account.getText().toString().equals("") || password.getText().toString().equals("")){
            Toast.makeText(RegisterActivity.this, "账户信息不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(account.length() < 6 || password.length() < 6){
            Toast.makeText(RegisterActivity.this, "账号或密码不能小于6位", Toast.LENGTH_SHORT).show();
            return false;
        } else if(name.getText().toString().equals("")||idNumber.getText().toString().equals("")){
            Toast.makeText(RegisterActivity.this, "身份信息不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }else if (idNumber.getText().toString().length()!=18){
            Toast.makeText(RegisterActivity.this, "身份证号有误", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;

    }


}
