/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.widget.gallery;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
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
public class MediaAdapterV2 extends RecyclerView.Adapter<MediaAdapterV2.ListViewHolder>
        implements ListPreloader.PreloadSizeProvider<MediaStoreData>,
        ListPreloader.PreloadModelProvider<MediaStoreData> {

    private final List<MediaStoreData> data;
    private final RequestBuilder<Drawable> requestBuilder;
    private final MediaListenerV2 listener;

    private int[] actualDimensions;

    public MediaAdapterV2(List<MediaStoreData> data, RequestManager requestManager, MediaListenerV2 listener) {
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
        View view = inflater.inflate(R.layout.layout_media_v2_item, viewGroup, false);
        return new ListViewHolder(view);
    }

    private HashMap<Uri, String> durations = new HashMap<>();

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull ListViewHolder viewHolder, int position) {
        MediaStoreData current = data.get(position);
        Key signature = new MediaStoreSignature(current.mimeType, current.dateModified, 0);

        requestBuilder
                .clone()
                .apply(RequestOptions.signatureOf(signature))
                .load(current.uri)
                .into(viewHolder.image);
        if (current.getType() == MediaStoreData.Type.VIDEO) {

            String duration = durations.get(current.uri);
            if (duration!= null){
                viewHolder.setDurationVideo(duration);
            }else {
                MediaPlayer mp = MediaPlayer.create(viewHolder.itemView.getContext(), current.uri);
                if (mp != null) {
                    int durationTime = mp.getDuration();
                    mp.release();
                    String durationContent = String.format("%d : %d",
                            TimeUnit.MILLISECONDS.toMinutes(durationTime),
                            TimeUnit.MILLISECONDS.toSeconds(durationTime) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(durationTime)));
                    durations.put(current.uri, durationContent);
                    viewHolder.setDurationVideo(durationContent);
                } else {
                    viewHolder.setDurationVideo(null);
                }
            }
        } else {
            viewHolder.setDurationVideo(null);
        }
        viewHolder.bindView(current);
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
        if (data!=null && data.size()>position && position>=0){
            MediaStoreData model = data.get(position);
            return Collections.singletonList(model);
        }
//        return Collections.singletonList(data.get(position));
        return new ArrayList<>();
    }

    @Nullable
    @Override
    public RequestBuilder<Drawable> getPreloadRequestBuilder(@NonNull MediaStoreData item) {
        MediaStoreSignature signature = new MediaStoreSignature(item.mimeType, item.dateModified, 0);
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
        private final View sendBg;
        private AppCompatTextView durationTv;


        ListViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            sendIv = itemView.findViewById(R.id.send_iv);
            durationTv = itemView.findViewById(R.id.duration_tv);
            sendBg = itemView.findViewById(R.id.send_layout);
            sendIv.setVisibility(View.INVISIBLE);
        }

        public void bindView(MediaStoreData model) {
            if (listener!= null && model!=null){
                ViewAnim.Builder.createAl(sendIv)
                        .zoomOut()
                        .duration(0)
                        .show(listener.isSelected(model.getRowId()));
                boolean show  =listener.isMaxItem() || listener.isSelected(model.getRowId());
                ViewAnim.Builder.createAl(sendBg)
                        .duration(0)
                        .show(show);
                image.setOnClickListener(v -> {
                    if (listener.isSelected(model.getRowId())){
                        boolean removed = listener.onChecked(model, false);
                        if (removed){
                            ViewAnim.Builder.createAl(sendIv)
                                    .zoomOut()
                                    .duration(250)
                                    .show(false);
                            ViewAnim.Builder.createAl(sendBg)
                                    .duration(250)
                                    .show(false);
                        }
                    }else {
                        boolean added = listener.onChecked(model, true);
                        if (added){
                            ViewAnim.Builder.createAl(sendIv)
                                    .zoomOut()
                                    .duration(250)
                                    .show(true);
                            ViewAnim.Builder.createAl(sendBg)
                                    .duration(250)
                                    .show(true);
                        }
                    }
                });
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
