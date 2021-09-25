package ru.evotor.external.customer_display.di

import android.app.Application
import android.content.Context
import dagger.Binds
import dagger.Module
import ru.evotor.external.customer_display.CustomerDisplayApp

@Module
abstract class ApplicationModule(application: CustomerDisplayApp) {
    @Binds
    abstract fun bindContext(application: Application): Context
}
