package ru.tinkoff.favouritepersons.room

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import ru.tinkoff.favouritepersons.domain.PersonItem

@Dao
interface PersonDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPerson(person : PersonItem)

    @Query("Select * from person")
    fun getAllPersons(): Flow<List<PersonItem>>

    @Update
    suspend fun updatePerson(person : PersonItem)

    @Query("Select * from person where id = :personId")
    fun getPersonById(personId : Int) : PersonItem

    @Delete
    suspend fun deletePerson(person : PersonItem)
    @Query("DELETE FROM person")
    fun clearTable()
}