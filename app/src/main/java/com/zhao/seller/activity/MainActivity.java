package com.zhao.seller.activity;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.zhao.seller.R;
import com.zhao.seller.custom.FragmentTabHost;
import com.zhao.seller.fragment.MyShopFragment;
import com.zhao.seller.fragment.NewFormFragment;
import com.zhao.seller.fragment.ProcessedFormFragment;

public class MainActivity extends BaseActivity {

    private FragmentTabHost mTabHost;
    private int exitTime;

    //定义一个布局
    private LayoutInflater layoutInflater;

    //定义数组来存放Fragment界面
    private Class fragmentArray[] = {NewFormFragment.class,ProcessedFormFragment.class, MyShopFragment.class};

    //定义数组来存放按钮图片
    private int mImageViewArray[] = {R.drawable.tab_new_form, R.drawable.tab_processed_form,R.drawable.tab_shop};

    //Tab选项卡的Tag
    private String mTextviewArray[] = {"newForm", "proForm", "shop"};



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        exitTime = 0;
    }


    /**
     * 初始化组件
     */
    private void initView() {
        //实例化布局对象
        layoutInflater = LayoutInflater.from(this);
        //实例化TabHost对象，得到TabHost
        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);

        //得到fragment的个数
        int count = mTextviewArray.length;

        for (int i = 0; i < count; i++) {
            //为每一个Tab按钮设置图标、文字和内容
            TabHost.TabSpec tabSpec = mTabHost.newTabSpec(mTextviewArray[i]).setIndicator(getTabItemView(i));
            //将Tab按钮添加进Tab选项卡中
            mTabHost.addTab(tabSpec, fragmentArray[i], null);
            //设置Tab按钮的背景
            //mTabHost.getTabWidget().getChildAt(i).setBackgroundResource(R.drawable.lb);
        }

    }

    /**
     * 给Tab按钮设置图标和文字
     */
    private View getTabItemView(int index) {

        View view = layoutInflater.inflate(R.layout.tabhost_item_view, null);

        ImageView imageView = (ImageView) view.findViewById(R.id.imageview);
        imageView.setImageResource(mImageViewArray[index]);

        TextView textView = (TextView) view.findViewById(R.id.tabitemtext);
        textView.setText(mTextviewArray[index]);

        return view;
    }

    public Fragment getFragment(String tag){
        // Log.d("FoodList",fragmentTag.get(index));
        return getSupportFragmentManager().findFragmentByTag(tag);
    }

    @Override
    public void onBackPressed(){
        if(exitTime == 0){
            Toast.makeText(MainActivity.this,"再按一次退出",Toast.LENGTH_SHORT).show();
            exittiming();
        }else {
            super.onBackPressed();
        }
    }

    private void exittiming(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(exitTime < 3){
                    exitTime++;
                    try {
                        Thread.sleep(1000);
                    }catch (Exception e){

                    }
                }
                exitTime = 0;
            }
        }).start();
    }

}
