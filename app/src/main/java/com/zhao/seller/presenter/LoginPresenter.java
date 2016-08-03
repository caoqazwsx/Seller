package com.zhao.seller.presenter;

import android.graphics.Bitmap;
import android.util.Log;


import com.zhao.seller.globalvariable.Globalvariable;
import com.zhao.seller.globalvariable.Utility;
import com.zhao.seller.httpconnection.HttpCallbackListener;
import com.zhao.seller.httpconnection.ServerResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;


/**
 * Created by zhao on 2016/4/16.
 */
public class LoginPresenter extends BasePresenter{

    public void loginCheck(String account,String password, final HttpCallbackListener listener){
        ServerResponse sr = new ServerResponse();
        String address = Globalvariable.SERVER_ADDRESS+"type=sellerLogin&account="+account+"&password="+password;
        sr.getStringResponse(address, new HttpCallbackListener() {
            @Override
            public void onFinish(Bitmap bm){}
            @Override
            public void onFinish(InputStream in) {}
            @Override
            public void onFinish(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String tem = jsonObject.getString("Data");
                    JSONArray res = new JSONArray(tem);
                    if (listener != null) {
                        if (res.length() != 0)
                            listener.onFinish(res.getJSONObject(0).getString("shopId"));
                        else
                            listener.onFinish("error");
                    }
                } catch (Exception e) {
                    if (listener != null) {
                        listener.onError(e);
                    }

                }
            }

            @Override
            public void onError(Exception e) {
                if (listener != null) {
                    listener.onError(e);
                }
            }
        });
    }
    public void register(String account,String password, String name,String idNumber,final HttpCallbackListener listener){

        final JSONObject user = new JSONObject();
        final JSONObject info = new JSONObject();     //前台数据存储
        JSONObject tabledata = new JSONObject();
        try {
            tabledata.put("account",Utility.encode(account));
            tabledata.put("password",password);
            tabledata.put("role","seller");
            user.put("table","user");
            user.put("data",tabledata);
            tabledata =  new JSONObject();
            tabledata.put("account",account);
            tabledata.put("name",Utility.encode(name));
            tabledata.put("idNumber",idNumber);
            info.put("table","SellerInfo");
            info.put("data",tabledata);

        }catch (Exception e){
            Log.d("register",e.toString());
        }

     String address = Globalvariable.SERVER_ADDRESS+"type=register&user="+Utility.encode(user.toString())+
             "&info="+Utility.encode(info.toString());
        update(address,listener);

    }

}
