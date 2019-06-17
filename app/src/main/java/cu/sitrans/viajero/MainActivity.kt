package cu.sitrans.viajero

import cu.sitrans.viajero.ui.base.AbstractActivity
import dagger.android.AndroidInjection

class MainActivity : AbstractActivity() {
    override fun layout(): Int = R.layout.activity_main

    override fun inject() {
        AndroidInjection.inject(this)
    }

}
