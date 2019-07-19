package cu.sitrans.viajero.ui.trip

import android.app.ProgressDialog
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson


import cu.sitrans.viajero.R
import cu.sitrans.viajero.mvi.MviView
import cu.sitrans.viajero.repository.model.Localidad
import cu.sitrans.viajero.repository.model.Viaje
import cu.sitrans.viajero.ui.base.AbstractFragment
import cu.sitrans.viajero.ui.base.SpaceItemDecoration
import cu.sitrans.viajero.viewmodel.ViewModelFactory
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.routes_fragment.*
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import android.util.TypedValue



class TripListFragment : AbstractFragment(), MviView<TripIntent, TripViewState> {


    private var currentTripList: MutableList<Viaje> = mutableListOf()
    private val loadingDialog by lazy { ProgressDialog(requireContext()) }

    override fun render(state: TripViewState) {
        if (state.isLoading)
            loadingDialog.show()
        else
            loadingDialog.hide()

        Timber.d(state.toString())


        currentTripList.addAll(state.viajes)

        currentTripList = currentTripList.distinct().toMutableList()
            .filter {
                it.denominacion?.contains("NO HAY") == false
            }
            .map {
                if (it.medio == null)
                    it.copy(
                        medio = "Omnibus"
                    )
                else
                    it
            }
            .toMutableList()

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
        const val SELECTED_DATE_BACK = "SELECTED_DATE_BACK"
        fun newInstance(origin: Localidad, destiny: Localidad, tripDate: Date, tripDateBack: Date?) =
            TripListFragment().apply {

                val bundle = Bundle()
                bundle.putString(ORIGIN_KEY, Gson().toJson(origin))
                bundle.putString(DESTINY_KEY, Gson().toJson(destiny))
                bundle.putLong(SELECTED_DATE, tripDate.time)
                bundle.putLong(SELECTED_DATE_BACK, tripDateBack?.time ?: -1)

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

        tripDate.text = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            .format(getSelectedDate())


        if (getSelectedDateBack().time > Date(-1).time) {
            tripDateBackLayout.visibility = View.VISIBLE
            tripDateBack.text = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                .format(getSelectedDateBack())
        }


        list.layoutManager = LinearLayoutManager(requireContext())

        val dip = 8f
        val r = resources
        val px = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dip,
            r.displayMetrics
        )

        list.addItemDecoration(SpaceItemDecoration(px.toInt(), true, true))

    }

    fun getOrigin() = Gson().fromJson(arguments?.getString(ORIGIN_KEY), Localidad::class.java)
    fun getDestiny() = Gson().fromJson(arguments?.getString(DESTINY_KEY), Localidad::class.java)
    fun getSelectedDate() = Date(arguments?.getLong(SELECTED_DATE) ?: -1)
    fun getSelectedDateBack() = Date(arguments?.getLong(SELECTED_DATE_BACK) ?: -1)

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
                , SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    .format(getSelectedDateBack())
            )
        )
    }

}


