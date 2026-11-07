package com.example.data.local

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "command_history")
data class CommandHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val command: String,
    val timestamp: Long = System.currentTimeMillis(),
    val exitCode: Int = 0,
    val outputSnippet: String = "",
    val executionTimeMs: Long = 0
)

@Entity(tableName = "saved_scripts")
data class SavedScriptEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String,
    val command: String,
    val category: String,
    val isFavorite: Boolean = false
)

@Dao
interface AzukuDao {
    @Query("SELECT * FROM command_history ORDER BY id DESC LIMIT 100")
    fun getCommandHistory(): Flow<List<CommandHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCommand(item: CommandHistoryEntity): Long

    @Query("DELETE FROM command_history")
    suspend fun clearHistory()

    @Query("SELECT * FROM saved_scripts ORDER BY isFavorite DESC, id ASC")
    fun getSavedScripts(): Flow<List<SavedScriptEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScript(script: SavedScriptEntity): Long

    @Query("DELETE FROM saved_scripts WHERE id = :id")
    suspend fun deleteScript(id: Long)
}

@Database(
    entities = [CommandHistoryEntity::class, SavedScriptEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AzukuDatabase : RoomDatabase() {
    abstract fun azukuDao(): AzukuDao
}
