package com.duzi.arcitecturesample

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.duzi.arcitecturesample.databinding.FragmentMainBinding
import com.duzi.arcitecturesample.util.getViewModelFactory
import kotlinx.android.synthetic.main.fragment_main.*

class MainFragment : Fragment() {

    private val viewModel by viewModels<MainViewModel> { getViewModelFactory() }
    private lateinit var viewDataBinding: FragmentMainBinding
    private lateinit var listAdapter: MainListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewDataBinding = FragmentMainBinding.inflate(inflater, container, false)
            .apply {
                viewmodel = viewModel
            }
        return viewDataBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewDataBinding.lifecycleOwner = this.viewLifecycleOwner

        setupListAdapter()
        setupObserver()
        setupFab()

        viewModel.loadTasks(true)
    }

    private fun setupObserver() {
        viewModel.openTaskEvent.observe(this, EventObserver {
            openTaskDetails(it)
        })
    }

    private fun setupListAdapter() {
        val viewModel = viewDataBinding.viewmodel
        if (viewModel != null) {
            listAdapter = MainListAdapter(viewModel)
            viewDataBinding.tasksList.adapter = listAdapter
        }
    }

    private fun setupFab() {
        add_task_fab?.let {
            it.setOnClickListener {
                navigateToAddNewTask()
            }
        }
    }

    private fun navigateToAddNewTask() {
        val action = MainFragmentDirections
            .actionMainFragmentToAddEditFragment(null, "New Task")

        findNavController().navigate(action)
    }

    private fun openTaskDetails(taskId: String) {
        val action = MainFragmentDirections
            .actionMainFragmentToDetailFragment(taskId)

        findNavController().navigate(action)
    }
}