package com.cds_project_client

import android.content.Context
import android.content.Intent
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cds_project_client.data.ItemStreaming
import com.cds_project_client.streaming.StreamingActivity

class StreamerListAdapter(
    var context: Context,
    var itemList:ArrayList<ItemStreaming>
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

            val intent = Intent(context, StreamingActivity::class.java)
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