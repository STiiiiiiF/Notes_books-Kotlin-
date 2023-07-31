package com.zakharov.lessonsqlkotlin

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.zakharov.lessonsqlkotlin.databinding.EditActivityBinding
import com.zakharov.lessonsqlkotlin.db.MyDbManager
import com.zakharov.lessonsqlkotlin.db.MyIntentConstants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class EditActivity : AppCompatActivity() {

    var id = 0
    var isEditState = false
    private var launcher: ActivityResultLauncher<Intent>? = null
    private lateinit var binding: EditActivityBinding
    private var tempImageUri = "empty"
    private val myDbManager = MyDbManager(this)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = EditActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        onClick()
        startLauncher()
        getMyIntents()
        Log.d("MyLog", "Time: ${getCurrentTime()}")
    }

    override fun onResume() {
        super.onResume()
        myDbManager.openDb()

    }

    override fun onDestroy() {
        super.onDestroy()
        myDbManager.closeDb()
    }

    private fun startLauncher() {
        launcher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                if (result.resultCode == RESULT_OK) {
                    binding.ivAvatar.setImageURI(result.data?.data)
                    tempImageUri = result.data?.data.toString()
                    contentResolver.takePersistableUriPermission(
                        result.data?.data!!,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                }
            }

    }

    private fun onClick() = with(binding) {
        fbAddImage.setOnClickListener {
            mainImageLayout.visibility = View.VISIBLE
            fbAddImage.visibility = View.GONE
        }
        ibDelete.setOnClickListener {
            mainImageLayout.visibility = View.GONE
            fbAddImage.visibility = View.VISIBLE
            tempImageUri = "empty"
        }
        ibEdit.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.type = "image/*"
            launcher?.launch(intent)
        }
        fbSave.setOnClickListener {
            val myTitle = edTitle.text.toString()
            val myDesc = edDescription.text.toString()

            if (myTitle != "" && myDesc != "") {
                CoroutineScope(Dispatchers.Main).launch {
                    if (isEditState) {
                        myDbManager.updateItem(myTitle, myDesc, tempImageUri, id, getCurrentTime())
                    } else {
                        myDbManager.insertToDb(myTitle, myDesc, tempImageUri, getCurrentTime())
                    }
                    finish()
                }
            }
        }
        fbEditText.setOnClickListener {
            edTitle.isEnabled = true
            edDescription.isEnabled = true
            fbEditText.visibility = View.GONE
            fbAddImage.visibility = View.VISIBLE
            if (tempImageUri == "empty") return@setOnClickListener
            ibEdit.visibility = View.VISIBLE
            ibDelete.visibility = View.VISIBLE
        }
    }

    private fun getMyIntents() {

        binding.fbEditText.visibility = View.GONE
        val intent = intent

        if (intent != null) {
            if (intent.getStringExtra(MyIntentConstants.I_TITLE_KEY) != null) with(binding) {
                fbAddImage.visibility = View.GONE
                edTitle.setText(intent.getStringExtra(MyIntentConstants.I_TITLE_KEY))
                isEditState = true
                edTitle.isEnabled = false
                edDescription.isEnabled = false
                fbEditText.visibility = View.VISIBLE
                edDescription.setText(intent.getStringExtra(MyIntentConstants.I_DESC_KEY))
                id = intent.getIntExtra(MyIntentConstants.I_ID_KEY, 0)
                if (intent.getStringExtra(MyIntentConstants.I_URI_KEY) != "empty") with(binding) {
                    mainImageLayout.visibility = View.VISIBLE
                    tempImageUri = intent.getStringExtra(MyIntentConstants.I_URI_KEY)!!
                    ivAvatar.setImageURI(Uri.parse(tempImageUri))
                    ibDelete.visibility = View.GONE
                    ibEdit.visibility = View.GONE
                }
            }
        }
    }

    private fun getCurrentTime(): String {
        val time = Calendar.getInstance().time
        val formatter = SimpleDateFormat("dd-MM-yy kk:mm", Locale.getDefault())
        return formatter.format(time)

    }
}


