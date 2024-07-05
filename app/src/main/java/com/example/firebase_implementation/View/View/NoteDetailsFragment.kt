package com.example.firebase_implementation.View.View

import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Note
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.firebase_implementation.View.Utils.UiStates
import com.example.firebase_implementation.View.Utils.hide
import com.example.firebase_implementation.View.Utils.show
import com.example.firebase_implementation.View.Utils.toast
import com.example.firebase_implementation.View.ViewModel.NoteViewModel
import com.example.firebase_implementation.databinding.FragmentNoteDetailsBinding
import dagger.hilt.android.AndroidEntryPoint
import java.util.Date

@AndroidEntryPoint
class NoteDetailsFragment : Fragment() {

    lateinit var binding: FragmentNoteDetailsBinding
    val viewModel:NoteViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNoteDetailsBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.addMessage.setOnClickListener {
            if (validation()) {
                viewModel.addNote(
                    com.example.firebase_implementation.View.Model.Note(
                        id = "",
                        text =  binding.noteMsg.text.toString(),
                        date = Date()

                    )
                )
            }
        }
        viewModel.addNote.observe(viewLifecycleOwner){ state ->
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

private fun validation():Boolean{
    var isValid = true
    if (binding.noteMsg.text.toString().isNullOrEmpty()) {
        isValid = false

    }
    return isValid
    }

}
}