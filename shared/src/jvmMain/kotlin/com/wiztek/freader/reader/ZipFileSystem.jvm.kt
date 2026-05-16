package com.wiztek.freader.reader

import okio.FileSystem
import okio.Path

actual fun openZip(fileSystem: FileSystem, path: Path): FileSystem {
    // JVM (non-Android) might not have okio-zipfilesystem in shared, so we might need a fallback
    // But we'll try to use it if it's available.
    // For now, let's assume it's NOT available in common shared for JVM to avoid sync issues.
    throw UnsupportedOperationException("Zip file system not yet supported on JVM")
}
