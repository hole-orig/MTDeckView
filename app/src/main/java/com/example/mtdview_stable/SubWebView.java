package com.example.mtdview_stable;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.*;
import android.webkit.CookieManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SubWebView extends AppCompatActivity {
    private WebView MTDSwv;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub);
        Intent MTDSi = getIntent();
        String MTDSu = MTDSi.getStringExtra("MTDsdata");
//        CookieManager MTDSCookieManager = CookieManager.getInstance();
//        MTDSCookieManager.setAcceptCookie(true);
//        MTDSwv.getSettings().setDomStorageEnabled(true);
        MTDSwv = findViewById(R.id.MTDsubview);
        MTDSwv.getSettings().setJavaScriptEnabled(true);
        MTDSwv.getSettings().setBuiltInZoomControls(true);
        MTDSwv.getSettings().setDisplayZoomControls(false);
        MTDSwv.getSettings().setAllowFileAccess(true);
        MTDSwv.getSettings().setLoadWithOverviewMode(true);
        MTDSwv.getSettings().setUseWideViewPort(true);
        MTDSwv.loadUrl(MTDSu);
        MTDSwv.setLongClickable(true);
        MTDSwv.setWebViewClient(new notOpen());
        registerForContextMenu(MTDSwv);
    }


    public static class notOpen extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView MTDSuwv, WebResourceRequest MTDSuwrr) {
                return false;
            //外部へはサブのビューから移動とゆーのも、外部のサイトのキャンペーンとか
            //クッキー食わせた状態でないと手間なんで
            //ただそこからさらに移動ってのはどうかと思うんで移動は外部へ

        }
    }

    private String MTDScmurl;
    @Override
    public void onCreateContextMenu(ContextMenu MTDScmcm, View MTDScmv, ContextMenu.ContextMenuInfo MTDScminfo) {
        super.onCreateContextMenu(MTDScmcm, MTDScmv, MTDScminfo);
        MTDSwv = (WebView) MTDScmv;
        WebView.HitTestResult SimgHR = MTDSwv.getHitTestResult();
        MTDScmurl = SimgHR.getExtra();

        MenuInflater inflater = getMenuInflater();
        //ダサ処理1　URLからファイルを判定、Tが仕様を変更したら追従せざるを得ない
        //メニューを複数に分けタップ先によって表示
        if (MTDScmurl != null) {
            //元画像取得可能なURL クエリパラメータに変更予定
            //今はドメインで判定しているが、同一ドメインにされた場合クエリに変更しないとダメかも
            if (MTDScmurl.contains("pbs.twimg.com")) {
                inflater.inflate(R.menu.contexts, MTDScmcm);
            }
            else {
                //元画像以外のURL
                inflater.inflate(R.menu.context2,MTDScmcm);
            }
        }
        else{
            //有効なURLでない
            inflater.inflate(R.menu.context3, MTDScmcm);
            MTDScmurl ="";
        }
    }


    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onContextItemSelected(MenuItem MTDScmitem) {
//        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        String MTDScmilurl = MTDScmurl
                .replaceAll("&name=[a-z,0-9]*", "&name=orig");
        switch (MTDScmitem.getItemId()) {
            case R.id.context_d:
                Intent MTDSidlip = new Intent(getApplication(), DL.class);
                MTDSidlip.putExtra("MTDdldata", MTDScmilurl);
                startActivity(MTDSidlip);
                return true;

            case R.id.context_o:
                Intent MTDSiop = new Intent(Intent.ACTION_VIEW, Uri.parse(MTDScmurl));
                startActivity(MTDSiop);
                return true;
            case R.id.context_c:
                return true;

            default:
                return super.onContextItemSelected(MTDScmitem);
        }
    }


    //メニュー作成
    @Override
    public boolean onCreateOptionsMenu(Menu MTDSmenu) {
        // 参照するリソースは上でリソースファイルに付けた名前と同じもの
        getMenuInflater().inflate(R.menu.sub, MTDSmenu);
        return true;
    }
    //メニュー処理
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem MTDMmitem) {
        switch (MTDMmitem.getItemId()) {

            case R.id.menuitems1:
                this.finish();
                return true;

            case R.id.menuitems2:
                Intent Simlu2 = new Intent(Intent.ACTION_VIEW, Uri.parse(MTDSwv.getUrl()));
                startActivity(Simlu2);
                return true;

            default:
                return super.onOptionsItemSelected(MTDMmitem);

        }
    }

    //戻るボタンで戻る（Deckと挙動を変えるべきかなと思うので）
    //厄介なページ開くまで封印
    @Override
    public void onBackPressed() {
        //移動後のページ
        if(MTDSwv!= null && MTDSwv.canGoBack()) {
            MTDSwv.goBack();
        }
        //最初のページ
        else {
            super.onBackPressed();
        }
    }

}