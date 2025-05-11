package com.example.mehmetalinacakandroid.adapter
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.mehmetalinacakandroid.databinding.RecyclerRowBinding
import com.example.mehmetalinacakandroid.model.Customer
import com.example.mehmetalinacakandroid.view.FirstFragmentDirections


class CustomerAdapter (var customerList: List<Customer>): RecyclerView.Adapter<CustomerAdapter.CustomerHolder> (){

    private var filteredList: List<Customer> = customerList
    class  CustomerHolder (val binding : RecyclerRowBinding): RecyclerView.ViewHolder(binding.root){


    }
override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomerHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RecyclerRowBinding.inflate(inflater, parent, false)
        return CustomerHolder(binding)
    }

    override fun getItemCount(): Int {
        return filteredList.size
    }

    fun filter(query: String) {
        filteredList = if (query.isEmpty()) {
            customerList
        } else {
            customerList.filter {
                it.isim.contains(query, ignoreCase = true) || it.ulke.contains(query, ignoreCase = true)
            }
        }
        notifyDataSetChanged()
    }

    fun updateData(newCustomerList: List<Customer>) {
        customerList = newCustomerList
        filter("") // Listeyi sıfırlamak için boş sorgu gönderiliyor
    }

    override fun onBindViewHolder(holder: CustomerHolder, position: Int) {
        val customer = filteredList[position]
        holder.binding.recyclerViewTextView.text = customer.isim
        holder.itemView.setOnClickListener {
            val action = FirstFragmentDirections.actionFirstFragmentToSecondFragment(
                bilgi = "eski",
                customer.id
            )
            Navigation.findNavController(holder.itemView).navigate(action)
        }
    }
}