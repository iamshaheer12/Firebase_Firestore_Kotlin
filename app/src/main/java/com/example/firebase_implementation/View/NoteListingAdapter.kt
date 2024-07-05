package com.example.firebase_implementation.View

import android.renderscript.ScriptGroup.Binding
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.firebase_implementation.View.Model.Note
import com.example.firebase_implementation.databinding.ItemNoteLayoutBinding

class NoteListingAdapter(
    val onItemClicked:(Int, Note) ->Unit,
    val onEditClicked:(Int, Note) ->Unit,
    val onDeleteClicked:(Int, Note) ->Unit
):RecyclerView.Adapter<NoteListingAdapter.MyViewholder>() {
    private  var list: MutableList<Note> = arrayListOf()
    inner class MyViewholder(val binding: ItemNoteLayoutBinding):RecyclerView.ViewHolder(binding.root){
        fun bind(item :Note){
            binding.noteIdValue.setText(item.id)
            binding.messageLabelValue.setText(item.text)
            binding.edit.setOnClickListener{
                onEditClicked.invoke(adapterPosition,item)
            }
            binding.delete.setOnClickListener{onEditClicked.invoke(adapterPosition,item)}
            binding.itemLayout.setOnClickListener{
                onEditClicked.invoke(adapterPosition,item)
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




}