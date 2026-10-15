/*
 * Copyright (C) 2023 The Android Open Source Project
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
package com.android.launcher3.ui;

import static com.android.launcher3.model.data.AppInfo.EMPTY_ARRAY;
import static com.android.launcher3.util.Executors.UI_HELPER_EXECUTOR;

import static com.google.common.truth.Truth.assertThat;

import static org.mockito.Mockito.doReturn;

import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Rect;
import android.os.UserHandle;
import android.os.UserManager;
import android.platform.test.flag.junit.SetFlagsRule;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.FrameLayout;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;

import com.android.launcher3.R;
import com.android.launcher3.allapps.ActivityAllAppsContainerView;
import com.android.launcher3.allapps.AllAppsTopLayoutPolicy;
import com.android.launcher3.allapps.WorkProfileManager;
import com.android.launcher3.logging.StatsLogManager;
import com.android.launcher3.model.data.AppInfo;
import com.android.launcher3.pm.UserCache;
import com.android.launcher3.util.SandboxApplication;
import com.android.launcher3.util.TestActivityContext;
import com.android.launcher3.util.UserIconInfo;
import com.android.launcher3.util.rule.MockUsersRule;
import com.android.launcher3.util.rule.MockUsersRule.MockUser;
import com.android.users.UserType;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

@SmallTest
@RunWith(AndroidJUnit4.class)
@MockUser(userType = UserType.MAIN)
@MockUser(userType = UserType.WORK)
public class ActivityAllAppsContainerViewTest {

    @Rule public MockitoRule mockitoRule = MockitoJUnit.rule();
    @Rule public SandboxApplication app = new SandboxApplication().withModelDependency();
    @Rule public MockUsersRule mockUserRule = new MockUsersRule(app);
    @Rule public TestActivityContext mContext = new TestActivityContext(app);

    private UserHandle mWorkHandle;

    @Mock
    private StatsLogManager mStatsLogManager;
    private AppInfo[] mWorkAppInfo;
    private ActivityAllAppsContainerView<?> mActivityAllAppsContainerView;
    private WorkProfileManager mWorkManager;

    @Rule public final SetFlagsRule mSetFlagsRule = new SetFlagsRule();

    @Before
    public void setUp() {
        mActivityAllAppsContainerView = new ActivityAllAppsContainerView(mContext);
        mWorkHandle = mockUserRule.findUser(UserIconInfo::isWork);

        mWorkManager = new WorkProfileManager(mActivityAllAppsContainerView,
                mStatsLogManager, UserCache.getInstance(app));
        mActivityAllAppsContainerView.setWorkManager(mWorkManager);
        ComponentName componentName = new ComponentName(mContext,
                "com.android.launcher3.tests.Activity" + "Gmail");
        AppInfo gmailWorkAppInfo = new AppInfo(componentName, "Gmail", mWorkHandle, new Intent());
        mWorkAppInfo = new AppInfo[]{gmailWorkAppInfo};
    }

    @Test
    public void testPhoneTopPadding_usesCurrentSafeInset() {
        assertThat(ActivityAllAppsContainerView.calculateTopPadding(0, 120, 24, true))
                .isEqualTo(144);
    }

    @Test
    public void testPhoneTopPadding_keepsLargerProfilePadding() {
        assertThat(ActivityAllAppsContainerView.calculateTopPadding(200, 120, 24, true))
                .isEqualTo(200);
    }

    @Test
    public void testPhoneTopPadding_recalculatesWithoutAccumulatingSpacing() {
        assertThat(ActivityAllAppsContainerView.calculateTopPadding(0, 120, 24, true))
                .isEqualTo(144);
        assertThat(ActivityAllAppsContainerView.calculateTopPadding(0, 60, 24, true))
                .isEqualTo(84);
    }

    @Test
    public void testTabletTopPadding_remainsUnchanged() {
        assertThat(ActivityAllAppsContainerView.calculateTopPadding(0, 120, 24, false))
                .isEqualTo(0);
        assertThat(ActivityAllAppsContainerView.calculateTopPadding(400, 120, 24, false))
                .isEqualTo(400);
    }

    @Test
    public void testSearchInsets_containerOwnedInsetDoesNotChangeSearchMarginOrTranslation() {
        ActivityAllAppsContainerView<?> container = new ActivityAllAppsContainerView(mContext) {
            @Override
            protected boolean ownsSearchTopInset() {
                return true;
            }
        };
        container.setLayoutParams(new FrameLayout.LayoutParams(500, 1000));
        container.getSearchView().setTranslationY(23);
        MarginLayoutParams original = (MarginLayoutParams)
                container.getSearchView().getLayoutParams();
        original.leftMargin = 17;
        original.rightMargin = 19;
        for (int inset : new int[]{120, 60, 120, 0}) {
            container.setInsets(new Rect(0, inset, 0, 0));
            MarginLayoutParams lp = (MarginLayoutParams)
                    container.getSearchView().getLayoutParams();
            assertThat(lp.topMargin).isEqualTo(AllAppsTopLayoutPolicy.calculatePhoneSearchMargin(
                    container.getResources().getDimensionPixelSize(R.dimen.bottom_sheet_handle_margin)
                            + container.getResources().getDimensionPixelSize(
                                    R.dimen.bottom_sheet_handle_height),
                    container.getResources().getDimensionPixelSize(R.dimen.all_apps_phone_search_gap),
                    container.getResources().getDimensionPixelSize(
                            R.dimen.all_apps_search_bar_content_overlap),
                    container.getResources().getDimensionPixelSize(
                            R.dimen.uwu_qsb_widget_vertical_padding)));
            assertThat(lp.leftMargin).isEqualTo(17);
            assertThat(lp.rightMargin).isEqualTo(19);
            assertThat(container.getSearchView().getTranslationY()).isEqualTo(23f);
        }
    }

    @Test
    public void testSearchInsets_baseHostRetainsLegacyMarginWithoutAccumulation() {
        mActivityAllAppsContainerView.setLayoutParams(new FrameLayout.LayoutParams(500, 1000));
        for (int inset : new int[]{120, 60, 120, 0}) {
            mActivityAllAppsContainerView.setInsets(new Rect(0, inset, 0, 0));
            MarginLayoutParams lp = (MarginLayoutParams)
                    mActivityAllAppsContainerView.getSearchView().getLayoutParams();
            assertThat(lp.topMargin).isEqualTo(inset);
        }
    }

    @Test
    public void testOnAppsUpdatedWithoutWorkApps_shouldShowTabsIsFalse() {
        mActivityAllAppsContainerView.getAppsStore().setApps(EMPTY_ARRAY, 0, null);

        mActivityAllAppsContainerView.onAppsUpdated();

        assertThat(mActivityAllAppsContainerView.shouldShowTabs()).isEqualTo(false);
    }

    @Test
    public void testOnAppsUpdatedWithWorkApps_shouldShowTabsIsTrue() {
        mActivityAllAppsContainerView.getAppsStore().setApps(mWorkAppInfo, 0, null);

        mActivityAllAppsContainerView.onAppsUpdated();

        assertThat(mActivityAllAppsContainerView.shouldShowTabs()).isEqualTo(true);
    }

    @Test
    public void testWorkProfileEnabled_requestQuietModeCalledCorrectly() throws Exception {
        /* Setup */
        UserManager userManager = app.spyService(UserManager.class);
        doReturn(true).when(userManager).requestQuietModeEnabled(false, mWorkHandle);

        /* Execution */
        mWorkManager.setWorkProfileEnabled(true);

        /* Assertion */
        awaitTasksCompleted();
        Mockito.verify(userManager).requestQuietModeEnabled(false, mWorkHandle);
    }

    private static void awaitTasksCompleted() throws Exception {
        UI_HELPER_EXECUTOR.submit(() -> null).get();
    }
}
