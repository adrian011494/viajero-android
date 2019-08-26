package cu.sitrans.viajero

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import cu.sitrans.viajero.ui.base.AbstractActivity
import cu.sitrans.viajero.ui.origin.OriginFragment
import cu.uci.apklisupdate.ApklisUpdate
import cu.uci.apklisupdate.UpdateCallback
import cu.uci.apklisupdate.model.AppUpdateInfo
import cu.uci.apklisupdate.view.ApklisUpdateFragment
import dagger.android.AndroidInjection
import timber.log.Timber

class MainActivity : AbstractActivity() {
    override fun layout(): Int = R.layout.activity_main

    override fun inject() {
        AndroidInjection.inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        loadRootFragment(R.id.container, OriginFragment.newInstance())

        checkUpdate()


    }

    private fun checkUpdate(){
        ApklisUpdate.hasAppUpdate(this, callback = object : UpdateCallback {

            override fun onNewUpdate(appUpdateInfo: AppUpdateInfo) {
                Timber.d("onNewUpdate $appUpdateInfo")
                //Start info fragment or do what you want.
                supportFragmentManager.beginTransaction().add(
                    R.id.container, ApklisUpdateFragment.newInstance(
                        updateInfo = appUpdateInfo,
                        actionsColor = ContextCompat.getColor(this@MainActivity, R.color.colorAccent)
                    )
                ).commit()

            }

            override fun onOldUpdate(appUpdateInfo: AppUpdateInfo) {
                Timber.d("onOldUpdate $appUpdateInfo")

            }

            override fun onError(e: Throwable) {
                e.printStackTrace()
            }
        })

    }
}
