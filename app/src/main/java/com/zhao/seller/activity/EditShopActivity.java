package com.zhao.seller.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.zhao.seller.R;
import com.zhao.seller.globalvariable.Globalvariable;
import com.zhao.seller.globalvariable.PictureUtil;
import com.zhao.seller.globalvariable.Utility;
import com.zhao.seller.httpconnection.HttpCallbackListener;
import com.zhao.seller.model.*;

import com.zhao.seller.presenter.ShopPresenter;
import com.zhao.seller.tablemodel.Shop;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;

public class EditShopActivity extends BaseActivity {
    private Shop shop;

    private TextView title;
    private Button back;

    private ImageView shopIcon;
    private Button btn_setIcon;
    private Bitmap uploadIcon;
    private EditText shopName;
    private EditText telephone;
    private TextView address;

    private EditText deliveryTime;
    private EditText serviceTime;
    private RadioGroup deliveryService;
    private String strDeliveryService;
    private EditText botPrice;

    private ListView listDiscount;
    private ArrayList<Discount> discounts;
    private Button btn_addDiscount;
    private EditText specialOffer;

    private Button btn_submit;



    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                   if(shop != null){
                       initShopInfo();
                   }
                    break;
                case 2:
                    Bitmap bm = (Bitmap)msg.obj;
                    shopIcon.setImageBitmap(bm);
                    break;
                case 3:
                    Toast.makeText(EditShopActivity.this, "提交成功", Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                case -1:
                    Toast.makeText(EditShopActivity.this, "init error", Toast.LENGTH_SHORT).show();
                    break;
                case -2:
                    Toast.makeText(EditShopActivity.this, "icon error", Toast.LENGTH_SHORT).show();
                    break;
                case -3:
                    Toast.makeText(EditShopActivity.this, "submit error", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_shop);
        title = (TextView) findViewById(R.id.title_text);
        back = (Button)findViewById(R.id.title_back);
        shopIcon = (ImageView)findViewById(R.id.editShopInfo_shopIcon);
        btn_setIcon = (Button)findViewById(R.id.editShopInfo_btn_setIcon);
        shopName = (EditText)findViewById(R.id.editShopInfo_shopName);
        telephone = (EditText)findViewById(R.id.editShopInfo_telephone);
        address = (TextView)findViewById(R.id.editShopInfo_address);
        deliveryTime = (EditText)findViewById(R.id.editShopInfo_deliveryTime);
        serviceTime = (EditText)findViewById(R.id.editShopInfo_serviceTime);
        deliveryService = (RadioGroup) findViewById(R.id.editShopInfo_deliveryService);
        botPrice = (EditText)findViewById(R.id.editShopInfo_botPrice);
        listDiscount = (ListView)findViewById(R.id.editShopInfo_discount);
        btn_addDiscount = (Button)findViewById(R.id.editShopInfo_add_discount);
        specialOffer = (EditText)findViewById(R.id.editShopInfo_specialOffer) ;
        btn_submit = (Button)findViewById(R.id.editShopInfo_submit);
        title.setText("商铺信息");
        strDeliveryService = "Seller";
        discounts = new ArrayList<Discount>();
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        deliveryService.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.editShopInfo_rbtn_seller) strDeliveryService = "Seller";
                else if(checkedId == R.id.editShopInfo_rbtn_others) strDeliveryService ="Others";
            }
        });
        btn_setIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");//相片类型
                startActivityForResult(intent, 0);
            }
        });
        btn_addDiscount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                discounts.add(new Discount(0,0));
                initListDiscount();
            }
        });
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!checkShopInput()) return ;
                ShopPresenter sp = new ShopPresenter();
                setShop();
                sp.submitShopInfo(shop,uploadIcon , new HttpCallbackListener() {
                    @Override
                    public void onFinish(String response) {
                        if(!response.equals("error")){
                            Globalvariable.SHOP_ID = Integer.parseInt(response);
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
                        Log.d("EditShopActivity",e.toString());
                        Message msg = new Message();
                        msg.what = -3;
                        handler.sendMessage(msg);

                    }
                });
            }
        });
        if(Globalvariable.SHOP_ID == 0) {
            address.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent it = new Intent(EditShopActivity.this, LocationActivity.class);
                    startActivityForResult(it,1);
                }
            });
        }
        init();

    }

    public void init(){
        if(Globalvariable.SHOP_ID == 0){
            shop = new Shop();
            shop.setId(0);
            shop.setSellerAccount(Globalvariable.ACCOUNT);
            shop.setVerifyState("WaitPass");
            shop.setState("rest");
            return;
        }
        ShopPresenter sp = new ShopPresenter();
        sp.getShop(new HttpCallbackListener() {
           @Override
           public void onFinish(String response) {
               try{
                   JSONArray tem = new JSONArray(response);
                   if(tem.length() != 0){
                       JSONObject jo = tem.getJSONObject(0);
                       jo.put("discount",jo.getString("discount"));
                       shop = new Gson().fromJson(jo.toString(),Shop.class);
                   }
                   Message msg = new Message();
                   msg.what = 1;
                   handler.sendMessage(msg);
               }catch (Exception e){
                   Log.d("EditShopActivity",e.toString());
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
               Log.d("EditShopActivity",e.toString());
               Message msg = new Message();
               msg.what = -1;
               handler.sendMessage(msg);
           }
       });
    }

    public void initShopInfo(){
        if(!shop.getShopIcon().equals(""))  updatePicture(shop.getShopIcon());
        shopName.setText(shop.getShopName());
        telephone.setText(shop.getTelephone());
        address.setText(shop.getAddress());
        deliveryTime.setText(String.valueOf(shop.getDeliveryTimes()));
        serviceTime.setText(shop.getServiceTime());
        if(shop.getDeliveryService().equals("Seller")){
            deliveryService.check(R.id.editShopInfo_rbtn_seller);
        }else{
            deliveryService.check(R.id.editShopInfo_rbtn_others);
        }
        botPrice.setText(String.valueOf(shop.getBotPrice()));
        try {
            JSONArray discountArray = new JSONArray(shop.getDiscount());
            for(int i = 0;i<discountArray.length();i++){
                JSONArray discount = discountArray.getJSONArray(i);
                discounts.add(new Discount(discount.getDouble(0),discount.getDouble(1)));
            }
        }catch (Exception e){
            Log.d("EditShopActivity",e.toString());
        }
        initListDiscount();
    }

    public void initListDiscount(){
        DiscountItemAdapter discountItemAdapter = new DiscountItemAdapter(EditShopActivity.this,R.layout.listview_item_discount,discounts);
        listDiscount.setAdapter(discountItemAdapter);
        Utility.setListViewHeightBasedOnChildren(listDiscount);
    }

    private void setShop(){
        shop.setShopName(shopName.getText().toString());
        shop.setTelephone(telephone.getText().toString());
        shop.setDeliveryTimes(Integer.parseInt(deliveryTime.getText().toString()));
        shop.setServiceTime(serviceTime.getText().toString());
        shop.setDeliveryService(strDeliveryService);
        shop.setBotPrice(Double.parseDouble(botPrice.getText().toString()));
        shop.setSpecialOffer(specialOffer.getText().toString());
        setDiscount();
    }

    private void setDiscount(){
        for(int i = 0; i<discounts.size(); i++){
            for(int j = i+1;j<discounts.size();j++){
                if(discounts.get(j).getEnough() < discounts.get(i).getEnough()){
                    Discount tem = discounts.get(i);
                    discounts.set(i,discounts.get(j));
                    discounts.set(j,tem);
                }
            }
        }
        JSONArray strDiscounts = new JSONArray();
        for(int i = 0; i< discounts.size(); i++){
            JSONArray strDiscount = new JSONArray();
            try {
                strDiscount.put(discounts.get(i).getEnough());
                strDiscount.put(discounts.get(i).getreduce());
            }catch (Exception e){
                Log.d("setDiscount",e.toString());
            }
            strDiscounts.put(strDiscount);
        }
        shop.setDiscount(strDiscounts.toString());
    }

    private boolean checkDiscountInput(){
        for(int i = 0; i < discounts.size(); i++){
            Discount discount1 = discounts.get(i);
            for(int j = 0; j<discounts.size();j++){
                if(j == i) continue;
                Discount discount2 = discounts.get(j);
                if(discount2.getEnough() == 0||discount2.getreduce() == 0) return false;
                else if((discount2.getEnough() > discount1.getEnough())&&(discount2.getreduce() <= discount1.getreduce())) return false;
                else if(discount1.getEnough() == discount2.getEnough()) return false;
                else if(discount2.getEnough() < discount1.getEnough()&&(discount2.getreduce() >= discount1.getreduce())) return false;
            }
        }
        return true;
    }

    private boolean checkShopInput(){
        if(!checkDiscountInput()){
            Toast.makeText(EditShopActivity.this, "减满输入错误，请检查", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(shopName.getText().toString().equals("")||botPrice.getText().toString().equals("")||telephone.getText().toString().equals("")||
                deliveryTime.getText().toString().equals("")||serviceTime.getText().toString().equals("")){
            Toast.makeText(EditShopActivity.this, "有必填信息为空，请检查", Toast.LENGTH_SHORT).show();
            return false;
        }
        try {
            Integer.parseInt(deliveryTime.getText().toString());
            Double.parseDouble(botPrice.getText().toString());
        }catch (Exception e){
            Toast.makeText(EditShopActivity.this, "输入数据类型或者格式错误", Toast.LENGTH_SHORT).show();
            return  false;
        }
      return true;

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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {


            if(data == null) return;
            Uri uri = data.getData();
            try{


                Bitmap photoBmp = PictureUtil.getBitmapFormUri(EditShopActivity.this,uri);
                //读取原图旋转角度
                File file = PictureUtil.getFileFromMediaUri(EditShopActivity.this,uri);
                int degree = PictureUtil.getBitmapDegree(file.getAbsolutePath());
                /**
                 * 把图片旋转为正的方向
                 */
                uploadIcon = PictureUtil.rotateBitmapByDegree(photoBmp, degree);
                shopIcon.setImageBitmap(uploadIcon);

            }catch (Exception e){
                Log.d("EditShopActivity", e.getMessage());
                e.printStackTrace();
            }
        }else if(requestCode == 1){
            if (resultCode == RESULT_OK) {
                boolean state = data.getBooleanExtra("state",false);
                if(state){
                    Log.d("Location",data.getDoubleExtra("lat",0)+"  "+getIntent().getDoubleExtra("lng",0));
                    shop.setLocation(data.getDoubleExtra("lng",0)+"," +data.getDoubleExtra("lat",0)+"");
                    shop.setAddress(data.getStringExtra("name"));
                    address.setText(data.getStringExtra("name"));

                }else{
                    Toast.makeText(EditShopActivity.this, "获取位置失败",
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


}
