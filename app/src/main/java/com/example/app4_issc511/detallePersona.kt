package com.example.app4_issc511

import android.Manifest
import android.content.pm.PackageManager
import android.database.CursorWindow
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Base64
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.graphics.drawable.toBitmap
import com.ablanco.imageprovider.ImageProvider
import com.ablanco.imageprovider.ImageSource
import com.example.app4.Modelo.ImageConverter
import com.example.app4_issc511.Modelo.DbOpenHelper
import com.example.app4_issc511.Modelo.PersonasDB
import kotlinx.android.synthetic.main.activity_detalle_persona.*
import java.io.ByteArrayOutputStream
import java.lang.reflect.Field

class detallePersona : AppCompatActivity() {

    var img : Bitmap? = null
    private lateinit var datasource:PersonasDB

    private var id = 0
    private var nombre = ""
    private var  apellido = ""

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
            apellido = extras.getString("apellido").toString()
            nombre = extras.getString("nombre").toString()

            //img = extras!!


            //TODO se inicializa los editText con los datos mandados de MainActivity
            txtNombre.setText(nombre)
            txtApellido.setText(apellido)
            imageButton.setImageBitmap(img)

        }

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
            datasource.modificarPersona(id, txtNombre.text.toString(), txtApellido.text.toString(), bazar!!)
            Toast.makeText(
                applicationContext, "Se editó correctamente",
                Toast.LENGTH_SHORT
            ).show()
        }else {
            // TODO se realizara una inserción
            // var vic = imageButton.background.toBitmap() //background.toBitmap().toString()
            var bazar  = imageConverter.base64(img!!)
            datasource.guardarPersona(txtNombre.text.toString(), txtApellido.text.toString(), bazar!!)
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