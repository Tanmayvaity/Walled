package com.example.walled.feature.feature_media_detail.presentation.detail

import android.Manifest.permission.POST_NOTIFICATIONS
import android.app.WallpaperManager
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.navigation.NavDirections
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.walled.R
import com.example.walled.core.domain.model.Media
import com.example.walled.databinding.FragmentMediaDetailBinding
import com.example.walled.databinding.FragmentOnlineBinding
import com.example.walled.util.Logger
import com.example.walled.util.PermissionUtil
import com.example.walled.util.PermissionUtil.showRejectionDialog
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue
import androidx.core.net.toUri


class MediaDetailFragment : Fragment() {
    private var _binding: FragmentMediaDetailBinding? = null
    private val binding get() = _binding!!
    private val args: MediaDetailFragmentArgs by navArgs()
    private lateinit var mediaObserver: Observer<Media>
    private lateinit var loadingObserver: Observer<Boolean>
    private lateinit var cacheImageUriObserver: Observer<Uri?>
    private val viewmodel: MediaDetailViewModel by viewModel<MediaDetailViewModel>()
    private var photoUrl: String = ""
    private val isLocalImage: Boolean by lazy { args.localImageUri.isNotEmpty() }

    private lateinit var notificationPermissionLauncher: ActivityResultLauncher<String>

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
                    message = "Notification permission has not been granted.You won't be able to view any download progress or stop it " +
                            "without this permission",
                    context = requireContext()
                )
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val callback = requireActivity().onBackPressedDispatcher.addCallback(this) {
            binding.root.findNavController().popBackStack()
        }
        if (!isLocalImage) {
            viewmodel.onEvent(MediaDetailEvent.FetchMedia(args.mediaId))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMediaDetailBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (isLocalImage) {
            setupLocalImageMode()
        } else {
            setupRemoteImageMode()
        }
    }

    private fun setupLocalImageMode() {
        val localUri = args.localImageUri.toUri()
        binding.btnDownload.visibility = View.GONE
        binding.pbLoading.pbBaseLoader.visibility = View.GONE

        Glide.with(requireContext())
            .load(localUri)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable?>?,
                    isFirstResource: Boolean
                ): Boolean = false

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable?>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean = false
            })
            .into(binding.ivMedia)

        binding.btnApplyWallpaper.setOnClickListener {
            try {
                val intent = Intent(WallpaperManager.ACTION_CROP_AND_SET_WALLPAPER).apply {
                    setDataAndType(localUri, "image/*")
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                val chooser = Intent.createChooser(
                    intent,
                    getString(R.string.app_name)
                ).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                requireContext().startActivity(chooser)
            } catch (e: Exception) {
                logger.error(e.message.toString())
                viewmodel.onEvent(MediaDetailEvent.SetWallpaper(localUri))
            }
        }
    }

    private fun setupRemoteImageMode() {
        mediaObserver = Observer<Media> { it ->
            photoUrl = it.urls.full
            Glide.with(requireContext()).load(it.urls.regular)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable?>?,
                        isFirstResource: Boolean
                    ): Boolean = false

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable?>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        binding.pbLoading.pbBaseLoader.visibility = View.GONE
                        return false
                    }
                })
                .into(binding.ivMedia)
        }
        loadingObserver = Observer<Boolean> {
            if (it) {
                binding.pbLoading.pbBaseLoader.visibility = View.VISIBLE
            }
        }

        cacheImageUriObserver = Observer<Uri?> { uri ->
            try {
                if (uri != null) {
                    val intent = Intent(WallpaperManager.ACTION_CROP_AND_SET_WALLPAPER).apply {
                        setDataAndType(uri, "image/*")
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                    val chooser = Intent.createChooser(
                        intent,
                        getString(R.string.app_name)
                    ).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    requireContext().startActivity(chooser)
                }
            } catch (e: Exception) {
                logger.error(e.message.toString())
                e.printStackTrace()
                if (uri != null) {
                    viewmodel.onEvent(MediaDetailEvent.SetWallpaper(uri))
                } else {
                    Toast.makeText(requireContext(), "Please try again", Toast.LENGTH_SHORT).show()
                    logger.error("Uri is null")
                }
            }
        }

        viewmodel.media.observe(viewLifecycleOwner, mediaObserver)
        viewmodel.isLoading.observe(viewLifecycleOwner, loadingObserver)
        viewmodel.tempImageUri.observe(viewLifecycleOwner, cacheImageUriObserver)

        binding.btnDownload.setOnClickListener {
            logger.debug("media download button clicked")

            PermissionUtil.handleRequestLogic(
                activity = requireActivity(),
                context = requireContext(),
                permission = POST_NOTIFICATIONS,
                onGranted = {
                    logger.debug("permission Granted called")
                    if (!photoUrl.isNotEmpty() || !photoUrl.isNotBlank()) return@handleRequestLogic
                    logger.info("photo url ${photoUrl}")
                    viewmodel.onEvent(MediaDetailEvent.DownloadMedia(args.mediaId, photoUrl))
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

        binding.btnApplyWallpaper.setOnClickListener {
            viewmodel.media.value?.let { it ->
                viewmodel.onEvent(MediaDetailEvent.DownloadToInternalCache(it.urls.full))
            }
        }
    }

    companion object {
        private val logger = Logger("MediaDetailFragment")

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MediaDetailFragment()
    }
}