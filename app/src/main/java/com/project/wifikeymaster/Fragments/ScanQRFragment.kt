package com.project.wifikeymaster.Fragments

import android.content.ContentValues
import android.content.Context.WINDOW_SERVICE
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Point
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.widget.Toast
import androidmads.library.qrgenearator.QRGContents
import androidmads.library.qrgenearator.QRGEncoder
import androidx.fragment.app.Fragment
import com.google.zxing.WriterException
import com.project.wifikeymaster.databinding.FragmentScanQRBinding
import java.io.OutputStream


class ScanQRFragment : Fragment() {

    lateinit var binding: FragmentScanQRBinding
    var bitmap: Bitmap? = null
    var qrgEncoder: QRGEncoder? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentScanQRBinding.inflate(layoutInflater, container, false)


        binding.cardGenerateQr.setOnClickListener {
            if(!binding.typedPassword.text!!.isEmpty()) {
                //generate qr for typed password
                generateQR()
            }
            else{
                Toast.makeText(requireContext(), "Password cannot be empty!", Toast.LENGTH_SHORT).show()
            }
        }

        binding.sharecode.setOnClickListener{
            //share qr code intent
           shareQRCode()

        }
        // Inflate the layout for this fragment
        return binding.root
    }

    private fun shareQRCode() {
        val share = Intent(Intent.ACTION_SEND)
        share.type = "image/jpeg"

        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "title")
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        val uri: Uri = requireContext().getContentResolver().insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            values
        )!!

        val outstream: OutputStream
        try {
            outstream = requireContext().getContentResolver().openOutputStream(uri)!!
            bitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, outstream)
            outstream.close()
        } catch (e: Exception) {
            System.err.println(e.toString())
        }

        share.putExtra(Intent.EXTRA_STREAM, uri)
        startActivity(Intent.createChooser(share, "Share Image"))
    }


    fun generateQR(){
        binding.linearImg.visibility = View.VISIBLE
        binding.sharecode.visibility = View.VISIBLE
        val manager: WindowManager? =
            requireContext().getSystemService(WINDOW_SERVICE) as WindowManager?
        val display: Display = manager!!.getDefaultDisplay()
        val point = Point()
        display.getSize(point)
        val width: Int = point.x
        val height: Int = point.y
        var dimen = if (width < height) width else height
        dimen = dimen * 3 / 4
        qrgEncoder = QRGEncoder(
            binding.typedPassword.getText().toString(),
            null,
            QRGContents.Type.TEXT,
            dimen
        )
        try {
            bitmap = qrgEncoder!!.encodeAsBitmap()
            binding.imgQrcode.setImageBitmap(bitmap)  //set bitmap of generated qr code on image view

            binding.cardGenerateInfo.visibility = View.GONE
            binding.cardGenerateQr.visibility = View.GONE
        } catch (e: WriterException) {
            Log.e("Tag", e.toString())
        }

    }


}
