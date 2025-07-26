package `in`.chess.scholars.global

import android.app.Application
import `in`.chess.scholars.global.di.DIContainer

/**
 * Custom Application class to manage the lifecycle of our dependency container.
 */
class MyApplication : Application() {
    // This container will hold all our application's dependencies.
    lateinit var container: DIContainer

    override fun onCreate() {
        super.onCreate()
        // Initialize the container when the application starts.
        container = DIContainer()
    }
}
