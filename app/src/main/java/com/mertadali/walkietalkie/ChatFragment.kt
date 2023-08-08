package com.mertadali.walkietalkie


import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.*
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.google.gson.Gson
import com.mertadali.walkietalkie.databinding.FragmentChatBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.util.*
import kotlin.collections.HashMap


@Suppress("DEPRECATION")
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
    private lateinit var storage : FirebaseStorage
    private lateinit var outputFilePath: String








    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firestore = Firebase.firestore
        auth = Firebase.auth
        storage = Firebase.storage

        mediaPlayer = MediaPlayer()

        mediaRecorder = MediaRecorder()
        registerLauncher()

        setHasOptionsMenu(true)







        if (ActivityCompat.checkSelfPermission(requireActivity(), android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(requireActivity(),
                arrayOf(android.Manifest.permission.RECORD_AUDIO,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE),111)







        // kullancıyıya bir ekran gelsin ve evet hayır şeklinde cevap versin bir de izin vermezse ne olacağını yap.



    }





    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        val view = binding.root
        return view



    }




    @SuppressLint("SuspiciousIndentation")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

       // var path: String = Environment.getExternalStorageDirectory().absolutePath
       // path += "/" + UUID.randomUUID().toString() + ".3gp"

        val uuid = UUID.randomUUID()
        val pathName = "$uuid.3gp"

        //val outputFile = File(Environment.getExternalStorageDirectory(),pathName)
        val outputFile = File.createTempFile("Audio", "3gp")
        outputFilePath = outputFile.absolutePath

        println(outputFilePath)




        super.onViewCreated(view, savedInstanceState)
        adapter = ChatRecyclerView()
        binding.chatRecycler.adapter = adapter
        binding.chatRecycler.layoutManager = LinearLayoutManager(requireContext())



        binding.sendButton.setOnClickListener {


            val storageRef = storage.reference

            val file = Uri.fromFile(File(outputFilePath))

            val voiceReference = storageRef.child("Audio").child(pathName)



            if (file != null){
                voiceReference.putFile(file).addOnSuccessListener {
                  val uploadVoiceReference = storage.reference.child("Audio").child(pathName)
                    uploadVoiceReference.downloadUrl.addOnSuccessListener {
                        val downloadUrl = it.toString()
                        auth.currentUser?.let { FirebaseUser ->
                            val user = FirebaseUser.email
                            val chatText = binding.chatText.text.toString()
                            val date = FieldValue.serverTimestamp()


                            val dataMap = HashMap<String, Any>()
                            dataMap.put("user", user!!)
                            dataMap.put("text", chatText)
                            dataMap.put("date", date)
                            dataMap.put("downloadUrl",downloadUrl)


                            firestore.collection("Chats").add(dataMap).addOnCompleteListener {task ->
                                if (task.isComplete && task.isSuccessful){
                                    binding.chatText.setText("")
                                    println("collection succsess")
                                }



                            }.addOnFailureListener {
                                Toast.makeText(requireContext(), it.localizedMessage, Toast.LENGTH_LONG).show()
                                binding.chatText.setText("")

                            }
                        }

                    }
                }.addOnFailureListener {
                    Toast.makeText(requireContext(), it.localizedMessage, Toast.LENGTH_LONG).show()
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
                                    val downloadUrl = documents.get("downloadUrl")
                                    val chat = Chat(user, text,downloadUrl)



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
                    mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)     // Tekrar kontrol et bu kısımı
                    mediaRecorder.setOutputFile(outputFilePath)
                    mediaRecorder.prepare()
                    mediaRecorder.start()

                }catch (e : IOException){
                    e.printStackTrace()

                }




            }
        binding.stopButton.setOnClickListener {
            // stop recording
            println("stop")
            mediaRecorder.stop()
            mediaRecorder.release()
            binding.recordButton.isEnabled = true
            binding.stopButton.isEnabled = false
        }
        binding.startButton.setOnClickListener {
            try {
                val mediaPlayer = MediaPlayer()
                mediaPlayer.setDataSource(outputFilePath)
                mediaPlayer.prepare()
                mediaPlayer.start()
                binding.recordButton.isEnabled = false

            }catch (e : IOException){
                e.printStackTrace()
            }


        }


        }

    private fun notificationSend(notification: PushNotification) = CoroutineScope(Dispatchers.IO).launch {


        try {

           val response = RetrofitObject.api.postNotification(notification)
            if (response.isSuccessful){
                println(Gson().toJson(response))

            }else{
                println(response.errorBody().toString())
            }



        }catch (e: IOException){
            e.printStackTrace()
        }
    }




    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun registerLauncher(){
          PackageManager.PERMISSION_GRANTED
        }






    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.signout_menu,menu)
        super.onCreateOptionsMenu(menu, inflater)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        FirebaseAuth.getInstance().signOut()
        val action = ChatFragmentDirections.actionChatFragmentToLoginFragment()
        findNavController().navigate(action)
        return super.onOptionsItemSelected(item)
    }



    }





























