package ru.evotor.external.customer_display.ui

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
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
            .replace(R.id.main_container, StartFragment())
            .addToBackStack(StartFragment::class.java.canonicalName)
            .commit()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            1 -> {
                Toast.makeText(this, "go to settings", Toast.LENGTH_SHORT).show()
                supportFragmentManager.beginTransaction()
                    .replace(R.id.main_container, SettingsFragment())
                    .addToBackStack(StartFragment::class.java.canonicalName)
                    .commit()
                true
            }
            2 -> {
                Toast.makeText(this, "show help", Toast.LENGTH_SHORT).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}