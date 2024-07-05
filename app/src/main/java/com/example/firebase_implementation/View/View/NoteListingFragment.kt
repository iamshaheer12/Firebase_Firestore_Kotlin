package com.example.firebase_implementation.View.View

import android.content.ContentValues.TAG
import android.nfc.Tag
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.firebase_implementation.View.NoteListingAdapter
import com.example.firebase_implementation.View.Utils.UiStates
import com.example.firebase_implementation.View.Utils.hide
import com.example.firebase_implementation.View.Utils.show
import com.example.firebase_implementation.View.Utils.toast
import com.example.firebase_implementation.View.ViewModel.NoteViewModel
import com.example.firebase_implementation.databinding.FragmentNoteListingBinding
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.ViewModelLifecycle

@AndroidEntryPoint
class NoteListingFragment : Fragment() {

    private lateinit var binding: FragmentNoteListingBinding
    private val viewModel: NoteViewModel by viewModels()
    val adapter by lazy {
        NoteListingAdapter(
            onItemClicked = {pos,item ->

            },
            onEditClicked = {pos,item ->}
            ,
            onDeleteClicked = {pos,item ->}

        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNoteListingBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerNote.adapter = adapter
        viewModel.getNotes()
        viewModel.note.observe(viewLifecycleOwner){ state ->
         when(state){
             is UiStates.Failure ->{
                 binding.progressBar.hide()
                 toast(state.error.toString())
              Log.e("error1",state.error.toString())
             }
             is UiStates.Loading ->{
               binding.progressBar.show()
             }
             is UiStates.Success ->{
                 binding.progressBar.hide()
                 adapter.updateList(state.data.toMutableList())
//                 state.data.forEach {
//                     Log.e(TAG,it.toString())
//                 }
             }
         }
        }
    }
}
