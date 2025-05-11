package com.example.mehmetalinacakandroid.view

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.Layout.Directions
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.core.widget.addTextChangedListener
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.example.mehmetalinacakandroid.R
import com.example.mehmetalinacakandroid.adapter.CustomerAdapter
import com.example.mehmetalinacakandroid.databinding.FragmentFirstBinding
import com.example.mehmetalinacakandroid.model.Customer
import com.example.mehmetalinacakandroid.roomdb.CustomerDAO
import com.example.mehmetalinacakandroid.roomdb.CustomerDatabase
import com.google.firebase.auth.FirebaseAuth
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class FirstFragment : Fragment(),PopupMenu.OnMenuItemClickListener {private var _binding: FragmentFirstBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: CustomerDatabase
    private lateinit var customerDao: CustomerDAO
    private val mDisposable = CompositeDisposable()
    private lateinit var customerAdapter: CustomerAdapter
    private var customerList: List<Customer> = listOf()
    private lateinit var popup : PopupMenu

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = Room.databaseBuilder(
            requireContext(),
            CustomerDatabase::class.java,
            "Musteriler"
        ).build()
        customerDao = db.customerDAO()
    }
//ON RESUME FONSKİYONU EKLENECEK
    override fun onResume() {
        super.onResume()
        verileriAl()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //bu fonksiyon sadece ekleme için bunu kaldırmayı unutma
        binding.floatingActionButton.setOnClickListener { yeniEkle(it) }
        binding.floatingActionButton.setOnClickListener { floatingButtonTiklandi(it) }

        // Fragment Result Listener
        parentFragmentManager.setFragmentResultListener("data_updated", viewLifecycleOwner) { _, _ ->
            verileriAl()
        }

        binding.CustomerRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        customerAdapter = CustomerAdapter(customerList)
        binding.CustomerRecyclerView.adapter = customerAdapter

        //popup menu

        popup = PopupMenu(requireContext(),binding.floatingActionButton)

        val inflater = popup.menuInflater
        inflater.inflate(R.menu.my_pop_up_menu,popup.menu)
        popup.setOnMenuItemClickListener(this)
        // Veri tabanından verileri al
        verileriAl()

        // EditText için filtreleme işlemi
        var searchJob: Job? = null
        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchJob?.cancel()
                searchJob = CoroutineScope(Dispatchers.Main).launch {
                    delay(300) // 300ms gecikme
                    customerAdapter.filter(s.toString())
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }
     fun verileriAl() {
        mDisposable.add(
            customerDao.getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { newCustomerList -> customerAdapter.updateData(newCustomerList) },
                    { throwable -> Log.e("FirstFragment", "Veri alırken hata oluştu: ${throwable.message}") }
                )
        )
    }

    fun floatingButtonTiklandi(view: View){

        popup.show()
    }
    fun yeniEkle(view: View) {
        val action = FirstFragmentDirections.actionFirstFragmentToSecondFragment(
            bilgi = "yeni",
            id = 0
        )
        Navigation.findNavController(view).navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        mDisposable.clear() // Tüm işlemleri temizler, ancak nesne kullanılabilir kalır


    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.uploadMenu) {
            val action = FirstFragmentDirections.actionFirstFragmentToSecondFragment(bilgi = "yeni", 0)
            Navigation.findNavController(requireView()).navigate(action)
        } else if (item?.itemId == R.id.logoutMenu) {
            FirebaseAuth.getInstance().signOut() // Kullanıcıyı çıkış yap
            val intent = Intent(requireContext(), MainActivity2::class.java)
            startActivity(intent)
            requireActivity().finish() // Mevcut aktiviteyi kapat
        }
        return true
    }

}