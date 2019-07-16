package cu.sitrans.viajero.ui.origin

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import cu.sitrans.viajero.R

class Adapter(val context: Context, fm: FragmentManager?) : FragmentPagerAdapter(fm) {

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> context.getString(R.string.bus)
            1 -> context.getString(R.string.tren)
            else -> ""

        }
    }

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> Fragment()
            1 -> Fragment()
            else -> Fragment()
        }
    }

    override fun getCount(): Int = 2
}