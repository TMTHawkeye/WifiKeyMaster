package com.project.wifikeymaster.Activities

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.project.wifikeymaster.Fragments.HomeFragment
import com.project.wifikeymaster.Fragments.SavePasswordFragment
import com.project.wifikeymaster.Fragments.ScanQRFragment
import com.project.wifikeymaster.R
import com.project.wifikeymaster.databinding.ActivityMainBinding
import com.simform.custombottomnavigation.Model
import io.paperdb.Paper

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar!!.hide()

        Paper.init(this)

        setBottomNavBar(savedInstanceState)
    }

    private fun setBottomNavBar(savedInstanceState: Bundle?) {
        val activeIndex = savedInstanceState?.getInt("activeIndex") ?: 1

        binding.bottomNavigation.apply {

            setSelectedIndex(activeIndex)

            add(
                Model(
                    icon = R.drawable.save_pass_svg,
                    id = 0,
                    text = R.string.savePass,
                )
            )
            add(
                Model(
                    icon = R.drawable.home_icon_svg,
                    id = 1,
                    text = R.string.home,
                    count = R.string.empty_value
                )
            )
            add(
                Model(
                    R.drawable.qr_code_svg,
                    id = 2,
                    text = R.string.qrcode,
                    count = R.string.empty_value
                )
            )

            showFragment("Home")


            setOnClickMenuListener {
                val name = when (it.id) {
                    1 -> showFragment("Home")
                    0 -> showFragment("Save Password")
                    2 -> showFragment("QR Code")
                    else -> showFragment("Home")
                }
            }

            setOnReselectListener {
                when (it.id) {
                    0 -> {
                        Toast.makeText(
                            context,
                            "You are already on Saved Passwords screen",
                            Toast.LENGTH_LONG
                        ).show()

                    }
                    1 -> {
                        Toast.makeText(context, "You are already on home screen", Toast.LENGTH_LONG)
                            .show()

                    }
                    2 -> {
                        Toast.makeText(
                            context,
                            "You are already on Wifi QR Code screen",
                            Toast.LENGTH_LONG
                        ).show()

                    }
                }
            }

        }
    }

    fun showFragment(displayString: String) {
        lateinit var fragment: Fragment
        when (displayString) {
            "Home" -> {
                fragment = HomeFragment.newInstance(displayString)
            }
            "Save Password" -> {
                fragment = SavePasswordFragment()
            }
            "QR Code" -> {
                fragment = ScanQRFragment()
            }
            else -> fragment = HomeFragment.newInstance(displayString)

        }
        val transaction = supportFragmentManager.beginTransaction()
        transaction
            .replace(R.id.nav_host_fragment, fragment)
            .commit()
    }

    override fun onBackPressed() {
        popUp(this)
    }

    fun popUp(context: Activity) {
        val dialog = Dialog(context, R.style.MyAlertDialogTheme)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.backpress_dialogue)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent);
        val exitBtn: CardView = dialog.findViewById(R.id.exitbtn)
        val rateBtn: CardView = dialog.findViewById(R.id.ratenow)
        exitBtn.setOnClickListener { v: View? ->
            dialog.dismiss()
            context.finishAffinity()
        }
        rateBtn.setOnClickListener { v: View ->
            dialog.dismiss()
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName")))
        }
        dialog.show()
    }
}
