package cu.sitrans.viajero.ui.origin

import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.format.DateFormat
import android.text.method.LinkMovementMethod
import android.text.util.Linkify
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.kunzisoft.switchdatetime.SwitchDateTimeDialogFragment


import cu.sitrans.viajero.R
import cu.sitrans.viajero.mvi.MviView
import cu.sitrans.viajero.repository.model.Localidad
import cu.sitrans.viajero.ui.base.AbstractFragment
import cu.sitrans.viajero.ui.trip.TripListFragment
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
import cu.sitrans.viajero.repository.model.Contacto
import cu.sitrans.viajero.ui.info.InfoFragment
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
    private lateinit var currentTripDateBack: Date
    private lateinit var currentDestiny: Localidad
    private lateinit var currentOrigin: Localidad
    private var currentLocalidades: List<Localidad> = listOf()
    private lateinit var viewModel: OriginViewModel

    private val disposables: CompositeDisposable = CompositeDisposable()

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = viewModelFactory.create(OriginViewModel::class.java)

        actionSearch.setOnClickListener {
            if (this::currentOrigin.isInitialized && this::currentDestiny.isInitialized && this::currentTripDate.isInitialized)
                start(
                    TripListFragment.newInstance(
                        currentOrigin,
                        currentDestiny,
                        currentTripDate,
                        if (this::currentTripDateBack.isInitialized) currentTripDateBack else null
                    )
                )
        }

        selectDate.setOnClickListener {

            selectTripDate()

        }

        selectDateInput.setOnClickListener {
            selectTripDate()
        }



        selectDateBack.setOnClickListener {

            selectTripDate(true)

        }

        selectDateInputBack.setOnClickListener {
            selectTripDate(true)
        }

        origin.setOnClickListener {
            if(currentLocalidades.isEmpty())
                refreshIntentPublisher.onNext(OriginIntent.RefreshIntent)
        }

        destiny.setOnClickListener {
            if(currentLocalidades.isEmpty())
                refreshIntentPublisher.onNext(OriginIntent.RefreshIntent)
        }

        origin.setOnItemClickListener { position ->
            currentOrigin = currentLocalidades.get(position)
            destiny.callOnClick()
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

            if (it.itemId == R.id.action_agencias)
                showAgencias()
            else
                showInfoView()
            return@setOnMenuItemClickListener true
        }
        bind()
    }

    private fun showInfoView() {


        start(InfoFragment.newInstance())

    }


    private fun showAgencias() {
        agenciasIntentPublisher.onNext(OriginIntent.AgenciasIntent)
    }


    private fun selectTripDate(isBack: Boolean = false) {

        val c = if (this::currentTripDate.isInitialized) Calendar.getInstance().apply {
            set(Calendar.YEAR, currentTripDate.year + 1900)
            set(Calendar.MONTH, currentTripDate.month)
            set(Calendar.DAY_OF_MONTH, currentTripDate.date)
        } else Calendar.getInstance()

        val mYear = c.get(Calendar.YEAR)
        val mMonth = c.get(Calendar.MONTH)
        val mDay = c.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(requireContext(), DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->

            val date = Calendar.getInstance().apply {
                set(Calendar.YEAR, year)
                set(Calendar.MONTH, month)
                set(Calendar.DAY_OF_MONTH, dayOfMonth)
            }.time

            if (isBack)
                changeBackDate(date)
            else
                changeStartDate(date)

        }, mYear, mMonth, mDay).show()


        val dateTimeDialogFragment = SwitchDateTimeDialogFragment.newInstance(
            if (isBack) getString(R.string.select_date_back_dialog) else getString(R.string.select_date_dialog),
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
                if (isBack)
                    changeBackDate(date)
                else
                    changeStartDate(date)
            }

            override fun onNegativeButtonClick(date: Date) {
                // Date is get on negative button click
            }
        })

        // Show
        // dateTimeDialogFragment.show(fragmentManager, "dialog_start_time")

    }

    private fun changeStartDate(date: Date) {
        currentTripDate = date

        selectDate.setText(
            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                .format(date)
        )

        checkAllData()

        selectDateBack.callOnClick()
    }

    private fun changeBackDate(date: Date) {
        currentTripDateBack = date

        selectDateBack.setText(
            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
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



    override fun onDestroy() {
        disposables.clear()
        super.onDestroy()

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


    private var loadingDialog: ProgressDialog? = null
    override fun render(state: OriginViewState) {

        if (state.isLoading) {
            loadingDialog = ProgressDialog(requireContext())
                .apply {
                    setCancelable(false)
                }
            loadingDialog?.show()
        } else
            loadingDialog?.hide()

        Timber.e(state.error)

        if (state.localidades.isNotEmpty()) {
            currentLocalidades = state.localidades



            origin.setItems(state.localidades.map { it.Nombre }.toTypedArray())



            destiny.setItems(state.localidades.map { it.Nombre }.toTypedArray())
            origin.isEnabled = true
            destiny.isEnabled = true

        }/* else {
            origin.isEnabled = false
            destiny.isEnabled = false
        }*/

        if (state.agencias.isNotEmpty()) {
            SimpleSearchDialogCompat(
                requireContext(), getString(R.string.agencias),
                getString(R.string.search), null, ArrayList(state.agencias), object : SearchResultListener<Contacto> {
                    override fun onSelected(dialog: BaseSearchDialogCompat<*>?, item: Contacto?, position: Int) {
                        val s = SpannableString(
                            "Dir: ${item?.direccion ?: "-"}\nTel: ${item?.telefono ?: "-"}"
                        )
                        Linkify.addLinks(s, Linkify.ALL)

                        AlertDialog.Builder(requireContext())
                            .setTitle(item?.title)
                            .setMessage(s)
                            .setPositiveButton(android.R.string.ok, null)
                            .apply {
                                if (!item?.coordenadas.isNullOrBlank())
                                    setNeutralButton(
                                        getString(R.string.ver_mapa),
                                        DialogInterface.OnClickListener { _, _ ->
                                            val intent = Intent(
                                                Intent.ACTION_VIEW,
                                                Uri.parse("geo:${item?.coordenadas?.replace(";", ",")}")
                                            )
                                            startActivity(intent)

                                        })
                            }
                            .create()
                            .apply {
                                setOnShowListener {

                                    this.findViewById<TextView>(android.R.id.message)?.movementMethod =
                                        LinkMovementMethod.getInstance()

                                    this.findViewById<TextView>(android.R.id.message)?.setTextIsSelectable(true)
                                }
                            }
                            .show()


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
