package com.example.firebase_implementation.View.View

import com.example.firebase_implementation.View.ViewModel.NoteViewModel
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
import com.example.firebase_implementation.R
import com.example.firebase_implementation.View.Model.Note
import com.example.firebase_implementation.View.Model.User
import com.example.firebase_implementation.View.NoteListingAdapter
import com.example.firebase_implementation.View.Utils.UiStates
import com.example.firebase_implementation.View.Utils.hide
import com.example.firebase_implementation.View.Utils.show
import com.example.firebase_implementation.View.Utils.toast
import com.example.firebase_implementation.View.ViewModel.AuthViewModel
//import com.example.firebase_implementation.View.ViewModel.com.example.firebase_implementation.View.ViewModel.NoteViewModel
import com.example.firebase_implementation.databinding.FragmentNoteListingBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NoteListingFragment : Fragment() {

    private lateinit var binding: FragmentNoteListingBinding
    private val viewModel: NoteViewModel by viewModels()
    private var recyclerViewState: Parcelable? = null
    private var notesList: List<Note> = emptyList()

    private val authViewModel: AuthViewModel by viewModels()

    var postion = -1
    val adapter by lazy {
        NoteListingAdapter(
            onItemClicked = { pos, item ->
                findNavController().navigate(R.id.action_noteListingFragment_to_noteDetailsFragment, Bundle().apply {
                    putString("item", "view")
                    putParcelable("note", item)
                })
            },
            onEditClicked = { pos, item ->
                findNavController().navigate(R.id.action_noteListingFragment_to_noteDetailsFragment, Bundle().apply {
                    putString("item", "edit")
                    putParcelable("note", item)
                })
            },
            onDeleteClicked = { pos, item ->
                postion = pos
                viewModel.deleteNote(item)
            }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNoteListingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        binding.recyclerNote.adapter = adapter
        deleteNote()
        viewModel.getNotes(user = getUser())
        observers()
        setOnclickListeners()
        setupSearchView()
       // registerConnectionObserver()
    }

//    override fun onDestroyView() {
//        super.onDestroyView()
//        unregisterConnectionObserver()
//    }

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
                    if (postion != -1) {
                        adapter.removeItem(postion)
                        postion = -1
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
                }
            }


//        viewModel.observe(viewLifecycleOwner) { state ->
//            when (state) {
//                is UiStates.Failure -> {
//                    binding.progressBar.hide()
//                    toast(state.error.toString())
//                    Log.e("error1", state.error.toString())
//                }
//                is UiStates.Loading -> {
//                    binding.progressBar.show()
//                }
//                is UiStates.Success<*> -> {
//                    binding.progressBar.hide()
//                    notesList = state.data
//                    adapter.updateList(state.data.toMutableList())
//                }
//            }
        }

//        lifecycleScope.launch {
//          val isAvailable =   viewModel.isNetworkAvailable().let {
//              if (!it){
//                  toast("Network is not connected ")
//              }
//
//          }
//            viewModel { isConnected ->
//                if (!isConnected) {
//
//                }
//
           // }
        //}
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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        recyclerViewState = binding.recyclerNote.layoutManager?.onSaveInstanceState()
        outState.putParcelable("recyclerViewState", recyclerViewState)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (savedInstanceState != null) {
            recyclerViewState = savedInstanceState.getParcelable("recyclerViewState")
            binding.recyclerNote.layoutManager?.onRestoreInstanceState(recyclerViewState)
        }
    }

    private fun setupSearchView() {
        val searchView = binding.searchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    filterNotes(newText)
                }
                return true
            }
        })
    }

    private fun filterNotes(query: String) {
        val filteredList = notesList.filter { item ->
            item.title.contains(query, ignoreCase = true) ||
                    item.message.contains(query, ignoreCase = true) ||
                    item.date.toString().contains(query, ignoreCase = true)
        }
        adapter.updateList(filteredList.toMutableList())
    }

//    private fun registerConnectionObserver() {
//        viewModel.registerConnectionObserver(viewLifecycleOwner)
//    }
//
//    private fun unregisterConnectionObserver() {
//        viewModel.unregisterConnectionObserver(viewLifecycleOwner)
//    }
}
