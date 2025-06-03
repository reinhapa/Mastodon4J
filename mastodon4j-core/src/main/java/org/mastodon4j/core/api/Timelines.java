/*
 * SPDX-License-Identifier: MIT
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2023-2025 Mastodon4J
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.mastodon4j.core.api;

import feign.Param;
import feign.QueryMap;
import feign.RequestLine;
import org.mastodon4j.core.api.entities.Status;

import java.awt.image.BaseMultiResolutionImage;
import java.util.List;

/**
 * Contains all timeline related REST call methods.
 *
 * @see <a href="https://docs.joinmastodon.org/methods/timelines/">mastodon/timelines</a>
 */
public interface Timelines {
    /**
     * <a href="https://docs.joinmastodon.org/methods/timelines/#public">View public timeline</a>.
     *
     * @return a list containing statuses from the public timeline
     */
    @RequestLine("GET /api/v1/timelines/public")
    List<Status> pub();

    /**
     * <a href="https://docs.joinmastodon.org/methods/timelines/#public">View public timeline</a>.
     *
     * @param parameters optional query parameters
     * @return a list containing statuses from the public timeline
     */
    @RequestLine("GET /api/v1/timelines/public")
    List<Status> pub(@QueryMap PubQueryParameters parameters);

    /**
     * <a href="https://docs.joinmastodon.org/methods/timelines/#tag">View hashtag timeline</a>.
     *
     * @param hashtag the tag id without the leading {@code #} symbol
     * @return a list containing statuses from the hashtag timeline
     */
    @RequestLine("GET /api/v1/timelines/tag/{hashtag}}")
    List<Status> tag(@Param("hashtag") String hashtag);

    /**
     * <a href="https://docs.joinmastodon.org/methods/timelines/#tag">View hashtag timeline</a>.
     *
     * @param hashtag the tag id without the leading {@code #} symbol
     * @param parameters optional query parameters
     * @return a list containing statuses from the hashtag timeline
     */
    @RequestLine("GET /api/v1/timelines/tag/{hashtag}}")
    List<Status> tag(@Param("hashtag") String hashtag, @QueryMap TagQueryParameters parameters);

    /**
     * <a href="https://docs.joinmastodon.org/methods/timelines/#home">View home timeline</a>.
     *
     * @return a list containing statuses from the local timeline
     */
    @RequestLine("GET /api/v1/timelines/home")
    List<Status> home();

    /**
     * <a href="https://docs.joinmastodon.org/methods/timelines/#home">View home timeline</a>.
     *
     * @param parameters optional query parameters
     * @return a list containing statuses from the local timeline
     */
    @RequestLine("GET /api/v1/timelines/home")
    List<Status> home(@QueryMap HomeTimelineQueryParameters parameters);

    /**
     * <a href="https://docs.joinmastodon.org/methods/timelines/#link">View link timeline</a>.
     *
     * @param url the URL of the trending article
     * @return a list containing statuses from the link timeline
     */
    @RequestLine("GET /api/v1/timelines/link?url={url}")
    List<Status> link(@Param("url") String url);

    /**
     * <a href="https://docs.joinmastodon.org/methods/timelines/#link">View link timeline</a>.
     *
     * @param url the URL of the trending article
     * @param parameters optional query parameters
     * @return a list containing statuses from the link timeline
     */
    @RequestLine("GET /api/v1/timelines/link?url={url}")
    List<Status> link(@Param("url") String url, @QueryMap LinkTimelineQueryParameters parameters);

    /**
     * <a href="https://docs.joinmastodon.org/methods/timelines/#list">View list timeline</a>.
     *
     * @param listId the list id
     * @return a list containing statuses from the list timeline
     */
    @RequestLine("GET /api/v1/timelines/list/{listId}")
    List<Status> list(@Param("listId") String listId);

    /**
     * <a href="https://docs.joinmastodon.org/methods/timelines/#list">View list timeline</a>.
     *
     * @param listId the list id
     * @param parameters optional query parameters
     * @return a list containing statuses from the list timeline
     */
    @RequestLine("GET /api/v1/timelines/list/{listId}")
    List<Status> list(@Param("listId") String listId, @QueryMap ListTimelineQueryParameters parameters);

    /**
     * Defines the basic timeline query parameters.
     */
    interface BasicTimelineQueryParameters {
        /**
         * @return all results returned will be lesser than this ID. In effect, sets an upper bound on results.
         */
        String maxId();
        /**
         * @return all results returned will be greater than this ID. In effect, sets a lower bound on results.
         */
        String sinceId();
        /**
         * @return results immediately newer than this ID. In effect, sets a cursor at this ID and paginates forward.
         */
        String minId();
        /**
         * @return maximum number of results to return. Defaults to 20 statuses. Max 40 statuses.
         */
        Integer limit();
    }

    interface ContentTimelineQueryParameters  {
        /**
         * @return show only local statuses? Defaults to false.
         */
        Boolean local();
        /**
         * @return show only remote statuses? Defaults to false.
         */
        Boolean remote();
        /**
         * @return show only statuses with media attached? Defaults to false.
         */
        Boolean onlyMedia();
    }

    interface HomeTimelineQueryParameters extends BasicTimelineQueryParameters {
    }

    interface ListTimelineQueryParameters  extends BasicTimelineQueryParameters {
    }

    interface LinkTimelineQueryParameters  extends BasicTimelineQueryParameters {
    }

    interface PubQueryParameters extends BasicTimelineQueryParameters, ContentTimelineQueryParameters {
    }

    interface TagQueryParameters  extends BasicTimelineQueryParameters, ContentTimelineQueryParameters {
        /**
         * @return statuses that contain any of these additional tags.
         */
        List<String> any();
        /**
         * @return statuses that contain all of these additional tags.
         */
        List<String> all();
        /**
         * @return statuses that contain none of these additional tags.
         */
        List<String> none();
    }
}
