package ru.evotor.external.customer_display.ui.settings

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_settings.*
import ru.evotor.external.customer_display.R
import ru.evotor.external.customer_display.repository.PictureItem
import ru.evotor.external.customer_display.repository.PicturesRepository
import ru.evotor.external.customer_display.ui.MainActivity
import ru.evotor.external.customer_display.ui.OnBackPressedListener
import javax.inject.Inject


class SettingsFragment : DaggerFragment(), OnBackPressedListener {

    @Inject
    lateinit var picturesRepository: PicturesRepository
    private val mainActivity by lazy { activity as MainActivity }
    private val settingsPicturesAdapter = SettingsPicturesAdapter()
    private var pictureItems: MutableList<PictureItem> = ArrayList()
    private var visibilityState: SettingsVisibilityState =
        SettingsVisibilityState.EMPTY_GALLERY_SHOW_HELP


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
        setUpRV()
        if (picturesRepository.isRealmEmpty()) {
            toggleHelpVisibility(SettingsVisibilityState.EMPTY_GALLERY_SHOW_HELP)
        } else {
            toggleHelpVisibility(SettingsVisibilityState.SHOW_SETTINGS)
        }
        settingsButtonAddPictures.setOnClickListener {
            startPickImagesScreen()
        }
    }

    private fun setUpRV() {
        settingsPicturesRV.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = settingsPicturesAdapter
            addItemDecoration(
                DividerItemDecoration(
                    requireContext(),
                    (layoutManager as LinearLayoutManager).orientation
                )
            )
        }
        settingsPicturesAdapter.bindPictures(picturesRepository.loadPicturesFromRealm())
    }

    private fun startPickImagesScreen() {
        val intent = Intent()
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
            Intent.createChooser(intent, "Select Picture"),
            PICK_IMAGE_MULTIPLE
        )
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
                toggleHelpVisibility(SettingsVisibilityState.CLICKED_HELP_SHOW_HELP)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun toggleHelpVisibility(state: SettingsVisibilityState) {
        when (state) {
            SettingsVisibilityState.EMPTY_GALLERY_SHOW_HELP -> {
                visibilityState = SettingsVisibilityState.EMPTY_GALLERY_SHOW_HELP
                settings_help_view.isVisible = true
                settingsToolbar.title = getString(R.string.settings_title)
                settingsToolbar.navigationIcon =
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_back)
            }
            SettingsVisibilityState.SHOW_SETTINGS -> {
                visibilityState = SettingsVisibilityState.SHOW_SETTINGS
                settings_help_view.isVisible = false
                settingsToolbar.title = getString(R.string.settings_title)
                settingsToolbar.navigationIcon =
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_back)
            }
            SettingsVisibilityState.CLICKED_HELP_SHOW_HELP -> {
                visibilityState = SettingsVisibilityState.CLICKED_HELP_SHOW_HELP
                settings_help_view.isVisible = true
                settingsToolbar.title = getString(R.string.settings_help_hint)
                settingsToolbar.navigationIcon =
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_back)
            }
        }
        mainActivity.invalidateOptionsMenu()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_MULTIPLE && resultCode == AppCompatActivity.RESULT_OK && null != data) {
            if (data.clipData != null) {
                val count = data.clipData!!.itemCount
                for (i in 0 until count) {
                    val imageUri = data.clipData!!.getItemAt(i).uri
                    pictureItems.add(
                        picturesRepository.createPictureItemFromUri(
                            imageUri
                        )
                    )
                }
            } else {
                val imageUri = data.data
                if (imageUri != null) {
                    pictureItems.add(
                        picturesRepository.createPictureItemFromUri(
                            imageUri
                        )
                    )
                }
            }
        }
        for (item in pictureItems) {
            picturesRepository.savePictureToRealm(item)
        }
        if (pictureItems.isNotEmpty()) {
            toggleHelpVisibility(SettingsVisibilityState.SHOW_SETTINGS)
            settingsPicturesAdapter.bindPictures(picturesRepository.loadPicturesFromRealm())
        }

    }

    override fun onBackPressed() {
        if (visibilityState == SettingsVisibilityState.CLICKED_HELP_SHOW_HELP) {
            toggleHelpVisibility(SettingsVisibilityState.SHOW_SETTINGS)
        } else mainActivity.supportFragmentManager.popBackStack()
    }

    companion object {
        const val HELP_MENU_ITEM_ID = 2
        const val PICK_IMAGE_MULTIPLE = 1
    }

    enum class SettingsVisibilityState {
        EMPTY_GALLERY_SHOW_HELP,
        SHOW_SETTINGS,
        CLICKED_HELP_SHOW_HELP
    }
}