package ru.evotor.external.customer_display.di

import dagger.Module
import dagger.android.AndroidInjectionModule
import dagger.android.ContributesAndroidInjector
import ru.evotor.external.customer_display.ui.MainActivity
import ru.evotor.external.customer_display.ui.display.CustomerDisplayFragment
import ru.evotor.external.customer_display.ui.settings.SettingsFragment
import ru.evotor.external.customer_display.ui.start.StartFragment

@Module(includes = [AndroidInjectionModule::class])
abstract class ActivityModule {

    @ContributesAndroidInjector
    abstract fun bindMainActivity(): MainActivity

    @ContributesAndroidInjector
    abstract fun bindCustomerDisplayFragment(): CustomerDisplayFragment

    @ContributesAndroidInjector
    abstract fun bindSettingsFragment(): SettingsFragment

    @ContributesAndroidInjector
    abstract fun bindStartFragment(): StartFragment
}
