package com.example.administrator.ybdriver.utils;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;

public class Tools {

    public static String inputStream2String (InputStream in) throws IOException {

        StringBuffer out = new StringBuffer();
        byte[]  b = new byte[4096];
        int n;
        while ((n = in.read(b))!= -1){
            out.append(new String(b,0,n));
        }
        Log.i("String的长度",new Integer(out.length()).toString());
        return  out.toString();
    }
}
