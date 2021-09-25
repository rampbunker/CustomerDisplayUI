package ru.evotor.external.customer_display

import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import ru.evotor.external.customer_display.di.ApplicationComponent
import ru.evotor.external.customer_display.di.DaggerApplicationComponent
import ru.evotor.external.customer_display.service.CustomerDisplayDataProvider
import javax.inject.Inject

class CustomerDisplayApp : DaggerApplication() {
    @Inject
    lateinit var provider: CustomerDisplayDataProvider

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        val appComponent: ApplicationComponent = DaggerApplicationComponent.builder()
                .application(this)
                .build()
        appComponent.inject(this)
        return appComponent
    }

    override fun onCreate() {
        super.onCreate()
    }
}