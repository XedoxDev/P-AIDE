package com.xedox.paide.utils.io;

import android.content.Context;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.StandardCharsets;

public class Assets {

    private final Context context;
    private String assetName;
    private String toPath;

    public static Assets from(Context context) {
        return new Assets(context);
    }

    private Assets(Context context) {
        this.context = context;
    }

    public Assets asset(String name) {
        this.assetName = name;
        return this;
    }

    public Assets toPath(String path) {
        this.toPath = path;
        return this;
    }

    public byte[] readBytes() throws IOException {
        try (InputStream is = context.getAssets().open(assetName);
                BufferedInputStream bis = new BufferedInputStream(is)) {
            byte[] buffer = new byte[bis.available()];
            bis.read(buffer);
            return buffer;
        }
    }

    public String read() throws IOException {
        try (InputStream is = context.getAssets().open(assetName);
                InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8);
                BufferedReader bufferedReader = new BufferedReader(reader)) {
            StringBuilder buffer = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                buffer.append(line).append("\n");
            }
            return buffer.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    public void copy() throws IOException {
        IFile file = new FileX(toPath, assetName);
        file.mkfile();
        file.write(read());
    }
    
    public void copyBinary() throws IOException {
        IFile file = new FileX(toPath, assetName);
        file.mkfile();
        file.write(readBytes());
    }
}
