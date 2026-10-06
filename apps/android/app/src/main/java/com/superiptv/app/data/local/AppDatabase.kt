package com.superiptv.app.data.local
import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow
@Entity(tableName="playlists") data class PlaylistEntity(@PrimaryKey val id:String,val name:String,val source:String,val sourceType:String,val createdAt:Long,val updatedAt:Long)
@Entity(tableName="channels",indices=[Index("playlistId"),Index("groupName")]) data class ChannelEntity(@PrimaryKey val id:String,val playlistId:String,val name:String,val streamUrl:String,val logoUrl:String?,val groupName:String,val favorite:Boolean=false)
@Dao interface PlaylistDao{@Query("SELECT * FROM playlists ORDER BY updatedAt DESC") fun observeAll():Flow<List<PlaylistEntity>>;@Insert(onConflict=OnConflictStrategy.REPLACE) suspend fun save(item:PlaylistEntity)}
@Dao interface ChannelDao{
 @Query("SELECT * FROM channels ORDER BY groupName,name") fun observeAll():Flow<List<ChannelEntity>>
 @Query("SELECT * FROM channels WHERE favorite=1 ORDER BY name") fun observeFavorites():Flow<List<ChannelEntity>>
 @Insert(onConflict=OnConflictStrategy.REPLACE) suspend fun saveAll(items:List<ChannelEntity>)
 @Query("DELETE FROM channels WHERE playlistId=:playlistId") suspend fun deleteForPlaylist(playlistId:String)
 @Query("UPDATE channels SET favorite=:favorite WHERE id=:id") suspend fun setFavorite(id:String,favorite:Boolean)
}
@Database(entities=[PlaylistEntity::class,ChannelEntity::class],version=2,exportSchema=false) abstract class AppDatabase:RoomDatabase(){
 abstract fun playlists():PlaylistDao;abstract fun channels():ChannelDao
 companion object{@Volatile private var instance:AppDatabase?=null;fun get(context:Context)=instance?:synchronized(this){instance?:Room.databaseBuilder(context.applicationContext,AppDatabase::class.java,"superiptv.db").fallbackToDestructiveMigration().build().also{instance=it}}}
}