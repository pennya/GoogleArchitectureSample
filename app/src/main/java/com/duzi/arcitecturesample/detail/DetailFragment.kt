package com.duzi.arcitecturesample.detail

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.duzi.arcitecturesample.DELETE_RESULT_OK
import com.duzi.arcitecturesample.EventObserver
import com.duzi.arcitecturesample.R
import com.duzi.arcitecturesample.databinding.FragmentDetailBinding
import com.duzi.arcitecturesample.util.getViewModelFactory
import com.duzi.arcitecturesample.util.setupRefreshLayout
import com.duzi.arcitecturesample.util.setupSnackbar
import com.google.android.material.snackbar.Snackbar

class DetailFragment: Fragment() {

    // 2. xml 이름을 통해 바인딩 객체 선언
    private lateinit var viewDataBinding: FragmentDetailBinding

    private val args: DetailFragmentArgs by navArgs()

    // 3. xml, activity/fragment 에서 사용될 livedata, fun 구현
    private val viewModel by viewModels<DetailViewModel> { getViewModelFactory() }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupFab()
        view?.setupSnackbar(this, viewModel.snackbarText, Snackbar.LENGTH_SHORT)
        setupNavigation()
        this.setupRefreshLayout(viewDataBinding.refreshLayout)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 4. xml inflate
        val view = inflater.inflate(R.layout.fragment_detail, container, false)

        // 5. 바인딩 객체에 inflate 한 view 바인딩
        viewDataBinding = FragmentDetailBinding.bind(view).apply {
            // 5. xml 에서 사용할 variable 값 초기
            viewmodel = viewModel
        }

        // 6. lifecycleowner 설정
        viewDataBinding.lifecycleOwner = this.viewLifecycleOwner


        viewModel.start(args.taskId)

        setHasOptionsMenu(true)
        return view
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_delete -> {
                viewModel.deleteTask()
                true
            }
            else -> false
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.taskdetail_fragment_menu, menu)
    }

    private fun setupNavigation() {
        viewModel.deleteTaskEvent.observe(this, EventObserver {
            val action = DetailFragmentDirections
                .actionDetailFragmentToTaskFragment(DELETE_RESULT_OK)
            findNavController().navigate(action)
        })
        viewModel.editTaskEvent.observe(this, EventObserver {
            val action = DetailFragmentDirections
                .actionDetailFragmentToAddEditFragment(
                    args.taskId,
                    "Edit Task"
                )
            findNavController().navigate(action)
        })
    }

    private fun setupFab() {
        activity?.findViewById<View>(R.id.edit_task_fab)?.setOnClickListener {
            viewModel.editTask()
        }
    }
}