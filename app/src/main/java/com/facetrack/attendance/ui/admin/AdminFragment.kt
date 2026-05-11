package com.facetrack.attendance.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.facetrack.attendance.data.local.AppDatabase
import com.facetrack.attendance.data.repository.AttendanceRepository
import com.facetrack.attendance.databinding.FragmentAdminBinding
import com.facetrack.attendance.ui.history.AttendanceAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class AdminFragment : Fragment() {

    private var _binding: FragmentAdminBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: AttendanceAdapter
    private lateinit var repository: AttendanceRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminBinding.inflate(inflater, container, false)
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

        adapter = AttendanceAdapter()
        binding.rvAllAttendance.layoutManager = LinearLayoutManager(context)
        binding.rvAllAttendance.adapter = adapter

        loadAllData()

        binding.etSearch.addTextChangedListener { text ->
            filterData(text.toString())
        }
    }

    private fun loadAllData() {
        viewLifecycleOwner.lifecycleScope.launch {
            val list = repository.getAllAttendanceFromFirestore()
            adapter.submitList(list)
        }
    }

    private fun filterData(query: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            val list = repository.getAllAttendanceFromFirestore()
            val filtered = list.filter { it.name.contains(query, ignoreCase = true) }
            adapter.submitList(filtered)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}