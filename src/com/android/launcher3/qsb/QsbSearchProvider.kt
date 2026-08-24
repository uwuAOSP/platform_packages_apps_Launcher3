/*
 * Copyright (C) 2026 Lawnchair
 * Copyright (C) 2026 The uwuAOSP Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.android.launcher3.qsb

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.android.launcher3.Launcher
import com.android.launcher3.R

/** Search providers supported by the dock search bar. */
enum class QsbSearchProvider(
    val id: String,
    @StringRes val nameRes: Int,
    @DrawableRes val icon: Int = R.drawable.ic_qsb_search,
    @DrawableRes val themedIcon: Int = icon,
    val themingMethod: ThemingMethod = ThemingMethod.TINT,
    val packageName: String = "",
    val action: String? = null,
    val className: String? = null,
    val supportsVoice: Boolean = false,
    val voiceClassName: String? = null,
    val website: String = "",
    val type: QsbSearchProviderType = QsbSearchProviderType.APP_AND_WEBSITE,
    val supportsLens: Boolean = false,
) {
    APP_SEARCH(
        id = "app_search",
        nameRes = R.string.search_provider_app_search,
        type = QsbSearchProviderType.LOCAL,
    ),
    GOOGLE(
        id = "google",
        nameRes = R.string.search_provider_google,
        icon = R.drawable.ic_super_g_color,
        themingMethod = ThemingMethod.THEME_BY_LAYER_ID,
        packageName = "com.google.android.googlequicksearchbox",
        action = "android.search.action.GLOBAL_SEARCH",
        supportsVoice = true,
        website = "https://www.google.com/",
        supportsLens = true,
    ),
    GOOGLE_GO(
        id = "google_go",
        nameRes = R.string.search_provider_google_go,
        icon = R.drawable.ic_super_g_color,
        themingMethod = ThemingMethod.THEME_BY_LAYER_ID,
        packageName = "com.google.android.apps.searchlite",
        action = "android.search.action.GLOBAL_SEARCH",
        supportsVoice = true,
        website = "https://www.google.com/",
        type = QsbSearchProviderType.APP,
    ),
    YOUTUBE(
        id = "youtube",
        nameRes = R.string.search_provider_youtube,
        icon = R.drawable.ic_youtube,
        themingMethod = ThemingMethod.THEME_BY_LAYER_ID,
        packageName = "com.google.android.youtube",
        action = Intent.ACTION_SEARCH,
        website = "https://youtube.com/",
    ),
    PIXEL_SEARCH(
        id = "pixel_search",
        nameRes = R.string.search_provider_pixel_search,
        icon = R.drawable.ic_super_g_color,
        themingMethod = ThemingMethod.THEME_BY_LAYER_ID,
        packageName = "rk.android.app.pixelsearch",
        supportsVoice = true,
        website = "https://play.google.com/store/apps/details?id=rk.android.app.pixelsearch",
        type = QsbSearchProviderType.APP,
        supportsLens = true,
    ),
    SESAME(
        id = "sesame",
        nameRes = R.string.search_provider_sesame,
        icon = R.drawable.ic_sesame,
        packageName = "ninja.sesame.app.edge",
        className = "ninja.sesame.app.edge.omni.OmniActivity",
        website = "https://play.google.com/store/apps/details?id=ninja.sesame.app.edge",
        type = QsbSearchProviderType.APP,
    ),
    WIKIPEDIA(
        id = "wikipedia",
        nameRes = R.string.search_provider_wikipedia,
        icon = R.drawable.ic_wikipedia,
        packageName = "org.wikipedia",
        className = "org.wikipedia.search.SearchActivity",
        website = "https://wikipedia.com/",
    ),
    GITHUB(
        id = "github",
        nameRes = R.string.search_provider_github,
        icon = R.drawable.ic_github,
        website = "https://github.com/search",
        type = QsbSearchProviderType.WEBSITE,
    ),
    DUCKDUCKGO(
        id = "duckduckgo",
        nameRes = R.string.search_provider_duckduckgo,
        icon = R.drawable.ic_duckduckgo,
        themedIcon = R.drawable.ic_duckduckgo_tinted,
        packageName = "com.duckduckgo.mobile.android",
        action = "com.duckduckgo.mobile.android.NEW_SEARCH",
        website = "https://duckduckgo.com/",
    ),
    ECOSIA(
        id = "ecosia",
        nameRes = R.string.search_provider_ecosia,
        icon = R.drawable.ic_ecosia,
        packageName = "com.ecosia.android",
        website = "https://www.ecosia.org/",
    ),
    PRESEARCH(
        id = "presearch",
        nameRes = R.string.search_provider_presearch,
        icon = R.drawable.ic_presearch,
        themedIcon = R.drawable.ic_presearch_tinted,
        packageName = "com.presearch",
        className = "org.chromium.chrome.browser.TextSearchActivity",
        supportsVoice = true,
        voiceClassName = "org.chromium.chrome.browser.VoiceSearchActivity",
        website = "https://presearch.com/",
    ),
    BING(
        id = "bing",
        nameRes = R.string.search_provider_bing,
        icon = R.drawable.ic_bing,
        packageName = "com.microsoft.bing",
        className = "com.microsoft.clients.bing.autosuggest.AutoSuggestActivity",
        supportsVoice = true,
        voiceClassName = "com.microsoft.clients.bing.voice.VoiceActivity",
        website = "https://bing.com/",
    ),
    BRAVE(
        id = "brave",
        nameRes = R.string.search_provider_brave,
        icon = R.drawable.ic_brave,
        themedIcon = R.drawable.ic_brave_tinted,
        packageName = "com.brave.browser",
        className = "org.chromium.chrome.browser.searchwidget.SearchWidgetProviderActivity",
        website = "https://search.brave.com/",
    ),
    YANDEX(
        id = "yandex",
        nameRes = R.string.search_provider_yandex,
        icon = R.drawable.ic_yandex,
        themedIcon = R.drawable.ic_yandex_tinted,
        packageName = "com.yandex.searchapp",
        className = "ru.yandex.searchplugin.MainActivity",
        supportsVoice = true,
        voiceClassName = "ru.yandex.searchplugin.AssistantActivityAlias",
        website = "https://ya.ru/",
    ),
    FENNEC(
        id = "Fennec",
        nameRes = R.string.search_provider_fennec,
        icon = R.drawable.ic_fennec,
        themedIcon = R.drawable.ic_fennec_tinted,
        packageName = "org.mozilla.fennec_fdroid",
        action = "org.mozilla.fenix.OPEN_TAB",
        className = "org.mozilla.fenix.IntentReceiverActivity",
        supportsVoice = true,
        voiceClassName = "org.chromium.chrome.browser.VoiceSearchActivity",
        website = "https://f-droid.org/packages/org.mozilla.fennec_fdroid/",
        type = QsbSearchProviderType.APP,
    ),
    FIREFOX(
        id = "Firefox",
        nameRes = R.string.search_provider_firefox,
        icon = R.drawable.ic_firefox,
        themedIcon = R.drawable.ic_firefox_tinted,
        packageName = "org.mozilla.firefox",
        action = "org.mozilla.fenix.OPEN_TAB",
        className = "org.mozilla.fenix.IntentReceiverActivity",
        website = "https://play.google.com/store/apps/details?id=org.mozilla.firefox",
        type = QsbSearchProviderType.APP,
    ),
    ICERAVEN(
        id = "Iceraven",
        nameRes = R.string.search_provider_iceraven,
        icon = R.drawable.ic_iceraven,
        themedIcon = R.drawable.ic_iceraven_tinted,
        packageName = "io.github.forkmaintainers.iceraven",
        action = "org.mozilla.fenix.OPEN_TAB",
        className = "org.mozilla.fenix.IntentReceiverActivity",
        supportsVoice = true,
        voiceClassName = "org.chromium.chrome.browser.VoiceSearchActivity",
        website = "https://github.com/fork-maintainers/iceraven-browser/releases/latest",
        type = QsbSearchProviderType.APP,
    ),
    STARTPAGE(
        id = "startpage",
        nameRes = R.string.search_provider_startpage,
        icon = R.drawable.ic_startpage,
        packageName = "com.startpage.app",
        className = "org.chromium.chrome.browser.searchwidget.SearchActivity",
        website = "https://startpage.com/?segment=startpage.lawnchair",
    ),
    STARTPAGE_EU(
        id = "startpage-eu",
        nameRes = R.string.search_provider_startpage_eu,
        icon = R.drawable.ic_startpage,
        website = "https://eu.startpage.com/?segment=startpage.lawnchair",
        type = QsbSearchProviderType.LOCAL,
    ),
    IRONFOX(
        id = "IronFox",
        nameRes = R.string.search_provider_ironfox,
        icon = R.drawable.ic_ironfox,
        themedIcon = R.drawable.ic_ironfox_tinted,
        packageName = "org.ironfoxoss.ironfox",
        action = "org.mozilla.fenix.OPEN_TAB",
        className = "org.mozilla.fenix.IntentReceiverActivity",
        supportsVoice = true,
        voiceClassName = "org.chromium.chrome.browser.VoiceSearchActivity",
        website = "https://gitlab.com/ironfox-oss/IronFox",
        type = QsbSearchProviderType.APP,
    ),
    WATERFOX(
        id = "Waterfox",
        nameRes = R.string.search_provider_waterfox,
        icon = R.drawable.ic_waterfox,
        themedIcon = R.drawable.ic_waterfox_tinted,
        packageName = "net.waterfox.android.release",
        action = "org.mozilla.fenix.OPEN_TAB",
        className = "org.mozilla.fenix.IntentReceiverActivity",
        website = "https://github.com/BrowserWorks/Waterfox",
        type = QsbSearchProviderType.APP,
    ),
    KAGI(
        id = "kagi",
        nameRes = R.string.search_provider_kagi,
        icon = R.drawable.ic_kagi,
        themedIcon = R.drawable.ic_kagi_tinted,
        packageName = "com.kagi.search",
        action = "WIDGET_SEARCH_TEXT",
        className = "com.kagi.search.HomeActivity",
        website = "https://kagi.com",
    ),
    CROMITE(
        id = "cromite",
        nameRes = R.string.search_provider_cromite,
        icon = R.drawable.ic_cromite,
        themedIcon = R.drawable.ic_cromite_tinted,
        packageName = "org.cromite.cromite",
        action = Intent.ACTION_WEB_SEARCH,
        className = "org.chromium.chrome.browser.searchwidget.SearchActivity",
        website = "https://github.com/uazo/cromite/releases/latest",
        type = QsbSearchProviderType.APP,
    ),
    VIVALDI(
        id = "vivaldi",
        nameRes = R.string.search_provider_vivaldi,
        icon = R.drawable.ic_vivaldi,
        themedIcon = R.drawable.ic_vivaldi_tinted,
        packageName = "com.vivaldi.browser",
        className = "org.chromium.chrome.browser.searchwidget.SearchWidgetProviderActivity",
        website = "https://vivaldi.com/",
    );

    fun launch(launcher: Launcher, forceWebsite: Boolean) {
        if (this == APP_SEARCH) {
            launcher.toggleAllApps(true)
            return
        }
        val intent = if (forceWebsite) null else createSearchIntent().takeIf(launcher::canResolve)
        launcher.startActivity(intent ?: createWebsiteIntent())
    }

    fun createSearchIntent(): Intent = Intent(action)
        .addFlags(INTENT_FLAGS)
        .apply {
            if (className != null) setClassName(packageName, className)
            else if (packageName.isNotEmpty()) setPackage(packageName)
        }

    fun createVoiceIntent(): Intent = when (this) {
        GOOGLE_GO -> createSearchIntent().putExtra("openMic", true)
        PIXEL_SEARCH -> Intent(Intent.ACTION_VOICE_COMMAND).addFlags(INTENT_FLAGS)
        else -> Intent(Intent.ACTION_VOICE_COMMAND).addFlags(INTENT_FLAGS).apply {
            if (voiceClassName != null) setClassName(packageName, voiceClassName)
            else if (packageName.isNotEmpty()) setPackage(packageName)
        }
    }

    fun createWebsiteIntent(): Intent = Intent(Intent.ACTION_VIEW, Uri.parse(website))
        .addFlags(INTENT_FLAGS)

    fun isAvailable(context: Context): Boolean = when (type) {
        QsbSearchProviderType.LOCAL -> this == APP_SEARCH || context.canResolve(createWebsiteIntent())
        QsbSearchProviderType.WEBSITE -> context.canResolve(createWebsiteIntent())
        QsbSearchProviderType.APP -> context.canResolve(createSearchIntent())
        QsbSearchProviderType.APP_AND_WEBSITE ->
            context.canResolve(createSearchIntent()) || context.canResolve(createWebsiteIntent())
    }

    companion object {
        private const val INTENT_FLAGS =
            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        fun fromId(id: String): QsbSearchProvider = entries.firstOrNull { it.id == id } ?: APP_SEARCH

        fun resolveDefault(context: Context): QsbSearchProvider =
            entries.firstOrNull { it != APP_SEARCH && it.isAvailable(context) } ?: APP_SEARCH
    }
}

enum class QsbSearchProviderType {
    APP_AND_WEBSITE,
    WEBSITE,
    APP,
    LOCAL,
}

private fun Context.canResolve(intent: Intent): Boolean =
    packageManager.resolveActivity(intent, 0) != null
