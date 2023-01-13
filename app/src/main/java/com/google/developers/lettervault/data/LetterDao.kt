package com.google.developers.lettervault.data

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery

/**
 * Room data object for all database interactions.
 *
 * @see Dao
 */
@Dao
interface LetterDao {

    //use raw query if sorting is needed
    @RawQuery(observedEntities = [Letter::class])
    fun getLetters(query: SupportSQLiteQuery): DataSource.Factory<Int, Letter>

    @Query("select * from letter where id = :letterId")
    fun getLetter(letterId: Long): LiveData<Letter>

    @Query("select * from letter where opened = 0 order by created asc")
    fun getRecentLetter() : LiveData<Letter>

    @Query("SELECT * FROM letter WHERE opened = 1 ORDER BY created ASC")
    fun openLetter(): Letter

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(letter: Letter): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg letter: Letter)

    @Update
    fun update(letter: Letter)

    @Delete
    fun delete(letter: Letter)

}