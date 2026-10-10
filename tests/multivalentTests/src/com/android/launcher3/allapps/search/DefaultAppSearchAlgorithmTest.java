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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import com.android.launcher3.allapps.BaseAllAppsAdapter.AdapterItem;
import com.android.launcher3.model.data.AppInfo;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class DefaultAppSearchAlgorithmTest {
    @Test
    public void pinyinSearch_returnsMatchingApp() {
        AppInfo wechat = app("微信");
        List<AppInfo> apps = List.of(app("Chrome"), wechat, app("支付宝"));

        assertSingleResult(wechat, search(apps, "weixin"));
        assertSingleResult(wechat, search(apps, "wx"));
        assertSingleResult(wechat, search(apps, "WEIXIN"));
    }

    @Test
    public void existingChineseAndLatinSearch_arePreserved() {
        AppInfo mail = app("电子邮件");
        AppInfo chrome = app("Chrome");
        List<AppInfo> apps = List.of(mail, chrome, app("Elephant"));

        assertSingleResult(mail, search(apps, "邮件"));
        assertSingleResult(chrome, search(apps, "chro"));
        assertTrue(search(apps, "phant").isEmpty());
        assertTrue(search(apps, "").isEmpty());
    }

    @Test
    public void nameAndPinyinBothMatch_appIsOnlyAddedOnce() {
        AppInfo wechat = app("微信 wx");
        assertSingleResult(wechat, search(List.of(wechat), "wx"));
    }

    @Test
    public void pinyinSearch_returnsAllMatchesInOrder() {
        List<AppInfo> apps = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            apps.add(app("测试" + i));
        }
        List<AdapterItem> results = search(apps, "ceshi");

        assertEquals(apps.size(), results.size());
        for (int i = 0; i < results.size(); i++) {
            assertSame(apps.get(i), results.get(i).itemInfo);
        }
    }

    private static AppInfo app(String title) {
        AppInfo info = new AppInfo();
        info.title = title;
        return info;
    }

    private static ArrayList<AdapterItem> search(List<AppInfo> apps, String query) {
        return DefaultAppSearchAlgorithm.getTitleMatchResult(apps, query, "私密空间");
    }

    private static void assertSingleResult(AppInfo app, List<AdapterItem> results) {
        assertEquals(1, results.size());
        assertSame(app, results.get(0).itemInfo);
    }
}
