package com.app.co.event_sharing.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.sendevent.data.Status
import com.app.co.event_sharing.data.Event
import com.app.co.event_sharing.di.AppModule
import com.app.co.event_sharing.di.KoinUtilities
import com.app.co.event_sharing.MainViewModel
import com.app.co.event_sharing.R
import com.app.co.event_sharing.adapter.EventsAdapter
import com.app.co.event_sharing.databinding.FragmentListEventsBinding
import com.app.co.event_sharing.listener.OnclickListener
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class ListEventsFragment : Fragment() {

    private val loadModules by lazy { KoinUtilities.loadModules(AppModule.eachModules()) }
    private fun injectModules() = loadModules
    internal val mainViewModel: MainViewModel by sharedViewModel()

    private var _binding: FragmentListEventsBinding? = null
    private val binding get() = _binding!!

    private var adapter: EventsAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injectModules()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentListEventsBinding.inflate(layoutInflater).apply {
            lifecycleOwner = viewLifecycleOwner
        }

        initView()
        observers()
        return binding.root
    }

    private fun initView() {
        mainViewModel.getEvents()
    }

    private fun observers() {
        mainViewModel.events.observe(viewLifecycleOwner) {
            setAdapter(it)
        }

        mainViewModel.isState.observe(viewLifecycleOwner) {
            when (it.name) {
                Status.LOADING.name -> {
                    binding.progress.visibility = View.VISIBLE
                    binding.main.visibility = View.GONE
                }
                Status.SUCCESS.name -> {
                    binding.progress.visibility = View.GONE
                    binding.main.visibility = View.VISIBLE
                }
                Status.FAILURE.name -> binding.progress.visibility = View.GONE
            }
        }
    }

    private fun setAdapter(body: Array<Event?>?) {
        adapter = EventsAdapter(
            requireContext(),
            body?.toMutableList()
        )

        val manager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL, false
        )

        binding.rvEvents.layoutManager = manager
        binding.rvEvents.adapter = adapter

        adapter?.onClickItem(object : OnclickListener {
            override fun flowId(id: String) {
                if (id.isNotBlank()) {
                    mainViewModel.flowEvent(eventId = id)
                    findNavController().navigate(R.id.action_Home_to_Events)
                }
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}