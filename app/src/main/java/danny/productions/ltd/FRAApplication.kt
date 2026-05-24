package danny.productions.ltd

import android.app.Application
import danny.productions.ltd.di.ServiceLocator

class FRAApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        ServiceLocator.init(this)
    }
}
