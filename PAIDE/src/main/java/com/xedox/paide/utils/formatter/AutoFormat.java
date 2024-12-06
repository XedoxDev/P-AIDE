package com.xedox.paide.utils.formatter;

import java.util.Stack;
import java.util.regex.Pattern;
import processing.core.PApplet;

public class AutoFormat implements Formatter {
    private char[] chars;
    private final StringBuilder buf = new StringBuilder();
    private final StringBuilder result = new StringBuilder();

    private int indentValue;

    private boolean EOF;

    private boolean inStatementFlag;
    private boolean overflowFlag;
    private boolean startFlag;
    private boolean if_flg;
    private boolean elseFlag;

    private int arrayLevel;
    private int arrayIndent;

    private int conditionalLevel;

    private int[][] sp_flg;
    private boolean[][] s_ind;
    private int if_lev;

    private int pos;
    private int level;

    private int curlyLvl;

    private int parenLevel;

    private boolean[] ind;
    private int[] p_flg;
    private int[][] s_tabs;

    private Stack<Boolean> doWhileFlags;

    private Stack<Boolean> ifWhileForFlags;

    private boolean jdoc_flag;

    private int tabs;

    private char lastNonWhitespace = 0;

    private void handleMultiLineComment() {
        final boolean savedStartFlag = startFlag;
        buf.append(nextChar());

        for (char ch = nextChar(); !EOF; ch = nextChar()) {
            buf.append(ch);
            while (ch != '/' && !EOF) {
                if (ch == '\n') {
                    writeIndentedComment();
                    startFlag = true;
                }
                buf.append(ch = nextChar());
            }
            if (buf.length() >= 2 && buf.charAt(buf.length() - 2) == '*') {
                jdoc_flag = false;
                break;
            }
        }

        writeIndentedComment();
        startFlag = savedStartFlag;
        jdoc_flag = false;
        return;
    }

    private void handleSingleLineComment() {
        char ch = nextChar();
        while (ch != '\n' && !EOF) {
            buf.append(ch);
            ch = nextChar();
        }
        writeIndentedLine();
        startFlag = true;
    }

    private void writeIndentedLine() {
        if (buf.length() == 0) {
            if (startFlag) startFlag = elseFlag = false;
            return;
        }
        if (startFlag) {
            boolean indentMore =
                    !buf.toString().matches("[\\s\\]\\}\\)]+;")
                            && (buf.charAt(0) != '{' || arrayLevel >= 0)
                            && overflowFlag;
            if (indentMore) {
                tabs++;
                if (arrayIndent > 0) tabs += arrayIndent;
            }
            printIndentation();
            startFlag = false;
            if (indentMore) {
                tabs--;
                if (arrayIndent > 0) tabs -= arrayIndent;
            }
        }
        if (lastNonSpaceChar() == '}' && bufStarts("else")) {
            result.append(' ');
        }

        if (elseFlag) {
            if (lastNonSpaceChar() == '}') {
                trimRight(result);
                result.append(' ');
            }
            elseFlag = false;
        }

        overflowFlag = inStatementFlag;
        arrayIndent = arrayLevel;
        result.append(buf);

        buf.setLength(0);
    }

    private char lastNonSpaceChar() {
        for (int i = result.length() - 1; i >= 0; i--) {
            char chI = result.charAt(i);
            if (chI != ' ' && chI != '\n') return chI;
        }
        return 0;
    }

    private void writeIndentedComment() {
        if (buf.length() == 0) return;

        int firstNonSpace = 0;
        while (buf.charAt(firstNonSpace) == ' ') firstNonSpace++;
        if (lookup_com("/**")) jdoc_flag = true;

        if (startFlag) printIndentation();

        if (buf.charAt(firstNonSpace) == '/' && buf.charAt(firstNonSpace + 1) == '*') {
            if (startFlag && lastNonWhitespace != ';') {
                result.append(buf.substring(firstNonSpace));
            } else {
                result.append(buf);
            }
        } else {
            if (buf.charAt(firstNonSpace) == '*' || !jdoc_flag) {
                result.append(" " + buf.substring(firstNonSpace));
            } else {
                result.append(" * " + buf.substring(firstNonSpace));
            }
        }
        buf.setLength(0);
    }

    private void printIndentation() {
        if (tabs <= 0) {
            tabs = 0;
            return;
        }
        final int spaces = tabs * indentValue;
        for (int i = 0; i < spaces; i++) {
            result.append(' ');
        }
    }

    private char peek() {
        return (pos + 1 >= chars.length) ? 0 : chars[pos + 1];
    }

    private void advanceToNonSpace(boolean allWsp) {
        if (EOF) return;

        if (allWsp) {
            do {
                pos++;
            } while (pos < chars.length && Character.isWhitespace(chars[pos]));
        } else {
            do {
                pos++;
            } while (pos < chars.length && chars[pos] == ' ');
        }

        if (pos == chars.length - 1) {
            EOF = true;
        } else {
            pos--;
        }
    }

    private char nextChar() {
        if (EOF) return 0;
        pos++;
        if (pos >= chars.length - 1) EOF = true;
        if (pos >= chars.length) return 0;

        char retVal = chars[pos];
        if (!Character.isWhitespace(retVal)) lastNonWhitespace = retVal;
        return retVal;
    }

    private void gotElse() {
        tabs = s_tabs[curlyLvl][if_lev];
        p_flg[level] = sp_flg[curlyLvl][if_lev];
        ind[level] = s_ind[curlyLvl][if_lev];
        if_flg = true;
        inStatementFlag = false;
    }

    private boolean readForNewLine() {
        final int savedTabs = tabs;
        char c = peek();
        while (!EOF && (c == '\t' || c == ' ')) {
            buf.append(nextChar());
            c = peek();
        }

        if (c == '/') {
            buf.append(nextChar());
            c = peek();
            if (c == '*') {
                buf.append(nextChar());
                handleMultiLineComment();
            } else if (c == '/') {
                buf.append(nextChar());
                handleSingleLineComment();
                return true;
            }
        }

        c = peek();
        if (c == '\n') {
            nextChar();
            tabs = savedTabs;
            return true;
        }
        return false;
    }

    private char prevNonWhitespace() {
        StringBuffer tot = new StringBuffer();
        tot.append(result);
        tot.append(buf);
        for (int i = tot.length() - 1; i >= 0; i--) {
            if (!Character.isWhitespace(tot.charAt(i))) return tot.charAt(i);
        }
        return 0;
    }

    private boolean bufStarts(final String keyword) {
        return Pattern.matches("^\\s*" + keyword + "(?![a-zA-Z0-9_&]).*$", buf);
    }

    private boolean bufEnds(final String keyword) {
        return Pattern.matches("^.*(?<![a-zA-Z0-9_&])" + keyword + "\\s*$", buf);
    }

    private void if_levSafe() {
        if (s_tabs[0].length <= if_lev) {
            for (int i = 0; i < s_tabs.length; i++) {
                s_tabs[i] = PApplet.expand(s_tabs[i]);
            }
        }
        if (sp_flg[0].length <= if_lev) {
            for (int i = 0; i < sp_flg.length; i++) {
                sp_flg[i] = PApplet.expand(sp_flg[i]);
            }
        }
        if (s_ind[0].length <= if_lev) {
            for (int i = 0; i < s_ind.length; i++) {
                s_ind[i] = PApplet.expand(s_ind[i]);
            }
        }
    }

    private boolean lookup_com(final String keyword) {
        final String regex = "^\\s*" + keyword.replace("*", "\\*") + ".*$";
        return Pattern.matches(regex, buf);
    }

    private static void trimRight(final StringBuilder sb) {
        while (sb.length() >= 1 && Character.isWhitespace(sb.charAt(sb.length() - 1))) {
            sb.setLength(sb.length() - 1);
        }
    }

    public String format(final String source) {
        final String normalizedText = source.replaceAll("\r", "");
        String cleanText = normalizedText;
        if (!normalizedText.endsWith("\n")) {
            cleanText += "\n";
        }

        result.setLength(0);
        indentValue = tabSize;

        boolean forFlag = if_flg = false;
        startFlag = true;
        int forParenthLevel = 0;
        conditionalLevel = parenLevel = curlyLvl = if_lev = level = 0;
        tabs = 0;
        jdoc_flag = inStatementFlag = overflowFlag = false;
        pos = arrayLevel = -1;

        int[] s_level = new int[10];
        sp_flg = new int[20][10];
        s_ind = new boolean[20][10];
        int[] s_if_lev = new int[10];
        boolean[] s_if_flg = new boolean[10];
        ind = new boolean[10];
        p_flg = new int[10];
        s_tabs = new int[20][10];
        doWhileFlags = new Stack<>();
        ifWhileForFlags = new Stack<>();

        chars = cleanText.toCharArray();

        EOF = false;

        while (!EOF) {
            char c = nextChar();

            switch (c) {
                default:
                    inStatementFlag = true;
                    buf.append(c);
                    break;

                case ',':
                    inStatementFlag = true;
                    trimRight(buf);
                    buf.append(", ");
                    advanceToNonSpace(false);
                    break;

                case ' ':
                case '\t':
                    elseFlag = bufEnds("else");
                    if (elseFlag) {
                        gotElse();
                        if (!startFlag || buf.length() > 0) {
                            buf.append(c);
                        }

                        writeIndentedLine();
                        startFlag = false;
                        break;
                    }
                    if (!startFlag || buf.length() > 0) buf.append(c);
                    break;

                case '\n':
                    if (EOF) break;

                    elseFlag = bufEnds("else");
                    if (elseFlag) gotElse();

                    if (lookup_com("//")) {
                        if (buf.charAt(buf.length() - 1) == '\n') {
                            buf.setLength(buf.length() - 1);
                        }
                    }

                    if (elseFlag) {
                        writeIndentedLine();
                        result.append("\n");

                        p_flg[level]++;
                        tabs++;
                    } else {
                        writeIndentedLine();
                        result.append("\n");
                    }
                    startFlag = true;
                    break;

                case '{':
                    elseFlag = bufEnds("else");
                    if (elseFlag) gotElse();

                    doWhileFlags.push(Boolean.valueOf(bufEnds("do")));

                    char prevChar = prevNonWhitespace();
                    if (arrayLevel >= 0 || prevChar == '=' || prevChar == ']') {
                        arrayLevel++;
                        buf.append(c);
                        break;
                    }

                    inStatementFlag = false;

                    if (s_if_lev.length == curlyLvl) {
                        s_if_lev = PApplet.expand(s_if_lev);
                        s_if_flg = PApplet.expand(s_if_flg);
                    }
                    s_if_lev[curlyLvl] = if_lev;
                    s_if_flg[curlyLvl] = if_flg;
                    if_lev = 0;
                    if_flg = false;
                    curlyLvl++;
                    if (startFlag && p_flg[level] != 0) {
                        p_flg[level]--;
                        tabs--;
                    }

                    trimRight(buf);
                    if (buf.length() > 0
                            || (result.length() > 0
                                    && !Character.isWhitespace(
                                            result.charAt(result.length() - 1)))) {
                        buf.append(" ");
                    }
                    buf.append(c);
                    writeIndentedLine();
                    readForNewLine();
                    writeIndentedLine();

                    result.append('\n');
                    tabs++;
                    startFlag = true;

                    if (p_flg[level] > 0) {
                        ind[level] = true;
                        level++;
                        s_level[level] = curlyLvl;
                    }
                    break;

                case '}':
                    if (arrayLevel >= 0) {
                        if (arrayLevel > 0) arrayLevel--;
                        if (arrayIndent > arrayLevel) arrayIndent = arrayLevel;
                        buf.append(c);
                        break;
                    }

                    inStatementFlag = false;

                    curlyLvl--;
                    if (curlyLvl < 0) {
                        curlyLvl = 0;
                        buf.append(c);
                        writeIndentedLine();
                    } else {
                        if_lev = s_if_lev[curlyLvl] - 1;
                        if (if_lev < 0) if_lev = 0;
                        if_levSafe();

                        if_flg = s_if_flg[curlyLvl];
                        trimRight(buf);
                        writeIndentedLine();
                        tabs--;

                        trimRight(result);
                        result.append('\n');
                        overflowFlag = false;
                        printIndentation();
                        result.append(c);
                        if (peek() == ';') result.append(nextChar());

                        if (doWhileFlags.empty()
                                || !doWhileFlags.pop().booleanValue()
                                || !new String(chars, pos + 1, chars.length - pos - 1)
                                        .trim()
                                        .startsWith("while")) {
                            readForNewLine();
                            writeIndentedLine();
                            result.append('\n');
                            startFlag = true;
                        } else {
                            result.append(' ');
                            advanceToNonSpace(true);
                            startFlag = false;
                        }

                        if (curlyLvl < s_level[level] && level > 0) level--;

                        if (ind[level]) {
                            tabs -= p_flg[level];
                            p_flg[level] = 0;
                            ind[level] = false;
                        }
                    }
                    break;

                case '"':
                case '“':
                case '”':
                case '\'':
                case '‘':
                case '’':
                    inStatementFlag = true;
                    char realQuote = c;
                    if (c == '“' || c == '”') realQuote = '"';
                    if (c == '‘' || c == '’') realQuote = '\'';
                    buf.append(realQuote);

                    char otherQuote = c;
                    if (c == '“') otherQuote = '”';
                    if (c == '”') otherQuote = '“';
                    if (c == '‘') otherQuote = '’';
                    if (c == '’') otherQuote = '‘';

                    char cc = nextChar();
                    while (!EOF && cc != otherQuote && cc != realQuote && cc != c) {
                        buf.append(cc);
                        if (cc == '\\') {
                            buf.append(cc = nextChar());
                        }

                        if (peek() == '\n') break;
                        cc = nextChar();
                    }
                    if (cc == otherQuote || cc == realQuote || cc == c) {
                        buf.append(realQuote);
                        if (readForNewLine()) {
                            chars[pos--] = '\n';
                        }
                    } else {
                        inStatementFlag = false;
                    }
                    break;

                case ';':
                    if (forFlag) {
                        trimRight(buf);
                        buf.append("; ");
                        advanceToNonSpace(false);
                        break;
                    }
                    buf.append(c);
                    inStatementFlag = false;
                    writeIndentedLine();
                    if (p_flg[level] > 0 && !ind[level]) {
                        tabs -= p_flg[level];
                        p_flg[level] = 0;
                    }
                    readForNewLine();
                    writeIndentedLine();
                    result.append("\n");
                    startFlag = true;

                    arrayLevel = -1;

                    if (if_lev > 0) {
                        if (if_flg) {
                            if_lev--;
                            if_flg = false;
                        } else {
                            if_lev = 0;
                        }
                    }
                    break;

                case '\\':
                    buf.append(c);
                    buf.append(nextChar());
                    break;

                case '?':
                    conditionalLevel++;
                    buf.append(c);
                    break;

                case ':':
                    if (peek() == ':') {
                        result.append(c).append(nextChar());
                        break;
                    } else if (conditionalLevel > 0) {
                        conditionalLevel--;
                        buf.append(c);
                        break;
                    } else if (forFlag) {
                        trimRight(buf);
                        buf.append(" : ");
                        advanceToNonSpace(false);
                        break;
                    }

                    buf.append(c);
                    inStatementFlag = false;
                    arrayLevel = -1;
                    if (tabs > 0) {
                        tabs--;
                        writeIndentedLine();
                        tabs++;
                    } else {
                        writeIndentedLine();
                    }

                    readForNewLine();
                    writeIndentedLine();
                    result.append('\n');
                    startFlag = true;
                    break;

                case '/':
                    final char next = peek();
                    if (next == '/') {
                        buf.append(c).append(nextChar());
                        handleSingleLineComment();
                        result.append("\n");
                    } else if (next == '*') {
                        if (buf.length() > 0) {
                            writeIndentedLine();
                        }
                        buf.append(c).append(nextChar());
                        handleMultiLineComment();
                    } else {
                        buf.append(c);
                    }
                    break;

                case ')':
                    parenLevel--;

                    if (forFlag && forParenthLevel > parenLevel) forFlag = false;

                    if (parenLevel < 0) parenLevel = 0;
                    buf.append(c);

                    boolean wasIfEtc =
                            !ifWhileForFlags.empty() && ifWhileForFlags.pop().booleanValue();
                    if (wasIfEtc) {
                        inStatementFlag = false;
                        arrayLevel = -1;
                    }

                    writeIndentedLine();
                    if (wasIfEtc && readForNewLine()) {
                        chars[pos] = '\n';
                        pos--;
                        if (parenLevel == 0) {
                            p_flg[level]++;
                            tabs++;
                            ind[level] = false;
                        }
                    }
                    break;

                case '(':
                    final boolean isFor = bufEnds("for");
                    final boolean isIf = bufEnds("if");

                    if (isFor || isIf || bufEnds("while")) {
                        if (!Character.isWhitespace(buf.charAt(buf.length() - 1))) {
                            buf.append(' ');
                        }
                        ifWhileForFlags.push(true);
                    } else {
                        ifWhileForFlags.push(false);
                    }

                    buf.append(c);
                    parenLevel++;

                    if (isFor && !forFlag) {
                        forParenthLevel = parenLevel;
                        forFlag = true;
                    } else if (isIf) {
                        writeIndentedLine();
                        s_tabs[curlyLvl][if_lev] = tabs;
                        sp_flg[curlyLvl][if_lev] = p_flg[level];
                        s_ind[curlyLvl][if_lev] = ind[level];
                        if_lev++;
                        if_levSafe();
                        if_flg = true;
                    }
            }
        }

        if (buf.length() > 0) writeIndentedLine();

        final String formatted = result.toString();
        return formatted.equals(cleanText) ? source : formatted;
    }
}
