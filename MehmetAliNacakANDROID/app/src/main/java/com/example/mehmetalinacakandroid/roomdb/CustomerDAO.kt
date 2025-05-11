package com.example.mehmetalinacakandroid.roomdb

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.mehmetalinacakandroid.model.Customer
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable

@Dao
interface CustomerDAO {

    @Query("SELECT * FROM  Customer")
    fun getAll() : Flowable<List<Customer>>

    @Query("SELECT * FROM Customer WHERE id= :id")

    fun  findByeId(id : Int) : Flowable<Customer>

    @Insert
    fun insert(customer: Customer) : Completable

    @Delete
    fun delete(customer: Customer) : Completable

    @Update
    fun update(customer: Customer): Completable

}