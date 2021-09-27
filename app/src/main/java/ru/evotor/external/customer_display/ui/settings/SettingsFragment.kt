package ru.evotor.external.customer_display.ui.settings

import android.os.Bundle
import android.view.*
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_settings.*
import ru.evotor.external.customer_display.R
import ru.evotor.external.customer_display.ui.MainActivity


class SettingsFragment : Fragment() {

    private val mainActivity by lazy { activity as MainActivity }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.fragment_settings, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivity.setSupportActionBar(settingsToolbar)
        mainActivity.supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
        }
        showHideHelp(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.settings_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun showHideHelp(show: Boolean) {
        settings_help_view.isVisible = show
    }
}