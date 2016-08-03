package com.zhao.seller.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.zhao.seller.R;
import com.zhao.seller.globalvariable.Globalvariable;
import com.zhao.seller.httpconnection.HttpCallbackListener;
import com.zhao.seller.presenter.LoginPresenter;

import java.io.InputStream;

public class LoginActivity extends BaseActivity {
    private int exitTime;

    private ProgressBar pb;
    private EditText account;
    private  EditText password;
    private  Button log;
    private  Button reg;
    private Button back;
    private  TextView titletext;
    private  final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    Toast.makeText(LoginActivity.this, "登陆成功", Toast.LENGTH_SHORT).show();
                    pb.setVisibility(View.GONE);
                    Globalvariable.LOGIN_STATE = true;
                    Globalvariable.ACCOUNT = account.getText().toString();
                    if(!((String) msg.obj).equals("")) {
                        Globalvariable.SHOP_ID = Integer.parseInt((String) msg.obj);
                    }
                    Intent it = new Intent(LoginActivity.this,MainActivity.class);
                    startActivity(it);
                    finish();
                    break;

                case -1:
                    pb.setVisibility(View.GONE);
                    Toast.makeText(LoginActivity.this, (String) msg.obj, Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        exitTime=0;

        log = (Button) findViewById(R.id.login);
        reg = (Button) findViewById(R.id.register);
        back = (Button) findViewById(R.id.title_back);
        titletext = (TextView) findViewById(R.id.title_text);
        titletext.setText("登录");
        pb = (ProgressBar) findViewById(R.id.progress_bar);
        log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                account = (EditText) findViewById(R.id.account);
                password = (EditText) findViewById(R.id.password);
                String acc = account.getText().toString();
                String pas = password.getText().toString();

                    pb.setVisibility(View.VISIBLE);
                    new LoginPresenter().loginCheck(acc, pas, new HttpCallbackListener() {
                        @Override
                        public void onFinish(Bitmap bm){}
                        @Override
                        public void onFinish(InputStream in){}
                        @Override
                        public void onFinish(String response) {
                            if(response.equals("error")){
                                Message msg = new Message();
                                msg.obj = "用户名或密码错误";
                                msg.what = -1;
                                handler.sendMessage(msg);
                            }else {
                                Message msg = new Message();
                                msg.obj = response;
                                msg.what = 1;
                                handler.sendMessage(msg);
                            }

                        }
                        @Override
                        public void onError(Exception e) {
                            Message msg = new Message();
                            msg.obj = "登录发生错误";
                            msg.what = -1;
                            handler.sendMessage(msg);
                            Log.d("Http", e.toString());

                        }
                    });
            }
        });
        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(it);
            }
        });

    }

}
