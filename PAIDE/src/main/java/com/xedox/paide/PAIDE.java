package com.xedox.paide;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.Settings;
import android.widget.Toast;

import com.xedox.paide.utils.Project;
import com.xedox.paide.utils.TaskExecutor;
import com.xedox.paide.utils.formatter.Formatter;
import com.xedox.paide.utils.formatter.EclipseFormat;
import com.xedox.paide.utils.io.Assets;
import com.xedox.paide.utils.io.FileX;
import com.xedox.paide.utils.io.IFile;

import io.github.rosemoe.sora.langs.textmate.registry.FileProviderRegistry;
import io.github.rosemoe.sora.langs.textmate.registry.GrammarRegistry;
import io.github.rosemoe.sora.langs.textmate.registry.ThemeRegistry;
import io.github.rosemoe.sora.langs.textmate.registry.model.ThemeModel;
import io.github.rosemoe.sora.langs.textmate.registry.provider.AssetsFileResolver;

import java.io.File;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.tm4e.core.registry.IThemeSource;

public class PAIDE extends Application {

    public static PAIDE APP;

    public static final byte MANAGE_REQUEST_CODE = 1;
    
    public static final String APP_FOLDER_NAME = "P-AIDE/";
    public static final String PROJECTS_FOLDER_NAME = "projects/";
    public static final String DATA_FOLDER_NAME = "data/data/com.xedox.paide.files/";
    
    public static final IFile EXTERNAL_STORAGE = new FileX(Environment.getExternalStorageDirectory());
    public static final IFile PROJECTS_FOLDER = new FileX(EXTERNAL_STORAGE, APP_FOLDER_NAME + PROJECTS_FOLDER_NAME);
    public static final IFile APP_FOLDER = new FileX(EXTERNAL_STORAGE, APP_FOLDER_NAME);
    
    public static Formatter formatter;
     
    @Override
    public void onCreate() {
        super.onCreate();
        APP = this;
        Project.context = APP;
        if (!APP_FOLDER.exists()) APP_FOLDER.mkdirs();
        if (!PROJECTS_FOLDER.exists()) PROJECTS_FOLDER.mkdirs();
        formatter = new EclipseFormat();
    }

    public static boolean requestManagePremission(Activity activity) {
        if (Environment.isExternalStorageManager()) return true;
        else {
            try {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setData(Uri.parse("package:" + APP.getPackageName()));
                activity.startActivityForResult(intent, MANAGE_REQUEST_CODE);
            } catch (Exception e) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                activity.startActivityForResult(intent, MANAGE_REQUEST_CODE);
                e.printStackTrace();
            }
            return false;
        }
    }

    public static void debug(String text) {
        debug(APP, text);
    }

    public static void debug(Context c, String text) {
        // off for release apk
        Toast.makeText(c, text, Toast.LENGTH_LONG).show();
    }

    public static List<Project> getProjects(Activity activity) {
        List<Project> projects = new ArrayList<>();
        if (requestManagePremission(activity)) {
            File[] files = PROJECTS_FOLDER.files();
            for (File file : files) {
                if (file.isDirectory()) {
                    projects.add(new Project(file.getName()));
                }
            }
        }
        return projects;
    }

    public static void initSchemes(Context c) {
        TaskExecutor.execute(
                () -> {
                    try {
                        FileProviderRegistry.getInstance()
                                .addFileProvider(
                                        new AssetsFileResolver(
                                                c.getApplicationContext().getAssets()));
                        ThemeRegistry themeRegistry = ThemeRegistry.getInstance();
                        String name = "darcula";
                        String themeAssetsPath = "soraeditor/themes/" + name + ".json";
                        ThemeModel model =
                                new ThemeModel(
                                        IThemeSource.fromInputStream(
                                                FileProviderRegistry.getInstance()
                                                        .tryGetInputStream(themeAssetsPath),
                                                themeAssetsPath,
                                                null),
                                        name);
                        themeRegistry.loadTheme(model);
                        ThemeRegistry.getInstance().setTheme("Darcula");
                        GrammarRegistry.getInstance().loadGrammars("soraeditor/langs.json");
                    } catch (Exception e) {
                        e.printStackTrace();
                        debug(c, e.toString());
                    }
                });
    }

    public static void copyProcessingCoreJar(Activity act) {
        IFile pcj = new FileX(PROJECTS_FOLDER.parent(), "processing-core.jar");
        if (!pcj.exists()) {
            TaskExecutor.execute(
                    () -> {
                        try {

                            Assets.from(APP)
                                    .asset(pcj.getName())
                                    .toPath(pcj.parent().getFullPath())
                                    .copyBinary();

                            act.runOnUiThread(
                                    () -> {
                                        Toast.makeText(
                                                        act,
                                                        R.string.processing_core_copyed_successful,
                                                        Toast.LENGTH_SHORT)
                                                .show();
                                    });
                        } catch (Exception err) {
                            err.printStackTrace();
                            act.runOnUiThread(
                                    () -> {
                                        Toast.makeText(
                                                        act,
                                                        R.string
                                                                .processing_core_copyed_unsuccessful,
                                                        Toast.LENGTH_SHORT)
                                                .show();
                                    });
                        }
                    });
        }
    }
}
