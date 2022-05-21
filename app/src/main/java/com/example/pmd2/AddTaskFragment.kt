package com.example.pmd2

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import com.example.pmd2.databinding.FragmentAddTaskBinding
import com.example.pmd2.firestore.Tasks
import com.example.pmd2.room.Database
import com.example.pmd2.room.TasksEntity
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch


class AddTaskFragment : BottomSheetDialogFragment() {

    private lateinit var auth: FirebaseAuth
    private var uid = Firebase.auth.currentUser?.uid
    var db = FirebaseFirestore.getInstance()

    private lateinit var binding: FragmentAddTaskBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_task, container, false)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireActivity(), R.style.CustomBottomSheetDialog)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAddTaskBinding.bind(view)


        binding.addTaskTitleInput.doAfterTextChanged {
            if (binding.addTaskTitleInput.text?.isEmpty() == true) {
                binding.titleLayout.error = "Enter title"
            } else {
                binding.titleLayout.error = null
            }
        }
        addTask()

        binding.addTaskActionCancel.setOnClickListener {
            dialog?.dismiss()

        }
    }


    private fun addTask(){
        binding.addTaskActionOk.setOnClickListener {
            val taskId = db.collection("users").document(uid.toString()).collection("tasks").document().id
            val title = binding.addTaskTitleInput.text.toString()
            val description = binding.addTaskDescriptionInput.text.toString()
            val task = Tasks(taskId, null,  title, description, false)

            if (binding.addTaskTitleInput.text?.isEmpty() == true){
                binding.titleLayout.error = "Enter title"
            }else{
                val query = db.collection("users").document(uid.toString()).collection("tasks")
                    .document(taskId)
                var listener: ListenerRegistration? = null
                listener = query.addSnapshotListener{ snapshots, e ->
                    if (e != null) {
                        Log.w("ContentValues", "Listen error", e)
                    }
                    snapshots?.reference?.set(task)
                    listener?.remove()
                    dialog?.dismiss()
                }

            }

        }


    }
    }





