package com.xedox.paide.utils.compiler;

import com.xedox.paide.PAIDE;
import com.xedox.paide.utils.io.Assets;
import com.xedox.paide.utils.io.IFile;
import java.io.IOException;

public class ProcessingUtils {
    public static String bindCode(IFile... files) {
        StringBuilder buffer = new StringBuilder();
        for (IFile file : files) {
            buffer.append(String.format("// %s\n", file.getName()))
                    .append(String.format("%s\n", file.read()));
        }
        return buffer.toString();
    }
    
    public static String toJavaCode(String processingCode) throws IOException {
        String sample = Assets.from(PAIDE.APP).asset("Sketch.java").read();
        return String.format(sample, processingCode);
    }
    
    public static String bindToJava(IFile... files) throws IOException {
        String pcode = bindCode(files);
        return toJavaCode(pcode);
    }
}
