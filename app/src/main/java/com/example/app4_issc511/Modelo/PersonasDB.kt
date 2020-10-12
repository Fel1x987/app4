package com.example.app4_issc511.Modelo

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import java.lang.Exception

class PersonasDB(context: Context) {

    private val openHelper: DbOpenHelper = DbOpenHelper(context)
    private val database: SQLiteDatabase

    init {
        database =  openHelper.writableDatabase
    }

    fun consultarPersonas():Cursor{
        return database.rawQuery("""
            select idPersona,nombrePersona,apellidoPersona, imgPersona 
              from tblpersonas
        """.trimIndent(),null)
        database.close()
    }

    fun consultarPersonas(idPersona:Int):Cursor{
        return database.rawQuery("""
            select idPersona,nombrePersona,apellidoPersona, imgPersona 
                     from tblpersonas 
                where idPersona =  $idPersona
        """.trimIndent(),null)
        database.close()
    }

    fun guardarPersona(nombrePersona: String, apellidoPersona: String, imgPersona: String)
    {
        val values =  ContentValues()
        values.put("nombrePersona",nombrePersona)
        values.put("apellidoPersona",apellidoPersona)
        values.put("imgPersona", imgPersona)
        database.insert("tblpersonas",null,values)
        database.close()
    }

    fun modificarPersona(id: Int,nombrePersona: String, apellidoPersona: String, imgPersona: String)
    {
        val values =  ContentValues()
        values.put("nombrePersona",nombrePersona)
        values.put("apellidoPersona",apellidoPersona)
        values.put("imgPersona", imgPersona)
        val whereArgs  = arrayOf(id.toString())

        database.update("tblpersonas",values,"idPersona=?",whereArgs)
        database.close()
    }

    fun eliminarPersona(id: Int):Boolean{
        val whereArgs = arrayOf(id.toString())

        try {
            database.delete("tblpersonas","idPersona=?",whereArgs)
            return true
        }catch (ex:Exception){
            return false
        }finally {
            database.close()
        }
    }

}