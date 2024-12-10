package com.xedox.paide.utils.compiler;

import com.android.tools.r8.D8;

import com.xedox.paide.PAIDE;
import com.xedox.paide.utils.Project;
import com.xedox.paide.utils.io.FileX;
import com.xedox.paide.utils.io.IFile;

import java.io.PrintWriter;
import org.eclipse.jdt.core.compiler.CompilationProgress;
import org.eclipse.jdt.core.compiler.batch.BatchCompiler;

public class PCompiler {
    public static void compileProject(Project project) throws Throwable {
        IFile[] files = project.src.ifiles();
        String javaCode = ProcessingUtils.bindToJava(files);

        IFile sketchJava = new FileX(project.build, "Sketch.java");
        if (!sketchJava.mkfile() || !sketchJava.write(javaCode)) {
            throw new RuntimeException("Could not create or write file Sketch.java");
        }

        IFile processingCoreJar = new FileX(PAIDE.APP_FOLDER, "processing-core.jar");
        if (!processingCoreJar.exists()) {
            throw new RuntimeException("processing-core.jar not found!");
        }

        IFile debug = new FileX(project.build, "output.txt");
        debug.mkfile();

        String classpath = processingCoreJar.getFullPath();
        String outputDir = project.build.getFullPath();

        String[] commandLineArgs = {
            "-source",
            "8",
            "-target",
            "8",
            "-d",
            outputDir,
            "-cp",
            classpath,
            sketchJava.getFullPath()
        };

        PrintWriter pw = new PrintWriter(debug.toFile());
        boolean result = BatchCompiler.compile(commandLineArgs, pw, pw, null);
        if (!result) {
            throw new RuntimeException("Failed compile Sketch.java");
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
