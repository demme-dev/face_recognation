package com.facetrack.attendance.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.facetrack.attendance.data.local.AppDatabase
import com.facetrack.attendance.data.repository.AttendanceRepository
import com.facetrack.attendance.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var repository: AttendanceRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
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

        val user = FirebaseAuth.getInstance().currentUser
        binding.tvWelcome.text = "Welcome, User"

        user?.uid?.let { uid ->
            viewLifecycleOwner.lifecycleScope.launch {
                val userProfile = repository.getUserProfile(uid)
                userProfile?.let {
                    binding.tvWelcome.text = "Welcome, ${it.name}"
                }
            }
        }

        updateTodayStatus()

        binding.btnSync.setOnClickListener {
            binding.btnSync.isEnabled = false
            viewLifecycleOwner.lifecycleScope.launch {
                repository.syncOfflineAttendance()
                binding.btnSync.isEnabled = true
                android.widget.Toast.makeText(context, "Sync Complete!", android.widget.Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateTodayStatus() {
        val currentUid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        viewLifecycleOwner.lifecycleScope.launch {
            repository.allAttendanceLocal.collect { list ->
                val today = android.text.format.DateFormat.format("dd-MM-yyyy", java.util.Date())
                val isPresent = list.any { 
                    it.userId == currentUid &&
                    android.text.format.DateFormat.format("dd-MM-yyyy", it.timestamp) == today 
                }
                
                if (isPresent) {
                    binding.tvAttendanceStatus.text = "Present"
                    binding.tvAttendanceStatus.setTextColor(android.graphics.Color.GREEN)
                } else {
                    binding.tvAttendanceStatus.text = "Not Marked"
                    binding.tvAttendanceStatus.setTextColor(android.graphics.Color.RED)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}