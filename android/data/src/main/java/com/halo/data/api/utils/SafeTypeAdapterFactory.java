package com.halo.data.api.utils;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

import timber.log.Timber;

/**
 * Create by ndn
 * Create on 9/28/20
 * com.halo.data.api.utils
 */
public class SafeTypeAdapterFactory implements TypeAdapterFactory {

    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        final TypeAdapter<T> delegate = gson.getDelegateAdapter(this, type);
        return new TypeAdapter<T>() {
            public void write(JsonWriter out, T value) throws IOException {
                try {
                    delegate.write(out, value);
                } catch (IOException e) {
                    delegate.write(out, null);
                }
            }

            public T read(JsonReader in) throws IOException {
                try {
                    return delegate.read(in);
                } catch (IOException e) {
                    Timber.w("IOException. Value skipped" + e);
                    in.skipValue();
                    return null;
                } catch (IllegalStateException e) {
                    Timber.w("IllegalStateException. Value skipped" + e);
                    in.skipValue();
                    return null;
                } catch (JsonSyntaxException e) {
                    Timber.w("JsonSyntaxException. Value skipped" + e);
                    in.skipValue();
                    return null;
                }
            }
        };
    }
}
