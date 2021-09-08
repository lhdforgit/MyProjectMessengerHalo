/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.giphy.sdk.core.network.api;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.giphy.sdk.core.models.enums.LangType;
import com.giphy.sdk.core.models.enums.MediaType;
import com.giphy.sdk.core.models.enums.RatingType;
import com.giphy.sdk.core.network.response.ListCategoryResponse;
import com.giphy.sdk.core.network.response.ListMediaResponse;
import com.giphy.sdk.core.network.response.ListStickerPacksResponse;
import com.giphy.sdk.core.network.response.ListTermSuggestionResponse;
import com.giphy.sdk.core.network.response.MediaResponse;
import com.giphy.sdk.core.network.response.StickerPackResponse;

import java.util.List;
import java.util.concurrent.Future;

/**
 * The interface describing all the endpoints supported by the sdk.
 */
public interface GPHApi {
    /**
     * Search for gifs or stickers
     *
     * @param searchQuery       search query term or phrase
     * @param type              can be sticker or gif
     * @param limit             (optional) number of results to return, maximum 100. Default 25.
     * @param offset            (optional) results offset, defaults to 0.
     * @param rating            (optional) limit results to those rated (y,g, pg, pg-13 or r).
     * @param lang              (optional) specify default country for regional content; format is 2-letter ISO 639-1 country code.
     * @param pingbackId        (optional) specify pingback user id
     * @param completionHandler
     * @return
     */
    @NonNull
    public Future search(@NonNull String searchQuery, @Nullable MediaType type, @Nullable Integer limit,
                         @Nullable Integer offset, @Nullable RatingType rating,
                         @Nullable LangType lang,
                         @Nullable String pingbackId,
                         @NonNull final CompletionHandler<ListMediaResponse> completionHandler);

    /**
     * Get the trending gifs or stickers
     *
     * @param type              can be sticker or gif
     * @param limit             (optional) limits the number of results returned. By default returns 25 results.
     * @param offset            (optional) results offset, defaults to 0);
     * @param rating            (optional) limit results to those rated (y,g, pg, pg-13 or r).
     * @param completionHandler
     * @return
     */
    @NonNull
    public Future trending(@Nullable MediaType type, @Nullable Integer limit,
                           @Nullable Integer offset, @Nullable RatingType rating,
                           @NonNull final CompletionHandler<ListMediaResponse> completionHandler);

    /**
     * The translate API draws on search, but uses the Giphy "special sauce" to handle translating from one vocabulary to another.
     *
     * @param term              term or phrase to translate into a GIF
     * @param type              can be sticker or gif
     * @param rating            (optional) limit results to those rated (y,g, pg, pg-13 or r).
     * @param lang              (optional) specify default country for regional content; format is 2-letter ISO 639-1 country code.
     * @param completionHandler
     * @return
     */
    @NonNull
    public Future translate(@NonNull String term, @Nullable MediaType type, @Nullable RatingType rating,
                            @Nullable LangType lang,
                            @NonNull final CompletionHandler<MediaResponse> completionHandler);

    /**
     * Returns a random GIF, limited by tag. Excluding the tag parameter will return a random GIF from the Giphy catalog.
     *
     * @param tag               the GIF tag to limit randomness by
     * @param type
     * @param rating            limit results to those rated (y,g, pg, pg-13 or r).
     * @param completionHandler
     * @return
     */
    @NonNull
    public Future random(@NonNull String tag, @Nullable MediaType type, @Nullable RatingType rating,
                         @NonNull final CompletionHandler<MediaResponse> completionHandler);


    /**
     * Returns a list of categories
     *
     * @param limit
     * @param offset
     * @param sort
     * @param completionHandler
     * @return
     */
    @NonNull
    public Future categoriesForGifs(@Nullable Integer limit, @Nullable Integer offset,
                                    @Nullable String sort,
                                    @NonNull final CompletionHandler<ListCategoryResponse> completionHandler);

    /**
     * Returns a list of subcategories for a category
     *
     * @param categoryEncodedName
     * @param limit
     * @param offset
     * @param sort
     * @param completionHandler
     * @return
     */
    @NonNull
    public Future subCategoriesForGifs(@NonNull String categoryEncodedName,
                                       @Nullable Integer limit, @Nullable Integer offset,
                                       @Nullable String sort,
                                       @NonNull final CompletionHandler<ListCategoryResponse> completionHandler);

    /**
     * Returns a list of gifs based on category & subcategory
     *
     * @param categoryEncodedName
     * @param subCategoryEncodedName
     * @param limit
     * @param offset
     * @param ratingType
     * @param langType
     * @param completionHandler
     * @return
     */
    @NonNull
    public Future gifsByCategory(@NonNull String categoryEncodedName,
                                 @NonNull String subCategoryEncodedName,
                                 @Nullable Integer limit, @Nullable Integer offset,
                                 @Nullable RatingType ratingType, @Nullable LangType langType,
                                 @NonNull final CompletionHandler<ListMediaResponse> completionHandler);

    /**
     * Returns meta data about a GIF, by GIF id
     *
     * @param gifId             the id of the gif we want to return
     * @param completionHandler
     * @return
     */
    @NonNull
    public Future gifById(@NonNull String gifId,
                          @NonNull final CompletionHandler<MediaResponse> completionHandler);

    /**
     * Returns meta data about multiple gifs
     *
     * @param gifIds the list of ids of the gifs we want to return
     * @return
     */
    @NonNull
    public Future gifsByIds(@NonNull List<String> gifIds,
                            @NonNull final CompletionHandler<ListMediaResponse> completionHandler);

    /**
     * Returns meta data about multiple gifs
     *
     * @param term              the list of ids of the gifs we want to return
     * @param completionHandler
     * @return
     */
    @NonNull
    public Future termSuggestions(@NonNull String term,
                                  @NonNull final CompletionHandler<ListTermSuggestionResponse> completionHandler);


    /**
     * Returns all sticker packs
     *
     * @param completionHandler
     * @return
     */
    @NonNull
    public Future stickerPacks(@NonNull final CompletionHandler<ListStickerPacksResponse> completionHandler);

    /**
     * Returns all child sticker packs for a given pack id
     *
     * @param packId            The ID of the sticker pack to get the children of
     * @param completionHandler
     * @return
     */
    @NonNull
    public Future stickerPackChildren(@NonNull String packId,
                                      @NonNull final CompletionHandler<ListStickerPacksResponse> completionHandler);

    /**
     * Returns an individual sticker pack
     *
     * @param packId            The ID of the sticker pack
     * @param completionHandler
     * @return
     */
    @NonNull
    public Future stickerPackById(@NonNull String packId,
                                  @NonNull final CompletionHandler<StickerPackResponse> completionHandler);

    /**
     * Gets all individual stickers for a given sticker pack
     *
     * @param packId            The ID of the sticker pack
     * @param completionHandler
     * @return
     */
    @NonNull
    public Future stickersByPackId(@NonNull String packId,
                                   @Nullable Integer limit, @Nullable Integer offset,
                                   @NonNull final CompletionHandler<ListMediaResponse> completionHandler);

    /**
     * Gets all gifs for a channel id
     *
     * @param channelId         The ID of the channel
     * @param completionHandler
     * @return
     */
    @NonNull
    public Future channelContent(@NonNull String channelId, @Nullable MediaType mediaType,
                                 @Nullable Integer limit, @Nullable Integer offset,
                                 @NonNull final CompletionHandler<ListMediaResponse> completionHandler);
}

