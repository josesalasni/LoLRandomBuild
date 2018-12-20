package com.example.pseudorex.app

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.*
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.google.gson.JsonObject
import org.json.JSONObject


class MainActivity : AppCompatActivity()  {

    var Champions = ArrayList <Champions>()
    var ChampionListSearch = ArrayList<Champions>()

    lateinit var ViewAdapter : ViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(mainToolbar)

        //Setting the Api Library
        var apiInterface = ApiClient().getApiLibrary()!!.create(INetworkAPI::class.java)

        //Get the Actual version of the game api
        var version = apiInterface.getVersion()

        version.enqueue(object : Callback<Version> {
            override fun onFailure(call: Call<Version>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Connection Error, please review if Internet is available", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<Version>, response: Response<Version>) {
                var versionNumber = response.body()?.n?.championVersion.toString()

                //Get the List of champions with the version number
                var json = apiInterface.getAllChampions(versionNumber)

                json.enqueue(object : Callback<JsonObject> {
                    override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                        Toast.makeText(this@MainActivity, "Connection Error, please review if Internet is available", Toast.LENGTH_SHORT).show()
                    }

                    override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                        val responseJson = JSONObject(response.body().toString() )

                        //Loop in the json data
                        val championObjectJson = responseJson.getJSONObject("data")
                        val championIterator = championObjectJson.keys()

                        while (championIterator.hasNext() )
                        {
                            //Get the individual champs and storing in a list
                            var key = championIterator.next()

                            //Get the list of champions
                            Champions.add(
                                Champions(
                                    key = key ,
                                    name = championObjectJson.getJSONObject(key).getString("name"))
                            )

                            ChampionListSearch.add(
                                Champions(
                                    key = key ,
                                    name = championObjectJson.getJSONObject(key).getString("name"))
                            )
                        }

                        //Filling the recycler View
                        ViewAdapter = ViewAdapter(ChampionListSearch,versionNumber, this@MainActivity)

                        Recycler.adapter = ViewAdapter
                        //Recycler.layoutManager = GridLayoutManager (this@MainActivity, GridLayoutManager.DEFAULT_SPAN_COUNT, false)
                        Recycler.layoutManager = GridLayoutManager(this@MainActivity, 3)

                    }

                })
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.toolbar_search -> {
            // do stuff
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.toolbar, menu)

        val searchItem = menu.findItem(R.id.toolbar_search)

        var searchView = searchItem.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextChange(newText: String): Boolean {

                var text = newText.toLowerCase()
                ChampionListSearch.clear()

                if (Champions.isEmpty() == false)
                {
                    for(i in Champions.indices) {
                        if (Champions[i].name.toLowerCase().contains( text)  ){
                            ChampionListSearch.add(
                                Champions(
                                    key = Champions[i].key ,
                                    name = Champions[i].name
                                )
                            )
                        }
                    }

                    //ViewAdapter.updateList(ChampionListSearch)
                    ViewAdapter.notifyDataSetChanged()
                }

                return false
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                // task HERE
                return false
            }

        })

        return true
    }
}

