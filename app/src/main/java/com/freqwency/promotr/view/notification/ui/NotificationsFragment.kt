package com.freqwency.promotr.view.notification.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.freqwency.promotr.R
import com.freqwency.promotr.application.PromotrApp
import com.freqwency.promotr.databinding.FragmentNotificationsBinding
import com.freqwency.promotr.utils.ErrorUtil
import com.freqwency.promotr.view.logins.ui.LoginActivity
import com.freqwency.promotr.view.notification.adapter.NotificationsAdapter
import com.freqwency.promotr.view.notification.viewmodel.deletenotification.DeleteNotificationBody
import com.freqwency.promotr.view.notification.viewmodel.deletenotification.DeleteNotificationtViewModel
import com.freqwency.promotr.view.notification.viewmodel.notificationlist.NotificationListViewModel
import com.freqwency.promotr.view.notification.viewmodel.updatenotification.UpdateNotificationBody
import com.freqwency.promotr.view.notification.viewmodel.updatenotification.UpdateNotificationtViewModel
import com.johncodeos.customprogressdialogexample.CustomProgressDialog
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class NotificationsFragment : Fragment() {

    val notificationListViewModel: NotificationListViewModel by viewModels()
    val deleteNotificationtViewModel: DeleteNotificationtViewModel by viewModels()
    val updateNotificationtViewModel: UpdateNotificationtViewModel by viewModels()
    private lateinit var activity: Activity
    var notificationsAdapter: NotificationsAdapter? = null
    private var _binding: FragmentNotificationsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    var count = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity = requireActivity()

        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val progressDialog by lazy { CustomProgressDialog(activity) }

        val rclNames = binding.rcvNotifications
        rclNames.setHasFixedSize(true)

        val token = PromotrApp.encryptedPrefs.bearerToken
        if (token.equals("")) {
            binding.loginLayout.visibility = View.VISIBLE
            binding.rcvNotifications.visibility = View.GONE

        } else {
            binding.loginLayout.visibility = View.GONE
            binding.rcvNotifications.visibility = View.VISIBLE
        }

        binding.signInBtn.setOnClickListener {
           // PromotrApp.encryptedPrefs.isFirstTime = true
            val intent = Intent(activity, LoginActivity::class.java)
            activity.startActivity(intent)
            //activity.finish()
        }

        binding.deleteBtn.setOnClickListener { view: View? ->
            var list = ArrayList<Int>()
            for (i in 0 until notificationsAdapter?.getSelected()!!.size) {
                list.add(notificationsAdapter!!.getSelected()!!.get(i).id)
            }

            if (list.size > 0) {
                getDeleteNotification(list, progressDialog)
            } else {
                showToast(getString(R.string.select_at_least_notification))
            }
        }

        if (!token.equals("")) {
            getNotificationList(progressDialog)
        }

        notificationListObserver()
        deleteNotificationObserver()
        updateNotificationObserver()
        return binding.root
    }

    private fun showToast(ClothsId: String) {
        Toast.makeText(activity, ClothsId, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * Notification list show
     */
    fun getNotificationList(progressDialog: CustomProgressDialog) {
        notificationListViewModel.getNotificationList(progressDialog, activity)
    }

    private fun notificationListObserver() {
        notificationListViewModel.progressIndicator.observe(viewLifecycleOwner, Observer {
        })
        notificationListViewModel.mNotificationListResponse.observe(
            viewLifecycleOwner
        ) {
            val list = it.peekContent().data?.notificationsList

            var lists = ArrayList<Int>()
            if (list != null) {
                for (i in 0 until list.size) {
                    lists.add(list.get(i).id)
                }
                val progressDialog by lazy { CustomProgressDialog(activity) }

                if (count == 0) {
                    if (list.size > 0) {
                        getUpdateNotification(lists, progressDialog)
                    }
                }
            }

            binding.deleteBtn.isVisible = list!!.size != 0

            binding.rcvNotifications.isHorizontalScrollBarEnabled = true
            binding.rcvNotifications.isHorizontalFadingEdgeEnabled = true
            //binding.rcvongoing.layoutManager = GridLayoutManager(activity, 3, GridLayoutManager.HORIZONTAL, false)
            binding.rcvNotifications.layoutManager =
                LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
            // binding.rcvNotifications.adapter = NotificationsAdapter(activity, list)
            notificationsAdapter = NotificationsAdapter(activity, list)
            binding.rcvNotifications.adapter = notificationsAdapter
        }

        notificationListViewModel.errorResponse.observe(viewLifecycleOwner) {
            ErrorUtil.handlerGeneralError(requireContext(), it)
        }
    }


    /**
     * Delete Notification
     */
    fun getDeleteNotification(bodys: ArrayList<Int>, progressDialog: CustomProgressDialog) {

        val deleteNotificationBody = DeleteNotificationBody(
            notification_ids = bodys
        )
        deleteNotificationtViewModel.getDeleteNotification(
            deleteNotificationBody,
            progressDialog,
            activity
        )
    }

    private fun deleteNotificationObserver() {
        deleteNotificationtViewModel.progressIndicator.observe(viewLifecycleOwner, Observer {
        })
        deleteNotificationtViewModel.mDeleteNotificationResponse.observe(
            viewLifecycleOwner
        ) {
            // val list = it.peekContent().data.notificationsDeletedCount
            val progressDialog by lazy { CustomProgressDialog(activity) }
            getNotificationList(progressDialog)
        }

        deleteNotificationtViewModel.errorResponse.observe(viewLifecycleOwner) {
            ErrorUtil.handlerGeneralError(requireContext(), it)
        }
    }


    /**
     * Update Notification
     */
    fun getUpdateNotification(bodys: ArrayList<Int>, progressDialog: CustomProgressDialog) {

        val updateNotificationBody = UpdateNotificationBody(
            notification_ids = bodys,
            is_read = true
        )
        updateNotificationtViewModel.getupdateUserNotification(
            updateNotificationBody,
            progressDialog,
            activity
        )
    }

    private fun updateNotificationObserver() {
        updateNotificationtViewModel.progressIndicator.observe(viewLifecycleOwner, Observer {
        })
        updateNotificationtViewModel.mUpdateNotificationResponse.observe(
            viewLifecycleOwner
        ) {
            // val list = it.peekContent().data.notificationsDeletedCount
            count++
            val progressDialog by lazy { CustomProgressDialog(activity) }
            getNotificationList(progressDialog)
        }

        updateNotificationtViewModel.errorResponse.observe(viewLifecycleOwner) {
            ErrorUtil.handlerGeneralError(requireContext(), it)
        }
    }
}

