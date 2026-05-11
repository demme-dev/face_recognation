package com.facetrack.attendance.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.facetrack.attendance.R
import com.facetrack.attendance.data.local.AppDatabase
import com.facetrack.attendance.data.repository.AttendanceRepository
import com.facetrack.attendance.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var repository: AttendanceRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val db = AppDatabase.getDatabase(requireContext())
        repository = AttendanceRepository(
            db.attendanceDao(),
            FirebaseFirestore.getInstance(),
            FirebaseAuth.getInstance()
        )

        loadProfile()

        binding.btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            findNavController().navigate(R.id.loginFragment)
        }

        binding.btnAdmin.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_adminFragment)
        }
    }

    private fun loadProfile() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        viewLifecycleOwner.lifecycleScope.launch {
            val user = repository.getUserProfile(uid)
            user?.let {
                binding.tvName.text = it.name
                binding.tvEmail.text = it.email
                if (it.isAdmin) {
                    binding.btnAdmin.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}