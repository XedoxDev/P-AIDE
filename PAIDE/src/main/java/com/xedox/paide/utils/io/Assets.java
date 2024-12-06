package com.xedox.paide.utils.io;

import android.content.Context;
import android.util.Log;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Assets {

    public Context context;

    public Assets(Context context) {
        this.context = context;
    }

    public String assetName;

    public Assets(Context context, String assetName) {
        this.context = context;
        this.assetName = assetName;
    }

    public String toPath;
    
    public static Assets from(Context context){
        return new Assets(context);
    }
    
    public Assets toPath(String path) {
        toPath = path;
        return this;
    }
    
    public Assets asset(String name) {
        assetName = name;
        return this;
    }
    
    public String read() {
        StringBuilder sb = new StringBuilder();
        try (var br = new BufferedReader(new InputStreamReader(context.getAssets().open(assetName)))) {
        	String line;
            while((line=br.readLine())!=null) {
                sb.append(line).append("\n");
            }
        } catch(Exception err) {
        	err.printStackTrace();
        }
        return sb.toString().trim();
    }
    
    public Assets copy() {
        IFile file = new FileX(toPath, assetName);
        file.mkfile();
        file.write(read());
        return this;
    }
}
