package com.example.mehmetalinacakandroid.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.room.Room
import com.example.mehmetalinacakandroid.adapter.CustomerAdapter
import com.example.mehmetalinacakandroid.databinding.FragmentSecondBinding
import com.example.mehmetalinacakandroid.model.Customer
import com.example.mehmetalinacakandroid.roomdb.CustomerDAO
import com.example.mehmetalinacakandroid.roomdb.CustomerDatabase
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers

class SecondFragment : Fragment() {

    private var _binding: FragmentSecondBinding? = null
    private val binding get() = _binding!!
    private  lateinit var db : CustomerDatabase
    private  lateinit var customerDao : CustomerDAO
    private   val  mDisposable =  CompositeDisposable()
    private var selectedCustomer : Customer? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = Room.databaseBuilder(
            requireContext(),
            CustomerDatabase::class.java,
            "Musteriler"
        ).build()
        customerDao = db.customerDAO()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.KaydetButton.setOnClickListener { Kaydet() }
        binding.SilButton.setOnClickListener { Sil() }
        binding.GuncelleButton.setOnClickListener { Guncelle() }

        arguments?.let {
            val bilgi = SecondFragmentArgs.fromBundle(it).bilgi
            if (bilgi == "yeni") {
                selectedCustomer = null
                updateButtonStates(true)
                binding.IsimText.setText("")
                binding.UlkeText.setText("")
            } else {
                val id = SecondFragmentArgs.fromBundle(it).id
                mDisposable.add(
                    customerDao.findByeId(id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                            this::handleResponse,
                            { throwable -> Log.e("SecondFragment", "Hata oluştu: ${throwable.message}") }
                        )
                )
            }
        }
    }

    private fun handleResponse(customer: Customer) {
        binding.IsimText.setText(customer.isim)
        binding.UlkeText.setText(customer.ulke)
        selectedCustomer = customer
        updateButtonStates(false)
    }

    private fun updateButtonStates(isNew: Boolean) {
        binding.KaydetButton.isEnabled = isNew
        binding.SilButton.isEnabled = !isNew
        binding.GuncelleButton.isEnabled = !isNew
    }

    fun Kaydet() {
        val isim = binding.IsimText.text.toString()
        val ulke = binding.UlkeText.text.toString()
        val customer = Customer(isim, ulke)

        mDisposable.add(
            customerDao.insert(customer)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { handleResponseForInsert("insert") },
                    { throwable -> Log.e("SecondFragment", "Hata oluştu: ${throwable.message}") }
                )
        )
    }

    fun Sil() {
        selectedCustomer?.let {
            mDisposable.add(
                customerDao.delete(it)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        { handleResponseForInsert("delete") },
                        { throwable -> Log.e("SecondFragment", "Hata oluştu: ${throwable.message}") }
                    )
            )
        }
    }

    fun Guncelle() {
        selectedCustomer?.let {
            val isim = binding.IsimText.text.toString()
            val ulke = binding.UlkeText.text.toString()
            val updatedCustomer = it.apply {
                this.isim = isim
                this.ulke = ulke
            }

            mDisposable.add(
                customerDao.update(updatedCustomer)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        { handleResponseForInsert("update") },
                        { throwable -> Log.e("SecondFragment", "Hata oluştu: ${throwable.message}") }
                    )
            )
        }
    }

    private fun handleResponseForInsert(actionType: String) {
        val action = SecondFragmentDirections.actionSecondFragmentToFirstFragment()
        Navigation.findNavController(requireView()).navigate(action)

        // Veri tabanı güncellenmiş olmalı
        parentFragmentManager.setFragmentResult("data_updated", Bundle())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

        mDisposable.clear() // Tüm işlemleri temizler, ancak nesne kullanılabilir kalır


        // FirstFragment'e veri güncellendiğini bildir
        parentFragmentManager.setFragmentResult("data_updated", Bundle())
    }
}