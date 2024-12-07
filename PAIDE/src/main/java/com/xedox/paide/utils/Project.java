package com.xedox.paide.utils;

import android.content.Context;
import com.xedox.paide.PAIDE;
import com.xedox.paide.utils.io.Assets;
import com.xedox.paide.utils.io.FileX;
import com.xedox.paide.utils.io.IFile;

public class Project {
    
    public static Context context;

    public String name;

    public IFile src;
    public IFile dir;
    public IFile build;

    public Project(String name) {
        this.name = name;
        dir = new FileX(PAIDE.PROJECTS_FOLDER, name);
        src = new FileX(dir, "src");
        build = new FileX(dir, "build");
    }

    public void create() {
        if (!dir.exists()) {
            dir.mkdir();
            src.mkdir();
            IFile main = new FileX(src, "main.pde");
            main.mkfile();
            try {
                main.write(Assets.from(context).asset("default_code.pde").read());
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    public void delete() {
        dir.removeDir();
    }
}
