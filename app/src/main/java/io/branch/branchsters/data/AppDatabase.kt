package io.branch.branchsters.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import io.branch.branchsters.data.dao.MonsterDao
import io.branch.branchsters.data.dao.QuestDao
import io.branch.branchsters.data.entity.Monster
import io.branch.branchsters.data.entity.Quest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [Quest::class, Monster::class],
    version = 2,
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
            // Prepopulate 6 quests
            val initialQuests = listOf(
                Quest(
                    name = "First Steps",
                    description = "Complete your profile setup and explore the app features"
                ),
                Quest(
                    name = "Monster Collector",
                    description = "Generate your first monster using Gemini AI"
                ),
                Quest(
                    name = "Quest Master",
                    description = "Complete 3 daily challenges to unlock special rewards"
                ),
                Quest(
                    name = "Social Butterfly",
                    description = "Share your monster with friends using Branch deep links"
                ),
                Quest(
                    name = "Power Leveler",
                    description = "Reach level 5 by completing various tasks and challenges"
                ),
                Quest(
                    name = "Ultimate Champion",
                    description = "Complete all quests and become the ultimate Branchster master"
                )
            )
            questDao.insertQuests(initialQuests)

            // Prepopulate a default monster
            val defaultMonster = Monster(
                monsterName = "Starter Branchster",
                monsterImage = "default_monster" // This can be a drawable resource or URL
            )
            monsterDao.insertMonster(defaultMonster)
        }
    }
}
