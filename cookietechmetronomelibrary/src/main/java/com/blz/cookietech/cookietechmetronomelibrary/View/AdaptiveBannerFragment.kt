package com.blz.cookietech.cookietechmetronomelibrary.View

import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.blz.cookietech.cookietechmetronomelibrary.BuildConfig
import com.blz.cookietech.cookietechmetronomelibrary.R
import com.google.android.gms.ads.*

import kotlinx.android.synthetic.main.fragment_adaptive_banner.*
import java.util.*

class AdaptiveBannerFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var adView: AdView
    private var AD_UNIT_ID = "ca-app-pub-3940256099942544/6300978111"
    private var initialLayoutComplete = false
    // Determine the screen width (less decorations) to use for the ad width.
    // If the ad hasn't been laid out, default to the full screen width.
    private val adSize: AdSize
        get() {
            val display = requireActivity().windowManager.defaultDisplay
            val outMetrics = DisplayMetrics()
            display.getMetrics(outMetrics)

            val density = outMetrics.density

            var adWidthPixels = ad_view_container.width.toFloat()
            if (adWidthPixels == 0f) {
                adWidthPixels = outMetrics.widthPixels.toFloat()
            }

            val adWidth = (adWidthPixels / density).toInt()
            return AdSize.getCurrentOrientationBannerAdSizeWithWidth(requireContext(), adWidth)
        }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_adaptive_banner, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(!BuildConfig.DEBUG){
            AD_UNIT_ID = "ca-app-pub-4221538464712089/6253494491";
        }



        adView = AdView(requireContext())
        ad_view_container.addView(adView)
        // Since we're loading the banner based on the adContainerView size, we need to wait until this
        // view is laid out before we can get the width.
        Log.d("akash_banner_debug", "onViewCreated: $adSize")
        ad_view_container.viewTreeObserver.addOnGlobalLayoutListener {
            if (!initialLayoutComplete) {
                initialLayoutComplete = true
                loadBanner()
            }
        }
    }

    /** Called before the activity is destroyed  */
    public override fun onDestroy() {
        adView.destroy()
        super.onDestroy()
    }

    /** Called when leaving the activity  */
    public override fun onPause() {
        adView.pause()
        super.onPause()
    }

    /** Called when returning to the activity  */
    public override fun onResume() {
        super.onResume()
        adView.resume()
    }

    private fun loadBanner() {
        adView.adUnitId = AD_UNIT_ID

        adView.adSize = adSize

        // Create an ad request.
        val adRequest = AdRequest.Builder().build()

        // Start loading the ad in the background.
        adView.loadAd(adRequest)

        adView.adListener = object : AdListener(){
            override fun onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                Log.d("akash_banner_debug", "onAdLoaded: $adSize")
            }

            override fun onAdFailedToLoad(adError : LoadAdError) {
                // Code to be executed when an ad request fails.
                Log.d("akash_banner_debug", "onAdFailedToLoad: $adError")
            }

            override fun onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
                Log.d("akash_banner_debug", "onAdOpened: $adSize")
            }

            override fun onAdClicked() {
                // Code to be executed when the user clicks on an ad.
                Log.d("akash_banner_debug", "onAdClicked: $adSize")
            }

            override fun onAdLeftApplication() {
                // Code to be executed when the user has left the app.
                Log.d("akash_banner_debug", "onAdLeftApplication: $adSize")
            }

            override fun onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
                Log.d("akash_banner_debug", "onAdClosed: $adSize")
            }
        }
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AdaptiveBannerFragment.
         */
        // TODO: Rename and change types and number of parameters

        @JvmStatic
        fun newInstance(/*param1: String, param2: String*/) =
                AdaptiveBannerFragment()/*.apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }*/
    }
}