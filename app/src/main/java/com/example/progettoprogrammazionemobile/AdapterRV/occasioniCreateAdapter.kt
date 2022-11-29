package com.example.progettoprogrammazionemobile.AdapterRV
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.appericolo.ui.preferiti.contacts.database.EventoDb
import com.example.progettoprogrammazionemobile.R
import com.example.progettoprogrammazionemobile.model.Evento
import kotlinx.android.synthetic.main.occasioni_create_card.view.*

class occasioniCreateAdapter(private var occasioniUtente: List<EventoDb>):
    RecyclerView.Adapter<occasioniCreateAdapter.viewHolder>() {
    private lateinit var cListener : OnCreatedClickListener

    interface OnCreatedClickListener{
        fun deleteEvent(idEvento : String, size: Int, position: String)
        fun modificaEvent (idEvento: String)
    }

    fun setOndeleteClickListener (listener: OnCreatedClickListener){
        cListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.occasioni_create_card, parent, false)
        return viewHolder(itemView, cListener)
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        val currentEvent = occasioniUtente[position]
        holder.idEvento = currentEvent.id_evento.toString()
        holder.tCreated.text = currentEvent.titolo
        holder.cCreated.text = currentEvent.categoria
        holder.dCreated.text = currentEvent.data_evento
    }

    override fun getItemCount(): Int {
            return  occasioniUtente.size
    }

    inner class viewHolder(itemView: View, listener: OnCreatedClickListener) : RecyclerView.ViewHolder(itemView){
        var idEvento : String = ""
        val tCreated : TextView = itemView.findViewById(R.id.titoloEventoCreato)
        val dCreated : TextView = itemView.findViewById(R.id.dataEventoCreato)
        val cCreated : TextView = itemView.findViewById(R.id.categoriaEventoCreato)
        val delete : ImageButton = itemView.findViewById(R.id.deleteOccasione)
        val modifica : ImageButton = itemView.findViewById(R.id.modficaOccasione)

        init {
            delete.setOnClickListener{
                listener.deleteEvent(idEvento as String, occasioniUtente.size, adapterPosition.toString())
                }
            modifica.setOnClickListener{
                listener.modificaEvent(idEvento as String)
                    }
            }

        }

    fun setData(contact: List<EventoDb>){
        this.occasioniUtente = contact
        notifyDataSetChanged()
    }
}