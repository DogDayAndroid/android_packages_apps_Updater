package top.easterNday.settings.Database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class Helper(context: Context) : SQLiteOpenHelper(context, dbName, null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE IF NOT EXISTS $icon_tableName (packageName TEXT PRIMARY KEY, appName TEXT, iconBitmap TEXT, iconColor TEXT, contributorName TEXT, isEnabled INTEGER, isEnabledAll INTEGER)")
        db.execSQL("CREATE TABLE IF NOT EXISTS $kernel_tableName (id INTEGER PRIMARY KEY, datetime REAL, describe TEXT, filename TEXT, size INTEGER, tag TEXT, url TEXT, version TEXT, downloadID INTEGER DEFAULT NULL, type TEXT)")
    }


    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Handle database upgrade if needed
    }

    companion object {
        const val dbAuthorities = "top.easterNday"
        const val dbName = "DogDay.db"

        const val icon_tableName = "ICON"
        const val kernel_tableName = "KERNEL"
    }
}
