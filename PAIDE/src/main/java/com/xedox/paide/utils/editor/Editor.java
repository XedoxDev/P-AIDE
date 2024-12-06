package com.xedox.paide.utils.editor;
import android.view.View;

public interface Editor {
    
    public static byte SORA_EDITOR = 1;
    
    public String getCode();
    public void setCode(String newText);
    
    public void formate();
    public float getTextSize();
    public void setTextSize(float newTextSize);
    
    public void release();
    
    public View getView();
    
    public void undo();
    public void redo();
}
