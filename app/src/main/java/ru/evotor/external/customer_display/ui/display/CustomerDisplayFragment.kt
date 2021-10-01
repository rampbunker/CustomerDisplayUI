package ru.evotor.external.customer_display.ui.display

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.android.support.DaggerFragment
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_customer_display.*
import ru.evotor.external.customer_display.R
import ru.evotor.external.customer_display.service.CustomerDisplayDataProvider
import javax.inject.Inject


class CustomerDisplayFragment : DaggerFragment() {
    private val dataAdapter = CustomerDisplayAdapter()

    @Inject
    lateinit var dataProvider: CustomerDisplayDataProvider

    private val disposable = CompositeDisposable()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.fragment_customer_display, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        customerDisplayRV?.apply {
            val manager = LinearLayoutManager(requireContext())
            manager.stackFromEnd = true
            layoutManager = manager

            dataAdapter.appendData("Добро пожаловать!")
            dataAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
                override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                    super.onItemRangeInserted(positionStart, itemCount)

                    customerDisplayRV.smoothScrollToPosition(positionStart)
                }
            })
            adapter = dataAdapter
        }
    }

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