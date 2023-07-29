package com.mertadali.walkietalkie


import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder

import android.os.Bundle
import android.os.Environment
import android.view.*
import android.widget.Toast
import androidx.core.app.ActivityCompat

import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.mertadali.walkietalkie.databinding.FragmentChatBinding
import java.io.IOException


class ChatFragment : Fragment() {

    // This property is only valid between onCreateView and onDestroyView.
    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!
    private lateinit var mediaRecorder: MediaRecorder
    private lateinit var  mediaPlayer : MediaPlayer
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var adapter: ChatRecyclerView
    private var chats = arrayListOf<Chat>()
    //private lateinit var permissionLauncher : ActivityResultLauncher<String>
    private val path: String = Environment.getExternalStorageDirectory().toString() + "/myrec1"    // kontrol









    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firestore = Firebase.firestore
        auth = Firebase.auth

        mediaPlayer = MediaPlayer()

        mediaRecorder = MediaRecorder()
        registerLauncher()


        println(path)

        if (ActivityCompat.checkSelfPermission(requireActivity(), android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(requireActivity(),
            arrayOf(android.Manifest.permission.RECORD_AUDIO,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE),111)

        // kullancıyıya bir ekran gelsin ve evet hayır şeklinde cevap versin bir de izin vermezse ne olacağını yap.



    }







    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        val view = binding.root
        return view



    }




    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {


        super.onViewCreated(view, savedInstanceState)
        adapter = ChatRecyclerView()
        binding.chatRecycler.adapter = adapter
        binding.chatRecycler.layoutManager = LinearLayoutManager(requireContext())

        binding.sendButton.setOnClickListener {
            auth.currentUser?.let { FirebaseUser ->
                val user = FirebaseUser.email
                val chatText = binding.chatText.text.toString()
                val date = FieldValue.serverTimestamp()


                val dataMap = HashMap<String, Any>()
                dataMap.put("user", user!!)
                dataMap.put("text", chatText)
                dataMap.put("date", date)

                firestore.collection("Chats").add(dataMap).addOnSuccessListener {
                    binding.chatText.setText("")

                }.addOnFailureListener {
                    Toast.makeText(requireContext(), it.localizedMessage, Toast.LENGTH_LONG).show()
                    binding.chatText.setText("")
                }
            }
        }



        firestore.collection("Chats").orderBy("date", Query.Direction.ASCENDING)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Toast.makeText(requireContext(), error.localizedMessage, Toast.LENGTH_LONG)
                        .show()

                } else {
                    if (value != null) {
                        if (value.isEmpty) {
                            Toast.makeText(requireContext(), "No message", Toast.LENGTH_LONG).show()
                        } else {
                            val documents = value.documents
                            chats.clear()

                            for (documents in documents) {
                                val text = documents.get("text") as String
                                val user = documents.get("user") as String
                                val chat = Chat(user, text)
                                chats.add(chat)
                                adapter.chats = chats
                            }
                        }
                        adapter.notifyDataSetChanged()
                    }

                }
            }
        binding.recordButton.setOnClickListener {
            println("deneme 1")

                // permission granted

                try {
                    mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC)
                    mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                    mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)     // Tekrar kontrol et bu kısımı
                    mediaRecorder.setOutputFile(path)
                    mediaRecorder.prepare()
                    mediaRecorder.start()

                }catch (e : IOException){
                    e.printStackTrace()
                    println(e)
                }




            }
        binding.stopButton.setOnClickListener {
            println("stop")
            mediaRecorder.stop()
            mediaRecorder.release()
            binding.recordButton.isEnabled = true
            binding.stopButton.isEnabled = false
        }
        binding.startButton.setOnClickListener {
            try {
                             val mediaPlayer = MediaPlayer()
                mediaPlayer.setDataSource(path)
                mediaPlayer.prepare()
                mediaPlayer.start()
                binding.recordButton.isEnabled = false

            }catch (e : IOException){
                e.printStackTrace()
            }


        }
        // stop recording




        }




    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun registerLauncher(){

            PackageManager.PERMISSION_GRANTED
        }
    }





























