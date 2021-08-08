package com.airqualitymonitor.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.airqualitymonitor.R
import com.airqualitymonitor.databinding.MainFragmentBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AQMListFragment : Fragment(), Adapter.ArticleItemListener {

    private var binding: MainFragmentBinding by autoCleared()
    private val viewModel: AQMViewModel by activityViewModels()
    private lateinit var adapter: Adapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = MainFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupObservers()
        (activity as AppCompatActivity?)!!.supportActionBar?.title = getString(R.string.app_name)
        (activity as AppCompatActivity?)!!.supportActionBar?.subtitle = ""
    }

    private fun setupRecyclerView() {
        adapter = Adapter(this)
        binding.charactersRv.layoutManager = LinearLayoutManager(requireContext())
        binding.charactersRv.adapter = adapter
    }


    private fun setupObservers() {
        viewModel.citiesToShow.observe(viewLifecycleOwner, Observer {
            setData(it)
        })
    }

    private fun setData(it: List<Cities>?) {
        if (it?.isNotEmpty() == true) {
            binding.progressBar.visibility = View.GONE
            binding.noData.visibility = View.GONE
            adapter.setData(it)
        } else {
            binding.noData.visibility = View.VISIBLE
            adapter.setData(ArrayList())
        }
    }

    override fun onClickedArticle(index: Int) {
        findNavController().navigate(
            R.id.action_charactersFragment_to_characterDetailFragment,
            bundleOf("index" to index)
        )
    }

}
