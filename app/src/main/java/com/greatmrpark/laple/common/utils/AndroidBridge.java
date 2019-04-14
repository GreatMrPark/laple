package com.greatmrpark.laple.common.utils;

import android.os.Handler;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.greatmrpark.laple.common.database.DBHelper;
import com.greatmrpark.laple.common.log.Dlog;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AndroidBridge {

    private final Handler handler = new Handler();
    private WebView mWebView;
    private DBHelper dbHelper;
    private  boolean newtwork;

    // 생성자
    public AndroidBridge(WebView mWebView, DBHelper dbHelper, boolean newtwork) {
        this.mWebView = mWebView;
        this.dbHelper = dbHelper;
        this.newtwork = newtwork;
    }

    // DB데이터 가져오기
    @JavascriptInterface
    public void requestData() { // must be final
        handler.post(new Runnable() {
            public void run() {
                Dlog.d("데이터 요청");
                String test  =  dbHelper.getResult();
                Dlog.d(test);
                mWebView.loadUrl("javascript:getAndroidData('"+test+"')");
            }
        });
    }

    // DB에 데이터 저장하기
    @JavascriptInterface
    public void saveData(final String item, final int num) { // must be final
        handler.post(new Runnable() {
            public void run() {
                Date d = new Date();

                String s = d.toString();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

                dbHelper.insert(sdf.format(d), item, num);
                Dlog.d("데이터 저장");
                String test  =  dbHelper.getResult();
                mWebView.loadUrl("javascript:getAndroidData('"+test+"')");
            }
        });
    }
}
