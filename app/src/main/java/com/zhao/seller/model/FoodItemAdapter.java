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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.zhao.seller.R;
import com.zhao.seller.activity.EditFoodActivity;
import com.zhao.seller.activity.FoodActivity;
import com.zhao.seller.httpconnection.HttpCallbackListener;
import com.zhao.seller.presenter.ShopPresenter;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhao on 2016/4/26.
 */

public class FoodItemAdapter extends ArrayAdapter<Food> {
    private int currentUpdatePicturePos;
    private int resourceId;
    private Viewhandler viewhandler;
    private List<View> views = new ArrayList<View>();
    private Handler handler = new Handler(){
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    Bitmap bm = (Bitmap)msg.obj;
                    int pos = msg.arg1;
                //    Log.d("FoodListItem", "updpicture: " + pos);
                    ImageView imageView = (ImageView)views.get(pos).findViewById(R.id.food_icon);
                    imageView.setImageBitmap(bm);
                    currentUpdatePicturePos++;
                    break;
                case 2:
                    FoodActivity fa = (FoodActivity)getContext();
                    fa.init();
                    break;
                case -1:
                    Log.d("FoodItemAdapter", "update icon error:"+ msg.arg1);
                    break;
                case -2:
                    Log.d("FoodItemAdapter", "delete error");
                    break;

            }
        }
    };
    public FoodItemAdapter(Context context, int textViewResourceId,
                           ArrayList<Food> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
        currentUpdatePicturePos = 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent){

        final Food foodItem = getItem(position);
        View view;
        if(views.size() <= position){
           // Log.d("foodlist", "position= " + position);
            view = LayoutInflater.from(getContext()).inflate(resourceId,null);
            views.add(position,view);
            // Log.d("ShopListItem", "" + (position - start) + "  size =" + views.size());
            viewhandler = new Viewhandler();
            viewhandler.food_name = (TextView)view.findViewById(R.id.food_name);
            viewhandler.market_price = (TextView)view.findViewById(R.id.market_price);
            viewhandler.food_icon = (ImageView)view.findViewById(R.id.food_icon);
            viewhandler.sales = (TextView)view.findViewById(R.id.food_sales);
            viewhandler.price = (TextView)view.findViewById(R.id.price);
            viewhandler.note = (TextView)view.findViewById(R.id.note);
            viewhandler.remain = (TextView)view.findViewById(R.id.remain);
            viewhandler.editFood = (Button)view.findViewById(R.id.edit_food);
            viewhandler.deleteFood = (Button)view.findViewById(R.id.delete_food);
            init(viewhandler,foodItem,position);
            view.setTag(viewhandler);
        }
        else{
          //  Log.d("list","positionfood= "+position);
            view = views.get(position);
            viewhandler = (Viewhandler)view.getTag();
        }

        return view;
    }

    private void init(final Viewhandler viewhandler,final Food foodItem,final int position){

        viewhandler.food_name.setText(foodItem.getFoodName());
        viewhandler.price.setText("￥"+foodItem.getPrice()+"/份");
        updatePicture(foodItem.getFoodIcon(), position);
        viewhandler.sales.setText("销售量：" + foodItem.getFoodSales());
        viewhandler.market_price.setText("门市价" + foodItem.getMarketPrice());
        if(foodItem.getNote().equals("")){
            viewhandler.note.setVisibility(View.GONE);
        }else {
            viewhandler.note.setText( foodItem.getNote());
        }
        viewhandler.remain.setText("剩余："+foodItem.getRemain());
        viewhandler.editFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(getContext(), EditFoodActivity.class);
                it.putExtra("foodId",foodItem.getId());
                getContext().startActivity(it);
            }
        });
        viewhandler.deleteFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                dialog.setTitle("警告");
                dialog.setMessage("食物删除后不可恢复，是否确认删除？");
                dialog.setCancelable(false);
                dialog.setPositiveButton("删除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ShopPresenter sp = new ShopPresenter();
                        Log.d("FoodItemAdapter",foodItem.getId()+"");
                        sp.deleteFood(foodItem,new HttpCallbackListener() {
                            @Override
                            public void onFinish(String response) {
                                if(response.equals("success")){
                                    Message msg = new Message();
                                    msg.what = 2;
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
                                Log.d("FoodItemAdapter",e.toString());
                                Message msg = new Message();
                                msg.what = -1;
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
    }

    private void updatePicture(final String path, final int position){

        new Thread(new Runnable() {
            @Override
            public void run() {
                while(currentUpdatePicturePos != position);
        ShopPresenter sp = new ShopPresenter();
        sp.getPicture(path, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {

            }

            @Override
            public void onFinish(InputStream in) {

            }

            @Override
            public void onFinish(Bitmap bm) {
                Message msg = new Message();
                msg.what = 1;
                msg.arg1 = position;
                msg.obj = bm;
                handler.sendMessage(msg);

            }

            @Override
            public void onError(Exception e) {
                Log.d("FoodListItem", e.toString());
                Message msg = new Message();
                msg.what = -1;
                msg.arg1 = position;
                msg.obj = e.toString();
                handler.sendMessage(msg);
            }
        });
            }
        }).start();
    }

    class Viewhandler{
        TextView food_name;
        ImageView food_icon;
        TextView sales;
        TextView market_price;
        TextView price;
        TextView note;
        TextView remain;
        Button editFood;
        Button deleteFood;


    }
}


