package com.mertadali.walkietalkie.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.mertadali.walkietalkie.databinding.FragmentLoginBinding


class LoginFragment : Fragment() {
    // This property is only valid between onCreateView and onDestroyView.
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth


        val currentUser = auth.currentUser
        if (currentUser != null) {
            readlnOrNull()
            val action = LoginFragmentDirections.actionLoginFragmentToChatFragment()
            findNavController().navigate(action)


    }




   /* public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (FirebaseAuth.getInstance().currentUser != null) {
            readlnOrNull()
            val action = LoginFragmentDirections.actionLoginFragmentToChatFragment()
            findNavController().navigate(action)
        }*/
    }






    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        val view = binding.root
        return view



    }





    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.signupButton.setOnClickListener {
            // sign up butonuna tıklandığında yapılacaklar. - Kullanıcı kayıt işlemi
            auth.createUserWithEmailAndPassword(binding.emailText.text.toString(),binding.passwordText.text.toString()).addOnSuccessListener {
                // kullanıcı oluşturuldu
                val action = LoginFragmentDirections.actionLoginFragmentToChatFragment()
                findNavController().navigate(action)    // kullanıcı oluşturulduğunda diğer fragmenta geçiş.




            }.addOnFailureListener {exception ->
                // hata aldık ve bu bana exception olarak verildi.
                Toast.makeText(requireContext(),exception.localizedMessage,Toast.LENGTH_LONG).show()
            }

        }
        binding.loginButton.setOnClickListener {
            // login butonuna tıklandığında yapılacaklar. - Kullanıcı giriş işlemi
            auth.signInWithEmailAndPassword(binding.emailText.text.toString(),binding.passwordText.text.toString()).addOnSuccessListener {
                val action = LoginFragmentDirections.actionLoginFragmentToChatFragment()
                findNavController().navigate(action)

            }.addOnFailureListener {exception ->
                Toast.makeText(requireContext(),exception.localizedMessage,Toast.LENGTH_LONG).show()
            }

        }





    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}