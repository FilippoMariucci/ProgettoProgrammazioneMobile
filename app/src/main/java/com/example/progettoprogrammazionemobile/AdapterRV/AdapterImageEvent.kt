package com.example.progettoprogrammazionemobile.AdapterRV

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.appericolo.ui.preferiti.contacts.database.EventoDb
import com.example.progettoprogrammazionemobile.R
import com.example.progettoprogrammazionemobile.database.ImageUrlDb
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.event_item.view.*

class AdapterImageEvent: RecyclerView.Adapter<AdapterImageEvent.MyViewHolder>() {

    private lateinit var cListener: onItemClickListener
    interface onItemClickListener{

        fun onItemClick(idevento: String)
        fun skipEvent(posizione: String)
    }

    fun setOnItemClickListener(listener: onItemClickListener){
        cListener = listener
    }

    private var contactsList = emptyList<EventoDb>()
    private var imageList = emptyList<ImageUrlDb>()

    class MyViewHolder(itemView: View, listener: onItemClickListener): RecyclerView.ViewHolder(itemView){
        var idEvent : String = ""
        val bottonInfo : FloatingActionButton = itemView.findViewById(R.id.buttonIminterest)
        val bottonDelete : FloatingActionButton = itemView.findViewById(R.id.skiphomeBtn)
        init {
            bottonInfo.setOnClickListener{
                listener.onItemClick(idEvent as String)
            }
            bottonDelete.setOnClickListener{
                listener.skipEvent(adapterPosition.toString() as String)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        return MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.event_item, parent,false), cListener)
    }

    override fun getItemCount(): Int {
        return contactsList.size
    }

//    public fun getItem(position: Int): EventoDb{
//        return contactsList[position]
//    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int){
        val currentItem = contactsList[position]
        holder.idEvent = currentItem.id_evento.toString()
        val url = currentItem.foto
        Glide.with(holder.itemView).load(url).into(holder.itemView.immagineEvento)

        holder.itemView.findViewById<TextView>(R.id.tvEventDesc).text = currentItem.titolo
        holder.itemView.findViewById<TextView>(R.id.prezzoEvento).text = currentItem.costo + "€"
        holder.itemView.findViewById<TextView>(R.id.categoriaEvento).text = currentItem.categoria
        holder.itemView.findViewById<TextView>(R.id.dataEvento).text = currentItem.data_evento
    }

    fun setData(contact: List<EventoDb>){
        this.contactsList = contact
        notifyDataSetChanged()
    }

    fun setImage(imageSingola: List<ImageUrlDb>){
        this.imageList = imageSingola
        notifyDataSetChanged()
    }

}