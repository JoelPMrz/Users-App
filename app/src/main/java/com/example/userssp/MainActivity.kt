package com.example.userssp

import android.content.Context
import android.content.DialogInterface

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.userssp.databinding.ActivityMainBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText

class MainActivity : AppCompatActivity(), OnClickListener {

    override fun onClick(user: User, position : Int) {
        Toast.makeText(this, "$position: ${user.getFullname()}" , Toast.LENGTH_SHORT).show()
    }

    private lateinit var userApadter : UserAdapter
    private lateinit var linearLayoutManager : RecyclerView.LayoutManager
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //SHARED PREFERENCES

        val preferences = getPreferences(Context.MODE_PRIVATE)

        val isFirstTime = preferences.getBoolean(getString(R.string.sp_first_time),true)

        Log.i("SP", "${getString(R.string.sp_first_time)} = $isFirstTime")

        if(isFirstTime) {
            //Inflamos la vista del registro de usuario
            val dialogView = layoutInflater.inflate(R.layout.dialog_registrer, null)
            //Lanzamos dialogo para bienvenida
            /*
            MaterialAlertDialogBuilder(this)
                .setTitle(R.string.dialog_title)//Título del dialogo
                .setView(dialogView) //pasamos la vista que hemos inflado
                .setPositiveButton(R.string.dialog_confirm) { _, _ ->
                    //Dentro las llaves, se ejecuta la acción tras pulsar el botón de confirmar
                    //Extraer y almacenamos lo insertado en el editText
                    val username =
                        dialogView.findViewById<TextInputEditText>(R.id.etUserName).text.toString()
                    //Insertamos el dato en las preferencias, con función de alcance with()
                    with(preferences.edit()) {
                        putBoolean(getString(R.string.sp_first_time), false)
                        putString(getString(R.string.sp_username), username)
                            .apply()// Guarda los cambios de manera asíncrona
                    }
                    //Mostramos un mensaje de bienvenida con Toast
                    Toast.makeText(this,R.string.registrer_succes, Toast.LENGTH_SHORT).show()
                } //Botón de confirmación
                //.setNegativeButton("Cancelar", null) //Botón de cancelación, no queremos que se pueda cancelar
                .setCancelable(false)
                .setNeutralButton(R.string.dialog_invited, null) // Botón neutro para continuar de forma temporal
                .show()
             */

            //Controlar error al poner nombre en blanco o vacio

            val dialog = MaterialAlertDialogBuilder(this)
                .setTitle(R.string.dialog_title)//Título del dialogo
                .setView(dialogView) //pasamos la vista que hemos inflado
                .setPositiveButton(R.string.dialog_confirm) { _, _ -> }
                .setCancelable(false)
                .setNeutralButton(R.string.dialog_invited, null)
                .create()

            dialog.show()

            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
                //Dentro las llaves, se ejecuta la acción tras pulsar el botón de confirmar
                //Extraer y almacenamos lo insertado en el editText
                val username =
                    dialogView.findViewById<TextInputEditText>(R.id.etUserName).text.toString()
                if (username.isBlank()) {
                    Toast.makeText(this, R.string.registrer_invalid, Toast.LENGTH_SHORT).show()
                } else {
                    //Insertamos el dato en las preferencias, con función de alcance with()
                    with(preferences.edit()) {
                        putBoolean(getString(R.string.sp_first_time), false)
                        putString(getString(R.string.sp_username), username)
                            .apply()
                    }
                    Toast.makeText(this, R.string.registrer_succes, Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }
            }



        }else{
            val username = preferences.getString(
                getString(R.string.sp_username),
                getString(R.string.hint_username)
            )
            Toast.makeText(this, "Bienvenido $username", Toast.LENGTH_SHORT).show()
        }

        userApadter = UserAdapter(getUsers(), this)

        linearLayoutManager = LinearLayoutManager(this)

        binding.recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = linearLayoutManager
            adapter = userApadter
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //CALLBACK
        val swipeHelper = ItemTouchHelper( object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT){
            //Miembros de la interfaz
            //Permite reordenar un listado
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder, ): Boolean = false

            //Definimos el confortamiento
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                //Almacenamos el usuario que se va a eliminar
                val removedUser = userApadter.getUserAt(viewHolder.adapterPosition)

                //Mostramos el mensaje del usuario eliminado con un Toast
                Toast.makeText(binding.recyclerView.context, " ${removedUser.getFullname()} eliminado", Toast.LENGTH_SHORT).show()

                // Elimina el usuario
                userApadter.remove(viewHolder.adapterPosition)
            }
        }) //swipeDirs: direcciones hacia ls que se va a deslizar

        swipeHelper.attachToRecyclerView(binding.recyclerView)

    }

    private fun getUsers(): MutableList<User>{
        val users = mutableListOf<User>()

        val joel = User(1,"Joel","Pérez", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTCp4vT4Y8k-yPCfj9Bephc1CpW7tqrgZTBvw&s")
        val clara = User(2,"Clara", "Mena", "https://s3.animalia.bio/animals/photos/full/original/chat-des-sables-felis-margaritajpg.webp")
        val miguel = User(3,"Miguel","Mena", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcT-Mw0QOG_Xchc27OJ3eC-RnnaeClqicZfhSQ&s")
        val javier = User(4,"Javier", "Maroto", "https://pixnio.com/free-images/2021/09/14/2021-09-14-08-26-12-900x1350.jpg")

        users.add(joel)
        users.add(clara)
        users.add(miguel)
        users.add(javier)

        return users
    }
}