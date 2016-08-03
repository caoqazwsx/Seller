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
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.zhao.seller.R;
import com.zhao.seller.globalvariable.Globalvariable;
import com.zhao.seller.globalvariable.PictureUtil;
import com.zhao.seller.httpconnection.HttpCallbackListener;
import com.zhao.seller.model.Food;
import com.zhao.seller.presenter.ShopPresenter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.InputStream;

public class EditFoodActivity extends BaseActivity {
    private int foodId;
    private Food food;

    private TextView title;
    private Button btn_back;

    private ImageView foodIcon;
    private Bitmap uploadIcon;
    private Button btn_setIcon;

    private EditText foodName;
    private EditText price;
    private EditText marketPrice;
    private EditText remain;
    private EditText note;

    private Button btn_submit;

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    if(food != null){
                        initFoodInfo();
                    }
                    break;
                case 2:
                    Bitmap bm = (Bitmap)msg.obj;
                    foodIcon.setImageBitmap(bm);
                    break;
                case 3:
                    Toast.makeText(EditFoodActivity.this, "提交成功", Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                case -1:
                    Toast.makeText(EditFoodActivity.this, "init error", Toast.LENGTH_SHORT).show();
                    break;
                case -2:
                    Toast.makeText(EditFoodActivity.this, "icon error", Toast.LENGTH_SHORT).show();
                    break;
                case -3:
                    Toast.makeText(EditFoodActivity.this, "submit error", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_food);

        title = (TextView)findViewById(R.id.title_text);
        btn_back = (Button)findViewById(R.id.title_back);

        foodIcon = (ImageView)findViewById(R.id.editFoodInfo_foodIcon);
        foodName = (EditText)findViewById(R.id.editFoodInfo_foodName);
        price = (EditText)findViewById(R.id.editFoodInfo_price);
        marketPrice = (EditText)findViewById(R.id.editFoodInfo_marketPrice);
        remain = (EditText)findViewById(R.id.editFoodInfo_remain);
        note = (EditText)findViewById(R.id.editFoodInfo_note);
        btn_submit = (Button)findViewById(R.id.editFoodInfo_submit);
        btn_setIcon = (Button)findViewById(R.id.editFoodInfo_btn_setIcon);

        foodId = getIntent().getIntExtra("foodId",0);
        title.setText("食物信息");
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!setFood()) return;
                ShopPresenter sp = new ShopPresenter();
                sp.submitFoodInfo(food, uploadIcon, new HttpCallbackListener() {
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
                        Log.d("EditShopActivity",e.toString());
                        Message msg = new Message();
                        msg.what = -3;
                        handler.sendMessage(msg);

                    }
                });
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
        init();
    }

    public void init(){
        if(foodId == 0) {
            food = new Food();
            food.setId(0);
            food.setFoodSales(0);
            food.setShopId(Globalvariable.SHOP_ID);
            return;
        }
        ShopPresenter sp = new ShopPresenter();
        sp.getFood(foodId,new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                try{
                    JSONArray tem = new JSONArray(response);
                    if(tem.length() != 0){
                        JSONObject jo = tem.getJSONObject(0);
                        food = new Gson().fromJson(jo.toString(),Food.class);
                    }
                    Message msg = new Message();
                    msg.what = 1;
                    handler.sendMessage(msg);
                }catch (Exception e){
                    Log.d("EditFoodActivity",e.toString());
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

    public void initFoodInfo(){
        if(!food.getFoodIcon().equals(""))  updatePicture(food.getFoodIcon());
        foodName.setText(food.getFoodName());
        price.setText(food.getPrice()+"");
        marketPrice.setText(food.getMarketPrice()+"");
        remain.setText(food.getRemain()+"");
        note.setText(food.getNote());
    }

    public boolean setFood(){
        if(foodName.toString().equals("")||price.getText().toString().equals("")||remain.getText().toString().equals("")){
            Toast.makeText(EditFoodActivity.this, "有必填信息为空，请检查", Toast.LENGTH_SHORT).show();
            return false;
        }
        food.setFoodName(foodName.getText().toString());
        try{
            food.setPrice(Double.parseDouble(price.getText().toString()));
            food.setMarketPrice(Double.parseDouble(marketPrice.getText().toString()));
            food.setRemain(Integer.parseInt(remain.getText().toString()));
        }catch (Exception e){
            Toast.makeText(EditFoodActivity.this, "输入数据类型或者格式错误", Toast.LENGTH_SHORT).show();
            return  false;
        }
        food.setNote(note.getText().toString());
        return  true;
    }

    public void updatePicture(final String path) {
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
                Log.d("EditFoodActivity", e.toString());
                Message msg = new Message();
                msg.what = -2;
                handler.sendMessage(msg);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            Uri uri = data.getData();
            try{
                Bitmap photoBmp = PictureUtil.getBitmapFormUri(EditFoodActivity.this,uri);
                //读取原图旋转角度
                File file = PictureUtil.getFileFromMediaUri(EditFoodActivity.this,uri);
                int degree = PictureUtil.getBitmapDegree(file.getAbsolutePath());
                /**
                 * 把图片旋转为正的方向
                 */
                uploadIcon = PictureUtil.rotateBitmapByDegree(photoBmp, degree);
                foodIcon.setImageBitmap(uploadIcon);

            }catch (Exception e){
                Log.d("EditFoodActivity", e.getMessage());
                e.printStackTrace();
            }
        }
    }


}
