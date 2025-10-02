package com.example.walled.feature.feature_media_detail.presentation.detail

import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
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
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue


class MediaDetailFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var _binding : FragmentMediaDetailBinding? = null
    private val binding get() = _binding!!
    private val args : MediaDetailFragmentArgs by navArgs()
    private lateinit var mediaObserver : Observer<Media>
    private lateinit var loadingObserver : Observer<Boolean>
    private val viewmodel : MediaDetailViewModel by viewModel<MediaDetailViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val callback = requireActivity().onBackPressedDispatcher.addCallback(this){
            binding.root.findNavController().popBackStack()
        }
        viewmodel.onEvent(MediaDetailEvent.FetchMedia(args.mediaId))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMediaDetailBinding.inflate(inflater,container,false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mediaObserver = Observer<Media>{ it ->
            Glide.with(requireContext()).load(it.urls.regular)
                .listener(object : RequestListener<Drawable>{
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: com.bumptech.glide.request.target.Target<Drawable?>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        //
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable?>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        binding.pbLoading.pbBaseLoader.visibility = View.GONE
//                        binding.clMediaDetailTopContainer.background = resource
                        return false
                    }

                })
                .into(binding.ivMedia)

        }
        loadingObserver = Observer<Boolean>{
            if(it){
                binding.pbLoading.pbBaseLoader.visibility = View.VISIBLE
            }
        }
        viewmodel.media.observe(viewLifecycleOwner,mediaObserver)
        viewmodel.isLoading.observe(viewLifecycleOwner,loadingObserver)

    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MediaDetailFragment().apply {

            }
    }
}