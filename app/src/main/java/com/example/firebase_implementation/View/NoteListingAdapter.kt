package com.example.firebase_implementation.View

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.firebase_implementation.View.Local_Data.NoteEntity
import com.example.firebase_implementation.View.Model.Note
import com.example.firebase_implementation.View.Utils.TypeConverter
import com.example.firebase_implementation.databinding.ItemNoteLayoutBinding
import java.util.Date

class NoteListingAdapter(
    val onItemClicked:(Int, Any) ->Unit,
    val onEditClicked:(Int, Any) ->Unit,
    val onDeleteClicked:(Int, Any) ->Unit
):RecyclerView.Adapter<NoteListingAdapter.MyViewHolder>() {
    private  var list: MutableList<Any> = arrayListOf()
    inner class MyViewHolder(private val binding: ItemNoteLayoutBinding):RecyclerView.ViewHolder(binding.root){
        @SuppressLint("SetTextI18n")
        fun bind(item: Any){
            when (item) {
                is Note -> {
                    // Handle Note
                    binding.noteTitle.text = item.title
                    binding.dateTime.text = TypeConverter.formatDate(item.date)

                       // formatTime(item.date)
                    binding.messageLabelValue.text = item.message
                }
                is NoteEntity -> {
                    // Handle NoteEntity
                    binding.noteTitle.text = item.title
                    binding.dateTime.text = TypeConverter.formatDate(Date(item.date))
                    binding.messageLabelValue.text = item.message
                }
                else -> {
                    // Handle unknown types if necessary
                    binding.noteTitle.text = "Unknown"
                    binding.dateTime.text = "Unknown"
                    binding.messageLabelValue.text = "Unknown"
                }
            }

            binding.edit.setOnClickListener{
                onEditClicked.invoke(adapterPosition,item)
            }
            binding.delete.setOnClickListener{onDeleteClicked.invoke(adapterPosition,item)}
            binding.itemLayout.setOnClickListener{
                onItemClicked.invoke(adapterPosition,item)
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val itemView =ItemNoteLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
    return MyViewHolder(itemView)

    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = list[position]
        holder.bind(item)
    }
    @SuppressLint("NotifyDataSetChanged")
    fun updateList(note: MutableList<Any>){
        this.list = note
        notifyDataSetChanged()

    }
    fun removeItem(position: Int){
        list.removeAt(position)
        notifyItemChanged(position)
    }







}