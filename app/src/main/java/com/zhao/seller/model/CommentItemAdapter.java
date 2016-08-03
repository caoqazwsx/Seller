package com.zhao.seller.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zhao.seller.R;
import com.zhao.seller.activity.CommentActivity;
import com.zhao.seller.custom.Grade;
import com.zhao.seller.globalvariable.Utility;
import com.zhao.seller.httpconnection.HttpCallbackListener;
import com.zhao.seller.presenter.ShopPresenter;


import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhao on 2016/5/7.
 */
public class CommentItemAdapter extends ArrayAdapter<Comment> {

   private boolean isHideAccount;
    private int resourceId;
    private Viewhandler viewhandler;
    private List<View> views = new ArrayList<View>();
    private Handler handler = new Handler(){
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    CommentActivity ca = (CommentActivity)getContext();
                    ca.init();
                    Toast.makeText(getContext(),"回复成功",Toast.LENGTH_SHORT).show();
                    break;
                case -1:
                    Toast.makeText(getContext(),"reply error",Toast.LENGTH_SHORT).show();
                    break;

            }
        }
    };
    public CommentItemAdapter(Context context, int textViewResourceId,
                           ArrayList<Comment> objects,boolean isHideAccount) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
        this.isHideAccount = isHideAccount;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final Comment comment = getItem(position);
        View view;
        if(views.size() <= position){
            view = LayoutInflater.from(getContext()).inflate(resourceId,null);
            views.add(position,view);
            viewhandler = new Viewhandler();
            viewhandler.account = (TextView)view.findViewById(R.id.comment_item_account);
            viewhandler.icon = (ImageView)view.findViewById(R.id.comment_item_icon);
            viewhandler.comment_text = (TextView)view.findViewById(R.id.comment_item_text);
            viewhandler.comment_food= (TextView)view.findViewById(R.id.comment_item_food);
            viewhandler.comment_time= (TextView)view.findViewById(R.id.comment_item_time);
            viewhandler.comment_reply = (TextView)view.findViewById(R.id.comment_item_reply);
            viewhandler.grade = (Grade)view.findViewById(R.id.comment_item_grade);
            viewhandler.input_reply = (EditText)view.findViewById(R.id.comment_input_reply);
            viewhandler.btn_reply = (Button)view.findViewById(R.id.comment_btn_reply);
            viewhandler.layout_reply = (LinearLayout)view.findViewById(R.id.comment_layout_reply);
            init(viewhandler,comment);
            view.setTag(viewhandler);
        }
        else{
            view = views.get(position);
            viewhandler = (Viewhandler)view.getTag();
        }
        return view;
    }

    private void init(final Viewhandler viewhandler,final Comment comment){
        viewhandler.grade.setGrade(comment.getShopGrade());
        if(isHideAccount) {
            viewhandler.account.setText(Utility.hideAccount(comment.getCommentAccount()));
        }else {
            viewhandler.account.setText(comment.getCommentAccount());
        }
        viewhandler.comment_food.setText(comment.getFood());
        viewhandler.comment_time.setText(comment.getCommentTime());
        viewhandler.comment_text.setText(comment.getCommentText());
        if(!comment.getReply().equals("")){
            viewhandler.comment_reply.setVisibility(View.VISIBLE);
            viewhandler.comment_reply.setText("商家回复："+comment.getReply());
            viewhandler.layout_reply.setVisibility(View.GONE);
        }else{
            viewhandler.comment_reply.setVisibility(View.GONE);
            viewhandler.layout_reply.setVisibility(View.VISIBLE);
        }
        viewhandler.btn_reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShopPresenter sp = new ShopPresenter();
                sp.submitCommentReply(comment.getId(), viewhandler.input_reply.getText().toString(), new HttpCallbackListener() {
                    @Override
                    public void onFinish(String response) {
                        if(response.equals("success")){
                            Message msg = new Message();
                            msg.what = 1;
                            handler.sendMessage(msg);
                        }else {
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
                        Log.d("CommentItemAdapter",e.toString());
                        Message msg = new Message();
                        msg.what = -1;
                        handler.sendMessage(msg);
                    }
                });
            }
        });
    }




    class Viewhandler{
        TextView account;
        ImageView icon;
        TextView comment_text;
        TextView comment_food;
        TextView comment_time;
        TextView comment_reply;
        LinearLayout layout_reply;
        EditText input_reply;
        Button btn_reply;
        Grade grade;

    }

}
