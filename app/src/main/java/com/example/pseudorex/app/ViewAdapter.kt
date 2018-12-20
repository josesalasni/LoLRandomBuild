package com.example.pseudorex.app

import android.content.Context
import android.content.Intent
import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.card_layout.view.*
import java.security.AccessController.getContext

class ViewAdapter (val Champions: List<Champions>, val versionNumber: String ,val context: Context) : RecyclerView.Adapter<ViewAdapter.ViewHolder> () {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.card_layout,parent,false), versionNumber   )
    }

    override fun getItemCount(): Int {
        return Champions.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems( Champions[position] )

        holder.itemView.setOnClickListener() {

            //If the user clicks in one champion, generate a build for him in the next activity
            var intent = Intent(context,  BuildActivity::class.java)
            intent.putExtra("Champion_name", Champions[position].name)
            intent.putExtra("Champion_key", Champions[position].key)
            intent.putExtra("Champion_version", versionNumber)

            context.startActivity(intent)

        }

    }

    //Notify to filter the champions
    fun updateList () {
        notifyDataSetChanged()
    }

    class ViewHolder (itemView: View, var versionNumber: String) : RecyclerView.ViewHolder (itemView) {
        fun bindItems (champion: Champions) {
            itemView.champion_name.text = champion.name
            Picasso.get().load("http://ddragon.leagueoflegends.com/cdn/" + versionNumber + "/img/champion/"+ champion.key + ".png" ).into(itemView.champion_image)
        }




    }


}