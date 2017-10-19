package dev.sim8500.airlycmp

import android.app.AlertDialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import rx.Observable
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

/**
 * Created by sbernad on 18/10/2017.
 */
class StationSensorsDialog : Subscriber<List<GiosSensorInfoModel>>() {

    var activity : StationsActivity? = null
    var alertDialog : AlertDialog? = null
    var p1TxtView : TextView? = null
    var titleTxtView : TextView? = null
    var detailsButton : Button? = null
    var loadedData : List<GiosSensorInfoModel>? = null

    fun prepareDialog(activity : StationsActivity, stationId : Int, stationName : String?) {

        this.activity = activity

        var dlgView: View = LayoutInflater.from(activity).inflate(R.layout.view_station_sensors, null)
        p1TxtView = dlgView.findViewById(R.id.p1TextView) as? TextView
        titleTxtView = dlgView.findViewById(R.id.titleTxtView) as? TextView
        detailsButton = dlgView.findViewById(R.id.detailsButton) as? Button


        titleTxtView?.text = stationName ?: ""

        loadData(stationId)

        alertDialog = AlertDialog.Builder(activity).setView(dlgView).create()

    }

    private fun loadData(stId : Int) {
        var rqMan = RequestsManager.getInstance()
        var giosService = rqMan.getGiosObservableService(GiosDataService::class.java)

        giosService.getSensorsInfo(stId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .subscribe(this)
    }

    fun show() {
        alertDialog?.show()
    }

    override fun onError(e: Throwable?) {
        Log.e("StationSensorsDialog", e?.toString())
    }

    override fun onNext(list: List<GiosSensorInfoModel>?) {
        if(list != null) {
            loadedData = list
            var sb : StringBuilder = StringBuilder()
            for(s in list) {
                if(!sb.isEmpty()) {
                    sb.append(", ")
                }
                sb.append(s.param.paramCode)
            }

            p1TxtView?.text = sb.toString()
            detailsButton?.setOnClickListener { v -> alertDialog?.dismiss()
                                                    activity?.onDetailsClicked(loadedData, titleTxtView?.text.toString()) }
        }

    }

    override fun onCompleted() {
    }
}