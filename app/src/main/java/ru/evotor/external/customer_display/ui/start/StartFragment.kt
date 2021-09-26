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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_start.*
import ru.evotor.external.customer_display.R
import ru.evotor.external.customer_display.ui.MainActivity


class StartFragment : Fragment() {

    private val mainActivity by lazy { activity as MainActivity }
    private val startGalleryAdapter = StartGalleryAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.fragment_start, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivity.setSupportActionBar(startToolbar)
        setTextWithLinkForEmptyGallery()
        startGalleryAdapter.bindPictures(getMockPictures())
        startGalleryRV?.apply {
            layoutManager = CenterZoomLayoutManager(requireContext())
            adapter = startGalleryAdapter
            addItemDecoration(BoundsOffsetDecoration())
        }
    }

    //  !!! Delete Mock Data Source !!!
    private fun getMockPictures(): List<String> {
        return listOf(
            "https://upload.wikimedia.org/wikipedia/commons/d/d9/Robin_Wright_Cannes_2017_%28cropped%29.jpg",
            "https://upload.wikimedia.org/wikipedia/commons/2/2c/Connie_Nielsen_by_Gage_Skidmore.jpg",
            "https://st.kp.yandex.net/im/kadr/1/2/4/kinopoisk.ru-Kenneth-Branagh-1241673.jpg",
            "https://upload.wikimedia.org/wikipedia/commons/c/c5/Pedro_Pascal_by_Gage_Skidmore.jpg",
            "https://upload.wikimedia.org/wikipedia/commons/8/84/David_Harbour_by_Gage_Skidmore.jpg",
            "https://upload.wikimedia.org/wikipedia/commons/7/7f/Rachel_Weisz_2018.jpg",
            "https://toronto.citynews.ca/wp-content/blogs.dir/sites/10/2019/06/NYET414-618_2019_013921.jpg"
        )
    }


    private fun setTextWithLinkForEmptyGallery() {
        val fullText = getText(R.string.start_empty_text) as SpannedString
        val spannableString = SpannableString(fullText)
        val annotations = fullText.getSpans(0, fullText.length, Annotation::class.java)

        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
//            Перейти в настройки
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
}