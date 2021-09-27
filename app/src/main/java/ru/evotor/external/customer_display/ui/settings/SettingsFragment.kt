package ru.evotor.external.customer_display.ui.settings

import android.os.Bundle
import android.view.*
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_settings.*
import ru.evotor.external.customer_display.R
import ru.evotor.external.customer_display.ui.MainActivity


class SettingsFragment : Fragment() {

    private val mainActivity by lazy { activity as MainActivity }
    private val settingsPicturesAdapter = SettingsPicturesAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.fragment_settings, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        mainActivity.setSupportActionBar(settingsToolbar)
        settingsToolbar.setNavigationOnClickListener { mainActivity.onBackPressed() }
//        showHideHelp(true)
        settingsPicturesAdapter.bindPictures(getMockPictures())
        settingsPicturesRV?.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = settingsPicturesAdapter
//            addItemDecoration(BoundsOffsetDecoration())
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu, menu)
        menu.add(Menu.NONE, 2, Menu.NONE, R.string.settings_help_hint)
            .setIcon(R.drawable.ic_help)
            .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun showHideHelp(show: Boolean) {
        settings_help_view.isVisible = show
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
}