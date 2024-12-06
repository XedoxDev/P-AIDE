package com.xedox.paide.utils.editor.soraEditor;

import android.os.Bundle;

import androidx.annotation.NonNull;

import com.xedox.paide.PAIDE;
import io.github.rosemoe.sora.lang.completion.SimpleCompletionItem;

import io.github.rosemoe.sora.lang.completion.CompletionHelper;
import io.github.rosemoe.sora.lang.completion.CompletionPublisher;
import io.github.rosemoe.sora.langs.textmate.TextMateLanguage;
import io.github.rosemoe.sora.langs.textmate.registry.GrammarRegistry;
import io.github.rosemoe.sora.langs.textmate.registry.ThemeRegistry;
import io.github.rosemoe.sora.text.CharPosition;
import io.github.rosemoe.sora.text.ContentReference;
import io.github.rosemoe.sora.util.MyCharacter;

import static com.xedox.paide.PAIDE.*;
import java.io.IOException;

public class TML extends TextMateLanguage {

    public String scope;
    public String lang;

    public TML(String scopeName, String lang) {
        super(
                GrammarRegistry.getInstance().findGrammar(scopeName),
                GrammarRegistry.getInstance().findLanguageConfiguration(scopeName),
                GrammarRegistry.getInstance(),
                ThemeRegistry.getInstance(),
                true);
        scope = scopeName;
    }

    @Override
    public void requireAutoComplete(
            ContentReference content,
            CharPosition position,
            CompletionPublisher publisher,
            Bundle extraArguments) {
        super.requireAutoComplete(content, position, publisher, extraArguments);
        try {
            for (SnippetsReader.Snippet s : SnippetsReader.read(paide, "processing")) {
                publisher.addItem(
                        new SimpleCompletionItem(s.prefix, s.description, s.length, s.body));
            }
        } catch (Exception err) {
            err.printStackTrace();
        }
        CompletionHelper.computePrefix(content, position, MyCharacter::isJavaIdentifierPart);
    }
}
