package com.example.walled.feature.feature_feed.presentation.home

import android.Manifest
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_IMAGES
import android.Manifest.permission.READ_MEDIA_VIDEO
import android.Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.registerForActivityResult
import androidx.fragment.app.Fragment
import com.example.walled.databinding.FragmentHomeBinding
import com.example.walled.feature.feature_feed.presentation.FeedEvent
import com.example.walled.feature.feature_feed.presentation.online.OnlineViewModel
import com.example.walled.util.Logger
import com.example.walled.util.PermissionUtil
import com.example.walled.util.PermissionUtil.showRejectionDialog
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var notificationPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var imagePermissionLauncher : ActivityResultLauncher<Array<String>>
    private val viewmodel : HomeViewModel by viewModel<HomeViewModel>()
    private val logger = Logger(TAG)

    override fun onAttach(context: Context) {
        super.onAttach(context)
        notificationPermissionLauncher = registerForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if(isGranted){
                logger.debug("Permission has been granted")
            }else{
                logger.debug("permission not granted")
                showRejectionDialog(
                    title = "Walled",
                    message = "Notification permission has not been granted.You won't be able to receive any notification from this " +
                            "app without this permission",
                    context = requireContext()
                )
            }
        }

        imagePermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){ result ->
            if(result.any{ it.value }){
                viewmodel.onEvent(FeedEvent.Fetch)
            }

        }


    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvPermissionStatus.text = PermissionUtil.isPermissionGranted(POST_NOTIFICATIONS,requireContext()).toString()

    }

    override fun onStart() {
        super.onStart()

        //TODO : Improve permission handling
        PermissionUtil.handleRequestLogic(
            activity = requireActivity(),
            context = requireContext(),
            permission = POST_NOTIFICATIONS,
            onGranted = {
                logger.debug("permission Granted called")
            },
            onRationale = {
                logger.debug("Permission Rationale called")
            },
            onPermissionInvoked = {
                logger.debug("permission invoked called")
                notificationPermissionLauncher.launch(
                    POST_NOTIFICATIONS
                )
            }
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            imagePermissionLauncher.launch(arrayOf(READ_MEDIA_IMAGES, READ_MEDIA_VISUAL_USER_SELECTED))
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            imagePermissionLauncher.launch(arrayOf(READ_MEDIA_IMAGES))
        } else {
            imagePermissionLauncher.launch(arrayOf(READ_EXTERNAL_STORAGE))
        }

    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }

    companion object {
        private const val TAG = "HomeFragment"
        private const val POST_NOTIFICATIONS = android.Manifest.permission.POST_NOTIFICATIONS
    }





}