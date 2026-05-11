package com.facetrack.attendance.ui.attendance

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.facetrack.attendance.data.local.AppDatabase
import com.facetrack.attendance.data.repository.AttendanceRepository
import com.facetrack.attendance.databinding.FragmentAttendanceBinding
import com.facetrack.attendance.util.FaceAnalyzer
import com.facetrack.attendance.util.ViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class AttendanceFragment : Fragment() {

    private var _binding: FragmentAttendanceBinding? = null
    private val binding get() = _binding!!

    private lateinit var cameraExecutor: ExecutorService
    private val viewModel: AttendanceViewModel by viewModels {
        val db = AppDatabase.getDatabase(requireContext())
        val repository = AttendanceRepository(db.attendanceDao(), FirebaseFirestore.getInstance(), FirebaseAuth.getInstance())
        ViewModelFactory(repository)
    }

    private var registrationStep = 0 // 0: Idle, 1: Front, 2: Left, 3: Right
    private val collectedEmbeddings = mutableListOf<List<Float>>()

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            startCamera()
        } else {
            Toast.makeText(context, "Camera permission required", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAttendanceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cameraExecutor = Executors.newSingleThreadExecutor()

        val uid = FirebaseAuth.getInstance().currentUser?.uid
        uid?.let { viewModel.loadUser(it) }

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED) {
            startCamera()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }

        binding.btnRegisterFace.setOnClickListener {
            startRegistration()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.attendanceMarked.collectLatest { marked ->
                if (marked) {
                    binding.tvStatus.text = "Attendance Marked!"
                }
            }
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.previewView.surfaceProvider)
            }

            val imageAnalyzer = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor, FaceAnalyzer { image, rect ->
                        requireActivity().runOnUiThread {
                            binding.overlayView.setFaceRect(rect)
                            handleFaceAnalysis(rect)
                        }
                    })
                }

            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(viewLifecycleOwner, cameraSelector, preview, imageAnalyzer)
            } catch (e: Exception) {
                Log.e("AttendanceFragment", "Use case binding failed", e)
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun handleFaceAnalysis(rect: android.graphics.Rect?) {
        if (rect == null) {
            if (viewModel.attendanceMarked.value) return
            binding.tvStatus.text = "Align your face"
            return
        }

        when (registrationStep) {
            0 -> {
                if (viewModel.attendanceMarked.value) return
                binding.tvStatus.text = "Face Detected. Hold steady..."
                val user = viewModel.currentUser.value
                if (user?.faceEmbeddings != null) {
                    viewModel.markAttendance(user.uid, user.name)
                }
            }
            1 -> binding.tvStatus.text = "Capture Front Face"
            2 -> binding.tvStatus.text = "Turn Left"
            3 -> binding.tvStatus.text = "Turn Right"
        }
    }

    private fun startRegistration() {
        registrationStep = 1
        collectedEmbeddings.clear()
        binding.btnRegisterFace.visibility = View.GONE
        
        Toast.makeText(context, "Registration Started: Look Front", Toast.LENGTH_SHORT).show()
        
        viewLifecycleOwner.lifecycleScope.launch {
            kotlinx.coroutines.delay(2000)
            captureForRegistration("Front")
            
            registrationStep = 2
            Toast.makeText(context, "Look Left", Toast.LENGTH_SHORT).show()
            kotlinx.coroutines.delay(2000)
            captureForRegistration("Left")
            
            registrationStep = 3
            Toast.makeText(context, "Look Right", Toast.LENGTH_SHORT).show()
            kotlinx.coroutines.delay(2000)
            captureForRegistration("Right")
            
            saveRegistration()
        }
    }

    private fun captureForRegistration(pos: String) {
        val dummyEmbeddings = List(128) { (0..100).random().toFloat() / 100f }
        collectedEmbeddings.add(dummyEmbeddings)
        Log.d("AttendanceFragment", "Captured $pos")
    }

    private suspend fun saveRegistration() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val finalEmbeddings = collectedEmbeddings[0]
        
        viewModel.registerFace(uid, finalEmbeddings)
        
        registrationStep = 0
        binding.btnRegisterFace.visibility = View.VISIBLE
        binding.tvStatus.text = "Face Registered!"
        Toast.makeText(context, "Face registration complete!", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        cameraExecutor.shutdown()
        _binding = null
    }
}