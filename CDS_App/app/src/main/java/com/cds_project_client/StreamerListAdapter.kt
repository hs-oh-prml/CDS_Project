package com.cds_project_client

import android.content.Context
import android.content.Intent
import android.text.Layout
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cds_project_client.data.ItemStreaming
import com.cds_project_client.streaming.StreamingActivity
import com.cds_project_client.util.CMClient

class StreamerListAdapter(
    var context: Context,
    var itemList:ArrayList<ItemStreaming>,
    var cmClient:CMClient
):RecyclerView.Adapter<StreamerListAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        TODO("Not yet implemented")
        val v = LayoutInflater.from(context)
            .inflate(R.layout.item_streaming_list, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount() = itemList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        TODO("Not yet implemented")
        var data = itemList[position]
        holder.viewers_num.text = data.viewers_num
        holder.streamer_id.text = data.streamer_id
        holder.itemView.setOnClickListener {

            ///////////////////////////////////////////////
            //         Enter Streaming as Viewer         //
            ///////////////////////////////////////////////
            Log.d("info", cmClient.cmClientStub.myself.currentSession.toString())
            Log.d("info", cmClient.cmClientStub.myself.currentGroup .toString())
            Log.d("info", cmClient.cmClientStub.myself.name.toString())


            var bRequestResult = cmClient.cmClientStub.changeGroup("g2")
            Log.d("response", bRequestResult.toString())
            val intent = Intent(context, StreamingActivity::class.java)
            intent.putExtra("streaming_mode", 0)
            context.startActivity(intent)

        }
    }

    inner class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        var streamer_id:TextView
        var viewers_num:TextView
        init{
            streamer_id = itemView.findViewById(R.id.streamer_id)
            viewers_num = itemView.findViewById(R.id.viewers_number)
        }
    }
}