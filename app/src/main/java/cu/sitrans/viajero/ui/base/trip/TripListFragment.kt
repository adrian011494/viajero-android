package cu.sitrans.viajero.ui.base.trip

import android.app.ProgressDialog
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson


import cu.sitrans.viajero.R
import cu.sitrans.viajero.mvi.MviView
import cu.sitrans.viajero.repository.model.Localidad
import cu.sitrans.viajero.repository.model.Viaje
import cu.sitrans.viajero.ui.base.AbstractFragment
import cu.sitrans.viajero.ui.base.origin.Adapter
import cu.sitrans.viajero.ui.base.origin.OriginIntent
import cu.sitrans.viajero.viewmodel.ViewModelFactory
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.routes_fragment.*
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class TripListFragment : AbstractFragment(), MviView<TripIntent, TripViewState> {


    private var currentTripList: List<Viaje> = listOf()
    private val loadingDialog by lazy { ProgressDialog(requireContext()) }
    override fun render(state: TripViewState) {
        if (state.isLoading)
            loadingDialog.show()
        else
            loadingDialog.hide()

        Timber.d(state.toString())


        currentTripList = state.viajes

        when (tabs.selectedTabPosition) {
            0 -> {
                list.adapter = AdapterTrip(currentTripList.filter {
                    it.medio?.contains("Ferro") == false
                })
            }

            1 -> {
                list.adapter = AdapterTrip(currentTripList.filter {
                    it.medio?.contains("Ferro") == true
                })
            }

        }

    }

    override fun layout(): Int = R.layout.routes_fragment

    companion object {
        const val ORIGIN_KEY = "ORIGIN_KEY"
        const val DESTINY_KEY = "DESTINY_KEY"
        const val SELECTED_DATE = "SELECTED_DATE"
        fun newInstance(origin: Localidad, destiny: Localidad, tripDate: Date) = TripListFragment().apply {

            val bundle = Bundle()
            bundle.putString(ORIGIN_KEY, Gson().toJson(origin))
            bundle.putString(DESTINY_KEY, Gson().toJson(destiny))
            bundle.putLong(SELECTED_DATE, tripDate.time)

            this.arguments = bundle

        }
    }

    private lateinit var viewModel: TripListViewModel


    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val disposables: CompositeDisposable = CompositeDisposable()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = viewModelFactory.create(TripListViewModel::class.java)

        initToolbar()


        tabs.addTab(tabs.newTab().setText(R.string.bus))
        tabs.addTab(tabs.newTab().setText(R.string.tren))
        tabs.addOnTabSelectedListener(object : TabLayout.BaseOnTabSelectedListener<TabLayout.Tab> {
            override fun onTabReselected(p0: TabLayout.Tab?) {


            }

            override fun onTabUnselected(p0: TabLayout.Tab?) {
            }

            override fun onTabSelected(p0: TabLayout.Tab?) {
                when (p0?.position) {
                    0 -> {
                        list.adapter = AdapterTrip(currentTripList.filter {
                            it.medio?.contains("Ferro") == false
                        })
                    }

                    1 -> {
                        list.adapter = AdapterTrip(currentTripList.filter {
                            it.medio?.contains("Ferro") == true
                        })
                    }

                }


            }

        })


        origin.text = getOrigin().Nombre
        destiny.text = getDestiny().Nombre

        tripDate.text = SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault())
            .format(getSelectedDate())


        list.layoutManager = LinearLayoutManager(requireContext())

    }

    fun getOrigin() = Gson().fromJson(arguments?.getString(ORIGIN_KEY), Localidad::class.java)
    fun getDestiny() = Gson().fromJson(arguments?.getString(DESTINY_KEY), Localidad::class.java)
    fun getSelectedDate() = Date(arguments?.getLong(SELECTED_DATE) ?: -1)

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

    private fun bind() {
        // Subscribe to the ViewModel and call render for every emitted state
        disposables.add(viewModel.states().subscribe(this::render))
        // Pass the UI's intents to the ViewModel
        viewModel.processIntents(intents())
    }


    override fun onResume() {
        super.onResume()
        bind()
    }


    override fun onPause() {
        super.onPause()
        disposables.clear()


    }

    override fun intents(): Observable<TripIntent> {
        return Observable.merge(
            initialIntent(),
            refreshIntentPublisher
        )

    }

    private val refreshIntentPublisher = PublishSubject.create<TripIntent.RefreshIntent>()


    private fun initialIntent(): Observable<TripIntent.InitialIntent> {
        return Observable.just(
            TripIntent.InitialIntent(
                getOrigin().Clave, getDestiny().Clave, SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    .format(getSelectedDate())
            )
        )
    }

}


