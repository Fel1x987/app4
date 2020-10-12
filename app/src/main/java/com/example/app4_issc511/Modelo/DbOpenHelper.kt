package com.example.app4_issc511.Modelo

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

val nombreDB = "personas2.db"
val versionDB = 4

class DbOpenHelper(context: Context) :
        SQLiteOpenHelper(context, nombreDB,null, versionDB) {

    override fun onCreate(db: SQLiteDatabase?) {
        db!!.execSQL("""
            create table tblpersonas (
                idPersona integer primary key autoincrement,
                nombrePersona text not null,
                apellidoPersona text not null,
                imgPersona text not null
            )
        """.trimIndent())
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
    }
}