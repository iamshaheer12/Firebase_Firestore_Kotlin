package com.example.firebase_implementation.View.View

import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import com.example.firebase_implementation.R
import com.example.firebase_implementation.View.Model.User
import com.example.firebase_implementation.View.Utils.UiStates
import com.example.firebase_implementation.View.Utils.hide
import com.example.firebase_implementation.View.Utils.show
import com.example.firebase_implementation.View.Utils.toast
import com.example.firebase_implementation.View.ViewModel.AuthViewModel
//import com.example.firebase_implementation.View.ViewModel.com.example.firebase_implementation.View.ViewModel.NoteViewModel
import com.example.firebase_implementation.databinding.FragmentNoteDetailsBinding
import com.example.firebase_implementation.databinding.FragmentSignUpPageBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignUpPage() : Fragment() {
    lateinit var binding: FragmentSignUpPageBinding
    val viewModel: AuthViewModel by viewModels()

//    constructor(parcel: Parcel) : this() {
//
//    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignUpPageBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        createUser()

        binding.suSignIn.setOnClickListener {
            val navHostFragment =
                requireActivity().supportFragmentManager.findFragmentById(R.id.nav_host) as NavHostFragment
            val navController = navHostFragment.navController
            navController.navigate(R.id.action_signUpPage_to_loginPage)

        }
        binding.suBtn.setOnClickListener {
            if (validateSignUp()) {
                viewModel.createUser(
                    email = binding.suUserName.text.toString().trim(),
                    password = binding.suPassword.text.toString().trim(),
                    user = User(
                        id = "",
                        email = binding.suUserName.text.toString().trim(),
                        password = binding.suPassword.text.toString().trim(),
                        name = binding.suName.text.toString().trim(),
                        imageUrl = ""

                    )


                )


            }
        }
    }


    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun validateSignUp(): Boolean {
        val name = binding.suName.text.toString().trim()
        val username = binding.suUserName.text.toString().trim()
        val password = binding.suPassword.text.toString().trim()

        if (name.isEmpty()) {
            binding.suName.error = "Name is required"
            binding.suName.requestFocus()
            return false
        }

        if (username.isEmpty()) {
            binding.suUserName.error = "Username is required"
            binding.suUserName.requestFocus()
            return false
        }

        if (!isValidEmail(username)) {
            binding.suUserName.error = "Enter a valid email address"
            binding.suUserName.requestFocus()
            return false
        }

        if (password.isEmpty()) {
            binding.suPassword.error = "Password is required"
            binding.suPassword.requestFocus()
            return false
        }

        if (password.length < 6) {
            binding.suPassword.error = "Password must be at least 6 characters"
            binding.suPassword.requestFocus()
            return false
        }

        return true
    }


    private fun createUser() {
        viewModel.create.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiStates.Failure -> {
                    binding.suProgressBar.hide()
                    toast(state.error.toString())
                    Log.e("error1", state.error.toString())
                }

                is UiStates.Loading -> {
                    binding.suProgressBar.show()
                }

                is UiStates.Success -> {
                    val navHostFragment =
                        requireActivity().supportFragmentManager.findFragmentById(R.id.nav_host) as NavHostFragment
                    val navController = navHostFragment.navController
                    navController.navigate(
                        R.id.action_signUpPage_to_loginPage
//                        ,Bundle().apply {
//                        putString("type","create")
//                    }
                    )
                    binding.suProgressBar.hide()


                    ///   requireActivity().onBackPressed()
                    toast(state.data)

//                 state.data.forEach {
//                     Log.e(TAG,it.toString())
//                 }
                }
            }
        }

    }
}
//    override fun writeToParcel(parcel: Parcel, flags: Int) {
//
//    }
//
//    override fun describeContents(): Int {
//        return 0
//    }

//    companion object CREATOR : Parcelable.Creator<SignUpPage> {
//        override fun createFromParcel(parcel: Parcel): SignUpPage {
//            return SignUpPage(parcel)
//        }
//
//        override fun newArray(size: Int): Array<SignUpPage?> {
//            return arrayOfNulls(size)
//        }
//    }


