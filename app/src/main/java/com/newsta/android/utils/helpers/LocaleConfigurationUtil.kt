package com.newsta.android.utils.helpers

import android.content.Context
import android.content.res.Configuration

class LocaleConfigurationUtil {

    companion object {

        fun adjustFontSize(context: Context, fontScale: Float = 1.0f): Context {

            val configuration: Configuration = context.resources.configuration
            configuration.fontScale = fontScale
            return context.createConfigurationContext(configuration)

        }

    }

}
