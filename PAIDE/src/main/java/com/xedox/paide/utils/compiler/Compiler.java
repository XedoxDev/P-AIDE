package com.xedox.paide.utils.compiler;

import com.android.tools.r8.D8;
import com.sun.tools.javac.Main;
import com.xedox.paide.PAIDE;
import com.xedox.paide.utils.Project;
import com.xedox.paide.utils.io.FileX;
import com.xedox.paide.utils.io.IFile;

public class Compiler {
    public static void compileProject(Project project) throws Throwable {
        IFile[] files = project.src.ifiles();
        String javaCode = ProcessingUtils.bindToJava(files);

        IFile sketchJava = new FileX(project.build, "Sketch.java");
        if (!sketchJava.mkfile() || !sketchJava.write(javaCode)) {
            throw new RuntimeException("Could not create or write file Sketch.java");
        }

        IFile processing_core_jar = new FileX(PAIDE.projects.parent(), "processing-core.jar");
        if (!processing_core_jar.exists()) {
            throw new Exception("processing-core.jar not found!");
        }

        String[] javacArgs = {
            "-sourcepath",
            project.src.getFullPath(),
            "-d",
            project.build.getFullPath(),
            "-classpath",
            processing_core_jar.getFullPath(),
            sketchJava.getFullPath()
        };

        try {
            Main.main(javacArgs);
        } catch (Exception e) {
            throw new RuntimeException("Compilation exception: " + e.getMessage(), e);
        }
    }

    public static void dexingClass(Project project) throws Exception {
        IFile classFile = new FileX(project.build, "Sketch.class");
        IFile dexFile = new FileX(project.build, "Sketch.dex");

        String[] args = {classFile.getFullPath(), "--output", dexFile.getFullPath()};
        try {
            D8.main(args);
        } catch (Exception e) {
            throw new Exception("Dex compilation exception: " + e.getMessage(), e);
        }
    }
}
