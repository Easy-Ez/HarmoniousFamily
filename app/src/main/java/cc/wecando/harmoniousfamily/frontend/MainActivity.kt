package cc.wecando.harmoniousfamily.frontend

import android.content.Context
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import cc.wecando.harmoniousfamily.Global.PREFERENCE_NAME_DEVELOPER
import cc.wecando.harmoniousfamily.Global.PREFERENCE_NAME_SETTINGS
import cc.wecando.harmoniousfamily.R
import cc.wecando.harmoniousfamily.databinding.ActivityMainBinding
import cc.wecando.harmoniousfamily.frontend.fragments.EnvStatusFragment
import cc.wecando.harmoniousfamily.frontend.fragments.PrefFragment
import cc.wecando.harmoniousfamily.utils.LocaleUtil
import com.google.android.material.navigation.NavigationView


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private val bind by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(LocaleUtil.onAttach(base))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(bind.root)


        val toggle = object : ActionBarDrawerToggle(
            this,
            bind.drawerLayout,
            bind.appBarMain.toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        ) {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                super.onDrawerSlide(drawerView, slideOffset)
            }

            override fun onDrawerOpened(drawerView: View) {
                super.onDrawerOpened(drawerView)
                super.onDrawerSlide(drawerView, 0F)
            }
        }
        bind.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()


        bind.navView.setNavigationItemSelectedListener(this)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, EnvStatusFragment.newInstance())
                .commit()
        }
    }

    override fun onBackPressed() {
        if (bind.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            bind.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val fragment = when (item.itemId) {
            R.id.nav_status -> {
                EnvStatusFragment.newInstance()
            }
            R.id.nav_settings -> {
                PrefFragment.newInstance(R.xml.pref_settings, PREFERENCE_NAME_SETTINGS)
            }
            R.id.nav_developer -> {
                PrefFragment.newInstance(R.xml.pref_developer, PREFERENCE_NAME_DEVELOPER)
            }
            else ->
                throw Error("Unknown navigation item: ${item.itemId}")
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.main_container, fragment)
            .commit()

        bind.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}