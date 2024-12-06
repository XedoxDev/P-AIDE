package com.xedox.paide.utils.editor.soraEditor;

import android.content.Context;

import android.graphics.Typeface;
import android.view.View;
import com.xedox.paide.utils.formatter.AutoFormat;
import com.xedox.paide.utils.formatter.Formatter;
import com.xedox.paide.utils.editor.Editor;

import io.github.rosemoe.sora.widget.CodeEditor;

public class SoraEditor extends CodeEditor implements Editor {

    public static Formatter formatter;

    static {
        formatter = new AutoFormat();
    }

    public SoraEditor(Context c) {
        super(c);
        setTypefaceText(
                Typeface.createFromAsset(
                        getContext().getAssets(), "general/JetBrainsMono-Regular.ttf"));
    }

    @Override
    public String getCode() {
        return getText().toString();
    }

    @Override
    public void setCode(String newText) {
        setText(newText);
    }

    @Override
    public void formate() {
        setCode(formatter.format(getCode()));
    }

    @Override
    public float getTextSize() {
        return getTextSizePx();
    }

    @Override
    public View getView() {
        return this;
    }
}
