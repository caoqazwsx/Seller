package com.zhao.seller.model;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.ListViewCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.zhao.seller.R;
import com.zhao.seller.activity.MainActivity;
import com.zhao.seller.fragment.NewFormFragment;
import com.zhao.seller.globalvariable.Globalvariable;
import com.zhao.seller.globalvariable.Utility;
import com.zhao.seller.httpconnection.HttpCallbackListener;
import com.zhao.seller.presenter.FormPresenter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by zhao on 2016/5/1.
 */
public class NewFormItemAdapter extends ArrayAdapter<Form> {

    private int resourceId;
    private Viewhandler viewhandler;
    private ArrayList<View> views = new ArrayList<View>();


    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    MainActivity ma = (MainActivity)getContext();
                    NewFormFragment nff = (NewFormFragment) ma.getFragment("newForm");
                    nff.init();
                    break;

                case -1:
                    Toast.makeText(getContext(),"接单失败",Toast.LENGTH_SHORT).show();
                    break;
                case -2:
                  Toast.makeText(getContext(),"error",Toast.LENGTH_SHORT).show();
                    break;


            }
        }
    };


    public NewFormItemAdapter(Context context, int textViewResourceId,
                              ArrayList<Form> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

       final Form formItem = getItem(position);
        View view;
        if (views.size() <= position) {
           // Log.d("NewFormItemAdapter", "position= " + position);
            view = LayoutInflater.from(getContext()).inflate(resourceId, null);
            views.add(position, view);
            viewhandler = new Viewhandler();
            viewhandler.newforms_item_formId = (TextView) view.findViewById(R.id.newforms_item_formId);
            viewhandler.newforms_item_state = (TextView)view.findViewById(R.id.newforms_item_state);
            viewhandler.newforms_item_address = (TextView)view.findViewById(R.id.newforms_item_address) ;
            viewhandler.newforms_item_telephone = (TextView)view.findViewById(R.id.newforms_item_telephone);
            viewhandler.newforms_item_time = (TextView)view.findViewById(R.id.newforms_item_time);
            viewhandler.newforms_item_total_price = (TextView)view.findViewById(R.id.newforms_item_total_price);
            viewhandler.newforms_item_list_food = (ListView) view.findViewById(R.id.newforms_item_list_food);
            viewhandler.newforms_item_cancel = (Button) view.findViewById(R.id.newforms_item_cancel);
            viewhandler.newforms_item_accept = (Button) view.findViewById(R.id.newforms_item_accept);
            viewhandler.newforms_item_back = (Button)view.findViewById(R.id.newforms_item_back);
            viewhandler.newforms_item_noBack = (Button)view.findViewById(R.id.newforms_item_noBack);
            viewhandler.backFormNote = (LinearLayout)view.findViewById(R.id.newforms_item_backFromNote);
            viewhandler.backReason = (TextView)view.findViewById(R.id.newforms_item_backReason);

            init(formItem,position);
            view.setTag(viewhandler);
        } else {
           // Log.d("NewFormItemAdapter", "position= " + position);
            view = views.get(position);
            viewhandler = (Viewhandler) view.getTag();
        }
        return view;
    }

    private void init(final Form formItem,int position){
        viewhandler.newforms_item_formId.setText(formItem.getId()+"");
        viewhandler.newforms_item_state.setText( parseFormState(formItem.getFormState()));
        viewhandler.newforms_item_address.setText("地址："+formItem.getFormAddress());
        viewhandler.newforms_item_telephone.setText("电话："+formItem.getTelephone());
        viewhandler.newforms_item_time.setText(formItem.getSubmitTime());
        ArrayList<CartFoodItem> cartFoodItems = getFoodList(formItem.getFormFood());
        FormFoodItemAdapter formFoodItemAdapter = new  FormFoodItemAdapter(getContext(), R.layout.listview_item_form_food, cartFoodItems);
        viewhandler.newforms_item_list_food.setAdapter(formFoodItemAdapter);
        Utility.setListViewHeightBasedOnChildren( viewhandler.newforms_item_list_food);
        viewhandler.newforms_item_total_price.setText("￥"+formItem.getPayPrice());

        viewhandler.newforms_item_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                dialog.setTitle("警告");
                dialog.setMessage("订单取消后不可更改，是否确认取消订单？");
                dialog.setCancelable(false);
                dialog.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FormPresenter fp = new FormPresenter();
                        Log.d("NewFormItemAdapter",formItem.getId()+"");
                        fp.cancelForm(formItem.getId(),new HttpCallbackListener() {
                            @Override
                            public void onFinish(String response) {
                                if(response.equals("success")){
                                    Message msg = new Message();
                                    msg.what = 1;
                                    handler.sendMessage(msg);
                                }else{
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
                                Log.d("NewFormItemAdapter",e.toString());
                                Message msg = new Message();
                                msg.what = -2;
                                handler.sendMessage(msg);
                            }
                        });

                    }
                });
                dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                dialog.show();

            }
        });
        viewhandler.newforms_item_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FormPresenter fp = new FormPresenter();
                fp.acceptForm(formItem.getId(),new HttpCallbackListener() {
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
                        Log.d("NewFormItemAdapter",e.toString());
                        Message msg = new Message();
                        msg.what = -1;
                        handler.sendMessage(msg);
                    }
                });


            }
        });
        viewhandler.newforms_item_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                dialog.setTitle("警告");
                dialog.setMessage("订单退单后不可更改，是否确认退单？");
                dialog.setCancelable(false);
                dialog.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FormPresenter fp = new FormPresenter();
                        Log.d("NewFormItemAdapter",formItem.getId()+"");
                        fp.cancelForm(formItem.getId(),new HttpCallbackListener() {
                            @Override
                            public void onFinish(String response) {
                                if(response.equals("success")){
                                    Message msg = new Message();
                                    msg.what = 1;
                                    handler.sendMessage(msg);
                                }else{
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
                                Log.d("NewFormItemAdapter",e.toString());
                                Message msg = new Message();
                                msg.what = -2;
                                handler.sendMessage(msg);
                            }
                        });

                    }
                });
                dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                dialog.show();
            }
        });
        viewhandler.newforms_item_noBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FormPresenter fp = new FormPresenter();
                Log.d("NewFormItemAdapter",formItem.getId()+"");
                fp.noBackForm(formItem.getId(),new HttpCallbackListener() {
                    @Override
                    public void onFinish(String response) {
                        if(response.equals("success")){
                            Message msg = new Message();
                            msg.what = 1;
                            handler.sendMessage(msg);
                        }else{
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
                        Log.d("NewFormItemAdapter",e.toString());
                        Message msg = new Message();
                        msg.what = -2;
                        handler.sendMessage(msg);
                    }
                });

            }
        });
       checkFormState(formItem);

    }


    private void checkFormState(Form formItem){

        if (formItem.getFormState().equals(Globalvariable.WAIT_PAY)){
            viewhandler.newforms_item_accept.setVisibility(View.GONE);
        }else if(formItem.getFormState().equals(Globalvariable.WAIT_BACK)){
            viewhandler.newforms_item_accept.setVisibility(View.GONE);
            viewhandler.newforms_item_back.setVisibility(View.VISIBLE);
            viewhandler.backFormNote.setVisibility(View.VISIBLE);
            viewhandler.newforms_item_noBack.setVisibility(View.VISIBLE);
            viewhandler.backReason.setText(formItem.getNote());
        }

    }

    private ArrayList<CartFoodItem> getFoodList(String formFood){
        ArrayList<CartFoodItem> cartFoodItems = new ArrayList<CartFoodItem>();
        try {
            JSONArray ja = new JSONArray(formFood);
            for(int i =0 ; i<ja.length();i++) {
                JSONObject jb = ja.getJSONObject(i);
                cartFoodItems.add(new CartFoodItem(jb.getInt("foodId"), jb.getString("foodName"), jb.getInt("foodNum"),
                        jb.getDouble("foodPrice"), jb.getDouble("foodTotalPrice")));
            }
        }catch (Exception e){
            Log.d("NewFormItemAdapter",e.toString());
        }
        return cartFoodItems;
    }

    private String parseFormState(String state){

        if(state.equals(Globalvariable.WAIT_ACCEPT)) return "待接单";
        else if(state.equals(Globalvariable.WAIT_PAY)) return "待支付";
        else if(state.equals(Globalvariable.FINISH)) return "订单完成";
        else if(state.equals(Globalvariable.CANCEL)) return "已取消";
        else if(state.equals(Globalvariable.WAIT_ARRIVED)) return "待送达";
        else if(state.equals(Globalvariable.WAIT_COMMENT)) return "待评价";
        else if(state.equals(Globalvariable.WAIT_BACK))  return "待退单";
        return "";
    }

    class Viewhandler {
        TextView newforms_item_formId;
        TextView newforms_item_address;
        TextView newforms_item_telephone;
        TextView newforms_item_time;
        TextView newforms_item_state;
        TextView newforms_item_total_price;
        ListView newforms_item_list_food;
        Button newforms_item_cancel;
        Button newforms_item_accept;
        Button newforms_item_back;
        Button newforms_item_noBack;
        LinearLayout backFormNote;
        TextView backReason;

    }

}
