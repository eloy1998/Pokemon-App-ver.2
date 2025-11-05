package com.example.pokemon3

import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.JsonHttpResponseHandler
import cz.msebera.android.httpclient.Header
import org.json.JSONObject
import java.util.Collections

class MainActivity : AppCompatActivity() {

    private lateinit var rvPokemon: RecyclerView
    private lateinit var searchBar: EditText
    private lateinit var adapter: PokemonAdapter
    private val pokemonList = mutableListOf<Pokemon>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rvPokemon = findViewById(R.id.rvPokemon)
        searchBar = findViewById(R.id.searchPokemon)

        rvPokemon.layoutManager = LinearLayoutManager(this)
        // Add item dividers
        rvPokemon.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))

        adapter = PokemonAdapter()
        rvPokemon.adapter = adapter

        fetchPokemonList()

        // 🔍 Search feature
        searchBar.addTextChangedListener { text ->
            val query = text.toString().lowercase()
            val filteredList = pokemonList.filter { p -> p.name.lowercase().contains(query) }
            adapter.submitList(filteredList)
            if (filteredList.isEmpty() && pokemonList.isNotEmpty()) {
                Toast.makeText(this, "No Pokémon found!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchPokemonList() {
        val client = AsyncHttpClient()
        val url = "https://pokeapi.co/api/v2/pokemon?limit=30"

        client.get(url, object : JsonHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<out Header>?, response: JSONObject) {
                try {
                    val results = response.getJSONArray("results")
                    val fetchedPokemon = Collections.synchronizedList(mutableListOf<Pokemon>())

                    if (results.length() == 0) return

                    pokemonList.clear()

                    for (i in 0 until results.length()) {
                        val pokemonJSON = results.getJSONObject(i)
                        val pokemonName = pokemonJSON.getString("name")
                        val detailUrl = pokemonJSON.getString("url")

                        val detailClient = AsyncHttpClient()
                        detailClient.get(detailUrl, object : JsonHttpResponseHandler() {
                            private fun addPokemonToList(pokemon: Pokemon) {
                                fetchedPokemon.add(pokemon)
                                if (fetchedPokemon.size == results.length()) {
                                    pokemonList.addAll(fetchedPokemon.sortedBy { it.name })
                                    adapter.submitList(pokemonList.toList())
                                }
                            }

                            override fun onSuccess(detailStatusCode: Int, detailHeaders: Array<out Header>?, detailResponse: JSONObject) {
                                try {
                                    val typesArray = detailResponse.getJSONArray("types")
                                    val typeName = if (typesArray.length() > 0) {
                                        typesArray.getJSONObject(0).getJSONObject("type").getString("name").replaceFirstChar { it.uppercase() }
                                    } else {
                                        "Unknown"
                                    }
                                    val imageUrl = "https://img.pokemondb.net/artwork/$pokemonName.jpg"

                                    val speciesUrl = detailResponse.getJSONObject("species").getString("url")
                                    val speciesClient = AsyncHttpClient()
                                    speciesClient.get(speciesUrl, object : JsonHttpResponseHandler() {
                                        override fun onSuccess(speciesStatusCode: Int, speciesHeaders: Array<out Header>?, speciesResponse: JSONObject) {
                                            try {
                                                val flavorTextEntries = speciesResponse.getJSONArray("flavor_text_entries")
                                                var description = "No description available."
                                                for (j in 0 until flavorTextEntries.length()) {
                                                    val flavorTextEntry = flavorTextEntries.getJSONObject(j)
                                                    if (flavorTextEntry.getJSONObject("language").getString("name") == "en") {
                                                        description = flavorTextEntry.getString("flavor_text").replace("\n", " ").replace("\u000c", " ")
                                                        break
                                                    }
                                                }
                                                addPokemonToList(Pokemon(pokemonName, imageUrl, typeName, description))
                                            } catch (e: Exception) {
                                                Log.e("MainActivity", "Error parsing species for $pokemonName", e)
                                                addPokemonToList(Pokemon(pokemonName, imageUrl, typeName, "Description not found."))
                                            }
                                        }

                                        override fun onFailure(speciesStatusCode: Int, speciesHeaders: Array<out Header>?, throwable: Throwable?, speciesErrorResponse: JSONObject?) {
                                            Log.e("MainActivity", "Failed to fetch species for $pokemonName", throwable)
                                            addPokemonToList(Pokemon(pokemonName, imageUrl, typeName, "Description not found."))
                                        }
                                    })
                                } catch (e: Exception) {
                                    Log.e("MainActivity", "Error parsing details for $pokemonName", e)
                                    addPokemonToList(Pokemon(pokemonName, "https://img.pokemondb.net/artwork/$pokemonName.jpg", "Unknown", "Description not found."))
                                }
                            }

                            override fun onFailure(detailStatusCode: Int, detailHeaders: Array<out Header>?, throwable: Throwable?, detailErrorResponse: JSONObject?) {
                                Log.e("MainActivity", "Failed to fetch details for $pokemonName", throwable)
                                addPokemonToList(Pokemon(pokemonName, "https://img.pokemondb.net/artwork/$pokemonName.jpg", "Unknown", "Description not found."))
                            }
                        })
                    }
                } catch (e: Exception) {
                    Log.e("MainActivity", "Error parsing initial Pokemon list", e)
                    Toast.makeText(this@MainActivity, "Error parsing Pokémon list: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(statusCode: Int, headers: Array<Header>?, throwable: Throwable?, errorResponse: JSONObject?) {
                Log.e("MainActivity", "Failed to fetch Pokémon list", throwable)
                Toast.makeText(this@MainActivity, "Failed to fetch Pokémon list", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
