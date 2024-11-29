package com.danilor.libretracker.managers

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

object ExcludedPackagesManager {
    private const val USER_EXCLUDED_PACKAGES_FILE_NAME = "userEP.json"
    private const val DEFAULT_EXCLUDED_PACKAGES_FILE_NAME = "defaultEP.json"
    private lateinit var filePath: String

    fun initialize(appContext: Context) {
        filePath = File(
            appContext.applicationContext.filesDir, USER_EXCLUDED_PACKAGES_FILE_NAME
        ).absolutePath
        createFileIfNotExists()
        loadUserExcludedPackagesFromFile()
        loadDefaultExcludedPackagesFromFile(appContext)
    }

    private var userExcludedPackages: MutableList<String> = mutableListOf()
    private var defaultExcludedPackages: MutableList<String> = mutableListOf()

    fun getDefaultExcludedPackages(): List<String> {
        return defaultExcludedPackages
    }

    fun getAllExcludedPackages(): List<String> {
        return userExcludedPackages + defaultExcludedPackages
    }

    fun addPackageToExclude(packageName: String) {
        if (!userExcludedPackages.contains(packageName)) {
            userExcludedPackages.add(packageName)
            saveExcludedPackagesToFile()
        }
    }

    fun removePackageFromExclude(packageName: String) {
        if (userExcludedPackages.contains(packageName)) {
            userExcludedPackages.remove(packageName)
            saveExcludedPackagesToFile()
        }
    }

    private fun createFileIfNotExists() {
        val file = File(filePath)
        if (!file.exists()) {
            file.writeText("{\"excluded_packages\":[]}")
        }
    }

    private fun loadDefaultExcludedPackagesFromFile(appContext: Context) {
        val assetManager = appContext.assets
        val inputStream = assetManager.open(DEFAULT_EXCLUDED_PACKAGES_FILE_NAME)
        val bufferedReader = BufferedReader(InputStreamReader(inputStream))

        val jsonString = bufferedReader.use { it.readText() }

        val jsonObject = JSONObject(jsonString)
        val excludedPackagesArray = jsonObject.getJSONArray("excluded_packages")

        defaultExcludedPackages = mutableListOf()
        for (i in 0 until excludedPackagesArray.length()) {
            defaultExcludedPackages.add(excludedPackagesArray.getString(i))
        }
    }

    private fun loadUserExcludedPackagesFromFile() {
        val file = File(filePath)
        if (!file.exists()) return

        val jsonString = file.readText()
        val jsonObject = JSONObject(jsonString)
        val excludedPackagesArray = jsonObject.getJSONArray("excluded_packages")

        userExcludedPackages = mutableListOf()
        for (i in 0 until excludedPackagesArray.length()) {
            userExcludedPackages.add(excludedPackagesArray.getString(i))
        }
    }

    private fun saveExcludedPackagesToFile() {
        val jsonObject = JSONObject()
        val excludedPackagesArray = JSONArray()

        for (pkg in userExcludedPackages) {
            excludedPackagesArray.put(pkg)
        }

        jsonObject.put("excluded_packages", excludedPackagesArray)

        val file = File(filePath)
        file.writeText(jsonObject.toString())
    }
}
