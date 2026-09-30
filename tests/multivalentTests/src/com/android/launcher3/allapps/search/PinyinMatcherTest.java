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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.Locale;

public class PinyinMatcherTest {
    @Test
    public void fullPinyinAndInitials_matchChineseLabels() {
        assertTrue(PinyinMatcher.matches("weixin", "微信"));
        assertTrue(PinyinMatcher.matches("wx", "微信"));
        assertTrue(PinyinMatcher.matches("zfb", "支付宝"));
        assertTrue(PinyinMatcher.matches("zhifub", "支付宝"));
    }

    @Test
    public void partialQuery_matchesAtSyllableBoundaries() {
        assertTrue(PinyinMatcher.matches("xin", "微信"));
        assertTrue(PinyinMatcher.matches("yinyue", "网易云音乐"));
        assertTrue(PinyinMatcher.matches("yy", "网易云音乐"));
        assertFalse(PinyinMatcher.matches("eixin", "微信"));
        assertFalse(PinyinMatcher.matches("hifu", "支付宝"));
    }

    @Test
    public void traditionalAndMixedLabels_match() {
        assertTrue(PinyinMatcher.matches("shezhi", "设置"));
        assertTrue(PinyinMatcher.matches("shezhi", "設置"));
        assertTrue(PinyinMatcher.matches("qqyinyue", "QQ音乐"));
        assertTrue(PinyinMatcher.matches("yinyue", "QQ音樂"));
        assertTrue(PinyinMatcher.matches("bilibili", "哔哩哔哩"));
    }

    @Test
    public void caseSpacesAndSyllableSeparators_areIgnored() {
        assertTrue(PinyinMatcher.matches("WEI XIN", "微信"));
        assertTrue(PinyinMatcher.matches("wei-xin", "微信"));
        assertTrue(PinyinMatcher.matches("wei'xin", "微信"));
    }

    @Test
    public void commonPolyphonicLabels_useContextualReadings() {
        assertTrue(PinyinMatcher.matches("yinyue", "音乐"));
        assertTrue(PinyinMatcher.matches("kuaile", "快乐"));
        assertTrue(PinyinMatcher.matches("yinhang", "银行"));
        assertTrue(PinyinMatcher.matches("yinhang", "銀行"));
        assertTrue(PinyinMatcher.matches("changanchuxing", "长安出行"));
        assertTrue(PinyinMatcher.matches("changan", "長安"));
        assertTrue(PinyinMatcher.matches("chongqing", "重庆"));
    }

    @Test
    public void nonPinyinQueries_doNotMatch() {
        assertFalse(PinyinMatcher.matches("", "微信"));
        assertFalse(PinyinMatcher.matches("  -'", "微信"));
        assertFalse(PinyinMatcher.matches("微信", "微信"));
        assertFalse(PinyinMatcher.matches("wx", "Chrome"));
        assertFalse(PinyinMatcher.matches("weixin!", "微信"));
    }

    @Test
    public void renamedLabel_doesNotReuseOldPinyin() {
        assertTrue(PinyinMatcher.matches("wx", "微信"));
        assertFalse(PinyinMatcher.matches("wx", "支付宝"));
        assertTrue(PinyinMatcher.matches("zfb", "支付宝"));
    }

    @Test
    public void latinQuery_isIndependentOfDefaultLocale() {
        Locale previous = Locale.getDefault();
        try {
            Locale.setDefault(Locale.forLanguageTag("tr-TR"));
            assertTrue(PinyinMatcher.matches("WEIXIN", "微信"));
        } finally {
            Locale.setDefault(previous);
        }
    }

    @Test
    public void cacheEviction_keepsMatchingCorrect() {
        for (int i = 0; i < 300; i++) {
            assertTrue(PinyinMatcher.matches("ceshi" + i, "测试" + i));
        }
        assertTrue(PinyinMatcher.matches("weixin", "微信"));
    }
}
