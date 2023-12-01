package kr.ds.helper

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import timber.log.Timber

class EventBusHelper<T : Any>(private val clazz: Class<T>) {
    private var subscription: IEventBusSubscription? = null

    fun start(handler: (T) -> Unit, lifecycle: Lifecycle? = null) {
        stop()
        lifecycle?.addObserver(lifecycleObserver)
        subscription = RxBus.instance.subscribe(clazz, handler)
    }

    fun stop() {
        subscription?.let {
            it.unsubscribe()
            subscription = null
        }
    }

    private val lifecycleObserver = object : DefaultLifecycleObserver {
        override fun onDestroy(owner: LifecycleOwner) {
            super.onDestroy(owner)
            Timber.d("onDestroy")
            stop()
            owner.lifecycle.removeObserver(this)
        }
    }

    companion object {
        fun publish(event: Any) {
            RxBus.instance.publish(event)
        }
    }
}