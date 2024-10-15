package com.danilor.libretracker

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

class ExcludedPackagesManager(private val context: Context) {

    init {
        createFileIfNotExists()
        loadExcludedPackagesFromFile()
    }

    companion object {
        private var excludedPackages: MutableList<String> = mutableListOf()
    }

    private val filePath = "excluded_package.json"

    fun getExcludedPackages(): List<String> {
        return excludedPackages
    }

    fun addPackageToExclude(packageName: String) {
        if (!excludedPackages.contains(packageName)) {
            excludedPackages.add(packageName)
            saveExcludedPackagesToFile()
        }
    }

    fun removePackageFromExclude(packageName: String) {
        if (excludedPackages.contains(packageName)) {
            excludedPackages.remove(packageName)
            saveExcludedPackagesToFile()
        }
    }

    private fun createFileIfNotExists() {
        val file = File(filePath)
        if (!file.exists()) {
            file.writeText("{\"excluded_packages\":[]}")
        }
    }

    private fun loadExcludedPackagesFromFile() {
        val file = File(filePath)
        if (!file.exists()) return

        val jsonString = file.readText()
        val jsonObject = JSONObject(jsonString)
        val excludedPackagesArray = jsonObject.getJSONArray("excluded_packages")

        excludedPackages = mutableListOf()
        for (i in 0 until excludedPackagesArray.length()) {
            excludedPackages.add(excludedPackagesArray.getString(i))
        }
    }

    private fun saveExcludedPackagesToFile() {
        val jsonObject = JSONObject()
        val excludedPackagesArray = JSONArray()

        for (pkg in excludedPackages) {
            excludedPackagesArray.put(pkg)
        }

        jsonObject.put("excluded_packages", excludedPackagesArray)

        val file = File(filePath)
        file.writeText(jsonObject.toString())
    }
}
