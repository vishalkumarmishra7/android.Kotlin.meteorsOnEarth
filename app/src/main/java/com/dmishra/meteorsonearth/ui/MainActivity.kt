package com.dmishra.meteorsonearth.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.MaterialDialog.SingleButtonCallback
import com.dmishra.meteorsonearth.R
import com.dmishra.meteorsonearth.adapter.MeteorsListAdapter
import com.dmishra.meteorsonearth.databinding.ActivityMainBinding
import com.dmishra.meteorsonearth.model.MeteorData
import com.dmishra.meteorsonearth.sync.SyncCallback
import com.dmishra.meteorsonearth.ui.MapActivity
import com.dmishra.meteorsonearth.util.Constant
import com.dmishra.meteorsonearth.util.DataManager
import io.realm.Realm
import io.realm.Sort

class MainActivity : AppCompatActivity(), MeteorsListAdapter.RecyclerItemClickListener {
    private val TAG: String = this.javaClass.name
    lateinit var meteorsListAdapter: MeteorsListAdapter
    lateinit var activityMainBinding: ActivityMainBinding
    lateinit var realm: Realm
    lateinit var context: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.context = getApplicationContext()

        //get layout view
        activityMainBinding = ActivityMainBinding.inflate(getLayoutInflater())
        val view: View = activityMainBinding.getRoot()
        setContentView(view)

        //init realm obj
        realm = Realm.getDefaultInstance()

        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
        activityMainBinding.recycler.setLayoutManager(layoutManager)

        fillRecyclerViewData(realm)

        activityMainBinding.refresh.setColorSchemeResources(R.color.colorPrimary)
        activityMainBinding.refresh.setOnRefreshListener(OnRefreshListener {
            Log.d(TAG, "Swipe Refresh done!!")
            refreshRecyclerView()
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }

    override fun onRestart() {
        super.onRestart()
        fillRecyclerViewData(realm)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        getMenuInflater().inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_sort -> {
                Log.d(TAG, "Sort List called!!")
                if (realm.where<MeteorData>(MeteorData::class.java).findAll().size > 0) {
                    showSortListDialog()
                } else {
                    Toast.makeText(this@MainActivity, R.string.toast_no_data, Toast.LENGTH_SHORT).show()
                }
            }
            R.id.action_refresh -> {
                Log.d(TAG, "Refresh called!!")
                refreshRecyclerView()
                Toast.makeText(this, "List refreshed!!", Toast.LENGTH_SHORT).show()
            }
        }
        return true
    }


    fun refreshRecyclerView() {
        meteorsListAdapter.clear()
        realm = Realm.getDefaultInstance()
        realm.executeTransaction(Realm.Transaction { realm -> realm.deleteAll() })
        fillRecyclerViewData(realm)
    }


    fun fillRecyclerViewData(realm: Realm) {
        Log.d(TAG, "fillRecyclerViewData called!!")
        val syncCallback: SyncCallback = object : SyncCallback(this) {
            override fun onSyncStarted() {
                activityMainBinding.recycler.setVisibility(View.GONE)
                activityMainBinding.refresh.post(Runnable {
                    Log.d(TAG, "setRefreshing=" + true)
                    activityMainBinding.refresh.setRefreshing(true)
                })
            }

            override fun onSyncSuccess() {
                super.onSyncSuccess()
                activityMainBinding.recycler.setVisibility(View.VISIBLE)
                fillRecyclerViewData(realm)
                Log.d(TAG, "setRefreshing=" + false)
                activityMainBinding.refresh.setRefreshing(false)
            }

            override fun onSyncFailed() {
                super.onSyncFailed()
                Log.d(TAG, "setRefreshing=" + false)
                activityMainBinding.refresh.setRefreshing(false)
            }
        }
        meteorsListAdapter = MeteorsListAdapter(this, realm, syncCallback, this)
        activityMainBinding.recycler.setAdapter(meteorsListAdapter)
    }

    fun showSortListDialog() {
        val materialDialog: MaterialDialog = MaterialDialog.Builder(this)
            .title(R.string.main_sort_title)
            .positiveText(R.string.main_sort_apply)
            .negativeText(R.string.main_sort_cancel)
            .negativeColor(ContextCompat.getColor(this, R.color.black))
            .customView(R.layout.view_sort_dialog, true)
            .onPositive(SingleButtonCallback { dialog, which -> applySortListDialog(dialog) })
            .cancelable(true)
            .build()

        val fieldSpinner = materialDialog.customView!!.findViewById<View>(R.id.sortFieldSpinner) as Spinner
        val radioAscending = materialDialog.customView!!.findViewById<View>(R.id.radioAscending) as RadioButton
        val radioDescending = materialDialog.customView!!.findViewById<View>(R.id.radioDescending) as RadioButton
        fieldSpinner.setSelection((fieldSpinner.adapter as ArrayAdapter<String>).getPosition(DataManager.getSortField(applicationContext)))
        radioAscending.isChecked = DataManager.getSortOrder() === Sort.ASCENDING
        radioDescending.isChecked = DataManager.getSortOrder() === Sort.DESCENDING

        materialDialog.show()
    }

    fun applySortListDialog(dialog: MaterialDialog) {
        val fieldSpinner = dialog.customView!!.findViewById<View>(R.id.sortFieldSpinner) as Spinner
        val radioAscendingButton = dialog.customView!!.findViewById<View>(R.id.radioAscending) as RadioButton
        if (radioAscendingButton.isChecked) {
            DataManager.setSortOrder(Sort.ASCENDING)
        } else {
            DataManager.setSortOrder(Sort.DESCENDING)
        }
        DataManager.setSortField(fieldSpinner.selectedItem.toString())
        fillRecyclerViewData(realm)
    }

    override fun onRecyclerItemClick(meteorItem: MeteorData?) {
        Log.d(TAG, "onRecyclerItemClick called!!")
        val mapActivityIntent = Intent(this, MapActivity::class.java)
        mapActivityIntent.putExtra(Constant.Intent.METEORITE_NAME, meteorItem?.name)
        mapActivityIntent.putExtra(Constant.Intent.METEORITE_LAT, meteorItem?.reclat)
        mapActivityIntent.putExtra(Constant.Intent.METEORITE_LNG, meteorItem?.reclong)
        startActivity(mapActivityIntent)
    }
}
