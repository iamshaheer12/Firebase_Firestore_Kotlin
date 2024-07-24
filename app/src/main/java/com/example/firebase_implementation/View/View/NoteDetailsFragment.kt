package com.example.firebase_implementation.View.View

import NoteData
import com.example.firebase_implementation.View.ViewModel.NoteViewModel
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.room.TypeConverters
import com.example.firebase_implementation.View.Local_Data.NoteEntity
import com.example.firebase_implementation.View.Model.Note
import com.example.firebase_implementation.View.Model.User
import com.example.firebase_implementation.View.Utils.NetworkUtil
import com.example.firebase_implementation.View.Utils.TypeConverter
import com.example.firebase_implementation.View.Utils.UiStates
import com.example.firebase_implementation.View.Utils.hide
import com.example.firebase_implementation.View.Utils.show
import com.example.firebase_implementation.View.Utils.toast
import com.example.firebase_implementation.View.ViewModel.AuthViewModel
import com.example.firebase_implementation.databinding.FragmentNoteDetailsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class NoteDetailsFragment : Fragment() {

    private lateinit var binding: FragmentNoteDetailsBinding
    private val viewModel: NoteViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()
    private var noteData: NoteData? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNoteDetailsBinding.inflate(inflater, container, false)

        noteData = when {
            arguments?.getParcelable<Note>("note") != null -> {
                NoteData.NoteType(arguments?.getParcelable("note")!!)
            }
            arguments?.getParcelable<NoteEntity>("noteEntity") != null -> {
                NoteData.NoteEntityType(arguments?.getParcelable("noteEntity")!!)
            }
            else -> null
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observers()
        binding.detailArrow.setOnClickListener { requireActivity().onBackPressed() }
        detail()
    }

    private fun detail() {
        when (arguments?.getString("item")) {
            "view" -> {
                binding.addMessageBtn.hide()
                when (val data = noteData) {
                    is NoteData.NoteType -> {
                        binding.detailTitle.setText(data.note.title)
                        binding.noteTitle.setText(data.note.title)
                        binding.date.setText(formatTime(data.note.date))
                        binding.noteMsg.setText(data.note.message)
                    }
                    is NoteData.NoteEntityType -> {
                        binding.detailTitle.setText(data.noteEntity.title)
                        binding.noteTitle.setText(data.noteEntity.title)
                        binding.date.setText(formatTime(TypeConverter.toDate(data.noteEntity.date)))
                        binding.noteMsg.setText(data.noteEntity.message)
                    }
                    null -> {
                        // Handle null case
                    }

                    else -> {}
                }
            }
            "edit" -> {
                binding.addMessageBtn.setText("Update")
                when (val data = noteData) {
                    is NoteData.NoteType -> {
                        binding.noteTitle.setText(data.note.title)
                        binding.date.setText(formatTime(data.note.date))
                        binding.detailTitle.setText(data.note.title)
                        binding.noteMsg.setText(data.note.message)

                        binding.addMessageBtn.setOnClickListener {
                            if (validation()) {
                                viewModel.updateNote(
                                    Note(
                                        id = data.note.id,
                                        userId = data.note.userId,
                                        title = binding.noteTitle.text.toString(),
                                        message = binding.noteMsg.text.toString(),
                                        date = Date()
                                    )
                                )
                            }
                        }
                    }
                    is NoteData.NoteEntityType -> {
                        binding.noteTitle.setText(data.noteEntity.title)
                        binding.date.setText(formatTime(TypeConverter.toDate(data.noteEntity.date)))
                        binding.detailTitle.setText(data.noteEntity.title)
                        binding.noteMsg.setText(data.noteEntity.message)

                        binding.addMessageBtn.setOnClickListener {
                            if (validation()) {
                                viewModel.updateLocalNote(
                                    NoteEntity(
                                        id = data.noteEntity.id,
                                        title = binding.noteTitle.text.toString(),
                                        userId = data.noteEntity.userId,
                                        message = binding.noteMsg.text.toString(),
                                        date = data.noteEntity.date
                                    )
                                )
                            }
                        }
                    }

                    else -> {}
                }
            }
            else -> {
                binding.detailTitle.setText("Create Note")
                binding.date.hide()
                binding.addMessageBtn.setOnClickListener { connectivityCheck() }
            }
        }
    }

    private fun observers() {
        viewModel.addNote.observe(viewLifecycleOwner) { state ->

            handleState(state, "addNote")
        }
        viewModel.addLocalNote.observe(viewLifecycleOwner) { state ->
            handleState(state, "addLocalNote")
        }
        viewModel.updateNote.observe(viewLifecycleOwner) { state ->
            handleState(state, "updateNote")
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
        return !binding.noteMsg.text.isNullOrEmpty()
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
                        id = "",
                        title = binding.noteTitle.text.toString(),
                        userId = getUser().id,
                        message = binding.noteMsg.text.toString(),
                        date = Date()
                    )
                )
            }
        } else {
            if (validation()) {
            //    lifecycleScope.launch{
                    viewModel.addLocalNote(
                        NoteEntity(
                            id = 0,
                            title = binding.noteTitle.text.toString(),
                            userId = getUser().id,
                            message = binding.noteMsg.text.toString(),
                            date = TypeConverter.fromDate(Date()) ?: 0L
                        )
                    )

              //  }

            }
        }
    }
}
