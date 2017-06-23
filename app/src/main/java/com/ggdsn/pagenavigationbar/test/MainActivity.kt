package com.ggdsn.pagenavigationbar.test

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bar1.setTitles(listOf("PageNavigationBar A", "PageNavigationBar B"))
        bar2.setTitles(listOf("Status AA", "Status BB"), 1)
    }
}
