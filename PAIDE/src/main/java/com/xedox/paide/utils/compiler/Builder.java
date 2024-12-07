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

    public static int maxActions = 6;

    public static int progress;
    public static ConsoleView console;
    public static ProgressBar progressBar;

    public static void build(Activity act, ConsoleView console, Project project, ProgressBar p) {
        Builder.console = console;
        Builder.progressBar = p;
        Builder.progress = 0;
        TaskExecutor.execute(
                () -> {
                    act.runOnUiThread(() -> progressBar.setVisibility(View.VISIBLE));

                    progressBar.setMax(maxActions);

                    try {
                        updateProgress(act, "> Removing build directory...");
                        project.build.removeDir();

                        updateProgress(act, "> Creating build directory...");
                        project.build.mkdirs();

                        updateProgress(act, "> Starting build...\n");

                        updateProgress(act, "> Compiling Sketch.java...");
                        PCompiler.compileProject(project);

                        updateProgress(act, "> Dexing Sketch.class...");
                        PCompiler.dexingClass(project);

                        updateProgress(act, "> Build Complete...");
                    } catch (Throwable e) {
                        error(act, e);
                        return;
                    } finally {
                        act.runOnUiThread(() -> progressBar.setVisibility(View.GONE));
                    }
                });
    }

    private static void updateProgress(Activity act, String message) {
        act.runOnUiThread(
                () -> {
                    progress++;
                    progressBar.setProgress(progress);
                    console.println(message);
                });
    }

    public static void error(Activity act, Throwable e) {
        String err = e.getLocalizedMessage();
        act.runOnUiThread(
                () -> {
                    e.printStackTrace();
                    console.println("> Build Error: " + err);
                    progressBar.setProgress(0);
                    progressBar.setVisibility(View.GONE);
                });
    }
}
