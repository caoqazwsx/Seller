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
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.zhao.seller.R;
import com.zhao.seller.globalvariable.Globalvariable;
import com.zhao.seller.httpconnection.HttpCallbackListener;
import com.zhao.seller.presenter.FormPresenter;

import java.io.InputStream;

public class EditSendActivity extends BaseActivity {

    private long formId;
    private String sendState;
    private TextView title;
    private Button btn_back;

    private EditText senderAccount;
    private RadioGroup radioGroup;
    private Button btn_submit;

    private  Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    finish();
                    break;

                case -1:

                    Toast.makeText(EditSendActivity.this, "error", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_send);
        formId = getIntent().getLongExtra("formId",0);
        sendState = "ON";
        title = (TextView)findViewById(R.id.title_text);
        btn_back = (Button)findViewById(R.id.title_back);
        senderAccount = (EditText)findViewById(R.id.edit_send_senderAaccount);
        radioGroup = (RadioGroup)findViewById(R.id.edit_send_radioGroup);
        btn_submit = (Button)findViewById(R.id.edit_send_submit);
        senderAccount.setText(getIntent().getStringExtra("senderAccount"));
        title.setText("标记配送状态");
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.edit_send_radio_sending:
                        sendState = "ON";
                        break;
                    case R.id.edit_send_radio_noSending:
                        sendState = "OFF";
                        break;
                }
            }
        });


        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FormPresenter fp = new FormPresenter();
                fp.editSendState(senderAccount.getText().toString(), sendState, formId, new HttpCallbackListener() {
                    @Override
                    public void onFinish(String response) {
                        if(response.equals("success")){
                            Message msg = new Message();
                            msg.what = 1;
                            handler.sendMessage(msg);
                        }else{
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
                        Log.d("EditSendActivity",e.toString());
                        Message msg = new Message();
                        msg.what = -1;
                        handler.sendMessage(msg);
                    }
                });
            }
        });


    }




}
