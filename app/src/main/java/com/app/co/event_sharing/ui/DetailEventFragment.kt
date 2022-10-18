package com.app.co.event_sharing.ui

import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.app.co.event_sharing.MainViewModel
import br.com.sendevent.data.Status
import com.app.co.event_sharing.R
import com.app.co.event_sharing.di.AppModule
import com.app.co.event_sharing.di.KoinUtilities
import com.app.co.event_sharing.util.load
import com.app.co.event_sharing.util.launchImage
import com.app.co.event_sharing.util.share
import com.app.co.event_sharing.databinding.FragmentEventsBinding
import com.bumptech.glide.Glide
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class DetailEventFragment : Fragment() {

    private val loadModules by lazy { KoinUtilities.loadModules(AppModule.eachModules()) }
    private fun injectModules() = loadModules
    private val mainViewModel: MainViewModel by sharedViewModel()

    private var _binding: FragmentEventsBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injectModules()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentEventsBinding.inflate(layoutInflater).apply {
            lifecycleOwner = viewLifecycleOwner
        }

        initView()
        observers()
        return binding.root
    }

    private fun initView() {
        if (mainViewModel.isScreen.value == false) {
            suspend { load(binding.progress, binding.main) }
            mainViewModel.isScreen(true)
        }

        mainViewModel.flowEvent.value.let {
            it?.let { id -> mainViewModel.getEventsById(id) }
        }

        binding.exit.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.cvShare.setOnClickListener {
            val bitmap = (binding.ivImageEvent.drawable as BitmapDrawable?)?.bitmap

            bitmap?.copy(bitmap.config, true)
                ?.let { uri -> share(requireContext(), binding.tvAboutEvent.text.toString(), uri) }
        }

        binding.cvCheckin.setOnClickListener {
            mainViewModel.event.value?.id?.let { eventId ->
                mainViewModel.flowEvent(eventId = eventId)
            }

            findNavController().navigate(R.id.action_Events_to_checkinFragment)
        }
    }

    private fun observers() {
        mainViewModel.isState.observe(viewLifecycleOwner) {
            when (it.name) {
                Status.LOADING.name -> {
                    binding.progress.visibility = View.VISIBLE
                }
                Status.SUCCESS.name -> {
                    binding.progress.visibility = View.GONE
                    binding.main.visibility = View.VISIBLE
                }
                Status.FAILURE.name -> {
                    binding.progress.visibility = View.GONE
                    binding.error.visibility = View.VISIBLE
                }
            }
        }

        mainViewModel.event.observe(viewLifecycleOwner) { event ->
            Glide.with(requireContext()).load("")
                .error(R.drawable.ic_noload).into(binding.ivImageEvent)

            launchImage(event?.image, 200, requireContext()) {
                binding.ivImageEvent.animate()
                    .setStartDelay(0).alpha(1f)
                    .setDuration(400).withEndAction {
                        binding.ivImageEvent.setImageBitmap(it)
                    }
            }

            binding.tvTitleEvent.text = event?.title
            binding.tvAboutEvent.text = event?.description
            binding.tvPeople.text =
                if (event?.people?.toList()?.size == 0)
                    getString(R.string.confirm_peoples_zero) else event?.people.toString()
            binding.tvDate.text = event?.date.toString()
            binding.tvLocation.text = getString(R.string.location_lbl)

            suspend { load(binding.progress, binding.main) }

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}