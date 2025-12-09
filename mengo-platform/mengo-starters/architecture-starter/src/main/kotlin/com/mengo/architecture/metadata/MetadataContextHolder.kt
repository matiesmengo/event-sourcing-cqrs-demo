package com.mengo.architecture.metadata

object MetadataContextHolder {
    private val metadataThreadLocal = ThreadLocal<Metadata?>()

    fun set(metadata: Metadata) = metadataThreadLocal.set(metadata)

    fun get(): Metadata? = metadataThreadLocal.get()

    fun clear() = metadataThreadLocal.remove()
}
