package cu.sitrans.viajero.ui.agencia


import android.os.Bundle
import android.view.View
import android.widget.ImageView
import cu.sitrans.viajero.BuildConfig
import cu.sitrans.viajero.R
import cu.sitrans.viajero.ui.base.AbstractFragment
import cu.sitrans.viajero.viewmodel.ViewModelFactory
import kotlinx.android.synthetic.main.info_fragment.*
import javax.inject.Inject
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import cu.sitrans.viajero.repository.model.Contacto
import kotlinx.android.synthetic.main.agencias_fragment.*


class AgenciasFragment : AbstractFragment() {


    override fun layout(): Int = R.layout.agencias_fragment

    companion object {
        fun newInstance(agencias: List<Pair<String, List<Contacto>>>) = AgenciasFragment()
            .apply {
                items = agencias
            }
    }


    var items: List<Pair<String, List<Contacto>>> = listOf()

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (items.isEmpty())
            pop()



    }



    private inner class HeaderViewHolder internal constructor(val rootView: View) :
        RecyclerView.ViewHolder(rootView) {
        val tvTitle: TextView = rootView.findViewById(R.id.tvTitle)
        val imgArrow: ImageView = rootView.findViewById(R.id.imgArrow)

    }

    private inner class ItemViewHolder internal constructor(val rootView: View) :
        RecyclerView.ViewHolder(rootView) {
        val tvItem: TextView = rootView.findViewById(R.id.tvItem)

    }


}
