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
import android.widget.ImageView;
import android.widget.TextView;

import com.zhao.seller.R;
import com.zhao.seller.activity.EditFoodActivity;
import com.zhao.seller.httpconnection.HttpCallbackListener;
import com.zhao.seller.presenter.ShopPresenter;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhao on 2016/6/2.
 */
public class FoodSalesItemAdapter  extends ArrayAdapter<Food> {
    private int resourceId;
    private Viewhandler viewhandler;
    private List<View> views = new ArrayList<View>();

    public FoodSalesItemAdapter(Context context, int textViewResourceId,
                           ArrayList<Food> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent){

        final Food foodItem = getItem(position);
        View view;
        if(views.size() <= position){

            view = LayoutInflater.from(getContext()).inflate(resourceId,null);
            views.add(position,view);

            viewhandler = new Viewhandler();
            viewhandler.food_name = (TextView)view.findViewById(R.id.food_name);
            viewhandler.sales = (TextView)view.findViewById(R.id.food_sales);
            viewhandler.price = (TextView)view.findViewById(R.id.food_price);
            viewhandler.food_name.setText(foodItem.getFoodName());
            viewhandler.price.setText("￥"+foodItem.getPrice());
            viewhandler.sales.setText("" + foodItem.getFoodSales());
            view.setTag(viewhandler);
        }
        else{
            //  Log.d("list","positionfood= "+position);
            view = views.get(position);
            viewhandler = (Viewhandler)view.getTag();
        }

        return view;
    }





    class Viewhandler{
        TextView food_name;
        TextView sales;
        TextView price;

    }
}
