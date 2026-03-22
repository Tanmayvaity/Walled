package com.example.walled.feature.feature_feed.presentation.home

import android.Manifest.permission.POST_NOTIFICATIONS
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_IMAGES
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
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.walled.core.domain.model.Image
import com.example.walled.databinding.FragmentHomeBinding
import com.example.walled.feature.feature_feed.presentation.FeedEvent
import com.example.walled.feature.feature_feed.presentation.adapter.LocalImagesAdapter
import com.example.walled.util.Logger
import com.example.walled.util.PermissionUtil
import com.example.walled.util.PermissionUtil.showRejectionDialog
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var notificationPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var imagePermissionLauncher: ActivityResultLauncher<Array<String>>
    private val viewmodel: HomeViewModel by viewModel<HomeViewModel>()
    private lateinit var localImagesAdapter: LocalImagesAdapter
    private val logger = Logger(TAG)

    override fun onAttach(context: Context) {
        super.onAttach(context)
        notificationPermissionLauncher = registerForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                logger.debug("Permission has been granted")
            } else {
                logger.debug("permission not granted")
                showRejectionDialog(
                    title = "Walled",
                    message = "Notification permission has not been granted.You won't be able to receive any notification from this " +
                            "app without this permission",
                    context = requireContext()
                )
            }
        }

        imagePermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
            if (result.any { it.value }) {
                viewmodel.onEvent(FeedEvent.Fetch)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        localImagesAdapter = LocalImagesAdapter(
            context = requireContext(),
            imageList = emptyList(),
            listener = object : LocalImagesAdapter.OnItemClickListener {
                override fun onItemClick(image: Image) {
                    val action = HomeFragmentDirections.actionHomeFragmentToMediaDetailFragment(
                        mediaId = "",
                        localImageUri = image.uri.toString()
                    )
                    binding.root.findNavController().navigate(action)
                }
            }
        )
        binding.rvLocalImages.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = localImagesAdapter
        }
    }

    private fun observeViewModel() {
        viewmodel.localImageList.observe(viewLifecycleOwner) { images ->
            localImagesAdapter.updateList(images)
            binding.tvEmpty.visibility = if (images.isEmpty() && viewmodel.isLoading.value != true) View.VISIBLE else View.GONE
        }

        viewmodel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.pbLoadingIndicator.pbBaseLoader.visibility = if (isLoading) View.VISIBLE else View.GONE
            if (!isLoading) {
                val isEmpty = viewmodel.localImageList.value.isNullOrEmpty()
                val hasError = viewmodel.error.value != null
                binding.tvEmpty.visibility = if (isEmpty && !hasError) View.VISIBLE else View.GONE
            }
        }

        viewmodel.error.observe(viewLifecycleOwner) { errorMsg ->
            if (errorMsg != null) {
                binding.tvError.text = errorMsg
                binding.tvError.visibility = View.VISIBLE
                binding.tvEmpty.visibility = View.GONE
            } else {
                binding.tvError.visibility = View.GONE
            }
        }
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
                notificationPermissionLauncher.launch(POST_NOTIFICATIONS)
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
    }
}
