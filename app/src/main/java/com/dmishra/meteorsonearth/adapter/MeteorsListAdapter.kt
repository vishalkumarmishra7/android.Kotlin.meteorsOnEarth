package com.dmishra.meteorsonearth.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.dmishra.meteorsonearth.R
import com.dmishra.meteorsonearth.databinding.ItemMeteoriteBinding
import com.dmishra.meteorsonearth.model.MeteorData
import com.dmishra.meteorsonearth.sync.SyncCallback
import com.dmishra.meteorsonearth.util.DataManager
import io.realm.Realm


class MeteorsListAdapter(
    context: Context,
    realm: Realm,
    syncCallback: SyncCallback,
    recyclerItemClickListener: RecyclerItemClickListener
) : RecyclerView.Adapter<MeteorsListAdapter.MeteorsViewHolder>() {

    private val TAG: String = this.javaClass.name
    var meteorDataList: MutableList<MeteorData>
    var context: Context
    var recyclerItemClickListener: RecyclerItemClickListener


    init {
        this.context = context
        meteorDataList = DataManager.loadMeteorList(realm, syncCallback, context) as MutableList<MeteorData>
        this.recyclerItemClickListener = recyclerItemClickListener
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MeteorsViewHolder {
        Log.d(TAG, "onCreateViewHolder called!!")
        val li = LayoutInflater.from(context)
        return MeteorsViewHolder(ItemMeteoriteBinding.inflate(li))
    }

    override fun onBindViewHolder(
        holder: MeteorsViewHolder,
        position: Int
    ) {
        Log.d(TAG, "onBindViewHolder bind position:$position")
        val meteorDataData: MeteorData = meteorDataList[position]
        holder.bind(meteorDataData)
    }

    override fun getItemCount(): Int {
        Log.d(TAG, "MeteorsListAdapter getItemCount=" + meteorDataList.size)
        return meteorDataList.size
    }

    private fun remove(item: MeteorData) {
        Log.d(TAG, "remove list item:$item")
        val pos = meteorDataList.indexOf(item)
        if (pos > -1) {
            meteorDataList.removeAt(pos)
            notifyItemRemoved(pos)
        }
    }

    private fun getItem(position: Int): MeteorData {
        return meteorDataList[position]
    }

    fun clear() {
        Log.d(TAG, "clear Recycler view.")
        while (itemCount > 0) {
            remove(getItem(0))
        }
    }

    interface RecyclerItemClickListener {
        fun onRecyclerItemClick(meteorItem: MeteorData?)
    }

    inner class MeteorsViewHolder(itemBinding: ItemMeteoriteBinding) :
        ViewHolder(itemBinding.getRoot()) {
        var itemMeteoriteBinding: ItemMeteoriteBinding
        fun bind(meteorDataData: MeteorData) {
            Log.d(TAG, "MeteorsViewHolder bind started")
            itemMeteoriteBinding.name.setText(meteorDataData.name)
            itemMeteoriteBinding.mass.setText(context.getString(R.string.main_mass, meteorDataData.mass))
            itemMeteoriteBinding.location.setText(meteorDataData.reclat.toString() + ", " + meteorDataData.reclong.toString())
            itemMeteoriteBinding.date.setText(meteorDataData.year?.substring(0, meteorDataData.year!!.indexOf("-")))
        }

        init {
            itemMeteoriteBinding = itemBinding
            itemBinding.getRoot().setOnClickListener(View.OnClickListener {
                Log.d(TAG,"MeteorsViewHolder clicked pos=" + meteorDataList[adapterPosition])
                recyclerItemClickListener.onRecyclerItemClick(meteorDataList[adapterPosition])
            })
        }
    }


}
