package com.example.demo;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.os.Build.VERSION.SDK;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Status";
    private ViewGroup decorView;
    private Unbinder unbinder;

    @BindView(R.id.tool_bar)
    Toolbar toolbar;

    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    @BindView(R.id.content_home)
    View contentHome;

    @BindView(R.id.tv_content)
    TextView tvContent;

    private int statusColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        unbinder = ButterKnife.bind(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            int flag = getWindow().getDecorView().getSystemUiVisibility();
            flag |= View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
            getWindow().getDecorView().setSystemUiVisibility(flag);
            getWindow().setStatusBarColor(Color.TRANSPARENT);

        } else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        toolbar.setFitsSystemWindows(true);
        setToolBar();

        requestData();

    }

    private void requestData() {
        new AsyncTask<String,Object,String>(){
            @Override
            protected String doInBackground(String... params){
                final OkHttpClient client = new OkHttpClient.Builder()
//                                    .proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("192.168.1.102", 8080)))
                        .build();

                final Request request = new Request.Builder()
                        .url(params[0])
                        .build();
                final Call call = client.newCall(request);
                Response response;
                String result = null;
                try {
                    response = call.execute();
                    if (response.isSuccessful()){
                        result = response.body().string();
                        response.body().close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return result;

            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                tvContent.setText(s);
            }
        }.execute("https://raw.github.com/square/okhttp/master/README.md");
    }

    private void addStatusInflatedView() {
        View statusBarTintView = new View(this);
        statusBarTintView.setId(R.id.statusbar_hint);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, getStatusBarHeight(this));
        params.gravity = Gravity.TOP;
        statusBarTintView.setLayoutParams(params);
        statusBarTintView.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

        //这个做法有一点需要注意:添加到顶部的view（冒充状态栏背景）会盖住根布局的上面一条状态栏的区域，因此需要将根布局的paddingtop增加一个状态栏的高度，或者添加fitSystemWindow=true
        ((ViewGroup)getWindow().getDecorView()).addView(statusBarTintView);

//        final View container = findViewById(R.id.container);
//        container.setPadding(container.getPaddingLeft(), container.getPaddingTop() + getStatusBarHeight(this),
//                container.getPaddingRight(), container.getPaddingBottom());

    }

    private void setToolBar() {
        toolbar.setTitle(R.string.nav_home);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
//        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);//setDisplayHomeAsUpEnabled(true)之后工具栏左侧显示“箭头”返回按钮，可用

        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.app_name, R.string.app_name);
        toggle.syncState();  //toggle.syncState()之后，工具栏左侧返回按钮图标更变为“三条横线”
        drawerLayout.addDrawerListener(toggle); //drawerLayout.addDrawerListener(toggle)之后，工具栏左侧按钮图标有拖入拖出动画

    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    public int getStatusBarHeight(Context context) {
        Context appContext = context.getApplicationContext();
        int result = 0;
        int resourceId =
                appContext.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = appContext.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    private View getStatusBarInflatedView(){
        View statusBarTintView = new View(this);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, getStatusBarHeight(this));
//        params.gravity = Gravity.TOP;
        statusBarTintView.setLayoutParams(params);
        statusBarTintView.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        return statusBarTintView;
    }

    public int getStatusColor(float slideOffSet,int oldStatusColor) {
        if (slideOffSet == 0f) {
            return oldStatusColor;
        }
//        float a = 1 - alpha / 255f;
        int alpha = oldStatusColor  >> 24 & 0xff;
//        int red = oldStatusColor >> 16 & 0xff;
//        int green = oldStatusColor >> 8 & 0xff;
//        int blue = oldStatusColor & 0xff;
//        red = (int) (red * (1 - slideOffSet) + 0.5);
//        green = (int) (green * (1 - slideOffSet) + 0.5);
//        blue = (int) (blue * (1 - slideOffSet) + 0.5);
        alpha = (int)(alpha * (1- slideOffSet));

        return (alpha & 0xff000000) | (oldStatusColor & 0x00ffffff);
    }
}
