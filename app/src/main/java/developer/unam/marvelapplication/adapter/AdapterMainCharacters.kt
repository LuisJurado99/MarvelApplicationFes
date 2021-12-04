package developer.unam.marvelapplication.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import developer.unam.marvelapplication.R
import developer.unam.marvelapplication.retrofit.characters.Result
import java.util.*

class AdapterMainCharacters(
    private val listCharacters: List<Result>,
    private val context: Context
) : RecyclerView.Adapter<AdapterMainCharacters.ViewHolder>(){
    private var listCharactersFilter: List<Result> = listCharacters

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById<ImageView>(R.id.imgCharacterRow)
        val name:TextView = itemView.findViewById<TextView>(R.id.tvNameCharacterRow)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(LayoutInflater.from(context).inflate(R.layout.row_characters, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listCharactersFilter[position]
        val urlImage = "${item.thumbnail.path}/landscape_incredible.${item.thumbnail.extension}"
        Log.e("imageAdapterMain","${item.name} imageResul $urlImage")
        Picasso.Builder(context).build().load(urlImage).into(holder.image)
        holder.name.text = item.name
    }

    override fun getItemCount(): Int = listCharacters.size


}