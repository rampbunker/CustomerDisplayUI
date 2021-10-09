package ru.evotor.external.customer_display.ui.settings

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.view.*
import android.widget.Toast
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
import java.io.File
import javax.inject.Inject


class SettingsFragment : DaggerFragment(), OnBackPressedListener {

    @Inject
    lateinit var picturesRepository: PicturesRepository
    private val mainActivity by lazy { activity as MainActivity }
    private val settingsPicturesAdapter = SettingsPicturesAdapter { deletePictureFromFile(it) }
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
        if (picturesRepository.isPicturesDirectoryEmpty()) {
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
        settingsPicturesAdapter.bindPictures(picturesRepository.loadAllPicturesFromFile())
    }

    private fun startPickImagesScreen() {
        val intent = Intent()
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.action = Intent.ACTION_OPEN_DOCUMENT
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
                    val pictureItem = picturesRepository.createPictureItemFromUri(imageUri)
                    pictureItems.add(pictureItem)
                }
            } else {
                val imageUri = data.data
                if (imageUri != null) {
                    val pictureItem = picturesRepository.createPictureItemFromUri(imageUri)
                    pictureItems.add(pictureItem)
                }
            }
        }
        for (item in pictureItems) {
            picturesRepository.savePictureToFile(item)
        }
        if (pictureItems.isNotEmpty()) {
            toggleHelpVisibility(SettingsVisibilityState.SHOW_SETTINGS)
            settingsPicturesAdapter.bindPictures(picturesRepository.loadAllPicturesFromFile())
        }
    }

    private fun deletePictureFromFile(pictureItem: PictureItem) {
        val directoryPath = File(
            requireContext().getExternalFilesDir(
                Environment.DIRECTORY_PICTURES
            ), ALBUM_DIRECTORY_NAME
        )
        if (directoryPath.exists()) {
            if (directoryPath.list()!!.isNotEmpty()) {
                for (i in directoryPath.list()!!) {
                    val pictureFilePath = File(directoryPath, i)
                    if (pictureFilePath.name.equals(pictureItem.filename)) {
                        pictureFilePath.delete()
                        if (picturesRepository.isPicturesDirectoryEmpty()) {
                            toggleHelpVisibility(SettingsFragment.SettingsVisibilityState.EMPTY_GALLERY_SHOW_HELP)
                        }
                        Toast.makeText(requireContext(), "Picture deleted", Toast.LENGTH_LONG)
                            .show()
                        return
                    }
                }
            }
            Toast.makeText(
                requireContext(),
                "Delete failed:list of files is empty",
                Toast.LENGTH_LONG
            ).show()
            return
        }
        Toast.makeText(
            requireContext(),
            "Delete failed: directory does not exist",
            Toast.LENGTH_LONG
        ).show()
    }

    override fun onBackPressed() {
        if (visibilityState == SettingsVisibilityState.CLICKED_HELP_SHOW_HELP) {
            toggleHelpVisibility(SettingsVisibilityState.SHOW_SETTINGS)
        } else mainActivity.supportFragmentManager.popBackStack()
    }

    companion object {
        const val HELP_MENU_ITEM_ID = 2
        const val PICK_IMAGE_MULTIPLE = 1
        const val ALBUM_DIRECTORY_NAME = "pictures"
    }

    enum class SettingsVisibilityState {
        EMPTY_GALLERY_SHOW_HELP,
        SHOW_SETTINGS,
        CLICKED_HELP_SHOW_HELP
    }
}