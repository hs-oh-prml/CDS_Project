package com.cds_project_client

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.VERTICAL
import com.cds_project_client.data.ItemStreaming
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    lateinit var itemList: ArrayList<ItemStreaming>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
    }

    fun dataInit(){
        // Dummy Data
        for(i in 0..10){
            itemList.add(ItemStreaming("${i}-Streamer", "${0/8}"))
        }
        
        ///////////////////////////////////////////////
        //           Get List from Server            //
        ///////////////////////////////////////////////

    }

    private fun init(){
        itemList = ArrayList()
        dataInit()
        val layoutManager = LinearLayoutManager(this, VERTICAL, false)
        val adapter = StreamerListAdapter(this, itemList)
        recycler_view.layoutManager = layoutManager
        recycler_view.adapter = adapter
    }

}
