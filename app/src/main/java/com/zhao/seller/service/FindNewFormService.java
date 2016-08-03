package com.zhao.seller.service;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Binder;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;

import com.google.gson.Gson;
import com.zhao.seller.R;
import com.zhao.seller.activity.MainActivity;
import com.zhao.seller.broadcastReceiver.AlarmReceiver;
import com.zhao.seller.globalvariable.Globalvariable;
import com.zhao.seller.httpconnection.HttpCallbackListener;
import com.zhao.seller.model.Form;
import com.zhao.seller.presenter.FormPresenter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class FindNewFormService extends Service {
    protected int size;
    private ArrayList<Form> formitemsSearch;
    private Controller mController;

    public FindNewFormService() {
        formitemsSearch = new ArrayList<Form>();
        mController = new Controller();
        size = -1;

    }

    @Override
    public IBinder onBind(Intent intent) {
      return mController;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("FindNewFormService", "ServiceStart1");
        formitemsSearch.clear();
        FormPresenter fp = new FormPresenter();//通过服务器端获取订单信息
        if(size != -1) {
            fp.getNewFormList(Globalvariable.SHOP_ID, new HttpCallbackListener() {
                @Override
                public void onFinish(String response) {
                    if (response != null) {
                        try {
                            JSONArray ja = new JSONArray(response);
                            ArrayList<Form> formItemsWaitPay = new ArrayList<Form>();
                            ArrayList<Form> formItemsWaitBack = new ArrayList<Form>();
                            for (int i = 0; i < ja.length(); i++) {
                                JSONObject jb = ja.getJSONObject(i);
                                jb.put("formFood", jb.getString("formFood"));
                                Form form = new Gson().fromJson(jb.toString(), Form.class);
                                if (form.getFormState().equals(Globalvariable.WAIT_ACCEPT)) {
                                    formitemsSearch.add(form);
                                } else if (form.getFormState().equals(Globalvariable.WAIT_PAY)) {
                                    formItemsWaitPay.add(form);
                                } else if (form.getFormState().equals(Globalvariable.WAIT_BACK)) {
                                    formItemsWaitBack.add(form);
                                }
                            }
                            for (int i = 0; i < formItemsWaitBack.size(); i++) {
                                formitemsSearch.add(formItemsWaitBack.get(i));
                            }
                            for (int i = 0; i < formItemsWaitPay.size(); i++) {
                                formitemsSearch.add(formItemsWaitPay.get(i));
                            }
                            Log.d("FormFragment size", "before:" + size);
                            if (formitemsSearch.size() > size) {
                                size = formitemsSearch.size();
                                showNotify();
                            } else {
                                size = formitemsSearch.size();
                            }
                            Log.d("FormFragment size", "after:" + size);

                        } catch (Exception e) {
                            e.printStackTrace();
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
                    e.printStackTrace();
                }
            });
        }
        Log.d("FindNewFormService", "ServiceStart2");
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent it = new Intent(this, AlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(this,1,it,PendingIntent.FLAG_CANCEL_CURRENT);
        long triggerAtTime = SystemClock.elapsedRealtime() + 5000;
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
        Log.d("FindNewFormService", "ServiceStart3");
        return super.onStartCommand(intent, flags, startId);
    }

    public void showNotify() {
        //第一步：获取NotificationManager
        NotificationManager nm = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);

        ///// 第二步：定义Notification
        Intent intent = new Intent(this, MainActivity.class);
        //PendingIntent是待执行的Intent
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        Notification notification = new Notification.Builder(this)
                .setContentTitle("商家")
                .setContentText("您有新的订单")
                .setSmallIcon(R.drawable.r0)
                .setContentIntent(pi)
                .build();
        // notification.flags = Notification.FLAG_NO_CLEAR;
        notification.defaults=Notification.DEFAULT_SOUND;

        /////第三步：启动通知栏，第一个参数是一个通知的唯一标识
        nm.notify(0, notification);

        //关闭通知
        //nm.cancel(0);
    }

    public class Controller extends Binder {

        public void setSize(int size){
            FindNewFormService.this.size = size;
        }
        public ArrayList<Form> getFormList(){
            return formitemsSearch;
        }

    }
}
