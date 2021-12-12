package g3.module.bodytunermagic

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.OutputStream

class FileUtilsEditor(private val context: Context, private val saveFileSuccess: (uri: String) -> Unit) {

    companion object{
        const val FORMAT_TYPE_JPG = "image/jpeg"
        const val FORMAT_TYPE_PNG = "image/png"
        const val FOLDER_SAVE_PHOTO = "testBody"
    }

    fun saveExternal(bitmap: Bitmap, fileName: String, extention: String, type: String, folderName: String, pathSuccessSave: (path: String) -> Unit) {
        Log.d("saveExternal", "folderName=$folderName")
        saveImageToStorage(bitmap, "$fileName$extention", type, Environment.DIRECTORY_PICTURES, folderName, MediaStore.Images.Media.EXTERNAL_CONTENT_URI) {
            val path = ""
            Log.d("saveExternal", "pathSuccessSave=$path")
            pathSuccessSave(it.path.toString())
        }
    }

    fun isThanQ(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
    }

//    fun getPathFolder(): String {
//        return "/storage/emulated/0/Pictures/${AppConst.FOLDER_SAVE_PHOTO}"
//    }

    fun getPathFolder(): String {
        return "${Environment.getExternalStorageDirectory()}${File.separator}${Environment.DIRECTORY_PICTURES}${File.separator}${FOLDER_SAVE_PHOTO}"
    }

    private fun saveImageFile(bitmap: Bitmap): String? {
        var out: FileOutputStream? = null

        val filename = getFilename()
        try {
            out = FileOutputStream(filename)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        return filename
    }

    private val SAVED_IMAGE_FORMAT = ".png";
    private val SAVED_IMAGE_NAME = System.currentTimeMillis().toString();
    private fun getFilename(): String {
        val file = File(context.cacheDir.toString())
        if (!file.exists()) {
            file.mkdirs()
        }
        val uriSting = (file.absolutePath + "/"
                + System.currentTimeMillis().toString() + SAVED_IMAGE_FORMAT)
        val files = File(uriSting)
        files.createNewFile()
        return uriSting
    }


    fun getPathCache(): String {
        val file = File(context.cacheDir.toString())
        return (file.absolutePath + "/"
                + SAVED_IMAGE_NAME + SAVED_IMAGE_FORMAT)

    }

    @Suppress("DEPRECATION")
    private fun saveImageToStorage(
            bitmap: Bitmap,
            filename: String = "screenshot.png",
            mimeType: String = "image/jpeg",
            directory: String = Environment.DIRECTORY_PICTURES,
            folderName: String = Environment.DIRECTORY_PICTURES,
            mediaContentUri: Uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            pathSuccess: (uri: Uri) -> Unit
    ) {
        val imageOutStream: OutputStream
        val uri: Uri
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Log.d("saveExternal", "filename=$filename")
            val values = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, filename)
                put(MediaStore.Images.Media.MIME_TYPE, mimeType)
                put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + File.separator + folderName)
            }

            context.contentResolver.run {
                uri = context.contentResolver.insert(mediaContentUri, values) ?: return
                imageOutStream = openOutputStream(uri) ?: return
            }
        } else {
            val imagePath = Environment.getExternalStoragePublicDirectory(directory).absolutePath + File.separator + folderName
            val image = File(imagePath, filename)
            imageOutStream = FileOutputStream(image)
            uri = Uri.fromFile(image)
        }

        if (mimeType == FORMAT_TYPE_JPG) {
            imageOutStream.use {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
            }
        } else if (mimeType == FORMAT_TYPE_PNG) {
            imageOutStream.use {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
            }
        }

        pathSuccess(uri)
    }
}