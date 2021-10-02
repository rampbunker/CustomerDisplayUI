package ru.evotor.external.customer_display.ui.display

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import dagger.android.support.DaggerFragment
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_customer_display.*
import ru.evotor.external.customer_display.R
import ru.evotor.external.customer_display.repository.PictureItem
import ru.evotor.external.customer_display.repository.PicturesRepository
import ru.evotor.external.customer_display.service.CustomerDisplayDataProvider
import javax.inject.Inject


class CustomerDisplayFragment : DaggerFragment() {

    @Inject
    lateinit var dataProvider: CustomerDisplayDataProvider

    @Inject
    lateinit var picturesRepository: PicturesRepository
    private val disposable = CompositeDisposable()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.fragment_customer_display, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val pictureItems: List<PictureItem> = picturesRepository.loadPicturesFromRealm()
        backgroundViewFlipper.isAutoStart = true
        backgroundViewFlipper.flipInterval = 10000
        for (pi in pictureItems) {
            setFlipperImage(pi)
        }
    }

    private fun setFlipperImage(pictureItem: PictureItem) {
        val flipperImageView = ImageView(context)
        flipperImageView.scaleType = ImageView.ScaleType.CENTER_CROP
        Glide.with(requireContext())
            .load(Uri.parse(pictureItem.uriString))
            .placeholder(R.drawable.ic_empty_gallery)
            .fallback(R.drawable.ic_empty_gallery)
            .into(flipperImageView)
        backgroundViewFlipper.addView(flipperImageView)
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
        val animUpOut = AnimationUtils.loadAnimation(context, R.anim.anim_up_out)
        animUpOut.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {
                customerDisplayText.isVisible = true
                customerDisplayText.text = text
                val animScaleIn = AnimationUtils.loadAnimation(context, R.anim.anim_scale_in)
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