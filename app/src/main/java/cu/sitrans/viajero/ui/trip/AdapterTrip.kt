package cu.sitrans.viajero.ui.trip

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import cu.sitrans.viajero.R
import cu.sitrans.viajero.repository.model.Viaje
import cu.sitrans.viajero.utils.toDate
import kotlinx.android.synthetic.main.item_trip.view.*
import java.lang.Exception
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*

class AdapterTrip(val itemList: List<Viaje>) : RecyclerView.Adapter<AdapterTrip.TripViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TripViewHolder {
        return TripViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_trip, null))
    }

    override fun getItemCount(): Int = itemList.size

    override fun onBindViewHolder(holder: TripViewHolder, position: Int) {
        holder.bind(itemList.get(position))
    }


    class TripViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(get: Viaje) {
            itemView.title.text = get.denominacion

            val tren = if (get.medio?.contains("Ferro") == true) {

                val mach = "[a-zA-Z]+T(\\d)_C(\\d)".toRegex().find(get.medio ?: "")

                itemView.context.getString(
                    R.string.tren_coche,
                    mach?.groups?.get(1)?.value ?: "",
                    mach?.groups?.get(2)?.value ?: ""
                )
            } else
                ""

            if (get.via.isNullOrBlank()) {
                itemView.desc.visibility = View.GONE
                itemView.desc.text = ""
            } else {
                itemView.desc.visibility = View.VISIBLE
                itemView.desc.text = itemView.context.getString(R.string.via, get.via ?: "")
            }

            if (tren.isNotBlank()) {
                itemView.desc.visibility = View.VISIBLE
                itemView.desc.text = tren + " " + itemView.desc.text

            }



            itemView.price.text = (get.precio?.toDoubleOrNull()?.toInt() ?: 0).toString() + " $"


            if (get.precio == null)
                itemView.price.visibility = View.GONE
            else
                itemView.price.visibility = View.VISIBLE

            try {

                itemView.dateStart.text = get.fecha?.toDate()

                itemView.dateEnd.text = ""
                val dateEnd = SimpleDateFormat("yy-MM-dd'T'HH:mm:ss.SSSz", Locale.getDefault()).parse(get.fecha_llegada)
                itemView.dateEnd.text = SimpleDateFormat("hh:mm a", Locale.getDefault())
                    .format(dateEnd)

            } catch (e: Exception) {
                e.printStackTrace()
            }



            itemView.duration.text = itemView.context.getString(R.string.capacidad, get.capac ?: "0")

            if (get.clima != null && get.clima == "true") {
                itemView.clima.visibility = View.VISIBLE
            } else
                itemView.clima.visibility = View.GONE


            if (get.tipo == null || get.tipo == "Ida") {
                itemView.tripIda.visibility = View.VISIBLE
                itemView.tripBack.visibility = View.GONE
            } else {
                itemView.tripIda.visibility = View.GONE
                itemView.tripBack.visibility = View.VISIBLE
            }

            val currentDate = Date()
            val dateTrip = SimpleDateFormat("yy-MM-dd'T'HH:mm:ss.SSSz", Locale.getDefault()).parse(get.fecha)

            val days = (dateTrip.time - currentDate.time) / (24 * 60 * 60 * 1000)

            if ((tren.isNullOrBlank() && days>=90) || (tren.isNotBlank() && days >= 30)){
                itemView.tripIda.setColorFilter(ContextCompat.getColor(itemView.context, R.color.descText), android.graphics.PorterDuff.Mode.SRC_IN)
                itemView.tripBack.setColorFilter(ContextCompat.getColor(itemView.context, R.color.descText), android.graphics.PorterDuff.Mode.SRC_IN)
                itemView.price.setTextColor(ContextCompat.getColor(itemView.context, R.color.descText))

            }
            else {
                itemView.tripIda.setColorFilter(ContextCompat.getColor(itemView.context, R.color.colorAccent), android.graphics.PorterDuff.Mode.SRC_IN)
                itemView.tripBack.setColorFilter(ContextCompat.getColor(itemView.context, R.color.colorPrimary), android.graphics.PorterDuff.Mode.SRC_IN)

                itemView.price.setTextColor(ContextCompat.getColor(itemView.context, R.color.colorAccent))
            }


        }

    }


}