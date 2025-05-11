package com.example.mehmetalinacakandroid.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Customer(
    @ColumnInfo(name  = "isim")
    var isim : String,
    @ColumnInfo(name  = "ulke")
    var ulke : String,


){


    @PrimaryKey(autoGenerate = true)
    var id = 0
}