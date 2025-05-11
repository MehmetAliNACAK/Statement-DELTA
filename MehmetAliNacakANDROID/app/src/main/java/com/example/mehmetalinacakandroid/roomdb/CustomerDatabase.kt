package com.example.mehmetalinacakandroid.roomdb

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.mehmetalinacakandroid.model.Customer

@Database(entities = [Customer::class], version = 1)
abstract class CustomerDatabase : RoomDatabase() {
    abstract fun customerDAO(): CustomerDAO
}