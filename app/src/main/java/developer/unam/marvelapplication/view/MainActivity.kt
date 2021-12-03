package developer.unam.marvelapplication.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.gson.Gson
import developer.unam.marvelapplication.R
import developer.unam.marvelapplication.adapter.AdapterMainCharacters
import developer.unam.marvelapplication.databinding.ActivityMainBinding
import developer.unam.marvelapplication.retrofit.IRetrofitLink
import developer.unam.marvelapplication.retrofit.characters.CharacterResponse
import developer.unam.marvelapplication.utils.MdCreate
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity() {
    private lateinit var binding:ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        val api = IRetrofitLink.retrofit.getCharacterTotal(MdCreate(this).addParams())
        api.enqueue(object : Callback<CharacterResponse> {
            override fun onResponse(
                call: Call<CharacterResponse>,
                response: Response<CharacterResponse>
            ) {
                val resultCharacters = response.body()?.data?.results
                Log.e("result",Gson().toJson(resultCharacters))
                if(resultCharacters != null){
                    val adaper = AdapterMainCharacters(resultCharacters,this@MainActivity)
                    val manager = GridLayoutManager(binding.rvCharacterMain.context,2)
                    binding.rvCharacterMain.adapter = adaper
                    binding.rvCharacterMain.layoutManager = manager
                }


            }

            override fun onFailure(call: Call<CharacterResponse>, t: Throwable) {
                Log.e("errorResponse","err ${t.message}")
            }
        })

    }
}