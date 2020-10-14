package com.example.app4_issc511
import android.Manifest
import android.content.pm.PackageManager
import android.database.CursorWindow
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.ablanco.imageprovider.ImageProvider
import com.ablanco.imageprovider.ImageSource
import com.example.app4.Modelo.ImageConverter
import com.example.app4_issc511.Entidades.Personas
import com.example.app4_issc511.Modelo.PersonasDB
import kotlinx.android.synthetic.main.activity_detalle_persona.*
import java.lang.reflect.Field


class detallePersona : AppCompatActivity() {

    var img : Bitmap? = null
    private lateinit var datasource:PersonasDB

    private var id = 0
    private var edoCivil = ""
    private var positionEstado = -1
    lateinit var spinner: Spinner
    private var edoCivil2=""



    private val imageConverter: ImageConverter = ImageConverter()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_persona)
        datasource = PersonasDB(this)
        val extras = this.intent.extras

        //TODO hacer que el cursor de la consulta soporte una tamaño mayor por el uso de Base64  to Bitmap
        try {
            val field: Field = CursorWindow::class.java.getDeclaredField("sCursorWindowSize")
            field.isAccessible = true
            field.set(null, 100 * 1024 * 1024) //the 100MB is the new size
        } catch (e: Exception) {
            e.printStackTrace()
        }

        if (extras != null) {
            id = extras.getInt("id")
            edoCivil = extras.getString("edoCivil")!!
            when (edoCivil) {
                "Casado" -> positionEstado = 0
                "Soltero" -> positionEstado = 1
                "Divorciado" -> positionEstado = 2
            }
            obtenerPersona(id);
        }
        spinner = findViewById(R.id.edoCivil_spinner)
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.edoCivilArray,
            android.R.layout.simple_spinner_item
        )

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        edoCivil_spinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (edoCivil==""){
                    val civil: String = parent?.getItemAtPosition(position).toString()
                    edoCivil2 = civil
                } else {
                    if(positionEstado == 1 || positionEstado == 2 || positionEstado == 0){
                        parent?.setSelection(positionEstado)
                        positionEstado = - 1
                    }
                    else{
                        parent?.setSelection(position)
                    }
                    val civil: String = parent?.getItemAtPosition(position).toString()
                    edoCivil2 = civil
                }
            }
        }
    }


    fun obtenerPersona(id: Int):ArrayList<Personas>{
        val datasource = PersonasDB(this)
        val registros =  ArrayList<Personas>()
        val cursor =  datasource.consultarPersonas(id)
        while (cursor.moveToNext()){
            val columnas = Personas(
                cursor.getInt(0),
                cursor.getString(1),
                cursor.getString(2),
                cursor.getString(3),
                cursor.getString(4)
            )
            registros.add(columnas)
        }
        txtNombre.setText(registros[0]._nombrePersona)
        txtApellido.setText(registros[0]._apellidoPersona)
        imageButton.setImageBitmap(imageConverter.bitmap(registros[0]._imgPersona))

        /////////////////////////////////////////////////////////////////////////////
       return registros
    }



    fun TomarFoto(view: View){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED) {

            ImageProvider(this@detallePersona).getImage(ImageSource.CAMERA){ bitmap ->
                img = bitmap
                imageButton.setImageBitmap(bitmap)
            }

        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                42424
            )
        }
    }

    fun Eliminar(view: View){
        //TODO Preguntar al usuario si en verdad lo desea eliminar

        val builder =  AlertDialog.Builder(this@detallePersona)
        builder.setMessage("¿Deseas eliminar a esta persona?")
            .setTitle("Eliminar")
            .setPositiveButton("Aceptar"){ dialog, lis ->
                if(datasource.eliminarPersona(id)){
                    Toast.makeText(
                        applicationContext, "Se realizo correctamente",
                        Toast.LENGTH_SHORT
                    ).show()

                    txtNombre.setText("")
                    txtApellido.setText("")
                    id = 0
                }
            }
            .setNegativeButton("Cancelar") { dialog, lis ->
                Toast.makeText(
                    applicationContext, "Se cancelo la eliminación",
                    Toast.LENGTH_SHORT
                ).show()
            }
        builder.create().show()



    }
    fun GuardarPersona(view: View){
        if(id != 0){
            //TODO se realizara una modificación
            var bazar  = imageConverter.base64(img!!)
            //print(bazar);
            datasource.modificarPersona(
                id,
                txtNombre.text.toString(),
                txtApellido.text.toString(),
                bazar!!,
                edoCivil2
            )
            Toast.makeText(
                applicationContext, "Se editó correctamente",
                Toast.LENGTH_SHORT
            ).show()
        }else {
            // TODO se realizara una inserción

            var bazar  = imageConverter.base64(img!!)

            datasource.guardarPersona(
                txtNombre.text.toString(),
                txtApellido.text.toString(),
                bazar!!,
                edoCivil2
            )
            Toast.makeText(
                applicationContext, "Se guardo correctamente",
                Toast.LENGTH_SHORT
            ).show()

        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 42424){
            ImageProvider(this@detallePersona).getImage(ImageSource.CAMERA){ bitmap ->
                img = bitmap
                imageButton.setImageBitmap(bitmap)
            }
        }
    }

}