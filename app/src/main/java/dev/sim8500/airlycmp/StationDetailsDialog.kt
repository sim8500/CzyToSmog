package dev.sim8500.airlycmp

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import rx.Observable
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.functions.Func2
import rx.schedulers.Schedulers

/**
 * Created by sbernad on 12/10/2017.
 */
class StationDetailsDialog : Subscriber<GiosSensorDatasetModel>() {

    var alertDialog : AlertDialog? = null
    var p1TxtView : TextView? = null
    var p2TxtView : TextView? = null
    var titleTxtView : TextView? = null

    val PM10_PARAM_CODE = "PM10"
    val PM25_PARAM_CODE = "PM2.5"
    var qualityIndexModel : GiosQualityIndexModel? = null

    fun prepareDialog(context : Context, model : GiosStationDetailsModel) {

        var dlgView : View = LayoutInflater.from(context).inflate(R.layout.view_station_details, null)
        p1TxtView = dlgView.findViewById(R.id.p1TextView) as? TextView
        p2TxtView = dlgView.findViewById(R.id.p2TextView) as? TextView
        titleTxtView = dlgView.findViewById(R.id.titleTxtView) as? TextView

        titleTxtView?.text = model.name ?: ""

        qualityIndexModel = model.paramIndices

        var p1Id : Int = -1
        var p2Id : Int = -1
        var errorStrBuilder = StringBuilder()

        for(m in model.sensors) {
            if(p1Id == -1 && m.param.paramCode.contains(PM10_PARAM_CODE)) {
                p1Id = m.id
            }

            if(p2Id == -1 && m.param.paramCode.contains(PM25_PARAM_CODE)) {
                p2Id = m.id
            }

            if(p1Id != -1 && p2Id != -1)
                break;
        }

        if(p1Id != -1 && p2Id != -1) {
            loadData(p1Id, p2Id)
        }
        else {
            p1TxtView?.text = errorStrBuilder.toString()
        }

        alertDialog = AlertDialog.Builder(context).setView(dlgView).create()
    }

    fun show() {
        alertDialog?.show()
    }

    private fun loadData(p1Id : Int, p2Id : Int) {
        var rqMan = RequestsManager.getInstance()
        var giosService = rqMan.getGiosObservableService(GiosDataService::class.java)

        Observable.zip( giosService.getSensorData(p1Id),
                        giosService.getSensorData(p2Id),
                        Func2<GiosSensorDataModel, GiosSensorDataModel, GiosSensorDatasetModel> {
                            p1, p2 -> prepareDataset(p1, p2)
                            }
                )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .subscribe(this)
    }

    private fun prepareDataset(p1Data : GiosSensorDataModel, p2Data : GiosSensorDataModel) : GiosSensorDatasetModel {
        return GiosSensorDatasetModel(p1Data, p2Data)
    }

    override fun onError(e: Throwable?) {
        Log.e("StationDetailsDialog", e?.toString())
    }

    override fun onCompleted() {
        Log.d("StationDetailsDialog", "onCompleted()")
    }

    override fun onNext(m: GiosSensorDatasetModel?) {

        if(m != null) {

            var pm10Model : GiosSensorDataModel? = null
            var pm25Model : GiosSensorDataModel? = null
            if(m.param1.key.contains(PM10_PARAM_CODE)) {
                pm10Model = m.param1
                pm25Model = m.param2
            }
            else {
                pm10Model = m.param2
                pm25Model = m.param1
            }

            p1TxtView?.text = extractParamData(pm10Model)
            p2TxtView?.text = extractParamData(pm25Model)

            p1TxtView?.setBackgroundColor(QualityIndexHelper.getQualityIndexColor(p1TxtView?.context,
                                                                                qualityIndexModel?.pm10IndexLevel?.id ?: 0))
            p2TxtView?.setBackgroundColor(QualityIndexHelper.getQualityIndexColor(p2TxtView?.context,
                                                                                qualityIndexModel?.pm25IndexLevel?.id ?: 0))
        }
    }

    fun extractParamData(paramModel : GiosSensorDataModel) : String? {
        var result : String? = null

        var sb = StringBuilder()
        sb.append(paramModel.key).append(" = ")
        try {
            sb.append(paramModel.values.first{ p -> p.value != null }.value)
            result = sb.toString()
        }
        catch(e: NoSuchElementException)
        {
            Log.e("StationDetailsDialog", e?.toString())
        }

        return result;
    }
}