package developer.unam.marvelapplication.retrofit

import android.media.MediaMetadataRetriever
import developer.unam.marvelapplication.retrofit.characters.CharacterResponse
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.QueryMap


interface IRetrofitLink {
     @GET("characters")
     fun getCharacterTotal(@QueryMap params: Map<String,String>): Call<CharacterResponse>

     companion object{
        val urlBase = "https://gateway.marvel.com/v1/public/"
        val retrofit = Retrofit.Builder()
            .baseUrl(urlBase)
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(IRetrofitLink::class.java)
     }

}