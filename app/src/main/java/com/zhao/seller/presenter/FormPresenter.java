package com.zhao.seller.presenter;

import android.graphics.Bitmap;
import android.util.Log;


import com.zhao.seller.globalvariable.Globalvariable;
import com.zhao.seller.globalvariable.Utility;
import com.zhao.seller.httpconnection.HttpCallbackListener;
import com.zhao.seller.model.CartFoodItem;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by zhao on 2016/5/1.
 */
public class FormPresenter extends BasePresenter {



    public void getFormInfo(long id,final HttpCallbackListener listener){
        String address = Globalvariable.SERVER_ADDRESS + "type=select&sql=" + Utility.encode("from Form where id ="+id);
        getList(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                try {
                    Log.d("FormInfo Data",response);
                    JSONObject jb = new JSONArray(response).getJSONObject(0);
                    jb.put("formFood",jb.getString("formFood"));
                    String strForm = jb.toString();
                    if(listener!=null) {
                        listener.onFinish(strForm);
                    }
                }catch (Exception e){
                    if(listener!=null) {
                        listener.onError(e);
                    }

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
                if(listener!=null) {
                    listener.onError(e);
                }

            }
        });
    }

    public void getSenderInfo(String senderAccount,final HttpCallbackListener listener) {
        String address = Globalvariable.SERVER_ADDRESS + "type=select&sql=" +
                Utility.encode("from SenderInfo where senderAccount="+senderAccount);
        getList(address, listener);
    }

    public void cancelForm(long id, final HttpCallbackListener listener) {
        String address = Globalvariable.SERVER_ADDRESS + "type=sellerCancelform&formId=" + id;
        update(address, listener);
    }

    public void acceptForm(long id,final HttpCallbackListener listener){
        String address = Globalvariable.SERVER_ADDRESS + "type=update&sql=" +
                Utility.encode("update Form set formState='WaitArrived' where id="+id);
        update(address, listener);
    }

    public void noBackForm(long id,final HttpCallbackListener listener){
        String address = Globalvariable.SERVER_ADDRESS + "type=update&sql=" +
                Utility.encode("update Form set formState='WaitArrived' where id="+id);
        update(address, listener);
    }

    public void getNewFormList(int shopId, final HttpCallbackListener listener) {
        String address = Globalvariable.SERVER_ADDRESS + "type=select&sql=" +
                Utility.encode("from Form where shopId="+shopId+" and " + "(formState='WaitPay' or formState='WaitAccept' or formState='WaitBack') order by submitTime desc");
        getList(address, listener);
    }

    public void getProcessedFormList(int shopId,final HttpCallbackListener listener){
        String address = Globalvariable.SERVER_ADDRESS + "type=select&sql=" +
                Utility.encode("from Form where shopId="+shopId+" and formState='"+Globalvariable.WAIT_ARRIVED+"' order by submitTime desc");
        getList(address, listener);
    }

    public void editSendState(String senderAccount,String sendState,long formId,final HttpCallbackListener listener){
        String address = Globalvariable.SERVER_ADDRESS + "type=update&sql=" +
                Utility.encode("update Form set sendState='"+sendState+"',senderAccount='"+senderAccount+"' where id="+formId);
        update(address,listener);

    }

    public ArrayList<CartFoodItem> getFoodList(String formFood){
        ArrayList<CartFoodItem> cartFoodItems = new ArrayList<CartFoodItem>();
        try {
            JSONArray ja = new JSONArray(formFood);
            for(int i =0 ; i<ja.length();i++) {
                JSONObject jb = ja.getJSONObject(i);
                cartFoodItems.add(new CartFoodItem(jb.getInt("foodId"), jb.getString("foodName"), jb.getInt("foodNum"),
                        jb.getDouble("foodPrice"), jb.getDouble("foodTotalPrice")));
            }
        }catch (Exception e){
            Log.d("FormInfoActivty",e.toString());
        }
        return cartFoodItems;
    }


}

