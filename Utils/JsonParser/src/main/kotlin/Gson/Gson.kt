package Gson

import com.google.gson.Gson
import com.google.gson.GsonBuilder

class Gson {
    companion object{
        fun getGson() : Gson {
            return GsonBuilder().setPrettyPrinting().create()
        }
    }
}