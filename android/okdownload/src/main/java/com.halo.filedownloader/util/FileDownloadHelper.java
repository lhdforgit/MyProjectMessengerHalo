/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.filedownloader.util;

import android.annotation.SuppressLint;
import android.content.Context;

import com.halo.filedownloader.connection.FileDownloadConnection;
import com.halo.filedownloader.database.FileDownloadDatabase;
import com.halo.filedownloader.model.FileDownloadModel;
import com.halo.filedownloader.stream.FileDownloadOutputStream;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import okhttp3.OkHttpClient;

/**
 * The helper for cache the {@code appContext} and {@code OK_HTTP_CLIENT} for the main process and
 * the filedownloader process.
 */
public class FileDownloadHelper {

    @SuppressLint("StaticFieldLeak")
    private static Context appContext;

    public static void holdContext(Context context) {
        appContext = context;
    }

    public static Context getAppContext() {
        return appContext;
    }

    @SuppressWarnings("UnusedParameters")
    public interface IdGenerator {
        /**
         * Invoke this method when the data is restoring from the database.
         *
         * @param oldId           the old download id which is generated through the different.
         * @param url             the url path.
         * @param path            the download path.
         * @param pathAsDirectory {@code true}: if the {@code path} is absolute directory to store
         *                        the downloading file, and the {@code filename} will be found in
         *                        contentDisposition from the response as default, if can't find
         *                        contentDisposition,the {@code filename} will be generated by
         *                        {@code url}.
         *                        </p>
         *                        {@code false}: if the {@code path} = (absolute directory/filename)
         * @return the new download id.
         */
        int transOldId(int oldId, String url, String path,
                       boolean pathAsDirectory);

        /**
         * Invoke this method when there is a new task from very beginning.
         * <p>
         * Important Ting: this method would be used on the FileDownloadService and the upper-layer,
         * so as default FileDownloadService is running on the `:filedownloader` process, and
         * upper-layer
         * is on the user process, in this case, if you want to cache something on this instance, it
         * would be two different caches on two processes.
         * <p>
         * Tips: if you want the FileDownloadService runs on the upper-layer process too, just
         * config {@code process.non-separate=true} on the filedownloader.properties, more detail
         *
         * @param url             the download url.
         * @param path            the download path.
         * @param pathAsDirectory {@code true}: if the {@code path} is absolute directory to store
         *                        the downloading file, and the {@code filename} will be found in
         *                        contentDisposition from the response as default, if can't find
         *                        contentDisposition,the {@code filename} will be generated by
         *                        {@code url}.
         *                        </p>
         *                        {@code false}: if the {@code path} = (absolute directory/filename)
         * @return the download task identify.
         */
        int generateId(String url, String path, boolean pathAsDirectory);
    }

    @SuppressWarnings("UnusedParameters")
    public interface ConnectionCountAdapter {
        /**
         * Before invoke this method to determine how many connection will be used to downloading
         * this task,
         * there are several conditions must be confirmed:
         * <p>
         * 1. the connection is support multiple connection(SUPPORT"Partial Content(206)" AND NOT
         * Chunked)
         * 2. the current {@link FileDownloadOutputStream} support seek
         * 3. this is a new task NOT resume from breakpoint( If the task resume from breakpoint
         * the connection count would be using
         * the one you determined when the task
         * first created ).
         * <p/>
         * The best strategy is refer to how much speed of each connection for the ip:port not file
         * size.
         *
         * @param downloadId  the download id.
         * @param url         the task url.
         * @param path        the task path.
         * @param totalLength the total length of the file.
         * @return the count of connection you want for the task. the value must be large than 0.
         */
        int determineConnectionCount(int downloadId, String url, String path, long totalLength);
    }

    public interface DatabaseCustomMaker {
        /**
         * The database is used for storing the {@link FileDownloadModel}.
         * <p/>
         * The data stored in the database is only used for task resumes from the breakpoint.
         * <p>
         * The task of the data stored in the database must be a task that has not finished
         * downloading yet, and if the task has finished downloading, its data will be
         * {@link FileDownloadDatabase#remove(int)} from the database, since that data is no longer
         * available for resumption of its task pass.
         *
         * @return Nullable, Customize {@link FileDownloadDatabase} which will be used for storing
         * downloading model.
         */
        FileDownloadDatabase customMake();
    }

    public interface OutputStreamCreator {
        /**
         * The output stream creator is used for creating {@link FileDownloadOutputStream} which is
         * used to write the input stream to the file for downloading.
         * <p>
         * <strong>Note:</strong> please create a output stream which append the content to the
         * exist file, which means that bytes would be written to the end of the file rather than
         * the beginning.
         *
         * @param file the file will used for storing the downloading content.
         * @return The output stream used to write downloading byte array to the {@code file}.
         * @throws FileNotFoundException if the file exists but is a directory
         *                               rather than a regular file, does not exist but cannot
         *                               be created, or cannot be opened for any other reason
         */
        FileDownloadOutputStream create(File file) throws IOException;

        /**
         * @return {@code true} if the {@link FileDownloadOutputStream} is created through
         * {@link #create(File)} support {@link FileDownloadOutputStream#seek(long)} function.
         * If the {@link FileDownloadOutputStream} is created through {@link #create(File)} doesn't
         * support {@link FileDownloadOutputStream#seek(long)}, please return {@code false}, in
         * order to let the internal mechanism can predict this situation, and handle it smoothly.
         */
        boolean supportSeek();
    }

    public interface ConnectionCreator {
        /**
         * The connection creator is used for creating {@link FileDownloadConnection} component
         * which is used to use some protocol to connect to the remote server.
         *
         * @param url the uniform resource locator, which direct the aim resource we need to connect
         * @return The connection creator.
         * @throws IOException if an I/O exception occurs.
         */
        FileDownloadConnection create(String url) throws IOException;
    }

    public interface OkHttpClientCustomMaker {

        /**
         * @return Nullable, Customize {@link OkHttpClient}, will be used for downloading files.
         * @see OkHttpClient
         */
        OkHttpClient customMake();
    }
}

