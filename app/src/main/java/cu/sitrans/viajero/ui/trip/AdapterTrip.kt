package cu.sitrans.viajero.ui.trip

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import cu.sitrans.viajero.R
import cu.sitrans.viajero.repository.model.Viaje
import kotlinx.android.synthetic.main.item_trip.view.*
import java.lang.Exception
import java.text.SimpleDateFormat
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
            itemView.desc.text = get.denominacion
            itemView.price.text = (get.precio?.toDoubleOrNull()?.toInt() ?: 0).toString() + " $"

            try {
                val dateStart = SimpleDateFormat("yy-MM-dd'T'HH:mm:ss.SSSz", Locale.getDefault()).parse(get.fecha)
                val dateEnd = SimpleDateFormat("yy-MM-dd'T'HH:mm:ss.SSSz", Locale.getDefault()).parse(get.fecha_llegada)


                SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault())
                    .format(dateStart)

                itemView.dateStart.text = SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault())
                    .format(dateStart)


                itemView.dateEnd.text = SimpleDateFormat("hh:mm a", Locale.getDefault())
                    .format(dateEnd)

            } catch (e: Exception) {
                e.printStackTrace()
            }



            itemView.duration.text = itemView.context.getString(R.string.capacidad, get.capac ?: "0")

//            if (get.clima != null && get.clima == "true") {
//                itemView.duration.text = itemView.context.getString(R.string.climatizado)
//            } else
//                itemView.duration.text = ""


        }

    }
}