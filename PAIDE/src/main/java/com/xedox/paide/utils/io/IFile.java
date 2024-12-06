package com.xedox.paide.utils.io;

import java.io.File;

public interface IFile {
    
    public static String TAG = "IFile";
    
    public String read();
    public boolean write(String txt);
    public boolean write(byte[] txt);
    
    public boolean isFile();
    public boolean isDir();
    
    public IFile[] ifiles();
    public File[] files();
    
    public String getPath();
    public IFile getFilePath();
    
    public String getFullPath();
    public IFile getFullFile();
    
    public File getFileObject();
    
    public void remove();
    public void removeDir();
    
    public boolean mkfile();
    public boolean mkdir();
    public boolean mkdirs();
    
    public String getName();
    public String getExtension();
    public String getNameNoExtension();
    
    public IFile parent();
    
    public static void deleteDir(String path) {
        File file = new File(path);
        if(file.isFile()) {
            file.delete();
            return;
        }
        for(File f: file.listFiles()) {
            if(f.isFile()) {
                f.delete();
            } else {
                deleteDir(f.getAbsolutePath());
            }
        }
    }
    
    public boolean exists();
}
