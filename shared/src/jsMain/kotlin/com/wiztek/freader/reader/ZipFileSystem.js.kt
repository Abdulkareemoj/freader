package com.wiztek.freader.reader

import okio.FileSystem
import okio.Path

actual fun openZip(fileSystem: FileSystem, path: Path): FileSystem {
    throw UnsupportedOperationException("Zip file system not yet supported on JS")
}
