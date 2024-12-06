package com.xedox.paide;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.Settings;
import android.widget.Toast;

import com.xedox.paide.utils.Project;
import com.xedox.paide.utils.TaskExecutor;
import com.xedox.paide.utils.io.FileX;
import com.xedox.paide.utils.io.IFile;

import io.github.rosemoe.sora.langs.textmate.registry.FileProviderRegistry;
import io.github.rosemoe.sora.langs.textmate.registry.GrammarRegistry;
import io.github.rosemoe.sora.langs.textmate.registry.ThemeRegistry;
import io.github.rosemoe.sora.langs.textmate.registry.model.ThemeModel;
import io.github.rosemoe.sora.langs.textmate.registry.provider.AssetsFileResolver;

import java.io.File;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.tm4e.core.registry.IThemeSource;

public class PAIDE extends Application {

    public static PAIDE paide;
    public static final byte MANAGE_REQUEST_CODE = 1;
    public static IFile projects;
    public static IFile externalStorage = new FileX(Environment.getExternalStorageDirectory());

    static {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        paide = this;
        projects = new FileX(externalStorage, "P-AIDE/projects");
        if (!projects.exists()) projects.mkdirs();
    }

    public static boolean requestManagePremission(Activity activity) {
        if (Environment.isExternalStorageManager()) return true;
        else {
            try {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setData(Uri.parse("package:" + paide.getPackageName()));
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
        debug(paide, text);
    }
    
    public static void debug(Context c, String text) {
        // off for release apk
        Toast.makeText(c, text, Toast.LENGTH_LONG).show();
    }

    public static List<Project> getProjects(Activity activity) {
        List<Project> projects = new ArrayList<>();
        if (requestManagePremission(activity)) {
            File[] files = PAIDE.projects.files();
            for (File file : files) {
                if (file.isDirectory()) {
                    projects.add(new Project(file.getName()));
                }
            }
        }
        return projects;
    }

    public static void initSchemes(Context c) {
        /*TaskExecutor.execute(
        () -> {*/
        try {
            FileProviderRegistry.getInstance()
                    .addFileProvider(
                            new AssetsFileResolver(c.getApplicationContext().getAssets()));
            var themeRegistry = ThemeRegistry.getInstance();
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
        // });
    }
}
