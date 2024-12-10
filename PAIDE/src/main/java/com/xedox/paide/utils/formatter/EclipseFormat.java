package com.xedox.paide.utils.formatter;

import org.eclipse.jdt.core.formatter.CodeFormatter;
import org.eclipse.jdt.internal.formatter.DefaultCodeFormatter;

public class EclipseFormat implements com.xedox.paide.utils.formatter.Formatter {

    public CodeFormatter cf;

    @Override
    public String format(String text) {
        String formatCode = text;
        if (cf == null) cf = new DefaultCodeFormatter();
        try {
            formatCode =
                    cf.format(
                                    CodeFormatter.K_COMPILATION_UNIT,
                                    text,
                                    0,
                                    text.length(),
                                    0,
                                    System.lineSeparator())
                            .toString();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return formatCode;
    }
}
