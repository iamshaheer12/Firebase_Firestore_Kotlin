package com.example.firebase_implementation.View.View

import java.util.UUID


import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import com.example.firebase_implementation.R
import com.example.firebase_implementation.View.Model.DeletedNote
import com.example.firebase_implementation.View.Model.Note
import com.example.firebase_implementation.View.Model.User
import com.example.firebase_implementation.View.NoteListingAdapter
import com.example.firebase_implementation.View.Utils.NetworkUtil
import com.example.firebase_implementation.View.Utils.UiStates
import com.example.firebase_implementation.View.Utils.hide
import com.example.firebase_implementation.View.Utils.show
import com.example.firebase_implementation.View.Utils.toast
import com.example.firebase_implementation.View.ViewModel.AuthViewModel
import com.example.firebase_implementation.View.ViewModel.NoteViewModel
//import com.example.firebase_implementation.View.ViewModel.com.example.firebase_implementation.View.ViewModel.NoteViewModel
import com.example.firebase_implementation.databinding.FragmentNoteListingBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class NoteListingFragment : Fragment() {

    private lateinit var binding: FragmentNoteListingBinding
    private val viewModel: NoteViewModel by viewModels()
    private var recyclerViewState: Parcelable? = null
//    private var isConnected = false
    private var notesList:List<Note> = emptyList()
    private var savedSearchQuery: String? = null



    private val authViewModel: AuthViewModel by viewModels()

    private var postion = -1
    private val adapter by lazy {
        NoteListingAdapter(
            onItemClicked = { pos, item ->
                //if (item is Note) {
                    findNavController().navigate(R.id.action_noteListingFragment_to_noteDetailsFragment, Bundle().apply {
                        putString("item", "view")
                        putParcelable("note", item)
                    })
//
          },
            onEditClicked = { pos, item ->
              //  if (item is Note) {
                    findNavController().navigate(R.id.action_noteListingFragment_to_noteDetailsFragment, Bundle().apply {
                        putString("item", "edit")
                        putParcelable("note", item)
                    })
            },
            onDeleteClicked = { pos, item ->
                postion = pos

                    viewModel.deleteNote1(item,requireContext())

            }
        )
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

      binding = FragmentNoteListingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        binding.recyclerNote.adapter = adapter
        deleteNote()
       viewModel.fetchNotes(user = getUser(), context = requireContext())
        //connectivityResult(requireContext())
//        viewModel.overrideNotesWithFirebaseData(getUser())
        observers()
        setOnclickListeners()
        setupSearchView()
        uploadNotesToFirebase()
        deleteNoteOnFirebaseFromLocal()
      //  deleteDatabase(requireContext())

       // registerConnectionObserver()
    }


    override fun onStop() {
        super.onStop()
        saveRecycleViewState()
    savedSearchQuery = binding.searchView.query.toString()
    }

  private fun saveRecycleViewState(){
    viewModel.recyclerViewState = binding.recyclerNote.layoutManager?.onSaveInstanceState()
  }
    private fun restoreRecyclerViewState() {
        viewModel.recyclerViewState?.let {
            binding.recyclerNote.layoutManager?.onRestoreInstanceState(it)
        }
    }


    private fun deleteNote() {
        viewModel.deleteNote.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiStates.Failure -> {
                    binding.progressBar.hide()
                    toast(state.error.toString())
                    Log.e("error1", state.error.toString())
                }
                is UiStates.Loading -> {
                    binding.progressBar.show()
                }
                is UiStates.Success -> {
                    binding.progressBar.hide()
                    if (postion != -1 && postion < notesList.size) {
                        adapter.removeItem(postion)
                       // adapter.notifyItemRemoved(postion) // Notify adapter of item removal
                        postion = -1
                    }
                }
            }
        }
        viewModel.deleteLocalNote.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiStates.Failure -> {
                    binding.progressBar.hide()
                    toast(state.error.toString())
                    Log.e("error1", state.error.toString())
                }
                is UiStates.Loading -> {
                    binding.progressBar.show()
                }
                is UiStates.Success -> {
                    binding.progressBar.hide()


                    if (postion != -1 && postion < notesList.size) { // Check index validity
                        val note = notesList[postion]
                        Log.e("Deleted Note",note.toString())
                        adapter.removeItem(postion)
                        viewModel.addDeletedLocalNote(
                            note = DeletedNote(
                                id = note.id,
                                userId = note.userId
                            )
                        )
                        postion = -1
                    } else {
                        Log.e("Index Error", "Invalid index: $postion")
                    }
                }
            }
        }
    }

    private fun setOnclickListeners() {
        binding.create.setOnClickListener {
            findNavController().navigate(R.id.action_noteListingFragment_to_noteDetailsFragment, Bundle().apply {
                putString("item", "create")
            })
        }

        binding.root.findViewById<de.hdodenhof.circleimageview.CircleImageView>(R.id.avatar_image).setOnClickListener {
            findNavController().navigate(R.id.action_noteListingFragment_to_profilePage)
        }
    }

    private fun observers() {
        viewModel.notes.observe(viewLifecycleOwner){state->
            when (state) {
                is UiStates.Failure -> {
                    binding.progressBar.hide()
                    toast(state.error.toString())
                    Log.e("error1", state.error.toString())
                }
                is UiStates.Loading -> {
                    binding.progressBar.show()
                }
                is UiStates.Success -> {
                    binding.progressBar.hide()
                    notesList = state.data
                    adapter.updateList(state.data.toMutableList())
                    restoreRecyclerViewState()

                }
            }




        }
    viewModel.searchResults.observe(viewLifecycleOwner) { notes ->
            if (notes != null){
                adapter.updateList(notes.toMutableList())
                restoreRecyclerViewState()
            }
            restoreRecyclerViewState()

        }
//        lifecycleScope.launch {
            viewModel.localNotes.observe(viewLifecycleOwner){state->
            when (state) {
                is UiStates.Failure -> {
                    binding.progressBar.hide()
                    toast(state.error.toString())
                    Log.e("error1", state.error.toString())
                }
                is UiStates.Loading -> {
                    binding.progressBar.show()
                }
                is UiStates.Success -> {
                    binding.progressBar.hide()
                    notesList = state.data
                    adapter.updateList(state.data.toMutableList())
                    restoreRecyclerViewState()

                }
            }

        }


    }

    private fun getUser(): User {
        var usr = User()
        authViewModel.getSession { user ->
            if (user != null) {
                usr = user
            } else {
                Log.e("error", "$user")
            }
        }
        return usr
    }



    private fun setupSearchView() {
        val searchView = binding.searchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                   // filterNotes(newText)
                    viewModel.setSearchQuery(newText)
                }
                return true
            }
        })
    }



    private fun uploadNotesToFirebase() {

            viewModel.uploadNotes(context = requireContext())

    }

    private fun deleteNoteOnFirebaseFromLocal(){
        viewModel.uploadDeletedNotes(context = requireContext())
    }



    private fun deleteDatabase(context: Context) {
        context.deleteDatabase("app_database")
    }

//
}
