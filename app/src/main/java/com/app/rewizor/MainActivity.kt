package com.app.rewizor

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.app.rewizor.exstension.replaceFragment
import com.app.rewizor.ui.TopicTabFragment
import com.app.rewizor.ui.utils.TOPIC
import com.app.rewizor.ui.utils.TOPIC_KEY
import com.app.rewizor.viewmodel.StartViewModel
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.toolbar.*
import org.koin.android.ext.android.inject
import org.koin.core.KoinComponent

class MainActivity : AppCompatActivity(),KoinComponent,  NavigationView.OnNavigationItemSelectedListener {

    private val viewModel: StartViewModel by inject()
    var toolbarTitle: CharSequence
        get() = toolbar.title
        set(value) {
            toolbar.title = value
        }
    private lateinit var drawer: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setNavigation()
        replaceFragment(fragment = TopicTabFragment.getInstance(
            Bundle().apply { putString(TOPIC_KEY, TOPIC.MAIN.title) }
        ))
        supportActionBar?.title = TOPIC.MAIN.title
    }

    private fun setNavigation() {
        setSupportActionBar(toolbar)
        toolbar.setNavigationIcon(R.drawable.ic_menu)

        drawer = drawer_layout

        toggle = ActionBarDrawerToggle(
            this,
            drawer,
            toolbar,
            R.string.navOpen,
            R.string.navClose
        )
        drawer_layout.addDrawerListener(toggle)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        supportActionBar?.setHomeButtonEnabled(false)
/*
        var drawer: DrawerLayout = drawer_layout
        var toggle: ActionBarDrawerToggle = ActionBarDrawerToggle(
            this,drawer,toolbar,, 2)
        )
*/
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        //        var categoryId = item.itemId
   //     toolbarTitle = item.title
   //     supportActionBar?.title = item.title
        var drawer: DrawerLayout = drawer_layout
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    companion object {
        const val FRAGMENT_CONTAINER = R.id.fr_container
    }
}
