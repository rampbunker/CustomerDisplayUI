package ru.evotor.external.customer_display.service

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject
import ru.evotor.external.integrations.customer_display.ICustomerDisplayClient
import ru.evotor.external.integrations.customer_display.ICustomerDisplayService
import java.lang.Thread.sleep
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.concurrent.thread
import kotlin.random.Random

private const val FAKE = true

@Singleton
class CustomerDisplayDataProvider @Inject constructor(private val appContext: Context) {

    private var clearEventThread: Thread? = null
    private var stringOutputEventThread: Thread? = null

    private var rowsNum = 10
    private var columnsNum = 10

    private val clearDisplayEventSubject = PublishSubject.create<Boolean>()
    val clearDisplayEventStream: Observable<Boolean> get() = clearDisplayEventSubject

    private val stringOutputEventSubject = PublishSubject.create<String>()
    val stringOutputEventStream: Observable<String> get() = stringOutputEventSubject

    private var customerDisplayService: ICustomerDisplayService? = null

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            customerDisplayService = ICustomerDisplayService.Stub.asInterface(service)
            customerDisplayService!!.registerClient(object : ICustomerDisplayClient.Stub() {
                override fun clearCustomerDisplay() {
                    clearDisplayEventSubject.onNext(true)
                }

                override fun stringOutputOnCustomerDisplay(data: String?) {
                    data?.let { s -> stringOutputEventSubject.onNext(s) }
                }

                override fun getOutputOptions(): Bundle {
                    return Bundle().apply {
                        putInt("DeviceColumns", columnsNum)
                        putInt("DeviceRows", rowsNum)
                    }
                }
            })
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            customerDisplayService?.unregisterClient()
            customerDisplayService = null
        }
    }

    fun init(deviceColumns: Int, deviceRows: Int) {
        rowsNum = deviceRows
        columnsNum = deviceColumns

        if (!FAKE) {
            val intent =
                    Intent("ru.evotor.external.integrations.customer_display.ICustomerDisplayService")
            val updateIntent = createExplicitFromImplicitIntent(appContext, intent)

            if (updateIntent != null) {
                appContext.bindService(updateIntent, serviceConnection, Context.BIND_AUTO_CREATE)
            }
        } else {
            //TODO
            clearEventThread = thread {
                try {
                    while (clearEventThread?.isInterrupted != true) {
                        clearDisplayEventSubject.onNext(true)
                        sleep(30000)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            stringOutputEventThread = thread {
                try {
                    while (stringOutputEventThread?.isInterrupted != true) {
                        stringOutputEventSubject.onNext(
                                "Товар с содержанием глютена ${Random.nextInt(40)}%\n      =${Random.nextInt(10000)}р"
                        )
                        sleep(2000)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun finish() {
        Log.d("MainLog", "provider::finish()")
        if (!FAKE) {
            customerDisplayService?.unregisterClient()
            appContext.unbindService(serviceConnection)
        } else {
            clearEventThread?.stop()
            stringOutputEventThread?.stop()
        }
    }

    private fun createExplicitFromImplicitIntent(
            context: Context,
            implicitIntent: Intent?
    ): Intent? {
        //Retrieve all services that can match the given intent
        val pm: PackageManager = context.packageManager
        val resolveInfo: List<*> = pm.queryIntentServices(implicitIntent!!, 0)

        //Make sure only one match was found
        if (resolveInfo == null || resolveInfo.size != 1) {
            return null
        }

        //Get component info and create ComponentName
        val serviceInfo: ResolveInfo = resolveInfo[0] as ResolveInfo
        val packageName = serviceInfo.serviceInfo.packageName
        val className = serviceInfo.serviceInfo.name
        val component = ComponentName(packageName, className)

        //Create a new intent. Use the old one for extras and such reuse
        val explicitIntent = Intent(implicitIntent)

        //Set the component to be explicit
        explicitIntent.component = component
        return explicitIntent
    }
}