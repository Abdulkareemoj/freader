package com.wiztek.freader.reader

import okio.FileSystem
import okio.Path
import okio.openZip as okioOpenZip

actual fun openZip(fileSystem: FileSystem, path: Path): FileSystem {
    return fileSystem.okioOpenZip(path)
}
