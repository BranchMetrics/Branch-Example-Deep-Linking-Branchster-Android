package io.branch.branchsters.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import io.branch.branchsters.R
import io.branch.branchsters.data.dao.MonsterDao
import io.branch.branchsters.data.dao.QuestDao
import io.branch.branchsters.data.entity.Monster
import io.branch.branchsters.data.entity.Quest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [Quest::class, Monster::class],
    version = 8,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun questDao(): QuestDao
    abstract fun monsterDao(): MonsterDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "branchsters_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateDatabase(database.questDao(), database.monsterDao())
                    }
                }
            }
        }

        private suspend fun populateDatabase(questDao: QuestDao, monsterDao: MonsterDao) {
            // Prepopulate 6 quests with dependencies
            val initialQuests = listOf(
                Quest(
                    id = 1,
                    name = "Create Branch Link",
                    description = "Generate a link to earn some XP for your monster.",
                    isLocked = false,
                    icon = R.drawable.link
                ),
                Quest(
                    id = 2,
                    name = "Share Branch Link",
                    description = "Share a link to earn some XP for your monster.",
                    isLocked = true,
                    dependsOnQuestId = 1, // Depends on "Create Branch Link"
                    icon = R.drawable.upload
                ),
                Quest(
                    id = 3,
                    name = "Trigger Branch Event",
                    description = "Trigger an event to earn some XP for your monster.",
                    isLocked = false,
                    icon = R.drawable.activity
                ),
                Quest(
                    id = 4,
                    name = "View Branch Event Data",
                    description = "View event data to earn some XP for your monster.",
                    isLocked = true,
                    dependsOnQuestId = 3, // Depends on "Trigger Branch Event"
                    icon = R.drawable.braces
                ),
                Quest(
                    id = 5,
                    name = "Generate Branch QR Code",
                    description = "Generate QR code to earn some XP for your monster",
                    isLocked = false,
                    icon = R.drawable.qr_code_white
                ),
                Quest(
                    id = 6,
                    name = "Share Branch QR Code",
                    description = "Share QR code to earn some XP for your monster",
                    isLocked = true,
                    dependsOnQuestId = 5, // Depends on "Generate Branch QR Code"
                    icon = R.drawable.upload
                )
            )
            questDao.insertQuests(initialQuests)

            // Prepopulate a default monster
            val defaultMonster = Monster(
                monsterName = "Starter Monster",
                monsterTitle = "Beginner",
                monsterImage = R.drawable.onboard_monster_1 // This can be a drawable resource or URL
            )
            monsterDao.insertMonster(defaultMonster)
        }
    }
}
