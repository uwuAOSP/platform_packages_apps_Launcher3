/*
 * Copyright (C) 2024 Lawnchair
 * Copyright (C) 2026 The uwuAOSP Project
 *
 * This file is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package com.android.launcher3.allapps;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.text.TextUtils;
import android.util.Log;

import com.android.launcher3.model.data.AppInfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Builds the Caddy app categories from the bundled flowerpot rules.
 */
final class CaddyCategorizer {
    private static final String TAG = "CaddyCategorizer";
    private static final String ASSET_PATH = "flowerpot";
    private static final String GOOGLE_APPS = "Google Apps";
    private static final String SYSTEM_APPS = "System Apps";
    private static final String OTHER = "Other";

    private final Context mContext;
    private final PackageManager mPackageManager;
    private final Map<String, List<Rule>> mRulesByCategory = new TreeMap<>();
    private boolean mLoaded;

    CaddyCategorizer(Context context) {
        mContext = context.getApplicationContext();
        mPackageManager = mContext.getPackageManager();
    }

    Map<String, List<AppInfo>> categorize(List<AppInfo> apps) {
        ensureLoaded();

        Map<String, List<AppInfo>> result = new LinkedHashMap<>();
        List<AppInfo> remaining = new ArrayList<>();
        for (AppInfo app : apps) {
            String packageName = app.getTargetPackage();
            if (TextUtils.isEmpty(packageName)) {
                continue;
            }
            if (packageName.startsWith("com.google.")) {
                result.computeIfAbsent(GOOGLE_APPS, key -> new ArrayList<>()).add(app);
            } else if (isSystemApp(packageName)) {
                result.computeIfAbsent(SYSTEM_APPS, key -> new ArrayList<>()).add(app);
            } else {
                remaining.add(app);
            }
        }

        Set<String> categorizedKeys = new HashSet<>();
        Map<String, List<AppInfo>> appsByPackage = new HashMap<>();
        for (AppInfo app : remaining) {
            if (!TextUtils.isEmpty(app.getTargetPackage())) {
                appsByPackage.computeIfAbsent(app.getTargetPackage(), key -> new ArrayList<>())
                        .add(app);
            }
        }

        for (Map.Entry<String, List<Rule>> entry : mRulesByCategory.entrySet()) {
            String category = prettifyCategoryName(entry.getKey());
            for (Rule rule : entry.getValue()) {
                for (String packageName : rule.matchingPackages(mPackageManager)) {
                    List<AppInfo> matchedApps = appsByPackage.get(packageName);
                    if (matchedApps == null) {
                        continue;
                    }
                    for (AppInfo app : matchedApps) {
                        String key = String.valueOf(app.getComponentKey());
                        if (categorizedKeys.add(key)) {
                            result.computeIfAbsent(category, ignored -> new ArrayList<>()).add(app);
                        }
                    }
                }
            }
        }

        List<AppInfo> otherApps = new ArrayList<>();
        for (AppInfo app : remaining) {
            if (!categorizedKeys.contains(String.valueOf(app.getComponentKey()))) {
                otherApps.add(app);
            }
        }
        if (!otherApps.isEmpty()) {
            result.put(OTHER, otherApps);
        }
        return result;
    }

    private void ensureLoaded() {
        if (mLoaded) {
            return;
        }
        mLoaded = true;
        try {
            String[] files = mContext.getAssets().list(ASSET_PATH);
            if (files == null) {
                return;
            }
            Arrays.sort(files);
            for (String file : files) {
                loadCategory(file);
            }
        } catch (IOException e) {
            Log.w(TAG, "Failed to load Caddy categories", e);
        }
    }

    private void loadCategory(String category) {
        List<Rule> rules = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                mContext.getAssets().open(ASSET_PATH + "/" + category)))) {
            boolean versionSeen = false;
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                if (line.startsWith("$")) {
                    versionSeen = true;
                    continue;
                }
                if (!versionSeen) {
                    Log.w(TAG, "Skipping rule before flowerpot version in " + category);
                    continue;
                }
                Rule rule = Rule.parse(line);
                if (rule != null) {
                    rules.add(rule);
                }
            }
        } catch (IOException e) {
            Log.w(TAG, "Failed to load flowerpot category " + category, e);
        }
        if (!rules.isEmpty()) {
            mRulesByCategory.put(category, rules);
        }
    }

    private boolean isSystemApp(String packageName) {
        try {
            ApplicationInfo info = mPackageManager.getApplicationInfo(packageName, 0);
            return (info.flags & ApplicationInfo.FLAG_SYSTEM) != 0;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    private static String prettifyCategoryName(String category) {
        String[] words = category.toLowerCase(Locale.US).split("_");
        StringBuilder builder = new StringBuilder();
        for (String word : words) {
            if (word.isEmpty()) {
                continue;
            }
            if (builder.length() > 0) {
                builder.append(' ');
            }
            builder.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1));
        }
        return builder.toString();
    }

    private interface Rule {
        Set<String> matchingPackages(PackageManager packageManager);

        static Rule parse(String line) {
            char prefix = line.charAt(0);
            if (prefix == ':') {
                return new IntentRule(new Intent(line.substring(1)));
            }
            if (prefix == ';') {
                return new IntentRule(new Intent(Intent.ACTION_MAIN).addCategory(line.substring(1)));
            }
            if (prefix == '&') {
                return CodeRule.parse(line.substring(1));
            }
            if (Character.isLetter(prefix)) {
                return packageManager -> Collections.singleton(line);
            }
            Log.w(TAG, "Unknown flowerpot rule: " + line);
            return null;
        }
    }

    private static final class IntentRule implements Rule {
        private final Intent mIntent;

        IntentRule(Intent intent) {
            mIntent = intent;
        }

        @Override
        public Set<String> matchingPackages(PackageManager packageManager) {
            Set<String> packages = new HashSet<>();
            for (ResolveInfo info : packageManager.queryIntentActivities(mIntent, 0)) {
                if (info.activityInfo != null && info.activityInfo.packageName != null) {
                    packages.add(info.activityInfo.packageName);
                }
            }
            return packages;
        }
    }

    private abstract static class CodeRule implements Rule {
        static CodeRule parse(String rule) {
            String[] parts = rule.split("\\|");
            String name = parts[0];
            if ("isGame".equals(name)) {
                return new CodeRule() {
                    @Override
                    boolean matches(ApplicationInfo info) {
                        return (info.flags & ApplicationInfo.FLAG_IS_GAME) != 0;
                    }
                };
            }
            if ("category".equals(name) && parts.length == 2) {
                int category = categoryFor(parts[1]);
                return new CodeRule() {
                    @Override
                    boolean matches(ApplicationInfo info) {
                        return info.category == category;
                    }
                };
            }
            Log.w(TAG, "Unknown flowerpot code rule: " + rule);
            return null;
        }

        @Override
        public Set<String> matchingPackages(PackageManager packageManager) {
            Set<String> packages = new HashSet<>();
            for (ApplicationInfo info : packageManager.getInstalledApplications(0)) {
                if (matches(info)) {
                    packages.add(info.packageName);
                }
            }
            return packages;
        }

        abstract boolean matches(ApplicationInfo info);

        private static int categoryFor(String value) {
            switch (value) {
                case "game":
                    return ApplicationInfo.CATEGORY_GAME;
                case "audio":
                    return ApplicationInfo.CATEGORY_AUDIO;
                case "video":
                    return ApplicationInfo.CATEGORY_VIDEO;
                case "image":
                    return ApplicationInfo.CATEGORY_IMAGE;
                case "social":
                    return ApplicationInfo.CATEGORY_SOCIAL;
                case "news":
                    return ApplicationInfo.CATEGORY_NEWS;
                case "maps":
                    return ApplicationInfo.CATEGORY_MAPS;
                case "productivity":
                    return ApplicationInfo.CATEGORY_PRODUCTIVITY;
                default:
                    return ApplicationInfo.CATEGORY_UNDEFINED;
            }
        }
    }
}
