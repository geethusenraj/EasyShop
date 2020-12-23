package com.ec.shop.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ec.shop.data.db.dao.CartDao
import com.ec.shop.data.db.entities.CartEntity


@Database(
    entities = [CartEntity::class],
    version = 1, exportSchema = false
)
abstract class AppDataBase : RoomDatabase() {

    abstract fun getCartDao(): CartDao

    companion object {

        @Volatile
        private var instance: AppDataBase? = null
        private val LOCK = Any()

        fun getDatabase(context: Context): AppDataBase {
            if (instance != null) {
                return instance!!
            }
            synchronized(LOCK) {
                instance ?: buildDatabase(context).also {
                    instance = it
                }
            }
            return instance!!

        }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                AppDataBase::class.java,
                "ECShopDatabase.db"
            ).build()
    }

}