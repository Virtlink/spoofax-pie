package mb.spoofax.runtime.pie

import com.google.inject.Inject
import com.google.inject.Provider
import mb.pie.runtime.core.*
import mb.pie.runtime.core.impl.store.InMemoryStore
import mb.pie.runtime.core.impl.store.LMDBBuildStoreFactory
import mb.vfs.path.PPath
import mb.vfs.path.PathSrv
import java.util.concurrent.ConcurrentHashMap


interface PieSrv {
  fun getPullingExecutor(dir: PPath, useInMemoryStore: Boolean): PullingExecutor
  fun getPushingExecutor(dir: PPath, useInMemoryStore: Boolean): PushingExecutor
}

class PieSrvImpl @Inject constructor(
  private val pathSrv: PathSrv,
  private val storeFactory: LMDBBuildStoreFactory,
  private val cacheFactory: Provider<Cache>,
  private val pullingExecutorFactory: PullingExecutorFactory,
  private val pushingExecutorFactory: PushingExecutorFactory
) : PieSrv {
  private val stores = ConcurrentHashMap<PPath, Store>()
  private val caches = ConcurrentHashMap<PPath, Cache>()
  private val pullingExecutors = ConcurrentHashMap<PPath, PullingExecutor>()
  private val pushingExecutors = ConcurrentHashMap<PPath, PushingExecutor>()


  override fun getPullingExecutor(dir: PPath, useInMemoryStore: Boolean): PullingExecutor {
    val store = getStore(dir, useInMemoryStore)
    val cache = getCache(dir)
    return pullingExecutors.getOrPut(dir) {
      pullingExecutorFactory.create(store, cache)
    }
  }

  override fun getPushingExecutor(dir: PPath, useInMemoryStore: Boolean): PushingExecutor {
    val store = getStore(dir, useInMemoryStore)
    val cache = getCache(dir)
    return pushingExecutors.getOrPut(dir) {
      pushingExecutorFactory.create(store, cache)
    }
  }


  private fun getStore(dir: PPath, useInMemoryStore: Boolean) = stores.getOrPut(dir) {
    if(useInMemoryStore) {
      InMemoryStore()
    } else {
      val storeDir = dir.resolve(".pie")
      val localStoreDir = pathSrv.localPath(storeDir) ?: throw RuntimeException("Cannot create PIE LMDB store at $storeDir because it is not on the local filesystem")
      storeFactory.create(localStoreDir)
    }
  }

  private fun getCache(dir: PPath) = caches.getOrPut(dir) {
    cacheFactory.get()
  }
}
