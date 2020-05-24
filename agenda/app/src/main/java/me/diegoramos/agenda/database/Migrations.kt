package me.diegoramos.agenda.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class Migrations {

    companion object {
        val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE Contact ADD COLUMN last_name TEXT")
            }
        }

        val MIGRATION_2_3: Migration = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE Contact ADD COLUMN mobile TEXT")
            }
        }

        val MIGRATION_3_4: Migration = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE IF NOT EXISTS `Contact_new` (`id` TEXT NOT NULL, `name` TEXT NOT NULL, `lastName` TEXT NOT NULL, `email` TEXT NOT NULL, PRIMARY KEY(`id`))")
                database.execSQL("INSERT INTO `Contact_new`(`id`, `name`, `lastName`, `email`) " +
                        "SELECT `id`, `name`, `lastName`, `email` FROM `Contact`")
                database.execSQL("INSERT INTO Phone(`number`, `contactId`)" +
                        "SELECT `phone`, `id` FROM `Contact`")

                database.execSQL("DROP TABLE `Contact`")
                database.execSQL("ALTER TABLE `Contact_new` RENAME TO `Contact`")

            }
        }
    }

}