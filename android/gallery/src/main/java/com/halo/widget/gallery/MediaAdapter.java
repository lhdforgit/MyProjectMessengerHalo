/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.widget.gallery;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.ListPreloader;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.MediaStoreSignature;
import com.halo.widget.anim.ViewAnim;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author ngannd
 * Create by ngannd on 11/12/2018
 * <p>
 * --------RxPermissions rxPermissions = new RxPermissions(this);
 * --------rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
 * ---------------.subscribe(new Observer<Boolean>() {
 * ---------------@Override public void onSubscribe(Disposable d) {
 * ---------------// Do nothing
 * ---------------}
 * ---------------@Override public void onNext(Boolean aBoolean) {
 * ---------------if (aBoolean) {
 * ---------------// Show Media
 * ---------------} else {
 * ---------------Toast.makeText(FeedDetailAbsAct.this, R.string.editor_permission_media_request_denied, Toast.LENGTH_LONG).show();
 * ---------------}
 * ---------------}
 * ---------------@Override public void onError(Throwable e) {
 * ---------------// Do nothing
 * ---------------}
 * ---------------@Override public void onComplete() {
 * ---------------// Do nothing
 * ---------------}
 * --------});
 * --------<p>
 * --------<p>
 * ---- Media Activity
 * <p>
 * <p>
 * ---------public class MediaAct extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<MediaStoreData>>, MediaListener {
 * ---------<p>
 * -------------------private RecyclerView recyclerView;
 * -------------------@Override public void onCreate(Bundle savedInstanceState) {
 * -----------------------super.onCreate(savedInstanceState);
 * -----------------------GlideApp.get(this).setMemoryCategory(MemoryCategory.HIGH);
 * -----------------------// After init this, Media loaded
 * -----------------------getSupportLoaderManager().initLoader(R.id.loader_id_media_store_data, null, this);
 * <p>
 * ------------------------GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 1);
 * ------------------------layoutManager.setOrientation(RecyclerView.HORIZONTAL);
 * ------------------------recyclerView.setLayoutManager(layoutManager);
 * ------------------------recyclerView.setHasFixedSize(true);
 * -------------------}
 * <p>
 * -------------------@Override public Loader<List<MediaStoreData>> onCreateLoader(int i, Bundle bundle) {
 * -----------------------return new MediaStoreDataLoader(this);
 * -------------------}
 * -------------------@Override public void onLoadFinished(Loader<List<MediaStoreData>> loader, List<MediaStoreData> mediaStoreData) {
 * --------------------------requestManager requestManager = GlideApp.with(this);
 * --------------------------MediaAdapter adapter = new RecyclerAdapter(getActivity(), mediaStoreData, requestManager, this);
 * --------------------------RecyclerViewPreloader<MediaStoreData> preloader = new RecyclerViewPreloader<>(requestManager, adapter, adapter, 3);
 * --------------------------recyclerView.addOnScrollListener(preloader);
 * --------------------------recyclerView.setAdapter(adapter);
 * -------------------}
 * -------------------@Override public void onLoaderReset(Loader<List<MediaStoreData>> loader) {
 * -------------------// Do nothing.
 * -------------------}
 * ---------}
 */
public class MediaAdapter extends RecyclerView.Adapter<MediaAdapter.ListViewHolder>
        implements ListPreloader.PreloadSizeProvider<MediaStoreData>,
        ListPreloader.PreloadModelProvider<MediaStoreData> {

    private final List<MediaStoreData> data;
    private final RequestBuilder<Drawable> requestBuilder;
    private final MediaListener listener;
    private int size;
    private int select = -1;

    private int[] actualDimensions;

    public MediaAdapter(List<MediaStoreData> data, RequestManager requestManager, MediaListener listener) {
        this.data = data;
        requestBuilder = requestManager
                .asDrawable()
                .apply(RequestOptions.centerCropTransform());
        this.listener = listener;

        setHasStableIds(true);
    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.layout_media_item, viewGroup, false);

        if (size <= 0 && viewGroup.getHeight() > 0 && viewGroup.getWidth() > 0) {
            size = viewGroup.getWidth() > viewGroup.getHeight() ? viewGroup.getHeight() : viewGroup.getWidth();
        }

        return new ListViewHolder(view);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull ListViewHolder viewHolder, int position) {
        MediaStoreData current = data.get(position);

        Key signature =
                new MediaStoreSignature(current.mimeType, current.dateModified, 0);

        requestBuilder
                .clone()
                .apply(RequestOptions.signatureOf(signature))
                .load(current.uri)
                .into(viewHolder.image);
        if (current.getType() == MediaStoreData.Type.VIDEO) {
            MediaPlayer mp = MediaPlayer.create(viewHolder.itemView.getContext(), current.uri);
            if (mp != null) {
                int duration = mp.getDuration();
                mp.release();
                viewHolder.setDurationVideo(String.format("%d : %d",
                        TimeUnit.MILLISECONDS.toMinutes(duration),
                        TimeUnit.MILLISECONDS.toSeconds(duration) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration))
                ));
            } else {
                viewHolder.setDurationVideo(null);
            }
        } else {
            viewHolder.setDurationVideo(null);
        }
        viewHolder.bindView(position);
    }

    @Override
    public long getItemId(int position) {
        return data.get(position).rowId;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @NonNull
    @Override
    public List<MediaStoreData> getPreloadItems(int position) {
        return Collections.singletonList(data.get(position));
    }

    @Nullable
    @Override
    public RequestBuilder<Drawable> getPreloadRequestBuilder(@NonNull MediaStoreData item) {
        MediaStoreSignature signature =
                new MediaStoreSignature(item.mimeType, item.dateModified, 0);
        return requestBuilder
                .clone()
                .apply(RequestOptions.signatureOf(signature))
                .load(item.uri);
    }

    @Nullable
    @Override
    public int[] getPreloadSize(@NonNull MediaStoreData item, int adapterPosition,
                                int perItemPosition) {
        return actualDimensions;
    }

    /**
     * ViewHolder containing views to display individual {@link MediaStoreData}.
     */
    final class ListViewHolder extends RecyclerView.ViewHolder {

        private final AppCompatImageView image;
        private final AppCompatImageView sendIv;
        private AppCompatTextView durationTv;


        ListViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            sendIv = itemView.findViewById(R.id.send_iv);
            durationTv = itemView.findViewById(R.id.duration_tv);
            sendIv.setVisibility(View.INVISIBLE);
        }

        public void bindView(int position) {
            ViewAnim.Builder.createAl(sendIv)
                    .zoomOut()
                    .duration(0)
                    .show(position==select);
            image.setOnClickListener(v -> {
                if (sendIv.getVisibility() == View.GONE) {
                    int ol = select;
                    select = position;
                    notifyItemChanged(ol);
                } else {
                    select = -1;
                }
                ViewAnim.Builder.createAl(sendIv)
                        .zoomOut()
                        .duration(250)
                        .show(sendIv.getVisibility() == View.GONE);
            });

            sendIv.setOnClickListener(v -> {
                select = -1;
                if (MediaAdapter.this.data != null && MediaAdapter.this.data.size() > getAdapterPosition()) {
                    MediaAdapter.this.listener.onSendMedia(MediaAdapter.this.data.get(getAdapterPosition()));
                }
                ViewAnim.Builder.createAl(sendIv)
                        .zoomOut()
                        .duration(250)
                        .show(false);
            });
            if (size > 0) {
                itemView.getLayoutParams().width = size;
                itemView.getLayoutParams().height = size;
            }

        }

        public void setDurationVideo(String format) {
            if (format != null) {
                durationTv.setVisibility(View.VISIBLE);
                durationTv.setText(format);
            } else {
                durationTv.setVisibility(View.GONE);
            }
        }
    }
}
