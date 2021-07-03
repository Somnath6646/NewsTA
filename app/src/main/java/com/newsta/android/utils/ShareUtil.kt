package com.newsta.android.utils

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.Person
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import com.newsta.android.MainActivity
import com.newsta.android.R
import com.newsta.android.ui.search.fragment.SearchFragment
import com.newsta.android.utils.models.Category

object ShareUtil {
    private const val SHARE_CATEGORY
            = "com.raywenderlich.android.memerepo.category.MEME_STORE_TARGET"

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun getShortcut(context: Context): ShortcutInfoCompat {
        val id = "search_with_newsta"
        return ShortcutInfoCompat.Builder(context, id)
            .setShortLabel("Search")
            .setLongLabel("Search with Newsta")
            .setIcon(IconCompat.createWithResource(context, R.drawable.ic_logo))
            .setCategories(setOf(SHARE_CATEGORY))
            .setIntent(Intent(context, MainActivity::class.java).apply {
                action = Intent.ACTION_SEND
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    putExtra(Intent.EXTRA_SHORTCUT_ID, id)
                }
            })
            .setLongLived(true)
            .setRank(0)
            .build()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun publishMemeShareShortcuts(context: Context) {
        ShortcutManagerCompat.addDynamicShortcuts(context, mutableListOf(getShortcut(context)))
    }

}
