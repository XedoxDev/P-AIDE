package com.xedox.paide.utils.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class FileX extends File implements IFile {
    
    public FileX(File file) {
        super(file.getAbsolutePath());
    }
    
    public FileX(String file) {
        super(file);
    }
    
    public FileX(String path, String file) {
        super(path, file);
    }
    
    public FileX(File path, String name) {
        super(path, name);
    }
    
    public FileX(IFile ifile, String name) {
        super(ifile.getFileObject(), name);
    }
    
    public FileX(IFile ifile) {
        super(ifile.getFullPath());
    }
    
    @Override
    public String read() {
        StringBuilder buffer = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(this))) {
        	String line;
            while((line=br.readLine())!=null){
                buffer.append(line).append("\n");
            }
        } catch(Exception err) {
        	err.printStackTrace();
        }
        return buffer.toString().trim();
    }

    @Override
    public void write(String txt) {
        try (FileWriter fw = new FileWriter(this)) {
        	fw.write(txt);
        } catch(Exception err) {
        	err.printStackTrace();
        }
    }

    @Override
    public boolean isDir() {
        return isDirectory();
    }

    @Override
    public IFile[] ifiles() {
        File[] files = listFiles();
        IFile[] ifiles = new IFile[files.length];
        for(int i = 0; i < files.length; i++) {
        	ifiles[i] = new FileX(files[i]);
        }
        return ifiles;
    }

    @Override
    public File[] files() {
        return listFiles();
    }

    @Override
    public IFile getFilePath() {
        return new FileX(getParent());
    }

    @Override
    public String getFullPath() {
        return getAbsolutePath();
    }

    @Override
    public IFile getFullFile() {
        return new FileX(getAbsolutePath());
    }

    @Override
    public File getFileObject() {
        return new File(getAbsolutePath());
    }

    @Override
    public void remove() {
        if(isFile()) {
            delete();
        }
    }

    @Override
    public void removeDir() {
        if(isDir()) {
            IFile.deleteDir(getFullPath());
        }
    }

    @Override
    public boolean mkfile() {
        try {
        	return createNewFile();
        } catch(Exception err) {
            err.printStackTrace();
        	return false;
        }
    }

    @Override
    public String getExtension() {
        String n = getName();
        return n.substring(n.lastIndexOf("."), n.length());
    }

    @Override
    public String getNameNoExtension() {
       String n = getName();
        return n.substring(0, n.lastIndexOf(".")-1);
    }

    @Override
    public IFile parent() {
        return new FileX(getParent());
    }
}
