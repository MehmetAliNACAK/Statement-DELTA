package com.example.mehmetalinacakandroid.view

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import com.example.mehmetalinacakandroid.databinding.ActivityMain2Binding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity2 : AppCompatActivity() {

    private lateinit var binding: ActivityMain2Binding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ViewBinding'i initialize et
        binding = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        // Firebase Authentication'i başlat
        auth = Firebase.auth

        // Kullanıcı daha önceden giriş yapmışsa kontrol et
        val guncelKullanici = auth.currentUser
        if (guncelKullanici != null) {
            // Kullanıcı daha önceden giriş yapmış
            navigateToMainActivity()
            return
        }

        // "Kayıt Ol" butonuna tıklama işlemi
        binding.button.setOnClickListener {
            val email = binding.EmailText.text.toString().trim()
            val password = binding.PasswordText.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                if (isValidEmail(email)) {
                    if (password.length >= 6) {
                        registerUser(email, password)
                    } else {
                        showToast("Şifre en az 6 karakter uzunluğunda olmalı!")
                    }
                } else {
                    showToast("Geçerli bir e-posta adresi giriniz!")
                }
            } else {
                showToast("Lütfen tüm alanları doldurun!")
            }
        }

        // "Giriş Yap" butonuna tıklama işlemi
        binding.button2.setOnClickListener {
            val email = binding.EmailText.text.toString().trim()
            val password = binding.PasswordText.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                if (isValidEmail(email)) {
                    loginUser(email, password)
                } else {
                    showToast("Geçerli bir e-posta adresi giriniz!")
                }
            } else {
                showToast("Email ve Şifre boş olamaz!")
            }
        }
    }

    private fun registerUser(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    showToast("Kayıt başarılı!")
                    navigateToMainActivity()
                }
            }
            .addOnFailureListener { exception ->
                showToast(exception.localizedMessage ?: "Kayıt işlemi başarısız oldu.")
            }
    }

    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                showToast("Giriş başarılı!")
                navigateToMainActivity()
            }
            .addOnFailureListener { exception ->
                showToast(exception.localizedMessage ?: "Giriş işlemi başarısız oldu.")
            }
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
