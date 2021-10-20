package com.example.rusalqrandbarcodescanner.services

import java.io.File
import java.io.FileWriter

object FileStorage {

    lateinit var file : File

    fun writeDataToFile(fileBody : String) {

        try {
            val writer : FileWriter = FileWriter(file)

            writer.append(fileBody)
            writer.flush()
            writer.close()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}