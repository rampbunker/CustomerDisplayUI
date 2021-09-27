package ru.evotor.external.customer_display.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import dagger.android.support.DaggerAppCompatActivity
import ru.evotor.external.customer_display.R
import ru.evotor.external.customer_display.ui.settings.SettingsFragment
import ru.evotor.external.customer_display.ui.start.StartFragment


class MainActivity : DaggerAppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        supportFragmentManager.beginTransaction()
//                .replace(R.id.main_container, CustomerDisplayFragment.newInstance())
//                .addToBackStack(CustomerDisplayFragment::class.java.canonicalName)
//                .commit()

        supportFragmentManager.beginTransaction()
            .replace(R.id.main_container, SettingsFragment())
            .addToBackStack(StartFragment::class.java.canonicalName)
            .commit()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        menu.add(Menu.NONE, R.id.action_help, Menu.NONE, R.string.settings_help_hint)
            .setIcon(R.drawable.ic_help)
            .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
        menu.add(Menu.NONE, R.id.action_settings, Menu.NONE, R.string.start_settings_hint)
            .setIcon(R.drawable.ic_settings)
            .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_help -> // show help
                true
            R.id.action_settings -> //go to settings
                true
            else -> super.onOptionsItemSelected(item)
        }
    }
}