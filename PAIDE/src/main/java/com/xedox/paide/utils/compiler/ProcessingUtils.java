package com.xedox.paide.utils.compiler;

import com.xedox.paide.PAIDE;
import com.xedox.paide.utils.io.Assets;
import com.xedox.paide.utils.io.IFile;

public class ProcessingUtils {
    public static String bindCode(IFile... files) {
        StringBuilder buffer = new StringBuilder();
        for (IFile file : files) {
            buffer.append(String.format("// %s\n", file.getName()))
                    .append(String.format("%s\n", file.read()));
        }
        return buffer.toString();
    }
    
    public static String toJavaCode(String processingCode) {
        String sample = Assets.from(PAIDE.paide).asset("Sketch.java").read();
        return String.format(sample, processingCode);
    }
}
