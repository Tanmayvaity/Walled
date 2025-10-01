package com.example.walled.feature.feature_feed.presentation.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.walled.databinding.FragmentHomeBinding
import com.example.walled.util.Logger
import com.example.walled.util.PermissionUtil
import com.example.walled.util.PermissionUtil.showRejectionDialog

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var notificationPermissionLauncher: ActivityResultLauncher<String>
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