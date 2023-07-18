package com.colworx.soccorapp.ui.stream

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.amplifyframework.core.Amplify
import com.colworx.soccorapp.databinding.FragmentStreamBinding
import com.colworx.soccorapp.ui.DialogHelper
import java.io.File


class StreamFragment : Fragment() {

    private var _binding: FragmentStreamBinding? = null
    private var imageUrl: Uri? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentStreamBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.btnPick.setOnClickListener {
            pickImage()
        }

        binding.btnupload.setOnClickListener {
            uploadImage()
        }
        return root
    }

    private fun pickImage() {

        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
        //startActivityForResult()
    }

    private fun getFileName(): String {
        val filepathColumn = arrayOf(MediaStore.Images.Media.TITLE)

        var cursor = this.activity!!.contentResolver.query(
            imageUrl!!,
            filepathColumn, null, null, null)

        cursor!!.moveToFirst()
        val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.TITLE)
        cursor.moveToFirst()
        val fileName = cursor.getString(columnIndex)

        cursor!!.close()

        return fileName
    }

    private fun uploadImage() {
        //val exampleFile = File(applicationContext.filesDir, "ExampleKey")
        if (imageUrl != null) {
            val filepathColumn = arrayOf(MediaStore.Images.Media.DATA)

            var cursor = this.activity!!.contentResolver.query(
                imageUrl!!,
                filepathColumn, null, null, null)

            cursor!!.moveToFirst()
            val columnindex = cursor!!.getColumnIndex(filepathColumn[0])
            val picturepath = cursor!!.getString(columnindex)



            cursor!!.close()

            //var photoPath = picturepath
            //val filename =  imageUrl?.lastPathSegment ?: "exampleKey"
            val filename = getFileName() + ".jpg"
            val exampleFile = File(picturepath)
            //exampleFile.writeText("Example file contents")
            //Amplify.Storage.uploadfile
            Amplify.Storage.uploadFile(filename, exampleFile,
                { Log.i("MyAmplifyApp", "Successfully uploaded: ${it.key}")
                    DialogHelper.showToast(this.requireActivity()!!, "Successfully uploaded: ${it.key}")
                },
                { Log.e("MyAmplifyApp", "Upload failed", it)
                    DialogHelper.showToast(this.requireActivity()!!, "Upload failed: ${it.localizedMessage ?: "error"}")
                }
            )
        }

    }

    //handle result of picked image
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE){
            imageUrl = data?.data
            binding.imageview.setImageURI(data?.data)
        }
    }

    companion object {
        //image pick code
        private val IMAGE_PICK_CODE = 1000
        //Permission code
        private val PERMISSION_CODE = 1001
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}