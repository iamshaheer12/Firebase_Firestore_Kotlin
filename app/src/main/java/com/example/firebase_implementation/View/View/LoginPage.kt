package com.example.firebase_implementation.View.View

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.example.firebase_implementation.R
import com.example.firebase_implementation.View.Utils.UiStates
import com.example.firebase_implementation.View.Utils.hide
import com.example.firebase_implementation.View.Utils.show
import com.example.firebase_implementation.View.Utils.toast
import com.example.firebase_implementation.View.ViewModel.AuthViewModel
import com.example.firebase_implementation.databinding.FragmentLoginPageBinding
import com.example.firebase_implementation.databinding.FragmentSignUpPageBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint

class LoginPage : Fragment() {

    lateinit var binding: FragmentLoginPageBinding
    val viewModel:AuthViewModel by viewModels()
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginPageBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onStart()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("1084307967037-9nisnko09bjnta7kath0n5nnsvn3jb1q.apps.googleusercontent.com")
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

         googleSigin()
        binding.googleSignIn.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }



        binding.forgotPassword.setOnClickListener {
            findNavController().navigate(R.id.action_loginPage_to_forgotPassword)
        }


        binding.lgSignUp.setOnClickListener{
            findNavController().navigate(R.id.action_loginPage_to_signUpPage2)
        }
        LoginUser()
        binding.lgBtn.setOnClickListener{
            if (validateSignIn()){
                Log.d("testing sign in", "email = ${binding.lgUserName.text.toString()}")
                viewModel.loginUser(
                    email = binding.lgUserName.text.toString(),
                    password =  binding.lgPassword.text.toString(),
                )
            }

        }

    }
    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun validateSignIn(): Boolean {
        val username = binding.lgUserName.text.toString().trim()
        val password = binding.lgPassword.text.toString().trim()

        if (username.isEmpty()) {
            binding.lgUserName.error = "Username is required"
            binding.lgUserName.requestFocus()
            return false
        }

        if (!isValidEmail(username)) {
            binding.lgUserName.error = "Enter a valid email address"
            binding.lgUserName.requestFocus()
            return false
        }

        if (password.isEmpty()) {
            binding.lgPassword.error = "Password is required"
            binding.lgPassword.requestFocus()
            return false
        }

        return true
    }
private  fun LoginUser(){
    viewModel.login.observe(viewLifecycleOwner){ state ->
        when(state){
            is UiStates.Failure ->{
                binding.lgProgressBar.hide()
                toast(state.error.toString())
                Log.e("error1",state.error.toString())
            }
            is UiStates.Loading ->{
                binding.lgProgressBar.show()
            }
            is UiStates.Success ->{
                val navHostFragment = requireActivity().supportFragmentManager.findFragmentById(R.id.nav_host) as NavHostFragment
                val navController = navHostFragment.navController
                navController.navigate(R.id.action_loginPage_to_noteListingFragment)

                binding.lgProgressBar.hide()

   ///   requireActivity().onBackPressed()
                toast(state.data)

//                 state.data.forEach {
//                     Log.e(TAG,it.toString())
//                 }
            }
        }
    }

}
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == RC_SIGN_IN) {
//            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
//            try {
//                val account = task.getResult(ApiException::class.java)!!
//                viewModel.signInWithGoogle(account.idToken!!)
//            } catch (e: ApiException) {
//                Log.w(TAG, "Google sign in failed", e)
//            }
//        }
//    }

    fun googleSigin(){
        viewModel.signInState.observe(viewLifecycleOwner){ state ->
            when(state){
                is UiStates.Failure ->{
                    binding.lgProgressBar.hide()
                    toast(state.error.toString())
                    Log.e("error1",state.error.toString())
                }
                is UiStates.Loading ->{
                    binding.lgProgressBar.show()
                }
                is UiStates.Success ->{
                    val navHostFragment = requireActivity().supportFragmentManager.findFragmentById(R.id.nav_host) as NavHostFragment
                    val navController = navHostFragment.navController
                    navController.navigate(R.id.action_loginPage_to_noteListingFragment)

                    binding.lgProgressBar.hide()

                    ///   requireActivity().onBackPressed()
                    toast(state.data)

//                 state.data.forEach {
//                     Log.e(TAG,it.toString())
//                 }
                }
            }
        }

    }

    companion object {
        private const val RC_SIGN_IN = 9001 // Request code for Google Sign-In
        private const val TAG = "SignInFragment" // Tag for logging
    }

    override fun onStart() {
        super.onStart()
        viewModel.getSession { user ->
            Log.e("User1232",user.toString())
//            Log.e()

            if (user!= null){


                val navHostFragment = requireActivity().supportFragmentManager.findFragmentById(R.id.nav_host) as NavHostFragment
                val navController = navHostFragment.navController
                navController.navigate(R.id.action_loginPage_to_noteListingFragment)
//                findNavController().navigate(R.id.action_loginPage_to_noteListingFragment)
            }
        }
    }
}