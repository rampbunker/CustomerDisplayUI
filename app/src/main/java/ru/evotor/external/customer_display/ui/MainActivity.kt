package ru.evotor.external.customer_display.ui

import android.os.Bundle
import android.view.Menu
import dagger.android.support.DaggerAppCompatActivity
import ru.evotor.external.customer_display.R

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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.start_menu, menu)
        return true
    }
}