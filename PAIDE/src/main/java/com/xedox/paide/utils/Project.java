package com.xedox.paide.utils;

import com.xedox.paide.utils.io.Assets;
import com.xedox.paide.utils.io.FileX;
import com.xedox.paide.utils.io.IFile;

import static com.xedox.paide.PAIDE.*;

public class Project {

    public String name;

    public IFile src;
    public IFile dir;
    public IFile build;

    public Project(String name) {
        this.name = name;
        dir = new FileX(projects, name);
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
                main.write(Assets.from(paide).asset("default_code.pde").read());
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    public void delete() {
        dir.removeDir();
    }
}
