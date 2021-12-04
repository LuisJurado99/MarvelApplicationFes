package developer.unam.marvelapplication.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import developer.unam.marvelapplication.R
import developer.unam.marvelapplication.adapter.AdapterMainCharacters
import developer.unam.marvelapplication.databinding.ActivityMainBinding
import developer.unam.marvelapplication.retrofit.IRetrofitLink
import developer.unam.marvelapplication.retrofit.characters.CharacterResponse
import developer.unam.marvelapplication.utils.MdCreate
import developer.unam.marvelapplication.utils.ShareUtil
import okhttp3.internal.Util
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var limit = 0
    private lateinit var apter: AdapterMainCharacters

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        title = "Heroes"
        updateRecycler()
        binding.btnPrevPage.visibility = View.GONE
    }

    override fun onResume() {
        super.onResume()
        binding.btnNextPage.setOnClickListener {
            limit += 100
            binding.btnPrevPage.visibility = View.VISIBLE
            updateRecycler(limit)
        }
        binding.btnPrevPage.setOnClickListener {
            limit -= 100
            binding.btnPrevPage.visibility =
                if(limit==0)
                    View.GONE
                else
                    View.VISIBLE

            updateRecycler(limit)
        }


    }

    private fun updateRecycler(limit: Int = 0, initName: String = "") {
        binding.pbMain.visibility = View.VISIBLE
        val map = MdCreate(this).addParams()
        map["limit"] = 100.toString()
        map["offset"] = limit.toString()
        if (initName.isNotEmpty())
            map["nameStartsWith"] = initName
        val api = IRetrofitLink.retrofit.getCharacterTotal(map)
        api.enqueue(object : Callback<CharacterResponse> {
            override fun onResponse(
                call: Call<CharacterResponse>,
                response: Response<CharacterResponse>
            ) {
                binding.pbMain.visibility = View.GONE
                val resultCharacters = response.body()?.data?.results
                binding.btnNextPage.visibility = if (response.body()?.data?.count ?: 0 < 100)
                    View.GONE
                else
                    View.VISIBLE

                if (resultCharacters != null) {
                    if (resultCharacters.isEmpty()) {
                        val material = MaterialAlertDialogBuilder(this@MainActivity)
                        material.setTitle(title)
                        material.setMessage("Not elements show")
                        material.setNeutralButton(android.R.string.ok) { dialog, _ ->
                            dialog.dismiss()
                            updateRecycler(limit)
                        }
                        material.create().show()
                    }
                    binding.pbMain.visibility = View.GONE
                    apter = AdapterMainCharacters(resultCharacters, this@MainActivity)
                    val manager = GridLayoutManager(binding.rvCharacterMain.context, 2)
                    binding.rvCharacterMain.adapter = apter
                    binding.rvCharacterMain.layoutManager = manager
                    binding.rvCharacterMain.scrollToPosition(0)
                } else {
                    val material = MaterialAlertDialogBuilder(this@MainActivity)
                    material.setTitle(title)
                    material.setMessage("Not elements show")
                    material.setNeutralButton(android.R.string.ok) { dialog, _ -> dialog.dismiss() }
                    material.create().show()
                }
            }

            override fun onFailure(call: Call<CharacterResponse>, t: Throwable) {
                Log.e("errorResponse", "err ${t.message}")
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_search, menu)
        val itemMenu = menu?.findItem(R.id.menu_search_item)
        val search = itemMenu?.actionView as SearchView
        search.queryHint = "Filtrar heroes en esta seccion"
        search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                updateRecycler(limit, query.toString())
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if ((newText ?: "").isEmpty()) {
                    updateRecycler(limit)

                }
                return false
            }

        })
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.logout) {
            Firebase.auth.signOut()
            startActivity(Intent(this, OAuthActivity::class.java))
            ShareUtil(this).clear()
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (backPressed + 2000 > System.currentTimeMillis()) {
            val intent = Intent(Intent.ACTION_MAIN)
            intent.addCategory(Intent.CATEGORY_HOME)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        } else {
            Toast.makeText(this, "Presiona para de nuevo para salir", Toast.LENGTH_SHORT)
                .show()
        }
        backPressed = System.currentTimeMillis()
    }

    companion object {
        private var backPressed: Long = 0
    }


}