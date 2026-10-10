/*
 * Copyright (C) 2015 The Android Open Source Project
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

import static com.android.launcher3.allapps.AlphabeticalAppsList.PRIVATE_SPACE_PACKAGE;
import static com.android.launcher3.allapps.BaseAllAppsAdapter.VIEW_TYPE_EMPTY_SEARCH;
import static com.android.launcher3.allapps.BaseAllAppsAdapter.VIEW_TYPE_PRIVATE_SPACE_HEADER;

import android.content.Context;
import android.os.Handler;

import androidx.annotation.AnyThread;

import com.android.launcher3.LauncherAppState;
import com.android.launcher3.LauncherPrefs;
import com.android.launcher3.R;
import com.android.launcher3.allapps.BaseAllAppsAdapter.AdapterItem;
import com.android.launcher3.model.data.AppInfo;
import com.android.launcher3.search.SearchAlgorithm;
import com.android.launcher3.search.SearchCallback;
import com.android.launcher3.search.StringMatcherUtility;
import com.android.launcher3.util.LooperExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicLong;

import org.uwuaosp.pinyin.PinyinSearch;

/**
 * The default search implementation.
 */
public class DefaultAppSearchAlgorithm implements SearchAlgorithm<AdapterItem> {

    private final LauncherAppState mAppState;
    private final LauncherPrefs mPrefs;
    private final Handler mResultHandler;
    private final boolean mAddNoResultsMessage;
    private final String mPrivateSpaceLabel;
    private final AtomicLong mRequestId = new AtomicLong();

    public DefaultAppSearchAlgorithm(Context context, LooperExecutor uiExecutor) {
        this(context, uiExecutor, false);
    }

    public DefaultAppSearchAlgorithm(
            Context context, LooperExecutor uiExecutor, boolean addNoResultsMessage) {
        mAppState = LauncherAppState.getInstance(context);
        mPrefs = LauncherPrefs.get(context);
        mResultHandler = new Handler(uiExecutor.getLooper());
        mAddNoResultsMessage = addNoResultsMessage;
        mPrivateSpaceLabel = context.getString(R.string.private_space_label);
    }

    @Override
    public void cancel(boolean interruptActiveRequests) {
        if (interruptActiveRequests) {
            mRequestId.incrementAndGet();
            mResultHandler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    public void doSearch(String query, SearchCallback<AdapterItem> callback) {
        doSearch(query, null, callback);
    }

    @Override
    public void doSearch(String query, String[] suggestedQueries, SearchCallback<AdapterItem> callback) {
        long requestId = mRequestId.incrementAndGet();
        boolean t9 = "t9".equals(mPrefs.get(LauncherPrefs.APP_SEARCH_INPUT_MODE));
        mAppState.getModel().enqueueModelUpdateTask((taskController, dataModel, apps) ->  {
            if (requestId != mRequestId.get()) return;
            ArrayList<AdapterItem> result = getTitleMatchResult(
                    apps.data, query, mPrivateSpaceLabel, t9, suggestedQueries);
            if (mAddNoResultsMessage && result.isEmpty()) {
                result.add(getEmptyMessageAdapterItem(query));
            }
            mResultHandler.post(() -> {
                if (requestId == mRequestId.get()) callback.onSearchResult(query, result);
            });
        });
    }

    private static AdapterItem getEmptyMessageAdapterItem(String query) {
        AdapterItem item = new AdapterItem(VIEW_TYPE_EMPTY_SEARCH);
        // Add a place holder info to propagate the query
        AppInfo placeHolder = new AppInfo();
        placeHolder.title = query;
        item.itemInfo = placeHolder;
        return item;
    }

    /**
     * Filters {@link AppInfo}s matching specified query
     */
    @AnyThread
    public static ArrayList<AdapterItem> getTitleMatchResult(
            List<AppInfo> apps, String query, String privateSpaceLabel) {
        return getTitleMatchResult(apps, query, privateSpaceLabel, false, null);
    }

    private static ArrayList<AdapterItem> getTitleMatchResult(List<AppInfo> apps, String query,
            String privateSpaceLabel, boolean t9, String[] suggestedQueries) {
        final ArrayList<AdapterItem> result = new ArrayList<>();
        if (query == null || query.trim().isEmpty()) return result;
        ArrayList<String> queries = new ArrayList<>();
        queries.add(query.toLowerCase(Locale.ROOT));
        if (suggestedQueries != null) {
            for (String suggestion : suggestedQueries) {
                if (suggestion != null && !suggestion.trim().isEmpty()) {
                    queries.add(suggestion.toLowerCase(Locale.ROOT));
                }
            }
        }
        ArrayList<PinyinSearch.Query> pinyinQueries = new ArrayList<>();
        for (String text : queries) pinyinQueries.add(PinyinSearch.prepare(text, t9));
        StringMatcherUtility.StringMatcher matcher =
                StringMatcherUtility.StringMatcher.getInstance();

        int total = apps.size();
        boolean hasPrivateSpaceApp = false;
        for (int i = 0; i < total; i++) {
            AppInfo info = apps.get(i);
            if (PRIVATE_SPACE_PACKAGE.equals(info.getTargetPackage())) {
                hasPrivateSpaceApp = true;
            }
            if (info.title != null && matchesTitle(info.title.toString(), queries,
                    pinyinQueries, matcher)) {
                result.add(AdapterItem.asApp(info));
            }
        }
        if (hasPrivateSpaceApp && matchesTitle(privateSpaceLabel, queries, pinyinQueries, matcher)) {
            result.add(new AdapterItem(VIEW_TYPE_PRIVATE_SPACE_HEADER));
        }
        return result;
    }

    private static boolean matchesTitle(String title, List<String> queries,
            List<PinyinSearch.Query> pinyinQueries, StringMatcherUtility.StringMatcher matcher) {
        for (int i = 0; i < queries.size(); i++) {
            if (StringMatcherUtility.matches(queries.get(i), title, matcher)
                    || pinyinQueries.get(i).matches(title)) return true;
        }
        return false;
    }
}
