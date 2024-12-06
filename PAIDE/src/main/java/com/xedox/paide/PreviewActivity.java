package com.xedox.paide;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.xedox.paide.utils.Project;
import com.xedox.paide.utils.compiler.DexLoader;
import com.xedox.paide.utils.io.FileX;
import dalvik.system.DexClassLoader;
import java.io.File;
import processing.core.PApplet;

public class PreviewActivity extends AppCompatActivity {

    public PApplet sketch;
    public Project project;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String name = getIntent().getStringExtra("name");
        project = new Project(name);
        try {
            sketch =
                    DexLoader.loadClass(
                            "Sketch",
                            new FileX(project.build, "Skethc.dex").getFullPath(),
                            "",
                            this);
        } catch (Exception e) {
            e.printStackTrace();
            back();
        }
    }

    @Override
    public void onBackPressed() {
        back();
    }

    public void back() {
        Intent i = new Intent(this, EditorActivity.class);
        i.putExtra("name", project.name);
        startActivity(i);
        finish();
    }
}
