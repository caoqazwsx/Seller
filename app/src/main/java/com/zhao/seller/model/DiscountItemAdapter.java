package com.zhao.seller.model;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;

import com.zhao.seller.R;
import com.zhao.seller.activity.EditShopActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhao on 2016/5/31.
 */
public class DiscountItemAdapter extends ArrayAdapter<Discount>{
    private int resourceId;
    private Viewhandler viewhandler;
    private List<View> views = new ArrayList<View>();

    private Handler handler = new Handler(){
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:

                    break;
                case -1:
                    Log.d("FoodListItem handler", "error:"+ msg.arg1);
                    break;

            }
        }
    };
    public DiscountItemAdapter(Context context, int textViewResourceId,
                           ArrayList<Discount> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent){

        final Discount discount = getItem(position);
        View view;
        if(views.size() <= position){
            // Log.d("foodlist", "position= " + position);
            view = LayoutInflater.from(getContext()).inflate(resourceId,null);
            views.add(position,view);
            // Log.d("ShopListItem", "" + (position - start) + "  size =" + views.size());
            viewhandler = new Viewhandler();

            viewhandler.enough = (EditText) view.findViewById(R.id.list_discount_enough);
            viewhandler.reduce = (EditText) view.findViewById(R.id.list_discount_reduce);
            viewhandler.delete = (Button)view.findViewById(R.id.list_discount_delete);
            if(!(discount.getEnough() == 0&&discount.getreduce() == 0)) {
                viewhandler.enough.setText(discount.getEnough() + "");
                viewhandler.reduce.setText(discount.getreduce() + "");
            }
            viewhandler.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    remove(discount);
                    EditShopActivity esa = (EditShopActivity) getContext();
                    esa.initListDiscount();
                }
            });
            viewhandler.enough.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    try {
                        discount.setEnough(Double.parseDouble(s.toString()));
                        Log.d("discount",s.toString());
                    }catch (Exception e){
                        discount.setEnough(0);
                    }

                }
            });
            viewhandler.reduce.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    try {
                        discount.setReduce(Double.parseDouble(s.toString()));
                        Log.d("discount",s.toString());
                    } catch (Exception e) {
                        discount.setReduce(0);
                    }

                }
            });
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
        EditText enough;
        EditText reduce;
        Button delete;
    }
}
