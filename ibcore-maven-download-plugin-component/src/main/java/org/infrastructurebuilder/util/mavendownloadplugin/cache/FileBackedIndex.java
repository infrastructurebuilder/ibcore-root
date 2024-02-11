/*
 * @formatter:off
 * Copyright © 2019 admin (admin@infrastructurebuilder.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * @formatter:on
 */
package org.infrastructurebuilder.util.mavendownloadplugin.cache;

//import org.apache.maven.plugin.logging.Log;

//import javax.annotation.concurrent.NotThreadSafe;
import java.io.*;
import java.net.URI;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;

/**
 * Binary file backed index.
 * The implementation is <b>NOT</b> thread safe and should be synchronized
 * by the lock.
 * @author Paul Polishchuk
 * @since 1.3.1
 */
//@NotThreadSafe
final class FileBackedIndex implements FileIndex {
    private static final String CACHE_FILENAME = "index.ser";
    private final Map<URI, String> index = new HashMap<>();
    private final File storage;
    private final ReentrantLock lock = new ReentrantLock();
//    private final Log log;
    private final Logger log;

    /**
     * Creates index backed by file "index.ser" in baseDir.
     * Throws runtime exceptions if baseDir does not exist, not a directory
     * or file can't be created in it.
     * @param baseDir directory where the index file should be stored.
     */
//    FileBackedIndex(final File baseDir, Log log) {
    FileBackedIndex(final File baseDir, Logger log) {
        this.log = log;
        this.storage = new File(baseDir, CACHE_FILENAME);
    }

    @Override
    public void put(final URI uri, final String path) {
        this.index.put(uri, path);
        try {
            this.load(storage);
        } catch (IncompatibleIndexException | IOException e) {
            log.warn("Could not load index cache index file, it will be rewritten.");
        }
        this.save();
    }

    @Override
    public String get(final URI uri) {
        try {
            this.load(storage);
        }
        catch (IncompatibleIndexException | IOException e) {
            log.warn("Error while reading from cache " + storage.getAbsolutePath());
        }
        return this.index.get(uri);
    }

    @Override
    public ReentrantLock getLock() {
        return this.lock;
    }

    /**
     * Loads index from the file storage replacing absent in-memory entries.
     * @param store file where index is persisted.
     * @throws IncompatibleIndexException is the store cannot be read due to a deserialization issue
     */
    @SuppressWarnings("unchecked")
    private void load(final File store) throws IncompatibleIndexException, IOException {
        if (store.length() != 0L) {
            try (
                final RandomAccessFile file = new RandomAccessFile(store, "r");
                final FileChannel channel = file.getChannel();
                final FileLock ignored = channel.lock(0L, Long.MAX_VALUE, true);
                final ObjectInputStream deserialize = new ObjectInputStream(Files.newInputStream(store.toPath()))
            ) {
                Map<URI, String> newEntries = (Map<URI, String>) deserialize.readObject();
                newEntries.forEach(index::putIfAbsent);
            } catch (final ClassNotFoundException | InvalidClassException e) {
                throw new IncompatibleIndexException(e);
            }
        }
    }

    /**
     * Saves current im-memory index to file based storage.
     */
    private void save() {
        try (
            final FileOutputStream file = new FileOutputStream(this.storage);
            final ObjectOutput res = new ObjectOutputStream(file);
            final FileChannel channel = file.getChannel();
            final FileLock ignored = channel.lock()
        ) {
            res.writeObject(this.index);
        } catch (final IOException ex) {
            throw new IllegalStateException(ex);
        }
    }
}
