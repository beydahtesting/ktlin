package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

abstract class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Child activities must call setContentView() before calling setUpToolbar()
    }

    protected fun setUpToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar?.let {
            setSupportActionBar(it)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the forward arrow menu only if getForwardIntent() returns non-null.
        if (getForwardIntent() != null) {
            menuInflater.inflate(R.menu.menu_toolbar, menu)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            R.id.action_forward -> {
                getForwardIntent()?.let { startActivity(it) }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * Override in child activities if a forward arrow is needed.
     * Return a non‑null Intent to enable the forward arrow.
     */
    protected open fun getForwardIntent(): Intent? = null
}
