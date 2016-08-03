package com.zhao.seller.model;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zhao.seller.R;
import com.zhao.seller.activity.ComplainActivity;
import com.zhao.seller.activity.FormInfoActivity;
import com.zhao.seller.globalvariable.Utility;
import com.zhao.seller.httpconnection.HttpCallbackListener;
import com.zhao.seller.presenter.ShopPresenter;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhao on 2016/6/5.
 */
public class ComplainItemAdapter extends ArrayAdapter<Complain> {

    private boolean isHideAccount;
    private int resourceId;
    private Viewhandler viewhandler;
    private List<View> views = new ArrayList<View>();
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    ComplainActivity ca = (ComplainActivity) getContext();
                    ca.init();
                    break;
                case -1:
                    Toast.makeText(getContext(), "confirm error", Toast.LENGTH_SHORT).show();
                    break;

            }
        }
    };

    public ComplainItemAdapter(Context context, int textViewResourceId,
                               ArrayList<Complain> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;

    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final Complain complain = getItem(position);
        View view;
        if (views.size() <= position) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, null);
            views.add(position, view);
            viewhandler = new Viewhandler();
            viewhandler.account = (TextView) view.findViewById(R.id.complain_item_account);
            viewhandler.time = (TextView) view.findViewById(R.id.complain_item_time);
            viewhandler.formId = (TextView) view.findViewById(R.id.complain_item_formId);
            viewhandler.complain_text = (TextView) view.findViewById(R.id.complain_item_text);
            viewhandler.handle_text = (TextView) view.findViewById(R.id.complain_item_handleText);
            viewhandler.btn_confirm = (Button)view.findViewById(R.id.complain_item_btn_confirm);
            viewhandler.form = (LinearLayout) view.findViewById(R.id.complain_form);
            init(viewhandler, complain);
            view.setTag(viewhandler);
        } else {
            view = views.get(position);
            viewhandler = (Viewhandler) view.getTag();
        }
        return view;
    }

    private void init(final Viewhandler viewhandler, final Complain complain) {

        viewhandler.account.setText(Utility.hideAccount(complain.getBuyerAccount()));
        viewhandler.time.setText(complain.getTime());
        viewhandler.formId.setText("订单："+complain.getFormId());
        viewhandler.complain_text.setText(complain.getComplainText());
        viewhandler.handle_text.setText("处理意见：" + complain.getHandleText());
        viewhandler.btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShopPresenter sp = new ShopPresenter();
                sp.confirmComplain(complain.getId(), new HttpCallbackListener() {
                    @Override
                    public void onFinish(String response) {
                        if (response.equals("success")) {
                            Message msg = new Message();
                            msg.what = 1;
                            handler.sendMessage(msg);
                        } else {
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
                        Log.d("ComplainItemAdapter", e.toString());
                        Message msg = new Message();
                        msg.what = -1;
                        handler.sendMessage(msg);
                    }
                });
            }
        });
        viewhandler.form.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(getContext(), FormInfoActivity.class);
                it.putExtra("formId",complain.getFormId());
                getContext().startActivity(it);
            }
        });
    }


    class Viewhandler {
        TextView account;
        TextView formId;
        TextView complain_text;
        TextView handle_text;
        TextView time;
        LinearLayout form;
        Button btn_confirm;
    }
}