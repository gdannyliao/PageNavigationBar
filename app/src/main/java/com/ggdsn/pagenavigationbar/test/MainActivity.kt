package com.ggdsn.pagenavigationbar.test

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bar1.setTitles(listOf("A", "B"), 1)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.left -> bar1.previousStep()
            R.id.right -> bar1.nextStep()
        }
    }
}
