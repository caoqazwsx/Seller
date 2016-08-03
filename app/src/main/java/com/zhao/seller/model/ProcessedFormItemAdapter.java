package com.zhao.seller.model;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.zhao.seller.R;
import com.zhao.seller.activity.EditSendActivity;
import com.zhao.seller.activity.MainActivity;
import com.zhao.seller.fragment.NewFormFragment;
import com.zhao.seller.fragment.ProcessedFormFragment;
import com.zhao.seller.globalvariable.Globalvariable;
import com.zhao.seller.globalvariable.Utility;
import com.zhao.seller.httpconnection.HttpCallbackListener;
import com.zhao.seller.presenter.FormPresenter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by zhao on 2016/5/16.
 */
public class ProcessedFormItemAdapter extends ArrayAdapter<Form> {

    private int resourceId;
    private Viewhandler viewhandler;
    private ArrayList<View> views = new ArrayList<View>();


    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    MainActivity ma = (MainActivity)getContext();
                    ProcessedFormFragment pff = ( ProcessedFormFragment) ma.getFragment("proForm");
                    pff.init();
                    break;

                case -1:
                    Toast.makeText(getContext(),"接单失败",Toast.LENGTH_SHORT).show();
                    break;
                case -2:
                    Toast.makeText(getContext(),"取消订单失败",Toast.LENGTH_SHORT).show();
                    break;


            }
        }
    };


    public ProcessedFormItemAdapter(Context context, int textViewResourceId,
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
            viewhandler.proforms_item_formId = (TextView) view.findViewById(R.id.proforms_item_formId);
            viewhandler.proforms_item_state = (TextView)view.findViewById(R.id.proforms_item_state);
            viewhandler.proforms_item_address = (TextView)view.findViewById(R.id.proforms_item_address) ;
            viewhandler.proforms_item_telephone = (TextView)view.findViewById(R.id.proforms_item_telephone);
            viewhandler.proforms_item_time = (TextView)view.findViewById(R.id.proforms_item_time);
            viewhandler.proforms_item_total_price = (TextView)view.findViewById(R.id.proforms_item_total_price);
            viewhandler.proforms_item_list_food = (ListView) view.findViewById(R.id.proforms_item_list_food);
            viewhandler.proforms_item_cancel = (Button) view.findViewById(R.id.proforms_item_cancel);
            viewhandler.proforms_item_setSendState = (Button) view.findViewById(R.id.proforms_item_set_sendstate);

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
        viewhandler.proforms_item_formId.setText(formItem.getId()+"");
        viewhandler.proforms_item_state.setText( parseFormState(formItem.getFormState()));
        viewhandler.proforms_item_address.setText("地址："+formItem.getFormAddress());
        viewhandler.proforms_item_telephone.setText("电话："+formItem.getTelephone());
        viewhandler.proforms_item_time.setText(formItem.getSubmitTime());
        ArrayList<CartFoodItem> cartFoodItems = getFoodList(formItem.getFormFood());
        FormFoodItemAdapter formFoodItemAdapter = new  FormFoodItemAdapter(getContext(), R.layout.listview_item_form_food, cartFoodItems);
        viewhandler.proforms_item_list_food.setAdapter(formFoodItemAdapter);
        Utility.setListViewHeightBasedOnChildren( viewhandler.proforms_item_list_food);
        viewhandler.proforms_item_total_price.setText("￥"+formItem.getPayPrice());
        viewhandler.proforms_item_cancel.setOnClickListener(new View.OnClickListener() {
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
        viewhandler.proforms_item_setSendState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(getContext(), EditSendActivity.class);
                it.putExtra("formId",formItem.getId());
                it.putExtra("senderAccount",formItem.getSenderAccount());
                getContext().startActivity(it);
            }
        });
        checkFormState(formItem);
    }



    private void checkFormState(Form formItem){
        if (formItem.getSendState().equals("ON")){
            viewhandler.proforms_item_setSendState.setText("已标记配送");
        }else if(formItem.getSendState().equals("Arrived")){
            viewhandler.proforms_item_setSendState.setText("已标记送达");
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
        TextView proforms_item_formId;
        TextView proforms_item_address;
        TextView proforms_item_telephone;
        TextView proforms_item_time;
        TextView proforms_item_state;
        TextView proforms_item_total_price;
        ListView proforms_item_list_food;
        Button proforms_item_cancel;
        Button proforms_item_setSendState;

    }
}
