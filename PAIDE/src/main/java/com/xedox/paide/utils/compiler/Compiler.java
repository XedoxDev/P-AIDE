package com.xedox.paide.utils.compiler;

import com.android.tools.r8.D8;
import com.sun.tools.javac.Main;

import com.xedox.paide.utils.Project;
import com.xedox.paide.utils.io.FileX;
import com.xedox.paide.utils.io.IFile;

public class Compiler {
    public static void compileProject(Project project) throws Exception {
        IFile[] files = project.src.ifiles();
        String javaCode = ProcessingUtils.bindCode(files);
        
        IFile sketchJava = new FileX(project.build, "Sketch.java");
        IFile sketchClass = new FileX(project.build, "Sketch.class");
        
        String[] args = {
            sketchJava.getFullPath(),
            "--output",
            sketchClass.getFullPath()
        };
        // NB-Javac: Main.main()
        Main.main(args);
    }
    
    public static void dexingClass(Project project) throws Exception {
    	IFile classFile = new FileX(project.build, "Sketch.class");
        IFile dexDir = project.build;
        String[] args = {
            classFile.getFullPath(),
            "--output",
            dexDir.getFullPath()
        };
        D8.main(args);
    }
}
