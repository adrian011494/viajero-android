package cu.sitrans.viajero.ui.base.origin

import android.app.ProgressDialog
import android.os.Bundle
import android.text.format.DateFormat
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import com.kunzisoft.switchdatetime.SwitchDateTimeDialogFragment


import cu.sitrans.viajero.R
import cu.sitrans.viajero.mvi.MviView
import cu.sitrans.viajero.repository.model.Localidad
import cu.sitrans.viajero.ui.base.AbstractFragment
import cu.sitrans.viajero.ui.base.trip.TripListFragment
import cu.sitrans.viajero.viewmodel.ViewModelFactory
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.content_scrolling.*
import kotlinx.android.synthetic.main.content_scrolling.destiny
import kotlinx.android.synthetic.main.content_scrolling.origin
import kotlinx.android.synthetic.main.routes_fragment.*
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import android.widget.Toast
import cu.sitrans.viajero.MainActivity
import cu.sitrans.viajero.repository.model.Contacto
import ir.mirrajabi.searchdialog.core.BaseSearchDialogCompat
import ir.mirrajabi.searchdialog.core.SearchResultListener
import ir.mirrajabi.searchdialog.SimpleSearchDialogCompat
import kotlin.collections.ArrayList


class OriginFragment : AbstractFragment(), MviView<OriginIntent, OriginViewState> {


    override fun layout(): Int = R.layout.origin_fragment

    companion object {
        fun newInstance() = OriginFragment()
    }

    private lateinit var currentTripDate: Date
    private lateinit var currentDestiny: Localidad
    private lateinit var currentOrigin: Localidad
    private var currentLocalidades: List<Localidad> = listOf()
    private lateinit var viewModel: OriginViewModel

    private val disposables: CompositeDisposable = CompositeDisposable()

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = viewModelFactory.create(OriginViewModel::class.java)

        actionSearch.setOnClickListener {
            if (this::currentOrigin.isInitialized && this::currentDestiny.isInitialized && this::currentTripDate.isInitialized)
                start(TripListFragment.newInstance(currentOrigin, currentDestiny, currentTripDate))
        }

        selectDate.setOnClickListener {

            selectTripDate()

        }

        selectDateInput.setOnClickListener {
            selectTripDate()
        }

        origin.setOnItemClickListener { position ->
            currentOrigin = currentLocalidades.get(position)
            destiny.requestFocus()
            checkAllData()
        }

        destiny.setOnItemClickListener { position ->
            currentDestiny = currentLocalidades.get(position)
            checkAllData()
            hideSoftInput()
            if (!this::currentTripDate.isInitialized)
                selectDate.callOnClick()
        }

        setHasOptionsMenu(true)
        toolbar.inflateMenu(R.menu.menu_home)
        toolbar.setOnMenuItemClickListener {

            showAgencias()
            return@setOnMenuItemClickListener true
        }
    }


    private fun showAgencias() {
        agenciasIntentPublisher.onNext(OriginIntent.AgenciasIntent)
    }


    private fun selectTripDate() {
        val dateTimeDialogFragment = SwitchDateTimeDialogFragment.newInstance(
            getString(R.string.select_date_dialog),
            getString(android.R.string.ok),
            getString(android.R.string.cancel)
        )


        // Assign values
        dateTimeDialogFragment.startAtCalendarView()
        dateTimeDialogFragment.set24HoursMode(DateFormat.is24HourFormat(this.requireContext()))
        dateTimeDialogFragment.minimumDateTime = GregorianCalendar(

            Calendar.getInstance().get(Calendar.YEAR),
            Calendar.getInstance().get(Calendar.MONTH),
            Calendar.getInstance().get(Calendar.DAY_OF_MONTH)

        ).time

        if (this::currentTripDate.isInitialized)
            dateTimeDialogFragment.setDefaultDateTime(currentTripDate)

        // Set listener
        dateTimeDialogFragment.setOnButtonClickListener(object : SwitchDateTimeDialogFragment.OnButtonClickListener {
            override fun onPositiveButtonClick(date: Date) {
                changeStartDate(date)
            }

            override fun onNegativeButtonClick(date: Date) {
                // Date is get on negative button click
            }
        })

        // Show
        dateTimeDialogFragment.show(fragmentManager, "dialog_start_time")

    }

    private fun changeStartDate(date: Date) {
        currentTripDate = date

        selectDate.setText(
            SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault())
                .format(date)
        )

        checkAllData()
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

    override fun intents(): Observable<OriginIntent> {
        return Observable.merge(
            initialIntent(),
            refreshIntentPublisher,
            agenciasIntentPublisher
        )

    }

    private fun initialIntent(): Observable<OriginIntent.InitialIntent> {
        return Observable.just(OriginIntent.InitialIntent)
    }

    private val refreshIntentPublisher = PublishSubject.create<OriginIntent.RefreshIntent>()
    private val agenciasIntentPublisher = PublishSubject.create<OriginIntent.AgenciasIntent>()


    private var loadingDialog:ProgressDialog? = null
    override fun render(state: OriginViewState) {

        if (state.isLoading) {
            loadingDialog = ProgressDialog(requireContext())
            loadingDialog?.show()
        }
        else
            loadingDialog?.hide()

        Timber.w(state.localidades.toString())
        Timber.e(state.error)

        if (state.localidades.isNotEmpty()) {
            currentLocalidades = state.localidades



            origin.setItems(state.localidades.map { it.Nombre }.toTypedArray())



            destiny.setItems(state.localidades.map { it.Nombre }.toTypedArray())
            origin.isEnabled = true
            destiny.isEnabled = true

        } else {
            origin.isEnabled = false
            destiny.isEnabled = false
        }

        if (state.agencias.isNotEmpty()) {
            SimpleSearchDialogCompat(
                requireContext(), getString(R.string.agencias),
                getString(R.string.search), null, ArrayList(state.agencias), object : SearchResultListener<Contacto> {
                    override fun onSelected(dialog: BaseSearchDialogCompat<*>?, item: Contacto?, position: Int) {
                        AlertDialog.Builder(requireContext())
                            .setTitle(item?.title)
                            .setMessage("Dir: ${item?.direccion ?: ""}\nTel: ${item?.telefono ?: ""}")
                            .create().show()


                    }

                }
            ).show()
        }


        if (state.error != null) {
            Toast.makeText(requireContext(), getString(R.string.net_error), Toast.LENGTH_SHORT).show()
        }

    }

    private fun checkAllData() {
        actionSearch.isEnabled =
            this::currentOrigin.isInitialized && this::currentDestiny.isInitialized && this::currentTripDate.isInitialized

    }


}
