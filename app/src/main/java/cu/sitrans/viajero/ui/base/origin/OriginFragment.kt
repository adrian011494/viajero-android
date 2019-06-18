package cu.sitrans.viajero.ui.base.origin

import android.os.Bundle


import cu.sitrans.viajero.R
import cu.sitrans.viajero.ui.base.AbstractFragment
import cu.sitrans.viajero.ui.base.Trip.TripListFragment
import cu.sitrans.viajero.viewmodel.ViewModelFactory
import kotlinx.android.synthetic.main.content_scrolling.*
import javax.inject.Inject

class OriginFragment : AbstractFragment() {
    override fun layout(): Int = R.layout.origin_fragment

    companion object {
        fun newInstance() = OriginFragment()
    }

    private lateinit var viewModel: OriginViewModel


    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = viewModelFactory.create(OriginViewModel::class.java)

        actionSearch.setOnClickListener {
            start(TripListFragment.newInstance())
        }
    }

}
