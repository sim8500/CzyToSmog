package dev.sim8500.airlycmp

import android.content.Context
import android.database.DataSetObserver
import android.location.Location
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.AdapterView
import android.widget.BaseAdapter
import android.widget.TextView
import com.google.android.gms.maps.model.LatLng

/**
 * Created by sbernad on 18/10/2017.
 */
class StationsAdapter(var stations : MutableList<GiosStationDataModel>, val activity : StationsActivity) : BaseAdapter(), AdapterView.OnItemClickListener {


    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        var dialog = StationSensorsDialog()
        dialog.prepareDialog(activity, stations.get(position).id, stations.get(position).stationName)

        dialog.show()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        var createNew = true
        var view : View? = null

        if(convertView != null) {
            var nameView = convertView.findViewById(R.id.stationTxtView) as? TextView
            var addrView = convertView.findViewById(R.id.addressTxtView) as? TextView

            if(nameView != null && addrView != null)
            {
                createNew = false
                nameView.text = stations.get(position).stationName
                addrView.text = stations.get(position).addressStreet
                view = convertView
            }
        }

        if(createNew) {
            var brandNew = LayoutInflater.from(activity).inflate(R.layout.view_station, null)
            var nameView = brandNew.findViewById(R.id.stationTxtView) as? TextView
            var addrView = brandNew.findViewById(R.id.addressTxtView) as? TextView

            nameView?.text = stations.get(position).stationName
            addrView?.text = stations.get(position).addressStreet

            view = brandNew
        }

        return view!!
    }

    override fun getItem(position: Int): Any {
        return stations.get(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return stations.size
    }

    fun sortByLocation(location : Location?) {
        if(stations.size > 0 && location != null) {
            stations.sortBy { e ->
                                var loc = Location("")
                                loc.latitude = e.gegrLat.toDouble()
                                loc.longitude = e.gegrLon.toDouble()
                                location.distanceTo(loc) }

            notifyDataSetChanged()
        }
    }
}