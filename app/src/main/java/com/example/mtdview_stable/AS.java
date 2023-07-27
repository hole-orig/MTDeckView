package com.example.mtdview_stable;

//これはJS読み取り部分だけ分離したコードで呼び出せる
//            String jsuri = "js/mtdeck.user.js";
//            String jsString = getjsString(getApplicationContext(), jsuri);

import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class AS extends AppCompatActivity {
    public static String getAString(Context ascontext, String asuri) {
        InputStream asis;
        BufferedReader asbr;
        String asstr;
        StringBuilder assb = null;
        try {
            // assetsからjsuriに入ったscriptの中身を取得する処理
            asis = ascontext.getAssets().open(asuri);
            // getAssets()で取得したbyte単位のデータをInputStreamReaderでchar単位のデータにしてバッファリング
            asbr = new BufferedReader(new InputStreamReader(asis));
            assb = new StringBuilder();
            // 1行ずつ読み込みと改行追加を繰り返し
            while ((asstr = asbr.readLine()) != null) {
                assb.append(asstr);
                assb.append("\n");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
//        assert assb != null;
        if (assb !=null) {
            return assb.toString();
        }
        else {
            return "";
        }
    }
}