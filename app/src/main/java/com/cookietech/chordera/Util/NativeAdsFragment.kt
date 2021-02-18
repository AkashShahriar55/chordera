package com.cookietech.chordera.Util

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import com.cookietech.chordera.R
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import kotlinx.android.synthetic.main.fragment_native_ads.*


class NativeAdsFragment : Fragment() {
    private var isFragmentDestroyed: Boolean = false
    lateinit var nativeAdsManager:NativeAdsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        nativeAdsManager = NativeAdsManager(requireContext())
        arguments?.let {

        }
    }



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_native_ads, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        native_ad_container.visibility = View.GONE
        isFragmentDestroyed = false
        val nativeAdListener = object : NativeAdListener{
            override fun onAdLoaded(nativeAd: NativeAd) {
                if(!isFragmentDestroyed){
                    setUpNativeAd(nativeAd)
                    native_ad_container.visibility = View.VISIBLE
                }

            }

            override fun onAdLoadFailed(adError: LoadAdError) {

            }

        }


        nativeAdsManager.fetchNativeAd(nativeAdListener)
    }

    private fun setUpNativeAd(nativeAd: NativeAd) {
        ad_heading.text = nativeAd.headline
        ad_view.mediaView = ad_media
        ad_view.callToActionView = ad_button
        nativeAd.starRating?.let { rating->
            ad_rating.rating = rating.toFloat()
        } ?: let {
            ad_rating.visibility = View.GONE
        }
        nativeAd.mediaContent?.let { mediaContent ->
            ad_media.setMediaContent(mediaContent)
        } ?: let{
            ad_media.visibility = View.GONE
        }

        nativeAd.icon?.let { icon->
            ad_icon.setImageDrawable(icon.drawable)
        } ?: let {
            ad_icon.visibility = View.GONE
        }

        nativeAd.advertiser?.let { advertiser->
            ad_advertiser.text = advertiser
        } ?: let {
            ad_advertiser.visibility = View.GONE
        }

        nativeAd.price?.let {price->
            ad_price.text = price
        } ?: let {
            ad_price.visibility = View.GONE
        }

        nativeAd.body?.let{body->
            ad_secondery_text.text = body
        } ?: let {
            ad_secondery_text.visibility = View.GONE
        }

        nativeAd.callToAction?.let { action->
            ad_button.text = action
        } ?: let {
            ad_button.visibility = View.GONE
        }

        ad_view.setNativeAd(nativeAd)
        native_ad_container.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.enter_animation_native_ad));
    }

    override fun onDestroy() {
        super.onDestroy()
        isFragmentDestroyed = true
        nativeAdsManager.destroyNativeAd();
    }

    companion object {
        @JvmStatic
        fun newInstance() =
                NativeAdsFragment().apply {
                    arguments = Bundle().apply {

                    }
                }
    }
}