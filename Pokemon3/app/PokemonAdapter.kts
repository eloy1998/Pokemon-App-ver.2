package com.example.pokemon3

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class PokemonAdapter(private val pokemonList: List<Pokemon>) :
    RecyclerView.Adapter<PokemonAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivPokemonImage: ImageView = view.findViewById(R.id.ivPokemonImage)
        val tvPokemonName: TextView = view.findViewById(R.id.tvPokemonName)
        val tvPokemonType: TextView = view.findViewById(R.id.tvPokemonType)
        val tvPokemonDetail: TextView = view.findViewById(R.id.tvPokemonDetail)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pokemon, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pokemon = pokemonList[position]
        holder.tvPokemonName.text = pokemon.name
        holder.tvPokemonType.text = "Type: ${pokemon.type}"
        holder.tvPokemonDetail.text = "A Pokémon from the API!"

        Glide.with(holder.itemView.context)
            .load(pokemon.imageUrl)
            .into(holder.ivPokemonImage)
    }

    override fun getItemCount(): Int = pokemonList.size
}