package ru.evotor.external.customer_display.di

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import ru.evotor.external.customer_display.CustomerDisplayApp
import javax.inject.Singleton

@Singleton
@Component(
        modules = [
            ApplicationModule::class,
            ActivityModule::class
        ]
)
interface ApplicationComponent : AndroidInjector<CustomerDisplayApp> {
    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        fun build(): ApplicationComponent
    }

    override fun inject(app: CustomerDisplayApp)
}