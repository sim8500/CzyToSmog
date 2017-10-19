package dev.sim8500.airlycmp

/**
 * Created by sbernad on 12/10/2017.
 */


/*==================================================================================================
quality index section
==================================================================================================*/
data class ParamIndexModel(var indexLevelName: String? = null, var id: Int = 0)

data class GiosQualityIndexModel(val pm10IndexLevel: ParamIndexModel?, val pm25IndexLevel: ParamIndexModel?)

/*==================================================================================================
sensor info section
==================================================================================================*/
data class GiosSensorParam(val paramName : String = " ", val paramCode : String, val idParam : Int)

data class GiosSensorInfoModel(val id : Int, val stationId : Int, val param : GiosSensorParam)

data class GiosStationDetailsModel(val sensors : List<GiosSensorInfoModel>, val paramIndices : GiosQualityIndexModel?, val name : String?)

/*==================================================================================================
sensor data section
==================================================================================================*/
data class SensorValueModel(val value : String?, val date : String)

data class GiosSensorDataModel(val key : String, val values : List<SensorValueModel>)

data class GiosSensorDatasetModel(val param1 : GiosSensorDataModel?, val param2 : GiosSensorDataModel?)

/*==================================================================================================
station data section
==================================================================================================*/
data class GiosStationDataModel(val stationName : String?,
                                val id : Int,
                                val gegrLat : String,
                                val gegrLon: String,
                                val addressStreet : String?)