package com.zhao.seller.presenter;

import android.util.Log;

import com.zhao.seller.globalvariable.Globalvariable;
import com.zhao.seller.globalvariable.Utility;
import com.zhao.seller.httpconnection.HttpCallbackListener;
import com.zhao.seller.httpconnection.ServerResponse;


/**
 * Created by zhao on 2016/4/28.
 */
public class PayPresenter  extends BasePresenter{

    public void pay(double price,long form_id,final HttpCallbackListener listener){
        ServerResponse sr = new ServerResponse();

        String address = "http://"+ Globalvariable.ServerIP +"/android_Server/requestAccept.action?type=pay&price="+ Utility.encode(price+"")+"&formId="
                +Utility.encode(""+form_id)+"&account="+Utility.encode(Globalvariable.ACCOUNT);
        Log.d("111PayActivity",address);
       update(address,listener);
    }

    public void getBalance(String account,final HttpCallbackListener listener){
        String address = Globalvariable.SERVER_ADDRESS+"type=select&sql="+ Utility.encode("from Coffer where account='"+account+"'");
        getList(address,listener);
    }
}
