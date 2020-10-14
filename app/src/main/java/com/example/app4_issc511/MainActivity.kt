package com.example.app4_issc511

import android.content.Context
import android.content.Intent
import android.database.CursorWindow
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.example.app4.Modelo.ImageConverter
import com.example.app4_issc511.Entidades.Personas
import com.example.app4_issc511.Modelo.PersonasDB
import kotlinx.android.synthetic.main.activity_detalle_persona.view.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.item_lista.view.*
import java.lang.reflect.Field


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        try {
            val field: Field = CursorWindow::class.java.getDeclaredField("sCursorWindowSize")
            field.isAccessible = true
            field.set(null, 100 * 1024 * 1024) //the 100MB is the new size
        } catch (e: Exception) {
            e.printStackTrace()
        }
        LlenarInformacion()
    }

    override fun onPause() {
        super.onPause()
        this.LlenarInformacion()
    }

    override fun onResume() {
        super.onResume()
        this.LlenarInformacion()
    }


    fun LlenarInformacion(){

        val datasource = PersonasDB(this)

        val registros =  ArrayList<Personas>()

        val cursor =  datasource.consultarPersonas()

        while (cursor.moveToNext()){
            val columnas = Personas(
                cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4)
            )
            registros.add(columnas)
        }

        val adaptador =  AdaptadorPersonas(this, registros)

        lstPersonas.adapter =  adaptador


        lstPersonas.setOnItemClickListener { adapterView, view, position, id ->

            val item = adapterView.getItemAtPosition(position) as Personas



            var intent  =  Intent(this@MainActivity, detallePersona::class.java).apply {
                putExtra("id", item._idPersona)
                putExtra("edoCivil", item._edoCivil)
            }
            //TODO: Antigua forma que se usa en JAVA
            //intent.putExtra("id",item._idPersona);
            startActivity(intent)
        }
    }

    fun AgregarPersonas(view: View) {
        var intent  =  Intent(this@MainActivity, detallePersona::class.java)
        startActivity(intent)
    }


    internal  class AdaptadorPersonas(context: Context, datos: List<Personas>):
        ArrayAdapter<Personas>(context, R.layout.item_lista, datos)
    {
        private val imageConverter: ImageConverter = ImageConverter()

        var _datos: List<Personas>

        init {
            _datos =  datos
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val inflater =  convertView ?: LayoutInflater.from(context).inflate(
                R.layout.item_lista, parent,
                false
            )


            val Entidad  = getItem(position)

            inflater.lbltitulo.text =  Entidad!!._nombrePersona
            inflater.lblsubtitulo.text =  Entidad!!._apellidoPersona

            inflater.profile_image.setImageBitmap(imageConverter.bitmap(Entidad!!._imgPersona))

            return inflater
        }
    }
}