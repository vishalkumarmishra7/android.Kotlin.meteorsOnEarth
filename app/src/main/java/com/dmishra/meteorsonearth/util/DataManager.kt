package com.dmishra.meteorsonearth.util

import android.content.Context
import android.util.Log
import com.dmishra.meteorsonearth.R
import com.dmishra.meteorsonearth.model.MeteorData
import com.dmishra.meteorsonearth.networkApi.MeteorApiService
import com.dmishra.meteorsonearth.sync.SyncCallback
import com.orhanobut.hawk.Hawk
import io.realm.Realm
import io.realm.RealmResults
import io.realm.Sort
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.SocketTimeoutException
import java.util.*


object DataManager {
    private const val TAG = "DataManager"
    private var meteorApiService: MeteorApiService? = null

    fun loadMeteorDB(realm: Realm, syncCallback: SyncCallback) {
        Log.d(TAG, "load DB start")
        syncCallback.onSyncStarted()
        meteorApiService = MeteorApiService()
        meteorApiService!!.getMeteorsDataList(object : Callback<List<MeteorData>> {
            override fun onResponse(
                call: Call<List<MeteorData>>,
                response: Response<List<MeteorData>>
            ) {
                if (response.isSuccessful) {
                    Log.w(TAG, "Response body is Successful!!")
                    if (response.body() != null) {
                        Log.w(TAG, "Response List is not null!!")
                        realm.executeTransaction { realm ->
                            realm.copyToRealmOrUpdate(response.body())
                            syncCallback.onSyncSuccess()
                        }
                    } else {
                        Log.w(TAG, "Response body is null")
                        syncCallback.onSyncFailed()
                    }
                } else {
                    Log.w(
                        TAG,
                        "Response not successful: " + response.errorBody()
                    )
                    syncCallback.onSyncFailed()
                }
            }

            override fun onFailure(call: Call<List<MeteorData>>, t: Throwable) {
                Log.w(TAG, "network Api call failed: $t")
                if (t is SocketTimeoutException) {
                    Log.d(TAG, "Request Timeout. Please try again!")
                } else {
                    Log.d(TAG, "Connection Error!")
                }
                syncCallback.onSyncFailed()
            }
        })
        Log.d(TAG, "load DB Done")
    }

    fun loadMeteorList(realm: Realm, syncCallback: SyncCallback, context: Context): List<MeteorData> {
        Log.d(TAG, "DataManager:LoadMeteorList called!!")
        val meteorDataList: MutableList<MeteorData> = ArrayList<MeteorData>()
        val meteorRealmResults: RealmResults<MeteorData> = realm
            .where<MeteorData>(MeteorData::class.java)
            .sort(getSortField(context).toLowerCase(), getSortOrder())
            .findAll()
        if (meteorRealmResults.size > 0) {
            meteorDataList.addAll(meteorRealmResults)
            Log.d(TAG, "DataManager:LoadMeteorList success!!")
        } else {
            Log.d(TAG, "DataManager:LoadMeteorList failed, Realm Db empty!!")
            loadMeteorDB(realm, syncCallback)
        }
        return meteorDataList
    }

    fun setSortField(sortField: String) {
        Hawk.put(Constant.Settings.SORT_FIELD, sortField)
    }

    fun getSortField(context: Context): String {
        return Hawk.get( Constant.Settings.SORT_FIELD, context.getString(R.string.default_field))
    }

    fun getSortOrder(): Sort? {
        return if (Hawk.get(Constant.Settings.SORT_ORDER, false)) {
            Sort.ASCENDING
        } else {
            Sort.DESCENDING
        }
    }

    fun setSortOrder(sort: Sort) {
        val ascending = sort == Sort.ASCENDING
        Hawk.put(Constant.Settings.SORT_ORDER, ascending)
    }
}
