package com.example.firebase_implementation.View

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.firebase_implementation.View.Model.Note
import com.example.firebase_implementation.View.Utils.TypeConverter
import com.example.firebase_implementation.databinding.ItemNoteLayoutBinding
import java.util.Date

class NoteListingAdapter(
    val onItemClicked: (Int, Note) -> Unit,
    val onEditClicked: (Int, Note) -> Unit,
    val onDeleteClicked: (Int, Note) -> Unit
) : RecyclerView.Adapter<NoteListingAdapter.MyViewHolder>() {

    private var list: MutableList<Note> = arrayListOf()

    inner class MyViewHolder(private val binding: ItemNoteLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(item: Note) {
            binding.noteTitle.text = item.title
            binding.dateTime.text = TypeConverter.formatDate(Date(item.date))
            binding.messageLabelValue.text = item.message

            binding.edit.setOnClickListener {
                onEditClicked.invoke(adapterPosition, item)
            }
            binding.delete.setOnClickListener {
                onDeleteClicked.invoke(adapterPosition, item)
            }
            binding.itemLayout.setOnClickListener {
                onItemClicked.invoke(adapterPosition, item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = ItemNoteLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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
    fun updateList(note: MutableList<Note>) {
        this.list = note
        notifyDataSetChanged()
    }

    fun removeItem(position: Int) {
        if (position >= 0 && position < list.size) {
            list.removeAt(position)
            notifyItemRemoved(position)  // Correct method to notify item removal
            notifyItemRangeChanged(position, itemCount)  // Notify the adapter that item range has changed
        }
    }
}
