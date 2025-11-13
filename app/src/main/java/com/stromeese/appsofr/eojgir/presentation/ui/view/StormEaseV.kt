package com.stromeese.appsofr.eojgir.presentation.ui.view

import android.content.DialogInterface
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.ValueCallback
import android.widget.FrameLayout
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.stromeese.appsofr.eojgir.presentation.app.StormEaseApplication
import com.stromeese.appsofr.eojgir.presentation.ui.load.StormEaseLoadFragment
import org.koin.android.ext.android.inject

class StormEaseV : Fragment(){

    private lateinit var stormEasePhoto: Uri
    private var stormEaseFilePathFromChrome: ValueCallback<Array<Uri>>? = null

    private val stormEaseTakeFile: ActivityResultLauncher<PickVisualMediaRequest> = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) {
        stormEaseFilePathFromChrome?.onReceiveValue(arrayOf(it ?: Uri.EMPTY))
        stormEaseFilePathFromChrome = null
    }

    private val stormEaseTakePhoto: ActivityResultLauncher<Uri> = registerForActivityResult(ActivityResultContracts.TakePicture()) {
        if (it) {
            stormEaseFilePathFromChrome?.onReceiveValue(arrayOf(stormEasePhoto))
            stormEaseFilePathFromChrome = null
        } else {
            stormEaseFilePathFromChrome?.onReceiveValue(null)
            stormEaseFilePathFromChrome = null
        }
    }

    private val stormEaseDataStore by activityViewModels<StormEaseDataStore>()


    private val stormEaseViFun by inject<StormEaseViFun>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(StormEaseApplication.STORM_EASE_MAIN_TAG, "Fragment onCreate")
        CookieManager.getInstance().setAcceptCookie(true)
        requireActivity().onBackPressedDispatcher.addCallback(this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (stormEaseDataStore.stormEaseView.canGoBack()) {
                        stormEaseDataStore.stormEaseView.goBack()
                        Log.d(StormEaseApplication.STORM_EASE_MAIN_TAG, "WebView can go back")
                    } else if (stormEaseDataStore.stormEaseViList.size > 1) {
                        Log.d(StormEaseApplication.STORM_EASE_MAIN_TAG, "WebView can`t go back")
                        stormEaseDataStore.stormEaseViList.removeAt(stormEaseDataStore.stormEaseViList.lastIndex)
                        Log.d(StormEaseApplication.STORM_EASE_MAIN_TAG, "WebView list size ${stormEaseDataStore.stormEaseViList.size}")
                        stormEaseDataStore.stormEaseView.destroy()
                        val previousWebView = stormEaseDataStore.stormEaseViList.last()
                        stormEaseAttachWebViewToContainer(previousWebView)
                        stormEaseDataStore.stormEaseView = previousWebView
                    }
                }

            })
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (stormEaseDataStore.stormEaseIsFirstCreate) {
            stormEaseDataStore.stormEaseIsFirstCreate = false
            stormEaseDataStore.stormEaseContainerView = FrameLayout(requireContext()).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                id = View.generateViewId()
            }
            return stormEaseDataStore.stormEaseContainerView
        } else {
            return stormEaseDataStore.stormEaseContainerView
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d(StormEaseApplication.STORM_EASE_MAIN_TAG, "onViewCreated")
        if (stormEaseDataStore.stormEaseViList.isEmpty()) {
            stormEaseDataStore.stormEaseView = StormEaseVi(requireContext(), object :
                StormEaseCallBack {
                override fun stormEaseHandleCreateWebWindowRequest(stormEaseVi: StormEaseVi) {
                    stormEaseDataStore.stormEaseViList.add(stormEaseVi)
                    Log.d(StormEaseApplication.STORM_EASE_MAIN_TAG, "WebView list size = ${stormEaseDataStore.stormEaseViList.size}")
                    Log.d(StormEaseApplication.STORM_EASE_MAIN_TAG, "CreateWebWindowRequest")
                    stormEaseDataStore.stormEaseView = stormEaseVi
                    stormEaseVi.stormEaseSetFileChooserHandler { callback ->
                        stormEaseHandleFileChooser(callback)
                    }
                    stormEaseAttachWebViewToContainer(stormEaseVi)
                }

            }, stormEaseWindow = requireActivity().window).apply {
                stormEaseSetFileChooserHandler { callback ->
                    stormEaseHandleFileChooser(callback)
                }
            }
            stormEaseDataStore.stormEaseView.stormEaseFLoad(arguments?.getString(
                StormEaseLoadFragment.STORM_EASE_D) ?: "")
//            ejvview.fLoad("www.google.com")
            stormEaseDataStore.stormEaseViList.add(stormEaseDataStore.stormEaseView)
            stormEaseAttachWebViewToContainer(stormEaseDataStore.stormEaseView)
        } else {
            stormEaseDataStore.stormEaseViList.forEach { webView ->
                webView.stormEaseSetFileChooserHandler { callback ->
                    stormEaseHandleFileChooser(callback)
                }
            }
            stormEaseDataStore.stormEaseView = stormEaseDataStore.stormEaseViList.last()

            stormEaseAttachWebViewToContainer(stormEaseDataStore.stormEaseView)
        }
        Log.d(StormEaseApplication.STORM_EASE_MAIN_TAG, "WebView list size = ${stormEaseDataStore.stormEaseViList.size}")
    }

    private fun stormEaseHandleFileChooser(callback: ValueCallback<Array<Uri>>?) {
        Log.d(StormEaseApplication.STORM_EASE_MAIN_TAG, "handleFileChooser called, callback: ${callback != null}")

        stormEaseFilePathFromChrome = callback

        val listItems: Array<out String> = arrayOf("Select from file", "To make a photo")
        val listener = DialogInterface.OnClickListener { _, which ->
            when (which) {
                0 -> {
                    Log.d(StormEaseApplication.STORM_EASE_MAIN_TAG, "Launching file picker")
                    stormEaseTakeFile.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }
                1 -> {
                    Log.d(StormEaseApplication.STORM_EASE_MAIN_TAG, "Launching camera")
                    stormEasePhoto = stormEaseViFun.stormEaseSavePhoto()
                    stormEaseTakePhoto.launch(stormEasePhoto)
                }
            }
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Choose a method")
            .setItems(listItems, listener)
            .setCancelable(true)
            .setOnCancelListener {
                Log.d(StormEaseApplication.STORM_EASE_MAIN_TAG, "File chooser canceled")
                callback?.onReceiveValue(null)
                stormEaseFilePathFromChrome = null
            }
            .create()
            .show()
    }

    private fun stormEaseAttachWebViewToContainer(w: StormEaseVi) {
        stormEaseDataStore.stormEaseContainerView.post {
            (w.parent as? ViewGroup)?.removeView(w)
            stormEaseDataStore.stormEaseContainerView.removeAllViews()
            stormEaseDataStore.stormEaseContainerView.addView(w)
        }
    }


}