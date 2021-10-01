package ru.evotor.external.customer_display.ui.display

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import dagger.android.support.DaggerFragment
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.my_fragment_customer_display.*
import ru.evotor.external.customer_display.R
import ru.evotor.external.customer_display.repository.PictureItem
import ru.evotor.external.customer_display.repository.PicturesRepository
import ru.evotor.external.customer_display.service.CustomerDisplayDataProvider
import javax.inject.Inject


class CustomerDisplayFragment : DaggerFragment() {

    @Inject
    lateinit var dataProvider: CustomerDisplayDataProvider
    private val dataAdapter = CustomerDisplayAdapter()

    @Inject
    lateinit var picturesRepository: PicturesRepository
    private val disposable = CompositeDisposable()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.my_fragment_customer_display, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val pictureItems: List<PictureItem> = picturesRepository.loadPicturesFromRealm()
        backgroundViewFlipper.isAutoStart = true
        backgroundViewFlipper.flipInterval = 1000
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

//        customerDisplayRV?.apply {
//            val manager = LinearLayoutManager(requireContext())
//            manager.stackFromEnd = true
//            layoutManager = manager
//
//            dataAdapter.appendData("Добро пожаловать!")
//            dataAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
//                override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
//                    super.onItemRangeInserted(positionStart, itemCount)
//
//                    customerDisplayRV.smoothScrollToPosition(positionStart)
//                }
//            })
//            adapter = dataAdapter
//        }


    override fun onStart() {
        super.onStart()

        dataProvider.init(DEVICE_COLUMNS, DEVICE_ROWS)

        dataProvider.clearDisplayEventStream
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                dataAdapter.clearData()
            }
            .let { d -> disposable.add(d) }

        dataProvider.stringOutputEventStream
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { dataString ->
                dataAdapter.appendData(dataString)
            }
            .let { d -> disposable.add(d) }
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