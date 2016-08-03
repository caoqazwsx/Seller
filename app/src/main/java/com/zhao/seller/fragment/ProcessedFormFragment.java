package com.zhao.seller.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
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
import com.zhao.seller.custom.DownUpListView;
import com.zhao.seller.globalvariable.Globalvariable;
import com.zhao.seller.httpconnection.HttpCallbackListener;
import com.zhao.seller.model.Form;
import com.zhao.seller.model.ProcessedFormItemAdapter;
import com.zhao.seller.presenter.FormPresenter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;


public class ProcessedFormFragment extends Fragment {
    private ArrayList<Form> formitems;
    private View rootView;
    private DownUpListView listView;

    private ProgressBar progressBar;

    private ProcessedFormItemAdapter formItemAdapter;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    listView.onRefreshComplete();
                    formItemAdapter = new ProcessedFormItemAdapter(getContext(), R.layout.listview_item_processed_form,formitems);
                    progressBar.setVisibility(View.GONE);
                    listView.setAdapter(formItemAdapter);
                    listView.setDividerHeight(10);
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(rootView == null){
            Log.d("FormFragment","rootview == null");
            View view = inflater.inflate(R.layout.fragment_processed_form, container, false);
            progressBar = (ProgressBar)view.findViewById(R.id.progress_bar);
            listView = (DownUpListView) view.findViewById(R.id.list_processed_forms);
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

        return rootView;
    }

    //订单模块可编辑，此方法用于检测处于状态并对UI加以改变


    public void init() {
        formitems.clear();
        listView.setAdapter(null);
        FormPresenter fp = new FormPresenter();//通过服务器端获取订单信息
        fp.getProcessedFormList(Globalvariable.SHOP_ID,new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                if (response != null) {
                    try {
                        JSONArray ja = new JSONArray(response);
                        ArrayList<Form> formItemsAlSingSend = new  ArrayList<Form>();
                        for (int i = 0; i < ja.length(); i++) {
                            JSONObject jb = ja.getJSONObject(i);
                            jb.put("formFood",jb.getString("formFood"));
                            Form form = new Gson().fromJson(jb.toString(),Form.class);
                            if(form.getSendState().equals("OFF")||form.getSendState().equals("")) {
                                formitems.add(form);
                            }else{
                                formItemsAlSingSend.add(form);
                            }
                        }
                        for(int i=0;i<formItemsAlSingSend.size();i++){
                            formitems.add(formItemsAlSingSend.get(i));
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
    @Override
    public void onResume(){
        if(!isHidden()) {
            listView.setAdapter(null);
            init();
        }
        super.onResume();
    }
}
