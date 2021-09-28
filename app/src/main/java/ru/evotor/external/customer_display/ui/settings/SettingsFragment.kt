package ru.evotor.external.customer_display.ui.settings

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_settings.*
import ru.evotor.external.customer_display.R
import ru.evotor.external.customer_display.ui.MainActivity
import ru.evotor.external.customer_display.ui.OnBackPressedListener


class SettingsFragment : Fragment(), OnBackPressedListener {

    private val mainActivity by lazy { activity as MainActivity }
    private val settingsPicturesAdapter = SettingsPicturesAdapter()
    private val HELP_MENU_ITEM_ID = 2
    private val PICK_IMAGE_MULTIPLE = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.fragment_settings, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        mainActivity.setSupportActionBar(settingsToolbar)
        settingsToolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        settingsPicturesAdapter.bindPictures(getMockPictures())
        settingsPicturesRV?.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = settingsPicturesAdapter
//            addItemDecoration(BoundsOffsetDecoration())
        }
        settings_button_add_pictures.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(
                Intent.createChooser(intent, "Select Picture"),
                PICK_IMAGE_MULTIPLE
            )
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        if (settings_help_view.isVisible) {
            menu.clear()
        } else {
            menu.add(Menu.NONE, HELP_MENU_ITEM_ID, Menu.NONE, R.string.settings_help_hint)
                .setIcon(R.drawable.ic_help)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
        }
        super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            HELP_MENU_ITEM_ID -> {
                showHideHelp(true)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showHideHelp(show: Boolean) {
        if (show) {
            settings_help_view.isVisible = true
            settingsToolbar.title = "Помощь"
            settingsToolbar.navigationIcon = requireContext().getDrawable(R.drawable.ic_close)
        } else {
            settings_help_view.isVisible = false
            settingsToolbar.title = "Настройки"
            settingsToolbar.navigationIcon = requireContext().getDrawable(R.drawable.ic_back)
        }
        mainActivity.invalidateOptionsMenu()
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

    override fun onBackPressed() {
        if (settings_help_view != null && settings_help_view.isVisible) {
            showHideHelp(false)
        } else mainActivity.supportFragmentManager.popBackStack()
    }
}