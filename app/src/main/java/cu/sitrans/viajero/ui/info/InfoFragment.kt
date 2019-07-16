package cu.sitrans.viajero.ui.info


import android.os.Bundle
import android.view.View
import cu.sitrans.viajero.BuildConfig
import cu.sitrans.viajero.R
import cu.sitrans.viajero.ui.base.AbstractFragment
import cu.sitrans.viajero.viewmodel.ViewModelFactory
import kotlinx.android.synthetic.main.info_fragment.*
import javax.inject.Inject


class InfoFragment : AbstractFragment() {


    override fun layout(): Int = R.layout.info_fragment

    companion object {
        fun newInstance() = InfoFragment()
    }


    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        versionNumber.text = getString(R.string.version, BuildConfig.VERSION_NAME)
    }


}
