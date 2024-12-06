package com.xedox.paide.utils.compiler;

import com.xedox.paide.utils.ConsoleView;
import com.xedox.paide.utils.Project;

public class Builder {
    public static void build(ConsoleView console, Project project) {
        project.build.removeDir();
        project.build.mkdirs();
        console.println("> Start building...");

        console.println("> Generate Sketch.java, compiling Sketch.class...");
        try {
            Compiler.compileProject(project);
        } catch (Exception e) {
            e.printStackTrace();
            console.println(e.toString());
            return;
        }

        console.println("> Dexing Sketch.class...");
        try {
            Compiler.dexingClass(project);
        } catch (Exception e) {
            e.printStackTrace();
            console.println(e.toString());
        }
    }
}
