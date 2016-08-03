package com.zhao.seller.fragment;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;
import com.zhao.seller.R;
import com.zhao.seller.activity.MainActivity;
import com.zhao.seller.custom.DownUpListView;
import com.zhao.seller.globalvariable.Globalvariable;
import com.zhao.seller.httpconnection.HttpCallbackListener;
import com.zhao.seller.model.Form;
import com.zhao.seller.model.NewFormItemAdapter;
import com.zhao.seller.presenter.FormPresenter;
import com.zhao.seller.service.FindNewFormService;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.ServiceConfigurationError;


public class NewFormFragment extends Fragment {
    private ArrayList<Form> formitems;
    private View rootView;
    private DownUpListView listView;
    private FindNewFormService.Controller mController;

    private ProgressBar progressBar;

    private NewFormItemAdapter formItemAdapter;



    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    updListView();
                    if(mController != null){
                        mController.setSize(formitems.size());
                    }
                    Log.d("FormFragment", "success init");
                    break;
                case -1:
                    listView.onRefreshComplete();
                    Toast.makeText(getActivity(), "请检查你的网络", Toast.LENGTH_SHORT).show();
                    break;
                case -2:
                    Toast.makeText(getActivity(), "无订单", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    break;
            }
        }
    };

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mController = (FindNewFormService.Controller)service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(rootView == null){
            Log.d("FormFragment","rootview == null");
            View view = inflater.inflate(R.layout.fragment_new_form, container, false);
            progressBar = (ProgressBar)view.findViewById(R.id.progress_bar);
            listView = (DownUpListView) view.findViewById(R.id.list_newforms);
            progressBar.setVisibility(View.VISIBLE);
            formitems = new ArrayList<Form>();
            listView.setOnRefreshListener(new DownUpListView.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    init();
                }
            });
            rootView=view;
        }
        else{
            Log.d("FormFragment","rootview != null");
            // 缓存的rootView需要判断是否已经被加过parent，如果有parent需要从parent删除，要不然会发生这个rootview已经有parent的错误。
            ViewGroup parent = (ViewGroup)rootView.getParent();
            if(parent!=null){
                Log.d("FormFragment","parent != null");
                parent.removeView(rootView);
            }
        }
        Intent it = new Intent(getContext(),FindNewFormService.class);
        getContext().bindService(it,serviceConnection,Context.BIND_AUTO_CREATE);
        getContext().startService(it);
        init();
        return rootView;
    }




    public void init() {
        formitems.clear();
        listView.setAdapter(null);
        FormPresenter fp = new FormPresenter();//通过服务器端获取订单信息
        fp.getNewFormList(Globalvariable.SHOP_ID,new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                if (response != null) {
                    try {
                        JSONArray ja = new JSONArray(response);
                        ArrayList<Form> formItemsWaitPay = new  ArrayList<Form>();
                        ArrayList<Form> formItemsWaitBack = new  ArrayList<Form>();
                        for (int i = 0; i < ja.length(); i++) {
                            JSONObject jb = ja.getJSONObject(i);
                            jb.put("formFood",jb.getString("formFood"));
                            Form form = new Gson().fromJson(jb.toString(),Form.class);
                            if(form.getFormState().equals(Globalvariable.WAIT_ACCEPT)) {
                                formitems.add(form);
                            }else if(form.getFormState().equals(Globalvariable.WAIT_PAY)){
                                formItemsWaitPay.add(form);
                            }else if(form.getFormState().equals(Globalvariable.WAIT_BACK)){
                                formItemsWaitBack.add(form);
                            }
                        }
                        for(int i=0;i<formItemsWaitBack.size();i++){
                            formitems.add(formItemsWaitBack.get(i));
                        }
                        for(int i=0;i<formItemsWaitPay.size();i++){
                            formitems.add(formItemsWaitPay.get(i));
                        }
                        Message msg = new Message();
                        msg.what = 1;
                        handler.sendMessage(msg);

                    } catch (Exception e) {
                        Log.d("FormFragment", e.toString());
                        Message msg = new Message();
                        msg.what = -1;
                        handler.sendMessage(msg);
                    }
                } else {
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
                Log.d("FormFragment", e.toString());
                Message msg = new Message();
                msg.what = -1;
                handler.sendMessage(msg);
            }
        });

    }

    public void updListView(){
        listView.onRefreshComplete();
        formItemAdapter = new NewFormItemAdapter(getContext(), R.layout.listview_item_new_form, formitems);
        progressBar.setVisibility(View.GONE);
        listView.setAdapter(formItemAdapter);
        listView.setDividerHeight(10);
    }


    public void showNewFormNotify(){
        NotificationManager notificationManager = (NotificationManager)getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent(getContext(),MainActivity.class);
      //  PendingIntent pi = PendingIntent.getActivities();
    }


    @Override
    public void onResume(){
        if(!isHidden()) {
            listView.setAdapter(null);
            init();
        }
        super.onResume();
    }

    @Override
    public void onDestroy(){
        getContext().unbindService(serviceConnection);
        Intent it = new Intent(getContext(),FindNewFormService.class);
        getContext().stopService(it);
        super.onDestroy();
    }



}
