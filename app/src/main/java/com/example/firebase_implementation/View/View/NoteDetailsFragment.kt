package com.example.firebase_implementation.View.View

import com.example.firebase_implementation.View.ViewModel.NoteViewModel
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.firebase_implementation.View.Model.User
import com.example.firebase_implementation.View.Utils.UiStates
import com.example.firebase_implementation.View.Utils.hide
import com.example.firebase_implementation.View.Utils.show
import com.example.firebase_implementation.View.Utils.toast
import com.example.firebase_implementation.View.ViewModel.AuthViewModel
import com.example.firebase_implementation.databinding.FragmentNoteDetailsBinding
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class NoteDetailsFragment : Fragment() {

    lateinit var binding: FragmentNoteDetailsBinding
    val viewModel: NoteViewModel by viewModels()
    val authViewModel:AuthViewModel by viewModels()

    private var note: com.example.firebase_implementation.View.Model.Note? = null // Declare a nullable Note variable




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

        note = arguments?.getParcelable("note")

        binding.detailArrow.setOnClickListener {
            requireActivity().onBackPressed()
        }
        detail()




        viewModel.addNote.observe(viewLifecycleOwner){ state ->
            when(state){
                is UiStates.Failure ->{
                    binding.detailProgressBar.hide()
                    toast(state.error.toString())
                    Log.e("error1",state.error.toString())
                }
                is UiStates.Loading ->{
                    binding.detailProgressBar.show()
                }
                is UiStates.Success ->{
                    binding.detailProgressBar.hide()
                    requireActivity().onBackPressed()
                  //  requireArguments().
                    //adapter.updateList(state.data.toMutableList())
//                 state.data.forEach {
//                     Log.e(TAG,it.toString())
//                 }
                }
            }
        }
        viewModel.updateNote.observe(viewLifecycleOwner){ state ->
            when(state){
                is UiStates.Failure ->{
                    binding.detailProgressBar.hide()
                    toast(state.error.toString())
                    Log.e("error1",state.error.toString())
                }
                is UiStates.Loading ->{
                    binding.detailProgressBar.show()
                }
                is UiStates.Success ->{
                    binding.detailProgressBar.hide()
                    requireActivity().onBackPressed()
                    //  requireArguments().
                    //adapter.updateList(state.data.toMutableList())
//                 state.data.forEach {
//                     Log.e(TAG,it.toString())
//                 }
                }
            }
        }
    }



private fun detail(){
 if (arguments?.getString("item") == "view"){
     binding.addMessageBtn.hide()
     binding.detailTitle.setText(note?.title)
     binding.noteTitle.setText(note?.title)
     Log.e("Title_Note",note?.title.toString())
     binding.date.setText(note?.date?.let { formatTime(it) })

     binding.noteMsg.setText(note?.message)


    // binding.noteMsg.

 }
 else if (arguments?.getString("item") == "edit"){
     binding.addMessageBtn.setText("Update")
     binding.noteTitle.setText(note?.title)
     binding.date.setText(note?.date?.let { formatTime(it) })


     binding.detailTitle.setText(note?.title)
     binding.noteMsg.setText(note?.message)


     binding.addMessageBtn.setOnClickListener {
         if (validation()){
             viewModel.updateNote(
                 com.example.firebase_implementation.View.Model.Note(
                     id = note?.id?:"",
                     userId = note?.userId?:"",
                     title = binding.noteTitle.text.toString(),
                     message =  binding.noteMsg.text.toString(),
                     date = Date()

                 )
             )
         }

     }


 }
    else{
     binding.detailTitle.setText("Create Note")
     binding.date.hide()

     binding.addMessageBtn.setOnClickListener {
//            if ()

         if (validation()) {
             viewModel.addNote(
                 com.example.firebase_implementation.View.Model.Note(
                     id = "",
                     title = binding.noteTitle.text.toString(),
                     userId = getUser().id,
                     message =  binding.noteMsg.text.toString(),
                     date = Date()

                 )
             )
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


fun getUser():User{
    var usr = User()
    authViewModel.getSession { user->
        if(user!= null){
            usr = user

        }
        else{
            Log.e("error","$user")
        }
    }

    return usr

}

    fun formatTime(date: Date, pattern: String = "dd MMM yyyy, hh:mm a"): String {
        val formatter = SimpleDateFormat(pattern, Locale.getDefault())
        return formatter.format(date)
    }


}
