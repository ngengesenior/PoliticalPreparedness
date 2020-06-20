package com.example.android.politicalpreparedness.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.android.politicalpreparedness.network.models.Election

@Dao
interface ElectionDao {

    //TODO: Add insert query
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(election: Election)

    //TODO: Add select all election query
    @Query("SELECT * FROM election_table ORDER BY electionDay")
    suspend fun getElections():List<Election>

    //TODO: Add select single election query
    @Query("SELECT * FROM election_table WHERE id = :id LIMIT 1")
    fun getElection(id:Int):Election?

    //TODO: Add delete query
    @Delete
    suspend fun delete(election: Election)

    @Query("DELETE FROM election_table WHERE id = :id")
    suspend fun deleteById(id: Int)

    //TODO: Add clear query
    @Query("DELETE FROM election_table")
    suspend fun clear()

}