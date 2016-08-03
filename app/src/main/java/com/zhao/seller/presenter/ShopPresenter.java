package com.zhao.seller.presenter;

import android.graphics.Bitmap;
import android.util.Base64;
import android.util.Log;

import com.google.gson.Gson;
import com.zhao.seller.globalvariable.Globalvariable;
import com.zhao.seller.globalvariable.Utility;
import com.zhao.seller.httpconnection.HttpCallbackListener;
import com.zhao.seller.httpconnection.ServerResponse;
import com.zhao.seller.model.Food;
import com.zhao.seller.model.Bill;
import com.zhao.seller.tablemodel.Shop;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * Created by zhao on 2016/5/18.
 */
public class ShopPresenter extends BasePresenter {


    public void getBalance(final HttpCallbackListener listener){
        String address = Globalvariable.SERVER_ADDRESS+"type=select&sql="+ Utility.encode("from Coffer where account='"+Globalvariable.ACCOUNT+"'");
        getList(address,listener);
    }

    public void cash(double cash,final HttpCallbackListener listener){
        Bill bill = new Bill();
        bill.setAccount(Globalvariable.ACCOUNT);
        bill.setIncomeAndExpenses(-1*cash);
        bill.setNote("Cash");
        bill.setTime(Utility.getStringNowTime());
        JSONObject intable = new JSONObject();
        try {
            intable.put("table","Bill");
            intable.put("data",new Gson().toJson(bill));
        }catch (Exception e){
            e.printStackTrace();
        }
        String address = Globalvariable.SERVER_ADDRESS+"type=insert&inserData="+Utility.encode(intable.toString());
        update(address,listener);

    }

    public void getFoodList(int shop_id, final HttpCallbackListener listener) {
        String address = Globalvariable.SERVER_ADDRESS + "type=select&sql=" + Utility.encode("from Food where shopId=" + shop_id+" order by foodSales desc");
        getList(address, listener);
    }

    public void getShop(final HttpCallbackListener listener){
        String address = Globalvariable.SERVER_ADDRESS + "type=select&sql="+Utility.encode("from Shop where id="+Globalvariable.SHOP_ID);
        getList(address,listener);
    }

    public void submitShopInfo(Shop shop, Bitmap icon, final HttpCallbackListener listener){
        JSONObject tableShop = new JSONObject();
        try {
            JSONObject jo = new JSONObject(new Gson().toJson(shop));
            jo.put("shopName",Utility.encode(jo.getString("shopName")));
            jo.put("specialOffer",Utility.encode(jo.getString("specialOffer")));
            jo.put("address",Utility.encode(jo.getString("address")));
            tableShop.put("table","shop");
            tableShop.put("data",jo.toString());

        }catch (Exception e){
            Log.d("submitShopInfo",e.toString());
        }

        String address ="http://"+ Globalvariable.ServerIP +"/android_Server/requestAccept.action";
        ServerResponse sr = new ServerResponse();
        String output = "type=editShopInfo&shop="+Utility.encode(tableShop.toString())+"&uploadIcon="+Utility.encode(bitmapToString(icon));
        sr.getStringPostResponse(address,output,new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String tem = jsonObject.getString("Data");
                    Log.d("update", "" + tem);
                    // JSONArray res = new JSONArray(tem);
                    if (listener != null) {
                        listener.onFinish(tem);
                    }
                } catch (Exception e) {
                    if (listener != null) {
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
                if (listener != null) {
                    listener.onError(e);
                }

            }
        });

    }

    public void changeShopState(String currentState,final HttpCallbackListener listener){
        String resState = null;
        if(currentState.equals("rest")) resState = "active";
        else if(currentState.equals("active")) resState = "rest";
        else resState = "rest";

        String address = Globalvariable.SERVER_ADDRESS + "type=update&sql="
                +Utility.encode("update Shop set state='"+resState+"' where id="+Globalvariable.SHOP_ID);
        update(address,listener);

    }

    public void getFood(int foodId,final HttpCallbackListener listener){
        String address = Globalvariable.SERVER_ADDRESS + "type=select&sql="+Utility.encode("from Food where id="+foodId);
        getList(address,listener);
    }

    public void submitFoodInfo(Food food,Bitmap icon,final HttpCallbackListener listener){
        JSONObject tableFood = new JSONObject();
        try {
            JSONObject jo = new JSONObject(new Gson().toJson(food));
            jo.put("foodName",Utility.encode(jo.getString("foodName")));
            jo.put("note",Utility.encode(jo.getString("note")));
            tableFood.put("table","food");
            tableFood.put("data",jo.toString());

        }catch (Exception e){
            Log.d("submitFoodInfo",e.toString());
        }

        String address ="http://"+ Globalvariable.ServerIP +"/android_Server/requestAccept.action";
        ServerResponse sr = new ServerResponse();
        String output = "type=editFoodInfo&food="+Utility.encode(tableFood.toString())+"&uploadIcon="+Utility.encode(bitmapToString(icon));
        sr.getStringPostResponse(address,output,new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String tem = jsonObject.getString("Data");
                    Log.d("update", "" + tem);
                    // JSONArray res = new JSONArray(tem);
                    if (listener != null) {
                        listener.onFinish(tem);
                    }
                } catch (Exception e) {
                    if (listener != null) {
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
                if (listener != null) {
                    listener.onError(e);
                }

            }
        });
    }

    public void deleteFood(Food food,final HttpCallbackListener listener){
        JSONObject tableFood = new JSONObject();
        try {
            JSONObject jo = new JSONObject(new Gson().toJson(food));
            jo.put("foodName",Utility.encode(jo.getString("foodName")));
            jo.put("note",Utility.encode(jo.getString("note")));
            tableFood.put("table","food");
            tableFood.put("data",jo.toString());

        }catch (Exception e){
            Log.d("deleteFood",e.toString());
        }
        String address = Globalvariable.SERVER_ADDRESS+"type=deleteFood&food="+Utility.encode(tableFood.toString());
        update(address,listener);
    }

    public void getCommentList(int shopId,final HttpCallbackListener listener){
        String address = Globalvariable.SERVER_ADDRESS+"type=select&sql="+Utility.encode("from Comment where shopId="+shopId+" order by commentTime desc");
        getList(address,listener);
    }

    public void submitCommentReply(int commentId,String reply,final HttpCallbackListener listener){
        String address = Globalvariable.SERVER_ADDRESS + "type=update&sql="
                +Utility.encode("update Comment set reply='"+Utility.encode(reply)+"' where id="+commentId);
        update(address,listener);
    }

    public void getComplainList(int shopId,final HttpCallbackListener listener){
        String address = Globalvariable.SERVER_ADDRESS+"type=select&sql="
                +Utility.encode("from Complain where shopId="+shopId+" and complainState='WaitConfirm' order by time desc");
        getList(address,listener);
    }

    public void confirmComplain(int complainId,final HttpCallbackListener listener){
        String address = Globalvariable.SERVER_ADDRESS + "type=update&sql="
                +Utility.encode("update Complain set complainState='Finish' where id="+complainId);
        update(address,listener);
    }

    private String bitmapToString(Bitmap bitmap){
        if(bitmap == null) return "null";
        String string = null;
        ByteArrayOutputStream bStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bStream);
        byte[] bytes = bStream.toByteArray();
        string = Base64.encodeToString(bytes, Base64.DEFAULT);
        return string;
    }
}
