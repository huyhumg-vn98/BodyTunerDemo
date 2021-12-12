package com.example.bodytunerdemo

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main3.*
import android.provider.MediaStore
import android.view.View


class Main3Activity : AppCompatActivity() {
    companion object{
        const val PICK_IMAGE = 1
        const val REQUEST_PICK_IMAGE = 110
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main3)

        pickImage.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Title"),PICK_IMAGE)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            var uri = data?.data
            val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
            CustomBodyTunerMagic.setBitmap(bitmap)
            pickImage.visibility = View.INVISIBLE
            CustomBodyTunerMagic.visibility = View.VISIBLE
        }
    }
}

