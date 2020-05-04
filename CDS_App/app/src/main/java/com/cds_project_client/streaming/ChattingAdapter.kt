package com.cds_project_client.streaming

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cds_project_client.R
import com.cds_project_client.data.ItemChatting

class ChattingAdapter (
    var context: Context,
    var itemList:ArrayList<ItemChatting>
): RecyclerView.Adapter<ChattingAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        TODO("Not yet implemented")
        val v = LayoutInflater.from(context)
            .inflate(R.layout.item_message, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount() = itemList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        TODO("Not yet implemented")
        var data = itemList[position]
        holder.user_id.text = data.user_id
        holder.message.text = data.message
    }

    inner class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        var user_id: TextView
        var message:TextView
        init{
            user_id = itemView.findViewById(R.id.user_id)
            message = itemView.findViewById(R.id.message)
        }
    }
}