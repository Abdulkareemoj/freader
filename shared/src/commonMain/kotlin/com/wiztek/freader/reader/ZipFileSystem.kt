package com.wiztek.freader.reader

import okio.FileSystem
import okio.Path

expect fun openZip(fileSystem: FileSystem, path: Path): FileSystem
