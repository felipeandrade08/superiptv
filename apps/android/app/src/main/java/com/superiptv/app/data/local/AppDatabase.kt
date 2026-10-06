package com.superiptv.app.data.local
import androidx.room.*
@Entity(tableName="playlists") data class PlaylistEntity(@PrimaryKey val id:String,val name:String,val source:String,val createdAt:Long)
@Dao interface PlaylistDao{@Query("SELECT * FROM playlists ORDER BY createdAt DESC") suspend fun all():List<PlaylistEntity>;@Insert(onConflict=OnConflictStrategy.REPLACE) suspend fun save(item:PlaylistEntity)}
@Database(entities=[PlaylistEntity::class],version=1,exportSchema=false) abstract class AppDatabase:RoomDatabase(){abstract fun playlists():PlaylistDao}
