package com.example.pokedexv2

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.GsonBuilder
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_type_page.*
import kotlinx.android.synthetic.main.row_layout.view.*
import okhttp3.*
import java.io.IOException
import java.util.*
import java.util.logging.Handler

class TypePageActivity : AppCompatActivity() {
    var spUrl: String = ""
    lateinit var load : LoadingAnimation
    val pokemonsList =
        PokemonList(
            mutableListOf(
                Result(
                    "name",
                    "url",
                    "spurl"
                )
            )
        )



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_type_page)
        val pokeUrl = intent.getStringExtra(CustomViewHolder.PokemonUrl)
        val navbarTitle = intent.getStringExtra(CustomViewHolder.PokemonName)
        supportActionBar?.title = navbarTitle
        PokemonTypeList.layoutManager = LinearLayoutManager(this@TypePageActivity)

        fetchPokemons(pokeUrl)



    }

    fun fetchPokemons(pokemonUrl: String) {
        println("Connecting")

        val url = pokemonUrl

        val request = Request.Builder().url(url).build()

        val client = OkHttpClient()
        client.newCall(request).enqueue(object: Callback {
            override fun onResponse(call: Call, response: Response) {
                val body =  response.body?.string()


                val gson = GsonBuilder().create()

                val pokemons = gson.fromJson(body, PokemonType::class.java)

                pokemonsList.results.clear()



                for(i in pokemons.pokemon){


                    fetchPokemonImg(i.pokemon.url){
                            SpriteUrl ->

                        if(!SpriteUrl.sprites.front_default.isNullOrEmpty()) {
                            spUrl = SpriteUrl.sprites.front_default
                        }else{
                            spUrl = "https://lh3.googleusercontent.com/proxy/-V-7GBLtcCHZWnIIzjXzBNPlCzoHjlqkJoGB_UhTmXNdQ6WdIzqjWKpY9NX_jxhzDY-beFQhmrbzUz_ogYa3LGac0FVqYZDC"
                        }

                        pokemonsList.results.add(
                            Result(
                                i.pokemon.name,
                                i.pokemon.url,
                                spUrl
                            )
                        )
                        runOnUiThread{
                            loading.visibility = View.GONE
                            val adapter = MainAdapter(pokemonsList,this@TypePageActivity,1)
                            PokemonTypeList.adapter = adapter

                        }
                    }
                }

            }
            override fun onFailure(call: Call, e: IOException) {
                println("Failed")
            }

        })

    }


    fun fetchPokemonImg(pokemonUrl: String, resultHandler: (Pokemon) -> Unit) {
        println("Connecting")

        val url = pokemonUrl

        val request = Request.Builder().url(url).build()

        val client = OkHttpClient()
        client.newCall(request).enqueue(object: Callback {
            override fun onResponse(call: Call, response: Response) {
                val body =  response.body?.string()
                //println(body)

                val gson = GsonBuilder().create()

                resultHandler(gson.fromJson(body, Pokemon::class.java))

            }

            override fun onFailure(call: Call, e: IOException) {
                println("Failed")
            }

        })

    }

}


