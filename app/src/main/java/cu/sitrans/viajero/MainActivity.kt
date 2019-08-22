package cu.sitrans.viajero

import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import cu.sitrans.viajero.ui.base.AbstractActivity
import cu.sitrans.viajero.ui.origin.OriginFragment
import dagger.android.AndroidInjection

class MainActivity : AbstractActivity() {
    override fun layout(): Int = R.layout.activity_main

    override fun inject() {
        AndroidInjection.inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        loadRootFragment(R.id.container, OriginFragment.newInstance())
    }
}
