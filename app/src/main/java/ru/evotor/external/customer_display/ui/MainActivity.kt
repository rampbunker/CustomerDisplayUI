package ru.evotor.external.customer_display.ui

import android.os.Bundle
import android.view.View
import dagger.android.support.DaggerAppCompatActivity
import ru.evotor.external.customer_display.R
import ru.evotor.external.customer_display.ui.display.CustomerDisplayFragment
import ru.evotor.external.customer_display.ui.settings.SettingsFragment
import ru.evotor.external.customer_display.ui.start.StartFragment


class MainActivity : DaggerAppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.beginTransaction()
            .replace(R.id.main_container, StartFragment())
            .addToBackStack(StartFragment::class.java.canonicalName)
            .commit()
    }

    fun goToSettings() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_container, SettingsFragment())
            .addToBackStack(SettingsFragment::class.java.canonicalName)
            .commit()
    }

    override fun onBackPressed() {
        var backPressedListener: OnBackPressedListener? = null

        for (fragment in supportFragmentManager.fragments) {
            if (fragment is OnBackPressedListener) {
                backPressedListener = fragment
                break
            }
        }
        if (backPressedListener != null) {
            backPressedListener.onBackPressed()
        } else {
            super.onBackPressed()
        }
    }

    fun onClickRunCustomerDisplay(v: View) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_container, CustomerDisplayFragment.newInstance())
            .addToBackStack(CustomerDisplayFragment::class.java.canonicalName)
            .commit()
    }
}