package com.xedox.paide.utils.editor.soraEditor;

import android.content.Context;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import com.xedox.paide.utils.io.Assets;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SnippetsReader {
    public static Snippet[] read(Context c, String langName) {
        String json =
                Assets.from(c)
                        .asset(String.format("soraeditor/%s/snippets.json", langName))
                        .read();
        Type snippetsListType = new TypeToken<Snippet[]>() {}.getType();
        Snippet[] snippets = new Gson().fromJson(json, snippetsListType);
        return snippets;
    }

    public static class Snippet {
        @SerializedName("prefix")
        public String prefix;

        @SerializedName("body")
        public String body;

        @SerializedName("description")
        public String description;

        @SerializedName("length")
        public int length;

        public Snippet(String prefix, String body, String description, int length) {
            this.prefix = prefix;
            this.body = body;
            this.description = description;
            this.length = length;
        }
    }
}
