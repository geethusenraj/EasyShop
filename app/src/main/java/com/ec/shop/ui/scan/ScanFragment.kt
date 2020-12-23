package com.ec.shop.ui.scan

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.ec.shop.R
import com.ec.shop.constants.Constants.Companion.SHOW_DIALOG
import com.ec.shop.ui.dialog.QrCodeResultDialog
import com.ec.shop.utils.ViewModelFactory
import com.ec.shop.utils.showSnackBar
import kotlinx.android.synthetic.main.fragment_scan.view.*
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions

class ScanFragment : Fragment(), EasyPermissions.PermissionCallbacks {

    private lateinit var mView: View
    private lateinit var viewModel: ScanViewModel
    private lateinit var resultDialog: QrCodeResultDialog

    companion object {
        const val RC_CAMERA = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(
            requireActivity(), ViewModelFactory(
                application = requireActivity().application
            )
        ).get(ScanViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView = inflater.inflate(R.layout.fragment_scan, container, false)
        viewModel.scannerViewItem.observe(requireActivity(), Observer {
            mView.containerScanner.addView(it)
        })
        viewModel.showQRDialog.observe(requireActivity(), Observer {
            if (it == SHOW_DIALOG) setResultDialog()
            else resultDialog.show(it.toString())
            setResultDialog()
        })
        return mView.rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.initViews()
        checkPermissionsAndStartQRScan()
    }


    private fun setResultDialog() {
        resultDialog = QrCodeResultDialog(requireContext())
        resultDialog.setOnDismissListener(object : QrCodeResultDialog.OnDismissListener {
            override fun onDismiss() {
                viewModel.resetPreview()
            }

            override fun onAdd() {
                mView.showSnackBar(getString(R.string.add_to_cart))
            }
        })
    }

    @AfterPermissionGranted(RC_CAMERA)
    private fun checkPermissionsAndStartQRScan() {
        val permission = Manifest.permission.CAMERA
        if (EasyPermissions.hasPermissions(requireContext(), permission)) {
            // Already have permission, do the thing
            viewModel.startQRCamera()
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(
                this, getString(R.string.camera_permission_explanation),
                RC_CAMERA, permission
            )
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(
            requestCode,
            permissions,
            grantResults, this
        )
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.permission_required_dialog_title))
            .setMessage(getString(R.string.permission_required_dialog_content))
            .setPositiveButton(
                android.R.string.ok
            ) { dialog, _ ->
                dialog.dismiss()
                checkPermissionsAndStartQRScan()
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {

    }

}
