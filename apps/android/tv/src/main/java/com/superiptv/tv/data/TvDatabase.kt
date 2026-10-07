package com.superiptv.tv.data
import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow
@Entity(tableName="tv_channels",indices=[Index("groupName"),Index("contentType"),Index("seriesName")]) data class TvChannel(@PrimaryKey val id:String,val name:String,val streamUrl:String,val logoUrl:String?,val groupName:String,val favorite:Boolean=false,val contentType:String="live",val description:String?=null,val year:Int?=null,val seasonNumber:Int?=null,val episodeNumber:Int?=null,val seriesName:String?=null)
@Entity(tableName="tv_history",indices=[Index("lastWatchedAt")]) data class TvHistory(@PrimaryKey val itemId:String,val name:String,val groupName:String,val contentType:String,val positionMs:Long,val durationMs:Long,val lastWatchedAt:Long)
@Dao interface TvChannelDao{
 @Query("DELETE FROM tv_channels") suspend fun clear()
 @Query("SELECT * FROM tv_channels ORDER BY groupName,name") fun observeAll():Flow<List<TvChannel>>
 @Query("SELECT id FROM tv_channels WHERE favorite=1") suspend fun favoriteIds():List<String>
 @Insert(onConflict=OnConflictStrategy.REPLACE) suspend fun saveAll(items:List<TvChannel>)
 @Query("UPDATE tv_channels SET favorite=:favorite WHERE id=:id") suspend fun favorite(id:String,favorite:Boolean)
}
@Dao interface TvHistoryDao{
 @Query("SELECT * FROM tv_history WHERE contentType!='live' AND durationMs>0 AND positionMs>0 AND positionMs<durationMs-30000 ORDER BY lastWatchedAt DESC LIMIT 20") fun continueWatching():Flow<List<TvHistory>>
 @Query("SELECT * FROM tv_history WHERE itemId=:id LIMIT 1") suspend fun find(id:String):TvHistory?
 @Insert(onConflict=OnConflictStrategy.REPLACE) suspend fun save(item:TvHistory)
}
@Database(entities=[TvChannel::class,TvHistory::class],version=3,exportSchema=false) abstract class TvDatabase:RoomDatabase(){abstract fun channels():TvChannelDao;abstract fun history():TvHistoryDao;companion object{@Volatile private var instance:TvDatabase?=null;fun get(c:Context)=instance?:synchronized(this){instance?:Room.databaseBuilder(c.applicationContext,TvDatabase::class.java,"superiptv-tv.db").fallbackToDestructiveMigration().build().also{instance=it}}}}
