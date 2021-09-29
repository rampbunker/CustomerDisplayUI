package ru.evotor.external.customer_display.ui.start

import android.os.Bundle
import android.text.Annotation
import android.text.SpannableString
import android.text.Spanned
import android.text.SpannedString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.*
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_start.*
import ru.evotor.external.customer_display.R
import ru.evotor.external.customer_display.repository.PicturesRepository
import ru.evotor.external.customer_display.ui.MainActivity
import javax.inject.Inject


class StartFragment : DaggerFragment() {

    @Inject
    lateinit var picturesRepository: PicturesRepository
    private val mainActivity by lazy { activity as MainActivity }
    private val startGalleryAdapter = StartGalleryAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.fragment_start, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        mainActivity.setSupportActionBar(startToolbar)
        setTextWithLinkForEmptyGallery()
        startGalleryAdapter.bindPictures(picturesRepository.loadPicturesFromRealm())
        startGalleryRV?.apply {
            layoutManager = CenterZoomLayoutManager(requireContext())
            adapter = startGalleryAdapter
            addItemDecoration(BoundsOffsetDecoration())
        }
        testGallery.setOnClickListener {
            start_empty_gallery_hint_view.isVisible = false
            startGalleryRV.isVisible = true
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu, menu)
        menu.add(Menu.NONE, SETTINGS_MENU_ITEM_ID, Menu.NONE, R.string.start_settings_hint)
            .setIcon(R.drawable.ic_settings)
            .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            SETTINGS_MENU_ITEM_ID -> {
                mainActivity.goToSettings()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setTextWithLinkForEmptyGallery() {
        val fullText = getText(R.string.start_empty_text) as SpannedString
        val spannableString = SpannableString(fullText)
        val annotations = fullText.getSpans(0, fullText.length, Annotation::class.java)

        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                mainActivity.goToSettings()
            }

            override fun updateDrawState(ds: TextPaint) {
                ds.isUnderlineText = false
            }
        }
        annotations?.find { it.value == "settings_link" }?.let {
            spannableString.apply {
                setSpan(
                    clickableSpan,
                    fullText.getSpanStart(it),
                    fullText.getSpanEnd(it),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                setSpan(
                    ForegroundColorSpan(
                        ContextCompat.getColor(requireContext(), R.color.accent)
                    ),
                    fullText.getSpanStart(it),
                    fullText.getSpanEnd(it),
                    0
                )
            }
        }

        start_text_empty_gallery.apply {
            text = spannableString
            movementMethod = LinkMovementMethod.getInstance()
        }
    }

    companion object {
        const val SETTINGS_MENU_ITEM_ID = 1
    }
}