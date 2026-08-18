package com.chlqudco.flash.ads

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.chlqudco.flash.R
import com.google.android.libraries.ads.mobile.sdk.MobileAds
import com.google.android.libraries.ads.mobile.sdk.banner.AdSize
import com.google.android.libraries.ads.mobile.sdk.banner.AdView
import com.google.android.libraries.ads.mobile.sdk.banner.BannerAd
import com.google.android.libraries.ads.mobile.sdk.banner.BannerAdRequest
import com.google.android.libraries.ads.mobile.sdk.common.AdLoadCallback
import com.google.android.libraries.ads.mobile.sdk.common.LoadAdError
import com.google.android.libraries.ads.mobile.sdk.initialization.InitializationConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.roundToInt

@Composable
internal fun AdMobBanner(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val activity = remember(context) { context.findActivity() }
    val isPreview = LocalInspectionMode.current
    val appId = stringResource(R.string.admob_app_id)
    val bannerId = stringResource(R.string.admob_banner_id)

    BoxWithConstraints(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        val width = maxWidth.value.roundToInt().coerceAtLeast(1)
        val adSize = remember(context, width) {
            AdSize.getLargeAnchoredAdaptiveBannerAdSize(context, width)
        }
        var adView by remember(width) { mutableStateOf<AdView?>(null) }

        LaunchedEffect(activity, width, isPreview, appId, bannerId) {
            if (activity == null || isPreview) return@LaunchedEffect

            try {
                withContext(Dispatchers.IO) {
                    if (!MobileAds.isInitialized) {
                        MobileAds.initialize(
                            context.applicationContext,
                            InitializationConfig.Builder(appId).build()
                        )
                    }
                }

                val view = AdView(activity)
                adView = view
                view.loadAd(
                    BannerAdRequest.Builder(
                        bannerId,
                        adSize
                    ).build(),
                    object : AdLoadCallback<BannerAd> {
                        override fun onAdLoaded(ad: BannerAd) {
                            Log.d(TAG, "Banner ad loaded")
                        }

                        override fun onAdFailedToLoad(adError: LoadAdError) {
                            Log.w(TAG, "Banner ad failed to load: $adError")
                        }
                    }
                )
            } catch (error: RuntimeException) {
                Log.w(TAG, "Banner ad setup failed", error)
            }
        }

        DisposableEffect(adView) {
            onDispose {
                adView?.destroy()
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(adSize.height.dp)
                .background(Color(0xFF05090D)),
            contentAlignment = Alignment.Center
        ) {
            if (adView != null) {
                AndroidView(
                    modifier = Modifier.wrapContentSize(),
                    factory = { adView!! }
                )
            }
        }
    }
}

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

private const val TAG = "AdMobBanner"
