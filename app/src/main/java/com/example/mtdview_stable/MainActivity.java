package com.example.mtdview_stable;

import android.content.ClipData;
import android.os.Message;
import android.webkit.*;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import static com.example.mtdview_stable.R.layout.activity_main;
import static java.security.AccessController.getContext;

public class MainActivity extends AppCompatActivity {

    private WebView MTDMwv;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_main);
        CookieManager MTDMCookieManager = CookieManager.getInstance();
        MTDMCookieManager.setAcceptCookie(true);
        MTDMCookieManager.setCookie("https://tweetdeck.twitter.com/", "tweetdeck_version=legacy; domain=twitter.com;  path=/; expires=2030-08-12T00:00:00.000Z; Secure;");
        MTDMCookieManager.flush();
        MTDMwv = findViewById(R.id.MTDview);
        MTDMwv.getSettings().setDomStorageEnabled(true);
        MTDMwv.getSettings().setJavaScriptEnabled(true);
        //Assetloader使うべき？
//        MTDMwv.loadUrl("https://tweetdeck.twitter.com");
        MTDMwv.loadUrl("file:///android_asset/html/start.html");
//        MTDwv.setWebViewClient(new MTDjswvc());
//        MTDMwv.setDownloadListener(new MTDMvdl());
//        MTDMwv.getSettings().setSupportMultipleWindows(true);
        MTDMwv.setWebViewClient(new MTDMuwvc());
        MTDMwv.setWebChromeClient(new MTDMfuwcc());
        registerForContextMenu(MTDMwv);
    }

    //.js適用、このアプリの本体
    private class jswvc extends WebViewClient {
        @Override
        public void onPageFinished(WebView jswv, String jsu) {
            //ここではMTDeck適用
            String asuri = "js/mtdeck.user.js";
            String jsString = AS.getAString(getApplicationContext(), asuri);
            jswv.evaluateJavascript(jsString, null);
            jswv.setWebViewClient(new MTDMuwvc());
        }
    }

//    private class ViewWindow extends WebChromeClient {
//        @Override
//        public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture,
//                                      Message resultMsg) {
//            WebView OpenView = new WebView(getApplicationContext());
//            OpenView.getSettings().setJavaScriptEnabled(true);
//            OpenView.getSettings().setBuiltInZoomControls(true);
//            OpenView.getSettings().setDisplayZoomControls(false);
//            OpenView.getSettings().setAllowFileAccess(true);
//            OpenView.getSettings().setLoadWithOverviewMode(true);
//            OpenView.getSettings().setUseWideViewPort(true);
//            OpenView.loadUrl(MTDVu);
//            OpenView.setLongClickable(true);
//            OpenView.setWebViewClient(new SubWebView.notOpen());
//            registerForContextMenu(OpenView);
//            OpenView.setWebViewClient(new WebViewClient(){
//
//            });
//        return true;
//        };
//    }

    String MTDVu;
    //正常動作のためにページ内URLを判定
    private class MTDMuwvc extends WebViewClient {
        //URL別リンク動作(全trueでもMTDeckの動作"だけ"はする)
        @Override
        public boolean shouldOverrideUrlLoading(WebView MTDMuwv, WebResourceRequest MTDMuwrr) {
            switch (MTDMuwrr.getUrl().getHost()) {
                // ログインとログオフのページをWebView内で表示する
                case "mobile.twitter.com":
                    return false;
                //ログアウトキャンセル時の処理
                case "tweetdeck.twitter.com":
                    MTDMuwv.setWebViewClient(new jswvc());
                    return false;

                //リリースノートやアカウントへのリンク対策
                case "twitter.com":
//                    if(MTDMuwrr.getUrl().getPath().equals("/home")) {
//                        MTDMuwv.loadUrl("https://tweetdeck.twitter.com");
//                        return false;
//                    }
                    if(MTDMuwrr.getUrl().getPath().equals("/logout")) {
                        Intent MTDMiui = new Intent(getApplication(), SubWebView.class);
                        String MTDMius = "https://twitter.com/logout";
                        MTDMiui.putExtra("MTDsdata", MTDMius);
                        startActivity(MTDMiui);
                        return true;
                    }
//                   if(MTDMuwrr.getUrl().getPath().equals("/i/tweetdeck_release_notes")) {
                        Intent MTDMiui = new Intent(getApplication(), SubWebView.class);
                        String MTDMius = String.valueOf(MTDMuwrr.getUrl());
                        MTDMiui.putExtra("MTDsdata", MTDMius);
                        startActivity(MTDMiui);
                        return true;
//                    }

//                // 他はTの標準アプリ等で表示する
                default:
                    Intent MTDMui = new Intent(Intent.ACTION_VIEW, MTDMuwrr.getUrl());
                    startActivity(MTDMui);
////                default:
//                    Intent MTDMiui = new Intent(getApplication(), SubWebView.class);
//                    String MTDMius = String.valueOf(MTDMuwrr.getUrl());
//                    MTDMiui.putExtra("MTDsdata", MTDMius);
//                    startActivity(MTDMiui);
                    return true;
//                //外部へはサブのビューから移動
                //とゆーのも、内部でないとクッキー食ってない可能性があり、鍵垢のツイートとか見られんので←操作系ごっちゃになるのでやめ
            }
        }
    }

    //ここからブラウザっぽい処理（DeckではMT適用しなくてもいると思われる）

    //Chromium標準の動画ダウンロード対応
//    class MTDMvdl implements DownloadListener {
//        @Override
//        public void onDownloadStart(String MTDvdlurl, String MTDvdluA,
//                                    String MTDvdlcD, String MTDvdlmimetype, long contentLength) {
//            Intent MTDvdli = new Intent(getApplication(), DL.class);
//            String MTDvdlius = String.valueOf(MTDvdlurl);
//            MTDvdli.putExtra("MTDdldata", MTDvdlius);
//            startActivity(MTDvdli);
//        }
//    }

    //ファイルアップ用の処理,20220513にDeprecatedな処理を変更

    private ValueCallback<Uri[]> MTDMfuvc;
    //ファイル表示
    private class MTDMfuwcc extends WebChromeClient {
        ActivityResultLauncher<Intent> MTDFCLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new MTDfuarcb());
        @Override
        public boolean onShowFileChooser(WebView MTDMfuwccwv,ValueCallback<Uri[]> MTDMfpc,FileChooserParams MTDMfufcp) {
            MTDMfuvc = MTDMfpc;
            Intent MTDui = new Intent(Intent.ACTION_GET_CONTENT);
            MTDui.setType("*/*");
            String[] MTDmime = {"image/jpeg","image/png","image/gif","video/mp4"};
            MTDui.putExtra(Intent.EXTRA_MIME_TYPES, MTDmime);
            MTDui.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            MTDui.addCategory(Intent.CATEGORY_OPENABLE);
            MTDFCLauncher.launch(MTDui);
            return true;
        }
    }
    //ファイル取得
    //複数ファイル選択を追加、1個の時はエラー起こすので使わない
    //からのデータを挿入しておいた方がいいのかも
    private class MTDfuarcb implements ActivityResultCallback<ActivityResult> {
        @Override
        public void onActivityResult(ActivityResult MTDar) {
            // リザルトのチェック（ここもうちょっときれいになるかも）
            Uri[] MTDresults = new Uri[0];
            if (MTDar.getResultCode() == RESULT_OK) {
                Intent MTDid = MTDar.getData();
                if (MTDid != null) {
                    ClipData MTDclp = MTDid.getClipData();
                    if (MTDclp == null) {
                        String MTDids = MTDid.getDataString();
                        MTDresults = new Uri[]{Uri.parse(MTDids)};
                    }
                    if (MTDclp != null) {
                        MTDresults = new Uri[MTDclp.getItemCount()];
                        for (int i = 0; i < MTDclp.getItemCount(); i++) {
                            ClipData.Item item = MTDclp.getItemAt(i);
                            MTDresults[i] = item.getUri();
                        }
                    }
                }
            }
            MTDMfuvc.onReceiveValue(MTDresults);

        }
    }



    //コンテキストメニュー作成
    private String MTDMcmurl;
    @Override
    public void onCreateContextMenu(ContextMenu MTDMcmcm, View MTDMcmlv, ContextMenu.ContextMenuInfo MTDMcminfo) {
        super.onCreateContextMenu(MTDMcmcm, MTDMcmlv, MTDMcminfo);
        MTDMwv = (WebView) MTDMcmlv;
        WebView.HitTestResult imgHR = MTDMwv.getHitTestResult();
        MTDMcmurl = imgHR.getExtra();
        MenuInflater inflater = getMenuInflater();
        //ダサ処理1　URLからファイルを判定、Tが仕様を変更したら追従せざるを得ない
        //メニューを複数に分けタップ先によって表示
        if (MTDMcmurl != null) {
            //元画像取得可能なURL
              if (MTDMcmurl.contains("https://pbs.twimg.com/")) {
                  inflater.inflate(R.menu.context1, MTDMcmcm);
              }
//              else if (MTDMcmurl.contains("https://video.twimg.com/")) {
//                  inflater.inflate(R.menu.context1, MTDMcmcm);
//              }
            else {
                //元画像以外のURL
                inflater.inflate(R.menu.context2, MTDMcmcm);
            }
        }
        else{
            //有効なURLでない
            inflater.inflate(R.menu.context3, MTDMcmcm);
            MTDMcmurl = "";
        }
    }

    //コンテキストメニュー処理
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onContextItemSelected(MenuItem MTDcmitem) {
            String MTDcmilurl = MTDMcmurl
                    .replaceAll("&name=[a-z,0-9]*", "&name=orig");
            switch (MTDcmitem.getItemId()) {
                    //DL
                case R.id.context_d:
                    Intent MTDidlip = new Intent(getApplication(), DL.class);
                    MTDidlip.putExtra("MTDdldata", MTDcmilurl);
                    startActivity(MTDidlip);
                    return true;
                // 外部アプリに渡す
                case R.id.context_i:
//
//                    Intent MTDiop = new Intent(Intent.ACTION_VIEW, Uri.parse(MTDcmilurl));
////                    Intent chooser = Intent.createChooser(MTDiop,"別アプリで開く");
//                    startActivity(MTDiop);
                    Intent MTDiip = new Intent(getApplication(), SubWebView.class);
                    MTDiip.putExtra("MTDsdata", MTDcmilurl);
                    startActivity(MTDiip);
                    return true;
                //終了
                case R.id.context_o:
                    Intent MTDSiop = new Intent(Intent.ACTION_VIEW);
                    startActivity(MTDSiop);
                    return true;

                case R.id.context_c:
                    return true;

                default:
                    return super.onContextItemSelected(MTDcmitem);
            }

    }

    //メニュー作成
    @Override
    public boolean onCreateOptionsMenu(Menu MTDMmenu) {
        // 参照するリソースは上でリソースファイルに付けた名前と同じもの
        getMenuInflater().inflate(R.menu.main, MTDMmenu);
        return true;
    }
    //メニュー処理
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem MTDMmitem) {
        switch (MTDMmitem.getItemId()) {

            case R.id.menuitemm1:

                MTDMwv.reload();
                MTDMwv.setWebViewClient(new jswvc());
                return true;

            case R.id.menuitemm2:
                String MTDmlu2 = "file:///android_asset/html/help.html";
                Intent imlu2 = new Intent(getApplication(), SubWebView.class);
                imlu2.putExtra("MTDsdata",MTDmlu2);
                startActivity(imlu2);
                return true;

            case R.id.menuitemm3:
                Context MTDmcontext = getApplicationContext();
                CharSequence MTDMmtext = "MTDeckView Stable\nPowered by MTDeck @mkizka\nMTDeck v. 1.9.0";
                int MTDmduration = Toast.LENGTH_LONG;
                Toast MTDatoast = Toast.makeText(MTDmcontext, MTDMmtext,MTDmduration);
                MTDatoast.setGravity(Gravity.CENTER, 0, 0);
                MTDatoast.show();
                return true;

            case R.id.menuitemm4:
//                this.finishAndRemoveTask();
                this.finish();
                return true;

            default:
                return super.onOptionsItemSelected(MTDMmitem);

        }
    }

}