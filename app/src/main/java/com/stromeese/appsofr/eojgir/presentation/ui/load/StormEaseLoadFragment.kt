package com.stromeese.appsofr.eojgir.presentation.ui.load

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.stromeese.appsofr.MainActivity
import com.stromeese.appsofr.R
import com.stromeese.appsofr.databinding.FragmentLoadStormEaseBinding
import com.stromeese.appsofr.eojgir.data.shar.StormEaseSharedPreference
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class StormEaseLoadFragment : Fragment(R.layout.fragment_load_storm_ease) {
    private lateinit var stormEaseLoadBinding: FragmentLoadStormEaseBinding

    private val stormEaseLoadViewModel by viewModel<StormEaseLoadViewModel>()

    private val stormEaseSharedPreference by inject<StormEaseSharedPreference>()

    private var stormEaseUrl = ""

    private val stormEaseRequestNotificationPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            stormEaseNavigateToSuccess(stormEaseUrl)
        } else {
            if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                stormEaseSharedPreference.stormEaseNotificationRequest =
                    (System.currentTimeMillis() / 1000) + 259200
                stormEaseNavigateToSuccess(stormEaseUrl)
            } else {
                stormEaseNavigateToSuccess(stormEaseUrl)
            }
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        stormEaseLoadBinding = FragmentLoadStormEaseBinding.bind(view)

        stormEaseLoadBinding.stormEaseGrandButton.setOnClickListener {
            val stormEasePermission = Manifest.permission.POST_NOTIFICATIONS
            stormEaseRequestNotificationPermission.launch(stormEasePermission)
            stormEaseSharedPreference.stormEaseNotificationRequestedBefore = true
        }

        stormEaseLoadBinding.stormEaseSkipButton.setOnClickListener {
            stormEaseSharedPreference.stormEaseNotificationRequest =
                (System.currentTimeMillis() / 1000) + 259200
            stormEaseNavigateToSuccess(stormEaseUrl)
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                stormEaseLoadViewModel.stormEaseHomeScreenState.collect {
                    when (it) {
                        is StormEaseLoadViewModel.StormEaseHomeScreenState.StormEaseLoading -> {

                        }

                        is StormEaseLoadViewModel.StormEaseHomeScreenState.StormEaseError -> {
                            requireActivity().startActivity(
                                Intent(
                                    requireContext(),
                                    MainActivity::class.java
                                )
                            )
                            requireActivity().finish()
                        }

                        is StormEaseLoadViewModel.StormEaseHomeScreenState.StormEaseSuccess -> {
                            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.S_V2) {
                                val stormEasePermission = Manifest.permission.POST_NOTIFICATIONS
                                val stormEasePermissionRequestedBefore = stormEaseSharedPreference.stormEaseNotificationRequestedBefore

                                if (ContextCompat.checkSelfPermission(requireContext(), stormEasePermission) == PackageManager.PERMISSION_GRANTED) {
                                    stormEaseNavigateToSuccess(it.data)
                                } else if (!stormEasePermissionRequestedBefore && (System.currentTimeMillis() / 1000 > stormEaseSharedPreference.stormEaseNotificationRequest)) {
                                    // первый раз — показываем UI для запроса
                                    stormEaseLoadBinding.stormEaseNotiGroup.visibility = View.VISIBLE
                                    stormEaseLoadBinding.stormEaseLoadingGroup.visibility = View.GONE
                                    stormEaseUrl = it.data
                                } else if (shouldShowRequestPermissionRationale(stormEasePermission)) {
                                    // временный отказ — через 3 дня можно показать
                                    if (System.currentTimeMillis() / 1000 > stormEaseSharedPreference.stormEaseNotificationRequest) {
                                        stormEaseLoadBinding.stormEaseNotiGroup.visibility = View.VISIBLE
                                        stormEaseLoadBinding.stormEaseLoadingGroup.visibility = View.GONE
                                        stormEaseUrl = it.data
                                    } else {
                                        stormEaseNavigateToSuccess(it.data)
                                    }
                                } else {
                                    // навсегда отклонено — просто пропускаем
                                    stormEaseNavigateToSuccess(it.data)
                                }
                            } else {
                                stormEaseNavigateToSuccess(it.data)
                            }
                        }

                        StormEaseLoadViewModel.StormEaseHomeScreenState.StormEaseNotInternet -> {
                            stormEaseLoadBinding.stormEaseStateGroup.visibility = View.VISIBLE
                            stormEaseLoadBinding.stormEaseLoadingGroup.visibility = View.GONE
                        }
                    }
                }
            }
        }
    }


    private fun stormEaseNavigateToSuccess(data: String) {
        findNavController().navigate(
            R.id.action_stormEaseLoadFragment_to_stormEaseV,
            bundleOf(STORM_EASE_D to data)
        )
    }

    companion object {
        const val STORM_EASE_D = "stormEaseData"
    }
}