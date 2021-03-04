package com.cookietech.chordera.views

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.cookietech.chordera.BuildConfig
import com.google.android.gms.ads.*
import com.google.android.gms.ads.formats.MediaView
import com.google.android.gms.ads.formats.NativeAdOptions
import com.google.android.gms.ads.formats.UnifiedNativeAd
import com.google.android.gms.ads.formats.UnifiedNativeAdView
import com.cookietech.chordera.R
import com.cookietech.chordera.appcomponents.RemoteConfigManager


class CustomExitDialog(context: Context, val exitDialogCommunicator: ExitDialogCommunicator) : Dialog(context) {
    private var mLastClickTime: Long = 0
    private var isAdloaded: Boolean = false
    lateinit var adLoader: AdLoader
    lateinit var adview: UnifiedNativeAdView
    private var nativadID: String ?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.exit_dialog)
        val yes = findViewById<TextView>(R.id.positive_btn)
        val no = findViewById<TextView>(R.id.negative_btn)

        yes.setOnClickListener {
            exitDialogCommunicator.onPositiveButtonClicked()
        }

        no.setOnClickListener {
            dismiss()
        }

        adview = findViewById<UnifiedNativeAdView>(R.id.unifiedNativeAdView)
        if (BuildConfig.DEBUG) {
            nativadID = "ca-app-pub-3940256099942544/1044960115"
        } else {
            nativadID = "ca-app-pub-4221538464712089/4832518490"
        }

        if(RemoteConfigManager.shouldShowNativeAdAtExit())
            loadAd()
    }

    private fun loadAd() {
        adLoader = AdLoader.Builder(context, nativadID)
                .forUnifiedNativeAd { ad: UnifiedNativeAd ->
                    // Show the ad.
                    Log.i("AdFragment", "Add Loaded")
                    populateUnifiedNativeAdView(ad, adview)
                    isAdloaded = true;
                   /* if(nativadlistener!=null){
                        nativadlistener?.nativeAdLoaded()
                    }*/
                }
                .withAdListener(object : AdListener() {
                    override fun onAdFailedToLoad(errorCode: Int) {
                        Log.i("AdFragment", "Add Load failed " + errorCode)

                        // Handle the failure by logging, altering the UI, and so on.
                    }
                })
                .withNativeAdOptions(adOptions)
                .build()


        adLoader.loadAd(AdRequest.Builder().build())

    }

    private fun populateUnifiedNativeAdView(nativeAd: UnifiedNativeAd, adView: UnifiedNativeAdView) {
        // Set the media view. Media content will be automatically populated in the media view once
        // adView.setNativeAd() is called.
        val mediaView = adView.findViewById<MediaView>(R.id.media_shop_ad)
        adView.mediaView = mediaView

//        // Set other ad assets.
        adView.headlineView = adView.findViewById(R.id.txt_title_ad)
        adView.bodyView = adView.findViewById(R.id.txt_sub_title_ad)
        adView.callToActionView = adView.findViewById(R.id.txt_buy_ad)
        adView.iconView = adView.findViewById(R.id.icon)
//        adView.priceView = adView.findViewById(R.id.ad_price)
//        adView.starRatingView = adView.findViewById(R.id.ad_stars)
//        adView.storeView = adView.findViewById(R.id.ad_store)
//        adView.advertiserView = adView.findViewById(R.id.ad_advertiser)

        // The headline is guaranteed to be in every UnifiedNativeAd.
        (adView.headlineView as TextView).setText(nativeAd.headline)
//
//        // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
//        // check before trying to display them.
        if (nativeAd.body == null) {
            adView.bodyView.visibility = View.INVISIBLE
        } else {
            adView.bodyView.visibility = View.VISIBLE
            (adView.bodyView as TextView).setText(nativeAd.body)
        }
//
        if (nativeAd.callToAction == null) {
            adView.callToActionView.visibility = View.INVISIBLE
        } else {
            adView.callToActionView.visibility = View.VISIBLE
            (adView.callToActionView as Button).setText(nativeAd.callToAction)
        }
//
        if (nativeAd.icon == null) {
            adView.iconView.visibility = View.GONE
        } else {
            (adView.iconView as ImageView).setImageDrawable(
                    nativeAd.icon.drawable)
            adView.iconView.visibility = View.VISIBLE
        }
//
//        if (nativeAd.price == null) {
//            adView.priceView.visibility = View.INVISIBLE
//        } else {
//            adView.priceView.visibility = View.VISIBLE
//            (adView.priceView as TextView).setText(nativeAd.price)
//        }
//
//        if (nativeAd.store == null) {
//            adView.storeView.visibility = View.INVISIBLE
//        } else {
//            adView.storeView.visibility = View.VISIBLE
//            (adView.storeView as TextView).setText(nativeAd.store)
//        }
//
//        if (nativeAd.starRating == null) {
//            adView.starRatingView.visibility = View.INVISIBLE
//        } else {
//            (adView.starRatingView as RatingBar)
//                    .setRating(nativeAd.starRating!!.toFloat())
//            adView.starRatingView.visibility = View.VISIBLE
//        }
//
//        if (nativeAd.advertiser == null) {
//            adView.advertiserView.visibility = View.INVISIBLE
//        } else {
//            (adView.advertiserView as TextView).setText(nativeAd.advertiser)
//            adView.advertiserView.visibility = View.VISIBLE
//        }

        val videoController: VideoController = nativeAd.getVideoController()

        videoController.setVideoLifecycleCallbacks(object : VideoController.VideoLifecycleCallbacks() {
            override fun onVideoStart() {
                super.onVideoStart()
            }
        })

        // This method tells the Google Mobile Ads SDK that you have finished populating your
        // native ad view with this native ad. The SDK will populate the adView's MediaView
        // with the media content from this native ad.
        adView.setNativeAd(nativeAd)
        adView.visibility = View.VISIBLE



    }

    var videoOptions = VideoOptions.Builder()
            .setStartMuted(true)
            .build()

    var adOptions = NativeAdOptions.Builder()
            .setVideoOptions(videoOptions)
            .build()


    interface ExitDialogCommunicator{
        fun onPositiveButtonClicked()
    }
}