package com.app.co.event_sharing.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.app.co.event_sharing.MainViewModel
import com.app.co.event_sharing.data.IAnimate
import com.app.co.event_sharing.data.Person
import com.app.co.event_sharing.data.PostStatus
import br.com.sendevent.data.Status
import com.app.co.event_sharing.di.AppModule
import com.app.co.event_sharing.di.KoinUtilities
import com.app.co.event_sharing.dialog.DialogError
import com.app.co.event_sharing.util.addChangedListener
import com.app.co.event_sharing.util.clearAll
import com.app.co.event_sharing.databinding.FragmentCheckinBinding
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class CheckInFragment : Fragment() {

    private val loadModules by lazy { KoinUtilities.loadModules(AppModule.eachModules()) }
    private fun injectModules() = loadModules
    private val viewModel: MainViewModel by sharedViewModel()

    private lateinit var binding: FragmentCheckinBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            viewModel.setIsPostExecuted()
            findNavController().popBackStack()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injectModules()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentCheckinBinding.inflate(layoutInflater).apply {
            lifecycleOwner = viewLifecycleOwner
        }

        initView()
        observer()
        return binding.root
    }

    private fun initView() {
        binding.exit.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.edtName.addChangedListener(
            onTextChanged = { s, _, _, _ ->
                if (s.isNotBlank()) {
                    viewModel.isSubmit()
                } else {
                    viewModel.isNoSubmit()
                }
            },
        )

        binding.edtEmail.addChangedListener(
            onTextChanged = { s, _, _, _ ->
                if (s.isNotBlank()) {
                    viewModel.isSubmit()
                } else {
                    viewModel.isNoSubmit()
                }
            },
        )

        binding.cvSubmit.setOnClickListener {
            if (binding.edtName.text.toString().isNotEmpty()
                && binding.edtEmail.text.toString().isNotEmpty()
            ) {

                binding.edtName.clearAll(false)
                binding.edtEmail.clearAll(false)

                viewModel.setCheckIn(
                    Person(
                        name = binding.edtName.text.toString(),
                        email = binding.edtEmail.text.toString(),
                        eventId = viewModel.event.value?.id.toString()
                    )
                )
            }
        }
    }

    private fun observer() {
        viewModel.isSubmit.observe(viewLifecycleOwner) {
            binding.cvSubmit.isClickable = it
            binding.cvSubmit.isFocusable = it
        }

        viewModel.isState.observe(viewLifecycleOwner) {
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

        viewModel.isStatusCheckIn.observe(viewLifecycleOwner) {
            when (it.name) {
                PostStatus.LOADING.name -> {
                    binding.progress.visibility = View.VISIBLE
                    binding.tvBtnCheckIn.visibility = View.GONE
                }
                PostStatus.SUCCESS.name -> {
                    binding.progress.visibility = View.GONE
                    binding.tvBtnCheckIn.visibility = View.VISIBLE
                }
                PostStatus.FAILURE.name -> {
                    binding.progress.visibility = View.GONE
                    binding.tvBtnCheckIn.visibility = View.VISIBLE
                    openDialogError()
                }
            }
        }

        viewModel.isChecking.observe(viewLifecycleOwner) {
            when (it) {
                true -> {
                    binding.tvBtnCheckIn.visibility = View.VISIBLE
                    binding.edtName.clearAll(true)
                    binding.edtEmail.clearAll(true)

                }
                false -> {
                    binding.viewResponse.visibility = View.VISIBLE
                    animToEnd(
                        IAnimate(
                            binding.viewResponse,
                            400, 4000,
                            done = null
                        )
                    )
                }
                else -> {
                    animToEnd(
                        IAnimate(
                            binding.error,
                            2000, 100,
                            done = true
                        )
                    )
                }
            }
        }
    }

    private fun animToEnd(iAnimate: IAnimate) {
        iAnimate.bind.animate().alpha(1F)
            .setDuration(iAnimate.time)
            .setStartDelay(iAnimate.delay)
            .withEndAction {
                iAnimate.bind.visibility = View.GONE
                viewModel.doneChecking(iAnimate.done)
            }
    }

    private fun openDialogError() {
        val dialogError = DialogError(requireContext())
        dialogError.show()
    }
}