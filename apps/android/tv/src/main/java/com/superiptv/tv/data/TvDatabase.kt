package com.superiptv.tv.data
import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow
@Entity(tableName="tv_channels",indices=[Index("groupName")]) data class TvChannel(@PrimaryKey val id:String,val name:String,val streamUrl:String,val logoUrl:String?,val groupName:String,val favorite:Boolean=false)
@Dao interface TvChannelDao{@Query("SELECT * FROM tv_channels ORDER BY groupName,name") fun observeAll():Flow<List<TvChannel>>;@Insert(onConflict=OnConflictStrategy.REPLACE) suspend fun saveAll(items:List<TvChannel>);@Query("UPDATE tv_channels SET favorite=:favorite WHERE id=:id") suspend fun favorite(id:String,favorite:Boolean)}
@Database(entities=[TvChannel::class],version=1,exportSchema=false) abstract class TvDatabase:RoomDatabase(){abstract fun channels():TvChannelDao;companion object{@Volatile private var instance:TvDatabase?=null;fun get(c:Context)=instance?:synchronized(this){instance?:Room.databaseBuilder(c.applicationContext,TvDatabase::class.java,"superiptv-tv.db").build().also{instance=it}}}}
