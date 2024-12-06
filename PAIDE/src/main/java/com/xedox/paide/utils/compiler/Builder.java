package com.xedox.paide.utils.compiler;

import android.app.Activity;
import android.view.View;
import android.widget.ProgressBar;
import com.xedox.paide.utils.ConsoleView;
import com.xedox.paide.utils.Project;
import com.xedox.paide.utils.TaskExecutor;
import com.xedox.paide.utils.io.FileX;
import com.xedox.paide.utils.io.IFile;

public class Builder {

    public static int maxActions = 2;

    public static void build(
            Activity act, ConsoleView console, Project project, ProgressBar progress) {
        TaskExecutor.execute(
                () -> {
                    act.runOnUiThread(
                            () -> {
                                progress.setVisibility(View.VISIBLE);

                                progress.setProgress(0);
                            });
                    project.build.removeDir();
                    project.build.mkdirs();
                    progress.setMax(maxActions);

                    IFile debug = new FileX(project.build, "debug.txt");
                    debug.mkfile();
                    act.runOnUiThread(
                            () -> {
                                console.println("> Start building...");

                                console.println(
                                        "> Generate Sketch.java, compiling Sketch.class...");
                            });
                    try {
                        Compiler.compileProject(project);
                        act.runOnUiThread(
                                () -> {
                                    progress.setProgress(1);
                                });
                    } catch (Throwable e) {
                        error(act, console, e, debug, progress);
                        return;
                    }
                    act.runOnUiThread(
                            () -> {
                                console.println("> Dexing Sketch.class...");
                            });
                    try {
                        Compiler.dexingClass(project);
                        act.runOnUiThread(
                                () -> {
                                    progress.setProgress(2);
                                });
                    } catch (Throwable e) {
                        error(act, console, e, debug, progress);
                        return;
                    }
                    act.runOnUiThread(
                            () -> {
                                progress.setVisibility(View.GONE);
                            });
                });
    }

    public static void error(
            Activity act, ConsoleView console, Throwable e, IFile debug, ProgressBar progress) {
        e.printStackTrace();
        act.runOnUiThread(
                () -> {
                    console.println("> error: " + e.getMessage());
                    progress.setProgress(0);
                    progress.setVisibility(View.GONE);
                });
        debug.write(e.toString());
    }
}
