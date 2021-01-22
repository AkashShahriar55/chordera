package com.cookietech.chordera.Util

import android.content.Context
import androidx.lifecycle.LiveData
import com.cookietech.chordera.BuildConfig
import com.cookietech.chordera.appcomponents.SingleLiveEvent
import com.google.android.gms.ads.*

import com.google.android.gms.ads.formats.UnifiedNativeAd
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions

const val testNativeAdId = "ca-app-pub-3940256099942544/2247696110"
const val nativeAdId = "ca-app-pub-4221538464712089/4832518490"

interface NativeAdListener{
    fun onAdLoaded(nativeAd: NativeAd)
    fun onAdLoadFailed(adError: LoadAdError)
}

class NativeAdsManager(val context:Context) {
    lateinit var nativeAd:NativeAd

    fun fetchNativeAd(nativeAdListener: NativeAdListener){
        val finalAdId = when{
            BuildConfig.DEBUG -> testNativeAdId
            else -> nativeAdId
        }
        val videoOptions = VideoOptions.Builder().setStartMuted(true).build();
        val nativeAdOptions = NativeAdOptions.Builder().setVideoOptions(videoOptions).build()
        val adLoader = AdLoader.Builder(context, finalAdId)
                .forNativeAd { ad ->
                    nativeAdListener.onAdLoaded(ad)
                }
                .withAdListener(object : AdListener() {
                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        // Handle the failure by logging, altering the UI, and so on.
                        nativeAdListener.onAdLoadFailed(adError)
                    }
                })
                .withNativeAdOptions(nativeAdOptions)
                .build()
        adLoader.loadAd(AdRequest.Builder().build())
    }
}