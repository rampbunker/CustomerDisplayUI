package ru.evotor.external.customer_display.ui.display

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.asksira.loopingviewpager.LoopingViewPager
import dagger.android.support.DaggerFragment
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_customer_display.*
import ru.evotor.external.customer_display.R
import ru.evotor.external.customer_display.repository.PictureItemNew
import ru.evotor.external.customer_display.repository.PicturesRepository
import ru.evotor.external.customer_display.service.CustomerDisplayDataProvider
import javax.inject.Inject


class CustomerDisplayFragment : DaggerFragment() {

    @Inject
    lateinit var dataProvider: CustomerDisplayDataProvider

    @Inject
    lateinit var picturesRepository: PicturesRepository
    private lateinit var slideshowViewPager: LoopingViewPager
    private var slideshowAdapter: InfiniteSlideshowAdapter? = null
    private val disposable = CompositeDisposable()
    private lateinit var animUpOut: Animation
    lateinit var animScaleIn: Animation


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.fragment_customer_display, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val pictureItems: ArrayList<PictureItemNew> = picturesRepository.loadAllPicturesFromFile()
        animScaleIn = AnimationUtils.loadAnimation(context, R.anim.anim_scale_in)
        animUpOut = AnimationUtils.loadAnimation(context, R.anim.anim_up_out)
        slideshowViewPager = view.findViewById(R.id.slideshowViewPager) as LoopingViewPager
        slideshowAdapter = InfiniteSlideshowAdapter(pictureItems, true)
        slideshowViewPager.adapter = slideshowAdapter
    }

    override fun onStart() {
        super.onStart()
        dataProvider.init(DEVICE_COLUMNS, DEVICE_ROWS)
        dataProvider.clearDisplayEventStream
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
            }
            .let { d -> disposable.add(d) }

        dataProvider.stringOutputEventStream
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { dataString ->
                showTextWithAnimation(dataString)
            }
            .let { d -> disposable.add(d) }
    }

    private fun showTextWithAnimation(text: String) {
        animUpOut.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {
                customerDisplayText.text = text
                customerDisplayText.startAnimation(animScaleIn)
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })
        customerDisplayText.startAnimation(animUpOut)
    }

    override fun onStop() {
        super.onStop()
        disposable.dispose()

//        crash here
//        dataProvider.finish()
    }

    companion object {
        const val DEVICE_COLUMNS = 20
        const val DEVICE_ROWS = 40

        @JvmStatic
        fun newInstance() = CustomerDisplayFragment()
    }
}