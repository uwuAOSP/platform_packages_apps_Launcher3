/*
 * Copyright (C) 2026 The uwuAOSP Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.android.launcher3.allapps.search;

import android.icu.text.Transliterator;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Tone-free full pinyin and initials matching for Chinese app labels. */
public final class PinyinMatcher {
    private static final int MAX_CACHED_LABELS = 256;
    private static final Pattern TOKEN = Pattern.compile("[a-z0-9]+");
    // Both ICU use and cache access are serialized. Labels, rather than app identities, are
    // the keys so a renamed app cannot retain stale pinyin. Old labels are bounded by the LRU.
    private static final Map<String, PinyinLabel> CACHE =
            new LinkedHashMap<>(MAX_CACHED_LABELS, 0.75f, true) {
                @Override
                protected boolean removeEldestEntry(Map.Entry<String, PinyinLabel> eldest) {
                    return size() > MAX_CACHED_LABELS;
                }
            };

    private PinyinMatcher() { }

    /** Matches a Latin query at any pinyin syllable boundary, never inside a syllable. */
    public static boolean matches(String query, String label) {
        String normalized = normalizeQuery(query);
        if (normalized.isEmpty() || !containsHan(label)) {
            return false;
        }
        PinyinLabel pinyin;
        synchronized (CACHE) {
            pinyin = CACHE.get(label);
            if (pinyin == null) {
                pinyin = new PinyinLabel(TransliteratorHolder.INSTANCE.transliterate(label));
                CACHE.put(label, pinyin);
            }
        }
        return pinyin.matches(normalized);
    }

    private static String normalizeQuery(String query) {
        String lower = query.toLowerCase(Locale.ROOT);
        StringBuilder normalized = new StringBuilder(lower.length());
        for (int i = 0; i < lower.length(); i++) {
            char ch = lower.charAt(i);
            if ((ch >= 'a' && ch <= 'z') || (ch >= '0' && ch <= '9')) {
                normalized.append(ch);
            } else if (!Character.isWhitespace(ch) && ch != '\'' && ch != '-') {
                return "";
            }
        }
        return normalized.toString();
    }

    private static boolean containsHan(String label) {
        for (int i = 0; i < label.length();) {
            int codePoint = label.codePointAt(i);
            if (Character.UnicodeScript.of(codePoint) == Character.UnicodeScript.HAN) {
                return true;
            }
            i += Character.charCount(codePoint);
        }
        return false;
    }

    // Do not initialize ICU transliteration until a Latin query encounters a Chinese label.
    private static final class TransliteratorHolder {
        // ICU Han-Latin defaults to character readings (for example, 音乐 -> yin le).
        // Resolve common app-label contexts before falling back to the platform dictionary.
        private static final Transliterator INSTANCE =
                Transliterator.createFromRules("App-Pinyin",
                        "音 { [乐樂] } > ' yue ';"
                                + "[银銀] { 行 } > ' hang ';"
                                + "[长長] } [安城江沙] > ' chang ';"
                                + ":: Han-Latin; :: Latin-ASCII; :: Lower;",
                        Transliterator.FORWARD);
    }

    private static final class PinyinLabel {
        private final String mFull;
        private final String mInitials;
        private final List<Integer> mSyllableStarts = new ArrayList<>();

        PinyinLabel(String transliterated) {
            StringBuilder full = new StringBuilder();
            StringBuilder initials = new StringBuilder();
            Matcher tokens = TOKEN.matcher(transliterated);
            while (tokens.find()) {
                mSyllableStarts.add(full.length());
                full.append(tokens.group());
                initials.append(tokens.group().charAt(0));
            }
            mFull = full.toString();
            mInitials = initials.toString();
        }

        boolean matches(String query) {
            for (int i = 0; i < mSyllableStarts.size(); i++) {
                if (mFull.startsWith(query, mSyllableStarts.get(i))
                        || mInitials.startsWith(query, i)) {
                    return true;
                }
            }
            return false;
        }
    }
}
