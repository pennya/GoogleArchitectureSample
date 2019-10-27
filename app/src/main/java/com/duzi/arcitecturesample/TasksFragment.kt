package com.duzi.arcitecturesample

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.duzi.arcitecturesample.data.TasksFilterType
import com.duzi.arcitecturesample.databinding.FragmentTasksBinding
import com.duzi.arcitecturesample.util.getViewModelFactory
import kotlinx.android.synthetic.main.fragment_tasks.*

class TasksFragment : Fragment() {

    private val viewModel by viewModels<TasksViewModel> { getViewModelFactory() }
    private lateinit var viewDataBinding: FragmentTasksBinding
    private lateinit var listAdapter: MainListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewDataBinding = FragmentTasksBinding.inflate(inflater, container, false)
            .apply {
                viewmodel = viewModel
            }
        setHasOptionsMenu(true)
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.menu_clear -> {
                viewModel.clearCompletedTasks()
                true
            }
            R.id.menu_filter -> {
                showFilteringPopUpMenu()
                true
            }
            R.id.menu_refresh -> {
                viewModel.loadTasks(true)
                true
            }
            else -> false
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.tasks_fragment_menu, menu)
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
        val action = TasksFragmentDirections
            .actionMainFragmentToAddEditFragment(null, "New Task")

        findNavController().navigate(action)
    }

    private fun openTaskDetails(taskId: String) {
        val action = TasksFragmentDirections
            .actionMainFragmentToDetailFragment(taskId)

        findNavController().navigate(action)
    }

    private fun showFilteringPopUpMenu() {
        val view = activity?.findViewById<View>(R.id.menu_filter)
            ?: return

        PopupMenu(requireContext(), view).run {
            menuInflater.inflate(R.menu.filter_tasks, menu)

            setOnMenuItemClickListener {
                viewModel.setFiltering(
                    when(it.itemId) {
                        R.id.active -> TasksFilterType.ACTIVE_TASKS
                        R.id.completed -> TasksFilterType.COMPLETED_TASKS
                        else -> TasksFilterType.ALL_TASKS
                    }
                )
                viewModel.loadTasks(true)
                true
            }
            show()
        }

    }
}