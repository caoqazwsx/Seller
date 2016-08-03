package com.zhao.seller.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.zhao.seller.R;
import com.zhao.seller.activity.CashActivity;
import com.zhao.seller.activity.CommentActivity;
import com.zhao.seller.activity.ComplainActivity;
import com.zhao.seller.activity.EditShopActivity;
import com.zhao.seller.activity.FoodActivity;
import com.zhao.seller.activity.HelpActivity;
import com.zhao.seller.activity.SalesCountActivity;
import com.zhao.seller.globalvariable.Globalvariable;
import com.zhao.seller.httpconnection.HttpCallbackListener;
import com.zhao.seller.presenter.ShopPresenter;
import com.zhao.seller.tablemodel.Shop;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;


public class MyShopFragment extends Fragment {
    private Shop shop;

    private View rootView;

    private Button btn_newShop;
    private Button btn_changeState;

    private ImageView shopIcon;
    private TextView shopName;

    private LinearLayout updBalance;
    private TextView balance;
    private TextView cash;

    private LinearLayout shopInfo;
    private LinearLayout shopFood;
    private LinearLayout shopSales;
    private LinearLayout shopComment;
    private LinearLayout shopComplain;
    private LinearLayout help;

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    checkShopState();
                    break;
                case 2:
                    Bitmap bm = (Bitmap)msg.obj;
                    shopIcon.setImageBitmap(bm);
                    initBalance();
                    break;
                case 3:
                    init();
                    break;
                case 4:
                    balance.setText("￥"+(String)msg.obj);
                    break;
                case -1:
                    Toast.makeText(getContext(), "init error", Toast.LENGTH_SHORT).show();
                    break;
                case -2:
                    Toast.makeText(getContext(), "icon error", Toast.LENGTH_SHORT).show();
                    break;
                case -3:
                    Toast.makeText(getContext(), "change state error", Toast.LENGTH_SHORT).show();
                    break;
                case -4:
                    Toast.makeText(getContext(), "initBalance error", Toast.LENGTH_SHORT).show();
                    break;

            }
        }
    };



    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_my_shop, container, false);
            btn_newShop = (Button)rootView.findViewById(R.id.my_shop_newShop);
            shopIcon = (ImageView) rootView.findViewById(R.id.my_shop_shopIcon);
            shopName = (TextView) rootView.findViewById(R.id.my_shop_shopName);
            balance = (TextView)rootView.findViewById(R.id.my_shop_balance);
            updBalance = (LinearLayout)rootView.findViewById(R.id.my_shop_updBalance);
            cash = (TextView)rootView.findViewById(R.id.my_shop_cash);
            shopInfo = (LinearLayout) rootView.findViewById(R.id.my_shop_shopInfo);
            shopFood = (LinearLayout) rootView.findViewById(R.id.my_shop_food);
            shopSales = (LinearLayout) rootView.findViewById(R.id.my_shop_salesCount);
            shopComment = (LinearLayout)rootView.findViewById(R.id.my_shop_Comment);
            shopComplain = (LinearLayout)rootView.findViewById(R.id.my_shop_complain);
            help = (LinearLayout)rootView.findViewById(R.id.my_shop_help);
            btn_changeState = (Button)rootView.findViewById(R.id.my_shop_changeState);

            updBalance.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    initBalance();
                }
            });

            cash.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent it = new Intent(getActivity(), CashActivity.class);
                    getActivity().startActivity(it);
                }
            });

            shopInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent it = new Intent(getActivity(), EditShopActivity.class);
                    getActivity().startActivity(it);
                }
            });
            shopFood.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent it = new Intent(getActivity(), FoodActivity.class);
                    getActivity().startActivity(it);
                }
            });
            shopSales.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent it = new Intent(getActivity(), SalesCountActivity.class);
                    getActivity().startActivity(it);
                }
            });
            shopComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent it = new Intent(getActivity(), CommentActivity.class);
                    getActivity().startActivity(it);
                }
            });
            shopComplain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent it = new Intent(getActivity(), ComplainActivity.class);
                    getActivity().startActivity(it);
                }
            });
            help.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent it = new Intent(getActivity(), HelpActivity.class);

                    getActivity().startActivity(it);
                }
            });
            btn_newShop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent it = new Intent(getContext(),EditShopActivity.class);
                    startActivity(it);
                }
            });
            btn_changeState.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ShopPresenter sp = new ShopPresenter();
                    sp.changeShopState(shop.getState(), new HttpCallbackListener() {
                        @Override
                        public void onFinish(String response) {
                            if(response.equals("success")){
                                Message msg = new Message();
                                msg.what = 3;
                                handler.sendMessage(msg);
                            }else {
                                Message msg = new Message();
                                msg.what = -3;
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
                            Log.d("MyShopFragment",e.toString());
                            Message msg = new Message();
                            msg.what = -3;
                            handler.sendMessage(msg);

                        }
                    });
                }
            });


        } else {
            Log.d("mineFragment", "rootview != null");
            // 缓存的rootView需要判断是否已经被加过parent，如果有parent需要从parent删除，要不然会发生这个rootview已经有parent的错误。
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if (parent != null) {
                Log.d("mineFragment", "parent != null");
                parent.removeView(rootView);
            }
        }
        return rootView;
    }

    public void init(){
        if (Globalvariable.SHOP_ID == 0) {
            btn_newShop.setVisibility(View.VISIBLE);
            btn_changeState.setVisibility(View.GONE);
            shopInfo.setClickable(false);
            shopFood.setClickable(false);
            shopSales.setClickable(false);
            shopName.setText("您没有开设商铺");
        } else {
            btn_newShop.setVisibility(View.GONE);
            ShopPresenter sp = new ShopPresenter();
            sp.getShop(new HttpCallbackListener() {
                @Override
                public void onFinish(String response) {
                    try {
                        JSONArray tem = new JSONArray(response);
                        if (tem.length() != 0) {
                            JSONObject jo = tem.getJSONObject(0);
                            jo.put("discount", jo.getString("discount"));
                            shop = new Gson().fromJson(jo.toString(), Shop.class);
                        }
                        Message msg = new Message();
                        msg.what = 1;
                        handler.sendMessage(msg);
                    } catch (Exception e) {
                        Log.d("EditShopActivity", e.toString());
                        Message msg = new Message();
                        msg.what = -1;
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
                    Log.d("EditShopActivity", e.toString());
                    Message msg = new Message();
                    msg.what = -1;
                    handler.sendMessage(msg);
                }
            });
        }
    }

    public void initBalance(){

        ShopPresenter sp = new ShopPresenter();
        sp.getBalance(new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {

                try {
                    JSONArray ja = new JSONArray(response);
                    if (ja.length() != 0) {
                        JSONObject jo = ja.getJSONObject(0);
                        Message msg = new Message();
                        msg.what = 4;
                        msg.obj = jo.getString("balance");
                        handler.sendMessage(msg);
                    } else {
                        Message msg = new Message();
                        msg.what = -4;
                        handler.sendMessage(msg);
                    }
                } catch (Exception e) {
                    Log.d("MyShopFragment1", e.toString());
                    Message msg = new Message();
                    msg.what = -4;
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
                Log.d("MyShopFragment2",e.toString());
                Message msg = new Message();
                msg.what = -1;
                handler.sendMessage(msg);

            }
        });
    }

    private void checkShopState(){
        if(shop != null){
            if(shop.getVerifyState().equals("Pass")) {
                shopInfo.setClickable(true);
                shopFood.setClickable(true);
                shopSales.setClickable(true);
                shopName.setText(shop.getShopName());
                if(shop.getState().equals("rest")) btn_changeState.setText("休息中");
                else if(shop.getState().equals("active")) btn_changeState.setText("营业中");
                btn_changeState.setVisibility(View.VISIBLE);
                updatePicture(shop.getShopIcon());
            }else{
                btn_changeState.setVisibility(View.GONE);
                shopInfo.setClickable(true);
                shopFood.setClickable(false);
                shopSales.setClickable(false);
                shopName.setText(shop.getShopName()+"(未通过验证)");
                updatePicture(shop.getShopIcon());
            }
        }
    }

    private void updatePicture(final String path) {
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
                msg.what = 2;
                msg.obj = bm;
                handler.sendMessage(msg);

            }

            @Override
            public void onError(Exception e) {
                Log.d("ShopListItem1111", e.toString());
                Message msg = new Message();
                msg.what = -2;
                handler.sendMessage(msg);
            }
        });

    }

    @Override
    public void onResume(){
        super.onResume();
        if(!isHidden()){
            init();
        }
    }

}
