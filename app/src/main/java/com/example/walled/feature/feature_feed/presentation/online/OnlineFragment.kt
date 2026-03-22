package com.example.walled.feature.feature_feed.presentation.online

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.walled.databinding.FragmentOnlineBinding
import com.example.walled.core.domain.model.Media
import com.example.walled.feature.feature_feed.presentation.adapter.ImagesAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel
import androidx.core.net.toUri
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.walled.R
import com.example.walled.core.domain.model.Image
import com.example.walled.feature.feature_feed.presentation.adapter.LocalImagesAdapter

class OnlineFragment : Fragment() {

    private var _binding: FragmentOnlineBinding? = null
    private val binding get() = _binding!!
    private val viewmodel: OnlineViewModel by viewModel<OnlineViewModel>()
//    private lateinit var imageListObserver: Observer<List<Media>>
    private lateinit var loadingIndicatorObserver: Observer<Boolean>

    //    private lateinit var errorObserver : Observer<String>
    private lateinit var imagesAdapter: ImagesAdapter
    private var loading: Boolean = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentOnlineBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeViewModel()
    }


    private fun setupRecyclerView() {
        imagesAdapter = ImagesAdapter(
            context = requireContext(),
            imageList = emptyList(),
            listener = object : ImagesAdapter.OnItemClickListener {
                override fun onUserNameClick(url: String) {
                    Intent(Intent.ACTION_VIEW).apply {
                        val packageManager = requireContext().packageManager
                        data = url.toString().toUri()
                        if (resolveActivity(packageManager) != null) {
                            startActivity(this)
                        }

                    }
                }

                override fun onItemClick(id: String) {
                    val action =
                        OnlineFragmentDirections.actionOnlineFragmentToMediaDetailFragment(id)
                    binding.root.findNavController().navigate(action)
                }

            }
        )
        binding.rvImages.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = imagesAdapter
        }

    }

    private fun observeViewModel() {
        viewmodel.imageList.observe(viewLifecycleOwner) { images ->
            imagesAdapter.updateList(images)
//            binding.tvEmpty.visibility = if (images.isEmpty() && viewmodel.isLoading.value != true) View.VISIBLE else View.GONE
        }

        viewmodel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.pbLoadingIndicator.pbBaseLoader.visibility = if (isLoading) View.VISIBLE else View.GONE
//            if (!isLoading) {
//                val isEmpty = viewmodel.localImageList.value.isNullOrEmpty()
//                val hasError = viewmodel.error.value != null
//                binding.tvEmpty.visibility = if (isEmpty && !hasError) View.VISIBLE else View.GONE
//            }
        }

        viewmodel.error.observe(viewLifecycleOwner) { errorMsg ->
//            if (errorMsg != null) {
//                binding.tvError.text = errorMsg
//                binding.tvError.visibility = View.VISIBLE
//                binding.tvEmpty.visibility = View.GONE
//            } else {
//                binding.tvError.visibility = View.GONE
//            }
        }
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()

    }


    companion object Companion {
        @JvmStatic
        fun newInstance(param1: String, param2: String) = OnlineFragment()
        private const val TAG = "OnlineFragment"
    }
}