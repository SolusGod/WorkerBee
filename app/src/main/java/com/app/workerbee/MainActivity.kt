package com.app.workerbee

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.app.workerbee.databinding.ActivityMainBinding
import org.w3c.dom.Text

class MainActivity : AppCompatActivity() {


    var list = ArrayList<Int>()
    var i: Int = 5 //Int i = 5
    var j = 2


    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tv.text = "sdfsd f"

        val newText = binding.tv.text

        i = 5
        j = 5

        list.add(1)
        list.add(2)
        list.add(3)

        for (i in list) {
            print(i)
        }

    }
}