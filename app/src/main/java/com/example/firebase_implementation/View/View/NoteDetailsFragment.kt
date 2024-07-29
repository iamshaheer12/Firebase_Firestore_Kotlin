package com.example.firebase_implementation.View.View

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.firebase_implementation.View.Model.Note
import com.example.firebase_implementation.View.Model.User
import com.example.firebase_implementation.View.Utils.NetworkUtil
import com.example.firebase_implementation.View.Utils.TypeConverter
import com.example.firebase_implementation.View.Utils.UiStates
import com.example.firebase_implementation.View.Utils.hide
import com.example.firebase_implementation.View.Utils.show
import com.example.firebase_implementation.View.Utils.toast
import com.example.firebase_implementation.View.ViewModel.AuthViewModel
import com.example.firebase_implementation.View.ViewModel.NoteViewModel
import com.example.firebase_implementation.databinding.FragmentNoteDetailsBinding
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

@AndroidEntryPoint
class NoteDetailsFragment : Fragment() {

    private lateinit var binding: FragmentNoteDetailsBinding
    private val viewModel: NoteViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()
    private var noteData: Note? = null
    val noteId = UUID.randomUUID().toString()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNoteDetailsBinding.inflate(inflater, container, false)

        noteData = arguments?.getParcelable("note")

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observers()
        binding.detailArrow.setOnClickListener { requireActivity().onBackPressed() }
        detail()
    }

    @SuppressLint("SetTextI18n")
    private fun detail() {
        when (arguments?.getString("item")) {
            "view" -> {
                binding.addMessageBtn.hide()
                noteData?.let { data ->
                    binding.detailTitle.text = data.title
                    binding.noteTitle.setText(data.title)
                    binding.date.text = formatTime(Date(data.date))
                    binding.noteMsg.setText(data.message)
                }
            }
            "edit" -> {
                binding.addMessageBtn.text = "Update"
                noteData?.let { data ->
                    binding.noteTitle.setText(data.title)
                    binding.date.text = formatTime(Date(data.date))
                    binding.detailTitle.text = data.title
                    binding.noteMsg.setText(data.message)

                    binding.addMessageBtn.setOnClickListener {
                        if (validation()) {
                            if (NetworkUtil.isNetworkAvailable(requireContext())) {
                                viewModel.updateNote(
                                    Note(
                                        id = data.id,
                                        userId = data.userId,
                                        title = binding.noteTitle.text.toString(),
                                        message = binding.noteMsg.text.toString(),
                                        date = TypeConverter.fromDate(Date()),
                                        synced = true

                                    )
                                )
                            } else {
                                viewModel.updateLocalNote(
                                    Note(
                                        id = data.id,
                                        userId = data.userId,
                                        title = binding.noteTitle.text.toString(),
                                        message = binding.noteMsg.text.toString(),
                                        date = TypeConverter.fromDate(Date()),
                                        synced = false

                                    )
                                )
                            }
                        }
                    }
                }
            }
            else -> {
                binding.detailTitle.text = "Create Note"
                binding.date.hide()
                binding.addMessageBtn.setOnClickListener { connectivityCheck() }
            }
        }
    }

    private fun observers() {
        viewModel.addNote.observe(viewLifecycleOwner) { state ->
            handleState(state, "addNote")
        }
        viewModel.updateNote.observe(viewLifecycleOwner) { state ->
            handleState(state, "updateNote")
        }
        viewModel.addLocalNote.observe(viewLifecycleOwner) { state ->
            handleState(state, "addLocalNote")
        }
        viewModel.updateLocalNote.observe(viewLifecycleOwner) { state ->
            handleState(state, "updateLocalNote")
        }
    }

    private fun handleState(state: UiStates<String>, action: String) {
        when (state) {
            is UiStates.Failure -> {
                binding.detailProgressBar.hide()
                toast(state.error.toString())
                Log.e("error", "$action: ${state.error}")
            }
            is UiStates.Loading -> binding.detailProgressBar.show()
            is UiStates.Success -> {
                binding.detailProgressBar.hide()
                requireActivity().onBackPressed()
            }
        }
    }

    private fun validation(): Boolean {
        //!binding.noteMsg.text.isNullOrEmpty()
        var isValidate = true
        if (binding.noteMsg.text.isNullOrEmpty() || binding.noteTitle.text.isNullOrEmpty()) {
            try {
                isValidate = false
            } catch (e: Exception) {
                TODO("Not yet implemented")
            }
        }
        return isValidate
    }

    private fun getUser(): User {
        var user = User()
        authViewModel.getSession { fetchedUser ->
            fetchedUser?.let {
                user = it
            } ?: run {
                Log.e("error", "User is null")
            }
        }
        return user
    }

    private fun formatTime(date: Date?, pattern: String = "dd MMM yyyy, hh:mm a"): String {
        val formatter = SimpleDateFormat(pattern, Locale.getDefault())
        return formatter.format(date)
    }

    private fun connectivityCheck() {
        if (NetworkUtil.isNetworkAvailable(requireContext())) {
            if (validation()) {
                viewModel.addNote(
                    Note(
                        id = noteId,
                        title = binding.noteTitle.text.toString(),
                        userId = getUser().id,
                        message = binding.noteMsg.text.toString(),
                        date = TypeConverter.fromDate(Date()),
                        synced = true


                    )
                )
            }
        } else {
            if (validation()) {
                viewModel.addLocalNote(
                    Note(
                        id = noteId,
                        title = binding.noteTitle.text.toString(),
                        userId = getUser().id,
                        message = binding.noteMsg.text.toString(),
                        date = TypeConverter.fromDate(Date()),
                        synced = false
                    )
                )
            }
        }
    }
}
