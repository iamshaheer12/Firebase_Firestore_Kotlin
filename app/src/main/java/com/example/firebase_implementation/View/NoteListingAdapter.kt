package com.example.firebase_implementation.View

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.firebase_implementation.View.Model.Note
import com.example.firebase_implementation.databinding.ItemNoteLayoutBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NoteListingAdapter(
    val onItemClicked:(Int, Note) ->Unit,
    val onEditClicked:(Int, Note) ->Unit,
    val onDeleteClicked:(Int, Note) ->Unit
):RecyclerView.Adapter<NoteListingAdapter.MyViewholder>() {
    private  var list: MutableList<Note> = arrayListOf()
    inner class MyViewholder(val binding: ItemNoteLayoutBinding):RecyclerView.ViewHolder(binding.root){
        fun bind(item :Note){
            binding.noteTitle.setText(item.title)
            binding.dateTime.setText(formatTime(item.date))

            binding.messageLabelValue.setText(item.message)
            binding.edit.setOnClickListener{
                onEditClicked.invoke(adapterPosition,item)
            }
            binding.delete.setOnClickListener{onDeleteClicked.invoke(adapterPosition,item)}
            binding.itemLayout.setOnClickListener{
                onItemClicked.invoke(adapterPosition,item)
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewholder {

        val itemView =ItemNoteLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
    return MyViewholder(itemView)

    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: MyViewholder, position: Int) {
        val item = list[position]
        holder.bind(item)
    }
    fun updateList(note: MutableList<Note>){
        this.list = note
        notifyDataSetChanged()

    }
    fun removeItem(position: Int){
        list.removeAt(position)
        notifyItemChanged(position)
    }



    fun formatTime(date: Date, pattern: String = "dd MMM yyyy, hh:mm a"): String {
        val formatter = SimpleDateFormat(pattern, Locale.getDefault())
        return formatter.format(date)
    }



}