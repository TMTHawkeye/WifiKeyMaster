package com.project.wifikeymaster.Activities

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.os.Bundle
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.project.wifikeymaster.HelperClasses.PasswordGenerator
import com.project.wifikeymaster.databinding.ActivityGeneratePasswordBinding


class GeneratePasswordActivity : AppCompatActivity() {
    lateinit var binding: ActivityGeneratePasswordBinding

    var MINIMUM_PASS_LENGTH = 8
    var MAXIMUM_PASS_LENGTH = 24

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGeneratePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar!!.hide()

        binding.generatePass.setOnClickListener {
            if (binding.seekbarLength.progress >= MINIMUM_PASS_LENGTH && binding.seekbarLength.progress <= MAXIMUM_PASS_LENGTH) {
                if (binding.switchLower.isChecked || binding.switchUpper.isChecked || binding.switchSymbols.isChecked || binding.digit.isChecked) {
                    generatePassword()
                } else {
                    Toast.makeText(
                        this,
                        "Please check atleast one of above types",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(this, "Password length is too short", Toast.LENGTH_SHORT).show()
            }
        }

        binding.sharePass.setOnClickListener {
            if(!binding.tvPassword.text!!.isEmpty()) {
                sharePassword()
            }
            else{
                Toast.makeText(this, "Password have not generated yet!", Toast.LENGTH_SHORT).show()
            }
        }

        binding.copyPass.setOnClickListener {
            if(!binding.tvPassword.text!!.isEmpty()) {
                copyPassword()
            }
            else{
                Toast.makeText(this, "Password have not generated yet!", Toast.LENGTH_SHORT).show()
            }
        }

        binding.backBtn.setOnClickListener {
            finish()
        }



        binding.seekbarLength.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                var passwordLength: Int = binding.seekbarLength.getProgress()
                binding.TwLength.setText(" ($passwordLength)")
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
            }

        })


    }

    private fun copyPassword() {
        val clipboard: ClipboardManager =
            this.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val clip: ClipData = ClipData.newPlainText("Password", binding.tvPassword.text.toString())
        clipboard.setPrimaryClip(clip)
        Snackbar.make(binding.root, "Copied to Clipboard", Snackbar.LENGTH_SHORT).show()

    }

    private fun sharePassword() {
        val intent = Intent(Intent.ACTION_SEND)
        val shareBody = binding.tvPassword.text.toString()
        intent.setType("text/plain")
        intent.putExtra(
            Intent.EXTRA_SUBJECT, binding.tvPassword.text.toString()
        )
        intent.putExtra(Intent.EXTRA_TEXT, shareBody)
        startActivity(Intent.createChooser(intent, binding.tvPassword.text.toString()))
    }


    fun generatePassword() {
        var aX = (0 until 100).random()
        var aY = (0 until 100).random()
        var aZ = (0 until 100).random()


        val pg =
            PasswordGenerator(
                binding.switchLower.isChecked(),
                binding.digit.isChecked(),
                binding.switchUpper.isChecked(),
                binding.switchSymbols.isChecked()
            )
        val passwordLength: Int = MINIMUM_PASS_LENGTH + binding.seekbarLength.getProgress()
        val newPassword = pg.generate(passwordLength)

//        if (Math.abs(aX) > Math.abs(aY) && Math.abs(aX) > Math.abs(aZ)) {
            binding.tvPassword.setText(newPassword)
            Snackbar.make(binding.root, "Password Generated", Snackbar.LENGTH_SHORT).show()
//        }
    }
}