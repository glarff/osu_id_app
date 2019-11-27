package com.e.osu_id_app


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.se.omapi.Session
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_file.view.*
import androidx.recyclerview.widget.LinearLayoutManager
import java.io.File
import androidx.recyclerview.widget.RecyclerView
import junit.framework.TestCase
import kotlinx.android.synthetic.main.activity_main.*
import org.apache.commons.lang3.StringUtils
import org.junit.Test
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.text.SimpleDateFormat
import java.util.stream.Collectors


class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        recyclerView.layoutManager = layoutManager

        val savedSessions = ArrayList<SessionCard>()
        val sdf = SimpleDateFormat("MM/dd/yy 'at' HH:mm z")

        var filesTotalCnt = 0
        var filesUploadedCnt = 0

        // using extension function walkBottomUp - gets the most recent folders first
        File("/storage/emulated/0/Android/media/com.osu_id_app/").walkBottomUp().forEach {

            //Create a Path object
            val path = Paths.get(it.absolutePath)

            if(Files.isDirectory(path) && it.name != "com.osu_id_app"  && it.parentFile.name != "com.osu_id_app" && it.parentFile.name != "stage"){

                filesTotalCnt = 0
                filesUploadedCnt = 0
                var path1: String = "Null"
                var path2: String = "Null"
                var path3: String = "Null"

                // Get files from sub directories
                File(it.absolutePath).walk().forEach {
                    if(it.isFile) {

                        if(filesTotalCnt == 0)
                        {
                            path1 = it.absolutePath
                            println(path1)
                        }
                        if(filesTotalCnt == 1)
                        {
                            path2 = it.absolutePath
                            println(path2)
                        }
                        if(filesTotalCnt == 2)
                        {
                            path3 = it.absolutePath
                            println(path3)
                        }
                        filesTotalCnt++
                    }
                }

                savedSessions.add(SessionCard(path1, path2, path3, it.absolutePath, it.name, it.lastModified(), sdf.format(it.lastModified()),filesTotalCnt.toString() + " Files") )

            }
        }

        // sort the sessions once complete to show the most recent first
        savedSessions.sortByDescending( { selector(it) } )

        var adapter = SessionCardAdapter(this, savedSessions)
        recyclerView.adapter = adapter

    }


    fun startSession (view: View) {

        val randomIntent = Intent(this@MainActivity, barcode_scan::class.java)

        val mDialogView = LayoutInflater.from(this).inflate(R.layout.activity_file, null)
        val builder = AlertDialog.Builder(this@MainActivity)

        builder.setTitle("Enter Session Name")

        builder.setView(mDialogView)

        builder.setPositiveButton("Start new Session"){dialog, which -> run {

            val searchIt = search()
            var input1 = mDialogView.editText.text.toString()
            var exists = true


            var dir = File(externalMediaDirs.first().toString())
            var filePath = Paths.get(dir.getAbsolutePath())


            filePath = searchIt.myMethod3(filePath, input1)
            println(filePath)

            try {
                exists = filePath.toString().isEmpty()
            } catch (e: Exception){
                println("No File matches")
            }

            if (!exists){

                Toast.makeText(this, "Name Already In Use", Toast.LENGTH_SHORT).show()

            } else {
                var fileString = "unsent/$input1"
                var liveUpload = "true"
                var path = "unsent"

                if(mDialogView.checkBox.isChecked) {
                    liveUpload = "false"
                }

                // Create Sent and Unsent Folders
                var createSentDir = File(externalMediaDirs.first(), "sent/$input1")
                createSentDir?.mkdirs()
                var filename = File(externalMediaDirs.first(), fileString)
                filename?.mkdirs()

                randomIntent.putExtra("FileName", input1)
                randomIntent.putExtra("Path", path)
                randomIntent.putExtra("LiveUpload", liveUpload)
                startActivity(randomIntent)
            }



        }
        }

        // Display a neutral button on alert dialog
        builder.setNeutralButton("Cancel"){_,_ -> }

        // Finally, make the alert dialog using builder
        val dialog: AlertDialog = builder.create()

        // Display the alert dialog on app interface
        dialog.show()

    }
}


