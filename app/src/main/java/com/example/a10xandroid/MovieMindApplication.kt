package com.example.a10xandroid

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Application class for MovieMind.
 * This class is used to initialize Hilt for dependency injection.
 */
@HiltAndroidApp
class MovieMindApplication : Application() 