package cu.sitrans.viajero.ui.origin

import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.ActivityNotFoundException
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.opengl.Visibility
import android.os.Bundle
import android.text.SpannableString
import android.text.format.DateFormat
import android.text.method.LinkMovementMethod
import android.text.util.Linkify
import android.view.MotionEvent
import android.view.View
import android.widget.DatePicker
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
import cu.sitrans.viajero.ui.agencia.AgenciasFragment
import cu.sitrans.viajero.ui.base.DrawableClickListener
import cu.sitrans.viajero.ui.info.InfoFragment
import ir.mirrajabi.searchdialog.core.BaseSearchDialogCompat
import ir.mirrajabi.searchdialog.core.SearchResultListener
import ir.mirrajabi.searchdialog.SimpleSearchDialogCompat
import java.lang.Exception
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


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

    //Edgar
    private var mYear90: Int = 0
    private var mMonth90: Int = 0
    private var mDay90: Int = 0
    private var currentTripDateBackOK: Boolean = true
    //*************

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
                        //if (this::currentTripDateBack.isInitialized) // original
                        //Edgar
                        if (this::currentTripDateBack.isInitialized && (currentTripDateBackOK) && (!(selectDateBack.getText().toString().equals(
                                ""
                            )))
                        )
                            currentTripDateBack
                        else null
                    )
                )

        }

        actionClearDateInputBack.visibility = View.GONE
        //Edgar
        actionClearDateInputBack.setOnClickListener {
            selectDateBack.setText("")
            currentTripDateBackOK = true
            actionClearDateInputBack.visibility = View.INVISIBLE
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
            if (currentLocalidades.isEmpty())
                refreshIntentPublisher.onNext(OriginIntent.RefreshIntent)
        }

        destiny.setOnClickListener {
            if (currentLocalidades.isEmpty())
                refreshIntentPublisher.onNext(OriginIntent.RefreshIntent)
        }

        origin.setOnItemClickListener { position ->
            currentOrigin = currentLocalidades.get(position)
            destiny.callOnClick()

            if (this::currentOrigin.isInitialized && this::currentDestiny.isInitialized && (currentOrigin.equals(
                    currentDestiny
                ))
            ) {
                Toast.makeText(requireContext(), getString(R.string.destinoigualorigen_error), Toast.LENGTH_SHORT)
                    .show()
            }

            checkAllData()
        }

        destiny.setOnItemClickListener { position ->
            currentDestiny = currentLocalidades.get(position)
            if (this::currentOrigin.isInitialized && this::currentDestiny.isInitialized && (currentOrigin.equals(
                    currentDestiny
                ))
            ) {
                Toast.makeText(requireContext(), getString(R.string.destinoigualorigen_error), Toast.LENGTH_SHORT)
                    .show()
            }
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

        //Edgar val dpd ****
        val dpd =
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

            }, mYear, mMonth, mDay)

        //Edgar
        val d = Calendar.getInstance()
        dpd.datePicker.minDate = (if (isBack && this::currentTripDate.isInitialized)
            currentTripDate.time
        else
            d.timeInMillis) - 1000


        d.add(6, 90)
        mYear90 = d.get(Calendar.YEAR)
        mMonth90 = d.get(Calendar.MONTH)
        mDay90 = d.get(Calendar.DAY_OF_MONTH)
        dpd.datePicker.maxDate = d.timeInMillis
        //****************

        dpd.show()


    }

    private fun changeStartDate(date: Date) {
        currentTripDate = date

        selectDate.setText(
            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                .format(date)
        )

        checkAllData()

        //selectDateBack.callOnClick()
    }

    private fun changeBackDate(date: Date) {

        if (this::currentTripDate.isInitialized && currentTripDate.compareTo(date) > 0) {

            currentTripDateBackOK = false
            Toast.makeText(requireContext(), getString(R.string.fecharegreso_error), Toast.LENGTH_SHORT).show()

        }
        currentTripDateBackOK = true
        currentTripDateBack = date
        selectDateBack.setText(
            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                .format(date)
        )

        actionClearDateInputBack.visibility = View.VISIBLE


        checkAllData()
    }

    private fun bind() {
        // Subscribe to the ViewModel and call render for every emitted state
        disposables.add(viewModel.states().subscribe(this::render))
        // Pass the UI's intents to the ViewModel
        viewModel.processIntents(intents())
    }


    override fun onDestroyView() {
        super.onDestroyView()

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


    private var loadingDialog: ProgressDialog? = null
    override fun render(state: OriginViewState) {

        if (state.isLoading) {
            loadingDialog?.cancel()
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


//            val items = state.agencias.groupBy {
//                it.provincia!!
//            }.toList()
//
//            start(AgenciasFragment.newInstance(items))
//


            val dialog = SimpleSearchDialogCompat(
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
                                            try {

                                                startActivity(intent)
                                            } catch (e: Exception) {
                                                if (e is ActivityNotFoundException) {
                                                    Toast.makeText(
                                                        requireContext(),
                                                        getString(R.string.no_map_app),
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                            }

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
            )


            dialog.setOnShowListener {
                dialog.findViewById<View>(ir.mirrajabi.searchdialog.R.id.dummy_background)
                    ?.setOnClickListener(View.OnClickListener { })
            }

            dialog.show()


        }


        if (state.error != null) {
            Toast.makeText(requireContext(), getString(R.string.net_error), Toast.LENGTH_SHORT).show()
        }

    }

    override fun onPause() {
        super.onPause()
        loadingDialog?.cancel()
    }

    private fun checkAllData() {//Edgar && currentTripDateBackOK
        //original
        /*actionSearch.isEnabled =
            this::currentOrigin.isInitialized && this::currentDestiny.isInitialized && this::currentTripDate.isInitialized
            */

        //Edgar************
        actionSearch.isEnabled =
            this::currentOrigin.isInitialized && this::currentDestiny.isInitialized && (!currentOrigin.equals(
                currentDestiny
            )) && this::currentTripDate.isInitialized && currentTripDateBackOK
        //**************

    }


}
