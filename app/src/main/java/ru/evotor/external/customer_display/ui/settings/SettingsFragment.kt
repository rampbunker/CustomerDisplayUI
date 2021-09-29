package ru.evotor.external.customer_display.ui.settings

import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.*
import androidx.appcompat.app.AppCompatActivity
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
    var pictureItems: MutableList<PictureItem> = ArrayList()


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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_MULTIPLE && resultCode == AppCompatActivity.RESULT_OK && null != data) {
            if (data.clipData != null) {
                val count = data.clipData!!.itemCount
                for (i in 0 until count) {
                    val imageUri = data.clipData!!.getItemAt(i).uri
                    pictureItems.add(
                        picturesRepository.createPictureItemFromUri(
                            imageUri,
                            getFileNameFromUri(imageUri)
                        )
                    )
                }
            } else {
                val imageUri = data.data
                if (imageUri != null) {
                    pictureItems.add(
                        picturesRepository.createPictureItemFromUri(
                            imageUri,
                            getFileNameFromUri(imageUri)
                        )
                    )
                }
            }
        }
        settingsPicturesAdapter.bindPictures(pictureItems)
    }

    private fun getFileNameFromUri(uri: Uri): String {
        var result = ""
        if (uri.scheme.equals("content")) {
            val cursor: Cursor? = mainActivity.contentResolver.query(uri, null, null, null, null)
            cursor.use { cursor ->
                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        result =
                            cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                    }
                }
            }
        }
        if (result == "") {
            result = uri.path.toString()
            val cut = result.lastIndexOf('/')
            if (cut != -1) {
                result = result.substring(cut + 1)
            }
        }
        return result
    }

    override fun onBackPressed() {
        if (settings_help_view != null && settings_help_view.isVisible) {
            showHideHelp(false)
        } else mainActivity.supportFragmentManager.popBackStack()
    }

    companion object {
        const val HELP_MENU_ITEM_ID = 2
        const val PICK_IMAGE_MULTIPLE = 1
    }
}