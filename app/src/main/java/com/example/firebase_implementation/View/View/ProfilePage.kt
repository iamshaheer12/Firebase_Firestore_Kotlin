package com.example.firebase_implementation.View.View

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.firebase_implementation.R
import com.example.firebase_implementation.View.Model.User
import com.example.firebase_implementation.View.Utils.UiStates
import com.example.firebase_implementation.View.Utils.hide
import com.example.firebase_implementation.View.Utils.show
import com.example.firebase_implementation.View.Utils.toast
import com.example.firebase_implementation.View.ViewModel.AuthViewModel
import com.example.firebase_implementation.databinding.FragmentProfilePageBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
class ProfilePage : Fragment() {
    private lateinit var binding: FragmentProfilePageBinding
    private val viewModel: AuthViewModel by viewModels()
    private var imageUri: Uri? = null

    private val pickMedia = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            imageUri = uri
            binding.circleImageView.setImageURI(uri)
            uploadImageAndUpdateUser(uri)
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            pickMedia.launch("image/*")
        } else {
            Toast.makeText(context, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfilePageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setContent()
        setOnClickListener()
        observeViewModel()
        setAnimations()
    }

    private fun observeViewModel() {

        viewModel.uploadState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiStates.Failure -> {
                    binding.prProgressBar.hide()
                    toast(state.error.toString())
                    Log.e("Uploading image", "Error in Uploading image : ${state.error}")
                }
                is UiStates.Loading -> {
                    binding.prProgressBar.show()
                }
                is UiStates.Success -> {
                  //  toast(state.data.toString())
                    toast("Successfully Uploaded the image ")
                    getUser { user ->
                    user.let {
                        val updatedUser = it.copy(imageUrl = state.data.toString())
                        viewModel.updateUser(updatedUser)
                    } ?: Log.e("ProfilePage", "User is null, cannot update")
                }
                    binding.prProgressBar.hide()
                }
            }
        }
    }

    private fun setContent() {
        getUser { user ->
            binding.prName.text = user.name
            binding.prEmail.text = user.email

            user.imageUrl.let {
                imageUri = Uri.parse(it)
              //  toast(it.toString())
                Glide.with(requireContext())
                    .load(imageUri)

                   .error(R.drawable.google)
                   .into(binding.circleImageView);
               // binding.circleImageView.setImageURI(imageUri)
            }
      }
    }

    private fun getUser(callback: (User) -> Unit) {
        viewModel.getSession { user ->
            user?.let { callback(it) }
        }
    }

    private fun setOnClickListener() {
        binding.prLogout.setOnClickListener { signOut() }
        binding.detailArrow.setOnClickListener { findNavController().popBackStack() }
        binding.circleImageView.setOnClickListener {
            //if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                pickMedia.launch("image/*")
//            } else {
//                requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
//            }
        }
    }

    private fun signOut() {
        viewModel.logOut {
            findNavController().navigate(R.id.action_profilePage_to_loginPage)
        }
    }

    private fun uploadImageAndUpdateUser(uri: Uri) {
        viewModel.uploadImage(uri){
        }
        }

    private fun setAnimations(){
        val topToBottom = AnimationUtils.loadAnimation(requireContext(), R.anim.top_to_bottom)
        val setToBack = AnimationUtils.loadAnimation(requireContext(), R.anim.set_to_back)
        val bottomToTop = AnimationUtils.loadAnimation(requireContext(), R.anim.bottom_to_top)
        val leftToRight = AnimationUtils.loadAnimation(requireContext(), R.anim.left_to_right)
        val rightToLeft = AnimationUtils.loadAnimation(requireContext(), R.anim.right_to_left)
        val combineAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.combine_animation_shake)





        binding.profileCard.startAnimation(topToBottom)
        binding.line1Card.startAnimation(leftToRight)
        binding.line2Card.startAnimation(rightToLeft)
//        binding.line3Card.startAnimation(bottomToTop)
        binding.prLogout.startAnimation(combineAnimation)
        binding.circleImageView.startAnimation(setToBack)
    }


}
