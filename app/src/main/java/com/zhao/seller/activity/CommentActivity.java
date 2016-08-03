package com.zhao.seller.activity;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.zhao.seller.R;
import com.zhao.seller.custom.Grade;
import com.zhao.seller.globalvariable.Globalvariable;
import com.zhao.seller.globalvariable.Utility;
import com.zhao.seller.httpconnection.HttpCallbackListener;
import com.zhao.seller.model.Comment;
import com.zhao.seller.model.CommentItemAdapter;
import com.zhao.seller.presenter.ShopPresenter;

import org.json.JSONArray;

import java.io.InputStream;
import java.util.ArrayList;

public class CommentActivity extends BaseActivity {
    private TextView title;
    private Button btn_back;

    private LinearLayout context;
    private ProgressBar progressBar;

    private ListView listView;
    private TextView score;
    private TextView sendScore;
    private TextView shopScore;
    private Grade sendGrade;
    private Grade shopGrade;

    private double sumSendScore;
    private double sumShopScore;
    private double length;

    private CheckBox checkBox;
    private boolean check;

    private ArrayList<Comment> commentsAll;
    private ArrayList<Comment> commentsContext;
    private CommentItemAdapter commentItemAdapter;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    updateList();
                    progressBar.setVisibility(View.GONE);
                    context.setVisibility(View.VISIBLE);
                    initScore();
                    break;
                case -1:
                    Toast.makeText(CommentActivity.this,"error",Toast.LENGTH_SHORT).show();
                    Log.d("Comment","error init");
                    break;
                case -2:
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(CommentActivity.this, "本店无此内容", Toast.LENGTH_SHORT).show();
                    break;

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        title = (TextView)findViewById(R.id.title_text);
        btn_back = (Button)findViewById(R.id.title_back);
        listView = (ListView)findViewById(R.id.comment_list);
        score = (TextView) findViewById(R.id.score);
        checkBox = (CheckBox) findViewById(R.id.checkBox);
        shopScore = (TextView)findViewById(R.id.comment_shopScore);
        sendScore = (TextView)findViewById(R.id.comment_sendScore);
        shopGrade = (Grade)findViewById(R.id.comment_shopGrade);
        sendGrade = (Grade)findViewById(R.id.comment_sendGrade);
        context = (LinearLayout)findViewById(R.id.comment_context);
        progressBar = (ProgressBar)findViewById(R.id.comment_progressbar);
        check = true;
        commentsAll = new ArrayList<Comment>();
        commentsContext = new ArrayList<Comment>();
        title.setText("店铺评价");
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                check = isChecked;
                updateList();
            }
        });
        init();
    }

    public void init() {
        context.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        commentsAll.clear();
        commentsContext.clear();
        listView.setAdapter(null);
        ShopPresenter sp = new ShopPresenter();
        sumSendScore = 0;
        sumShopScore = 0;
        length = 0;
        sp.getCommentList(Globalvariable.SHOP_ID, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {

                try {
                    JSONArray ja = new JSONArray(response);
                    if (ja.length() != 0) {
                        Log.d("Comment", ja.toString());
                        ArrayList<Comment> commentsAlReply = new ArrayList<Comment>();
                        length = ja.length();
                        for (int i = 0; i < ja.length(); i++) {
                            Comment comment = new Gson().fromJson(ja.getString(i), Comment.class);
                            sumSendScore += (double) comment.getSendGrade();
                            sumShopScore += (double) comment.getShopGrade();
                            if(!comment.getReply().equals("")) commentsAlReply.add(comment);
                            else {
                                commentsAll.add(comment);
                                if (!comment.getCommentText().equals("")) {
                                    commentsContext.add(comment);
                                }
                            }
                        }
                        for(int i = 0;i<commentsAlReply.size();i++) {
                            commentsAll.add(commentsAlReply.get(i));
                            if (!commentsAlReply.get(i).getCommentText().equals("")) {
                                commentsContext.add(commentsAlReply.get(i));
                            }
                        }
                        Message msg = new Message();
                        msg.what = 1;
                        handler.sendMessage(msg);
                    } else {
                        Message msg = new Message();
                        msg.what = -2;
                        handler.sendMessage(msg);
                    }
                } catch (Exception e) {
                    Log.d("CommentFragment", e.toString());
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
                Log.d("CommentFragment", e.toString());
                Message msg = new Message();
                msg.what = -1;
                handler.sendMessage(msg);

            }
        });
    }

    private void initScore(){
        sendScore.setText(sumSendScore/length+"");
        shopScore.setText(sumShopScore/length+"");
        score.setText((sumShopScore+sumSendScore)/length/2+"");
        sendGrade.setGrade((int)(sumSendScore/length));
        shopGrade.setGrade((int)(sumShopScore/length));

    }

    private void updateList(){
        if(check){
            commentItemAdapter = new CommentItemAdapter(CommentActivity.this,R.layout.listview_item_comment,
                    commentsContext,true);
        }else {
            commentItemAdapter = new CommentItemAdapter(CommentActivity.this,R.layout.listview_item_comment,
                    commentsAll,true);
        }
        listView.setAdapter(commentItemAdapter);
        Utility.setListViewHeightBasedOnChildren(listView);
    }

}
