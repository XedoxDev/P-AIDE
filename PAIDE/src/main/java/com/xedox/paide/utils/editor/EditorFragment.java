package com.xedox.paide.utils.editor;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import static com.xedox.paide.PAIDE.*;
import com.xedox.paide.utils.editor.soraEditor.SoraEditor;
import com.xedox.paide.utils.editor.soraEditor.TML;
import com.xedox.paide.utils.io.IFile;

import io.github.rosemoe.sora.langs.textmate.registry.ThemeRegistry;
import java.io.IOException;

import static android.view.ViewGroup.LayoutParams;

public class EditorFragment extends Fragment {

    public Editor editor;
    public int type;
    public IFile file;
    private LayoutParams layoutParams;

    public EditorFragment(IFile file, int type) {
        this.type = type;
        this.file = file;
        layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        if (container == null) container = new RelativeLayout(paide);
        if (type == Editor.SORA_EDITOR) {
            editor = createSoraEditor();
        }
        if (editor != null) container.addView(editor.getView(), layoutParams);
        editor.setCode(file.read());
        return container;
    }

    public SoraEditor createSoraEditor() {
        SoraEditor editor = new SoraEditor(paide);
        String lang = "processing";
        String extension = ".pde";
        if (file.getName().endsWith(".pde")) {
            lang = "processing";
            extension = ".pde";
        } else {
            extension = ".txt";
            lang = "Text";
        }
        TML tml = new TML("scope" + extension, lang);
        editor.setEditorLanguage(tml);
        return editor;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (editor != null) {
            editor.release();
        }
    }

    public void save() {
        file.write(editor.getCode());
    }
}
