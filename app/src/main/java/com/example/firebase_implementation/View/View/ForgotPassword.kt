package com.example.firebase_implementation.View.View

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import com.example.firebase_implementation.R
import com.example.firebase_implementation.View.Utils.UiStates
import com.example.firebase_implementation.View.Utils.hide
import com.example.firebase_implementation.View.Utils.show
import com.example.firebase_implementation.View.Utils.toast
import com.example.firebase_implementation.View.ViewModel.AuthViewModel
import com.example.firebase_implementation.databinding.FragmentForgotPasswordBinding
import com.example.firebase_implementation.databinding.FragmentLoginPageBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ForgotPassword : Fragment() {
  lateinit var binding: FragmentForgotPasswordBinding
  val viewModel:AuthViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentForgotPasswordBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ForgotPasswordUser()
        binding.frBtn.setOnClickListener {
            viewModel.forgotPassword(binding.frUserName.text.toString().trim())


        }

    }


    private  fun ForgotPasswordUser() {
        viewModel.forgot.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiStates.Failure -> {
                    binding.lgProgressBar.hide()
                    toast(state.error.toString())
                    Log.e("error1", state.error.toString())
                }

                is UiStates.Loading -> {
                    binding.lgProgressBar.show()
                }

                is UiStates.Success -> {
                    requireActivity().onBackPressed()
//                    val navHostFragment =
//                        requireActivity().supportFragmentManager.findFragmentById(R.id.nav_host) as NavHostFragment
//                    val navController = navHostFragment.navController
//                    navController.navigate(R.id.action_loginPage_to_noteListingFragment)

                    binding.lgProgressBar.hide()

                    ///   requireActivity().onBackPressed()
                    toast(state.data)

//                 state.data.forEach {
//                     Log.e(TAG,it.toString())
//                 }
                }
            }
        }


    }}