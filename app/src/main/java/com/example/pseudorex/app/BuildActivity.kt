package com.example.pseudorex.app

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.google.gson.JsonObject
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_build.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class BuildActivity : AppCompatActivity() {

    var Items = ArrayList <Items>()
    var Items3Price = ArrayList <Items>()
    var Boots = ArrayList <Items>()
    var Build = ArrayList<Items>()

    var versionApi : String = ""

    lateinit var utils : Utils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_build)

        setSupportActionBar(Toolbar2)

        //get data of champion selected from the main activity

        val championName = intent.getStringExtra("Champion_name")
        val championKey = intent.getStringExtra("Champion_key")
        versionApi = intent.getStringExtra("Champion_version")




        supportActionBar?.title = championName + " Build"

        //Setting the splash art image in the cardview
        //Picasso.get().load("http://ddragon.leagueoflegends.com/cdn/img/champion/loading/"+ championKey+ "_0.jpg" ).into(champion_splash_art)

        Picasso.get().load("http://ddragon.leagueoflegends.com/cdn/"+versionApi+"/img/champion/"+ championKey+ ".png" )
            .fit()
            .centerCrop()
            .into(champion_splash_art)

        //Call the Json File

        //Setting the Api Library
        var apiInterface = ApiClient().getApiLibrary()!!.create(INetworkAPI::class.java)

        //Get the Actual version of the game api
        var version = apiInterface.getVersion()

        version.enqueue(object : Callback<Version> {
            override fun onFailure(call: Call<Version>, t: Throwable) {
                Toast.makeText(this@BuildActivity, "Connection Error, please review if Internet is available", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<Version>, response: Response<Version>) {
                var versionNumber = response.body()?.n?.itemVersion.toString()

                //Get the List of Items with the version number
                var json = apiInterface.getAllItems(versionNumber)

                json.enqueue(object : Callback<JsonObject> {
                    override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                        Toast.makeText(this@BuildActivity, "Connection Error, please review if Internet is available", Toast.LENGTH_SHORT).show()
                    }

                    override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                        val responseJson = JSONObject(response.body().toString() )

                        //Loop in the json data for get all items
                        val itemObjectJson = responseJson.getJSONObject("data")
                        val itemIterator = itemObjectJson.keys()

                        while (itemIterator.hasNext() )
                        {
                            //Get all items and storing in a list
                            var key = itemIterator.next()

                            //Get the main list of items
                            Items.add(
                                Items(
                                    key = key ,
                                    name = itemObjectJson.getJSONObject(key).getString("name"),
                                    plaintext = itemObjectJson.getJSONObject(key).getString ("plaintext"),
                                    gold = itemObjectJson.getJSONObject(key).getJSONObject("gold").getString("total")
                                )
                            )

                        }

                        //Filling the data lists

                        utils = Utils(Items)
                        Items3Price = utils.makeList3Items()
                        Boots = utils.makeBootsList()


                        //Get the final build
                        var buildClass = BuildGenerator (Boots,Items3Price)
                        Build = buildClass.generateBuild()


                        //Finally show the items

                        setItems()

                    }

                })

            }

        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.toolbar_build, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.toolbar_renew -> {

            //Remake a new list and verify if already searched
            if (Boots.isEmpty() == false && Items3Price.isEmpty() == false) {

                var buildClass = BuildGenerator (Boots,Items3Price)
                Build = buildClass.generateBuild()

                this.setItems()

            }


            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    fun setItems (){

        Picasso.get().load("http://ddragon.leagueoflegends.com/cdn/"+ versionApi+"/img/item/"+ Build[0].key+ ".png" ).into(champion_item_1)
        Picasso.get().load("http://ddragon.leagueoflegends.com/cdn/"+ versionApi+"/img/item/"+ Build[1].key+ ".png" ).into(champion_item_2)
        Picasso.get().load("http://ddragon.leagueoflegends.com/cdn/"+ versionApi+"/img/item/"+ Build[2].key+ ".png" ).into(champion_item_3)
        Picasso.get().load("http://ddragon.leagueoflegends.com/cdn/"+ versionApi+"/img/item/"+ Build[3].key+ ".png" ).into(champion_item_4)
        Picasso.get().load("http://ddragon.leagueoflegends.com/cdn/"+ versionApi+"/img/item/"+ Build[4].key+ ".png" ).into(champion_item_5)
        Picasso.get().load("http://ddragon.leagueoflegends.com/cdn/"+ versionApi+"/img/item/"+ Build[5].key+ ".png" ).into(champion_item_6)
    }



}

class Utils (val ItemList : ArrayList<Items> ){

    var newItems = ArrayList<Items>()

    //Fun to get the items of 3 category
    fun makeList3Items() : ArrayList<Items> {

        //Get all items
        var key = ItemList.iterator()

        for (i in key){
            if (i.gold.toInt() >= 1800 ) {
                newItems.add(
                    Items(
                        key = i.key ,
                        name = i.name,
                        plaintext = i.plaintext,
                        gold = i.gold
                    )
                )
            }

        }

        //Finally returns the main list for builds
        return newItems
    }

    fun makeBootsList() : ArrayList<Items> {

        var newItems = ArrayList<Items>()

        //Get all items
        var key = ItemList.iterator()

        //Check if the items are a boots
        for (i in key){
            if (i.name.contains("Boots") ) {
                newItems.add(
                    Items(
                        key = i.key ,
                        name = i.name,
                        plaintext = i.plaintext,
                        gold = i.gold
                    )
                )
            }

        }

        return newItems
    }


}

class BuildGenerator (val bootsList : ArrayList<Items>, val itemList : ArrayList<Items>){
    var finalBuild = ArrayList<Items>()

    fun generateBuild (): ArrayList<Items> {
        Collections.shuffle(itemList)

        var count = 0;

        var iterator = itemList.iterator()

        for (i in iterator ) {

            //Only get 5 items
            if (count <5) {

                addItem( i )
                count++;
            }
        }

        //get the random boot

        Collections.shuffle(bootsList)

        addItem(bootsList[1])


        return finalBuild;
    }

    fun addItem (items : Items) {
        finalBuild.add(
            Items(
                key = items.key ,
                name = items.name,
                plaintext = items.plaintext,
                gold = items.gold
            )
        )
    }
}