package com.google.developers.lettervault.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.google.developers.lettervault.R
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.concurrent.Executors

@Database(entities = [Letter::class], version = 1, exportSchema = false)
abstract class LetterDatabase : RoomDatabase() {

    abstract fun letterDao(): LetterDao

    companion object {

        @Volatile
        private var instance: LetterDatabase? = null

        /**
         * Returns an instance of Room Database.
         *
         * @param context application context
         * @return The singleton LetterDatabase
         */
        fun getInstance(context: Context): LetterDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context,
                    LetterDatabase::class.java, "letter.db"
                ).addCallback( object : Callback(){
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        instance?.let {
                            Executors.newSingleThreadExecutor().execute(){
                                fillWithStartingData(context.applicationContext, it.letterDao())
                            }
                        }
                    }

                }).build()

            }
        }

        private fun fillWithStartingData(context: Context, dao: LetterDao) {
            val task = loadJsonArray(context)
            try {
                if (task != null) {
                    for (i in 0 until task.length()) {
                        val item = task.getJSONObject(i)
                        dao.insertAll(
                            Letter(
                                item.getLong("id"),
                                item.getString("subject"),
                                item.getString("content"),
                                item.getLong("created"),
                                item.getLong("expires"),
                                item.getLong("opened")
                            )
                        )
                    }
                }
            } catch (exception: JSONException) {
                exception.printStackTrace()
            }
        }

        private fun loadJsonArray(context: Context): JSONArray? {
            val builder = StringBuilder()
            val `in` = context.resources.openRawResource(R.raw.letter)
            val reader = BufferedReader(InputStreamReader(`in`))
            var line: String?
            try {
                while (reader.readLine().also { line = it } != null) {
                    builder.append(line)
                }
                val json = JSONObject(builder.toString())
                return json.getJSONArray("letter")
            } catch (exception: IOException) {
                exception.printStackTrace()
            } catch (exception: JSONException) {
                exception.printStackTrace()
            }
            return null
        }

    }
}
