package com.mertadali.walkietalkie

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mertadali.walkietalkie.databinding.FragmentLoginBinding


class LoginFragment : Fragment() {
    // This property is only valid between onCreateView and onDestroyView.
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

        }
        binding.loginButton.setOnClickListener {
            // login butonuna tıklandığında yapılacaklar. - Kullanıcı giriş işlemi

        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}