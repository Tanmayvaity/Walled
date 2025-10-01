package com.example.walled.feature.feature_feed.presentation.online

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.observe
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.walled.databinding.FragmentOnlineBinding
import com.example.walled.feature.feature_feed.domain.model.Media
import com.example.walled.feature.feature_feed.presentation.adapter.ImagesAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.example.walled.R

class OnlineFragment : Fragment() {

    private var _binding : FragmentOnlineBinding? = null
    private val binding get() = _binding!!
    private val viewmodel : OnlineViewModel by viewModel<OnlineViewModel>()
    private lateinit var imageListObserver: Observer<List<Media>>
    private lateinit var loadingIndicatorObserver : Observer<Boolean>
    private lateinit var imagesAdapter: ImagesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentOnlineBinding.inflate(inflater,container,false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        imageListObserver = Observer<List<Media>>{ list->
            Log.d(TAG, "data returned")
            imagesAdapter = ImagesAdapter(requireContext(),list)
            binding.rvImages.adapter = imagesAdapter
        }
        loadingIndicatorObserver = Observer<Boolean>{ it ->
            if(it){
                binding.pbLoadingIndicator.visibility = View.VISIBLE
            }else{
                binding.pbLoadingIndicator.visibility = View.GONE
            }
        }

        binding.rvImages.layoutManager = LinearLayoutManager(requireContext())
        viewmodel.imageList.observe(viewLifecycleOwner, imageListObserver)
        viewmodel.isLoading.observe(viewLifecycleOwner,loadingIndicatorObserver)

    }

    override fun onDestroy() {
        viewmodel.imageList.removeObserver(imageListObserver)
        _binding = null
        super.onDestroy()

    }


    companion object Companion {
        @JvmStatic
        fun newInstance(param1: String, param2: String) = OnlineFragment()
        private const val TAG = "OnlineFragment"
    }
}