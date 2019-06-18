package cu.sitrans.viajero.ui.base.Trip

import android.os.Bundle


import cu.sitrans.viajero.R
import cu.sitrans.viajero.ui.base.AbstractFragment
import cu.sitrans.viajero.ui.base.origin.Adapter
import cu.sitrans.viajero.viewmodel.ViewModelFactory
import kotlinx.android.synthetic.main.routes_fragment.*
import javax.inject.Inject

class TripListFragment : AbstractFragment() {
    override fun layout(): Int = R.layout.routes_fragment

    companion object {
        fun newInstance() = TripListFragment()
    }

    private lateinit var viewModel: TripListViewModel


    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = viewModelFactory.create(TripListViewModel::class.java)




        initToolbar()
        container.adapter = Adapter(requireContext(), childFragmentManager)

        tabs.setupWithViewPager(container)
    }

    private fun initToolbar() {
        toolbar.navigationIcon = resources.getDrawable(R.drawable.ic_arrow_back_black_24dp)

        toolbar.setNavigationOnClickListener {
            pop()
        }

        toolbar.inflateMenu(R.menu.menu_list_trips)
        toolbar.setOnMenuItemClickListener {

            return@setOnMenuItemClickListener true
        }

    }

}
