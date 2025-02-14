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
package org.mastodon4j;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.api.condition.EnabledIfSystemProperties;
import org.mastodon4j.core.MastodonClient;
import org.mastodon4j.core.api.Accounts;
import org.mastodon4j.core.api.BaseMastodonApi;
import org.mastodon4j.core.api.EventStream;
import org.mastodon4j.core.api.Lists;
import org.mastodon4j.core.api.MastodonApi;
import org.mastodon4j.core.api.Statuses;
import org.mastodon4j.core.api.Streaming;
import org.mastodon4j.core.api.Timelines;
import org.mastodon4j.core.api.entities.AccessToken;
import org.mastodon4j.core.api.entities.Account;
import org.mastodon4j.core.api.entities.Event;
import org.mastodon4j.core.api.entities.MList;
import org.mastodon4j.core.api.entities.Status;
import org.mastodon4j.core.api.entities.Subscription;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.mastodon4j.core.api.BaseMastodonApi.QueryOptions.Type.ACCOUNTS;
import static org.mastodon4j.core.api.BaseMastodonApi.QueryOptions.Type.HASHTAGS;

@EnabledIfEnvironmentVariable(named = "MASTODON_ACCESS_TOKEN", matches = ".*")
@EnabledIfEnvironmentVariable(named = "MASTODON_INSTANCE_URL", matches = ".*")
class MastodonClientTest {
    static AccessToken accessToken;
    static MastodonApi client;

    @BeforeAll
    static void prepare() {
        accessToken = AccessToken.create(System.getenv("MASTODON_ACCESS_TOKEN"));
        client = MastodonClient.create(System.getenv("MASTODON_INSTANCE_URL"), accessToken);
    }

    @Test
    void instance() {
        assertThat(client.instance()).isNotNull().satisfies(instance -> {
            log(instance);
            assertThat(instance).isNotNull();
        });
    }

    @Test
    void search() {
        assertThat(client.search("@reinhapa")).isNotNull().satisfies(search -> {
            log(search);
            assertThat(search).isNotNull();
        });
    }

    @Test
    void searchWithQueryOptions() {
        assertThat(client.search(BaseMastodonApi.QueryOptions.of("@reinhapa").limit(1))).isNotNull().satisfies(search -> {
            log(search);
            assertThat(search.accounts()).hasSize(1);
        });
        assertThat(client.search(BaseMastodonApi.QueryOptions.of("@reinhapa").type(ACCOUNTS))).isNotNull().satisfies(search -> {
            log(search);
            assertThat(search.accounts()).hasSize(2);
            assertThat(search.hashtags()).isEmpty();
            assertThat(search.statuses()).isEmpty();
        });
        assertThat(client.search(BaseMastodonApi.QueryOptions.of("#vdz23").type(HASHTAGS))).isNotNull().satisfies(search -> {
            log(search);
            assertThat(search.accounts()).isEmpty();
            assertThat(search.hashtags()).hasSize(1);
            assertThat(search.statuses()).isEmpty();
        });
    }

    @Nested
    @DisplayName("accounts")
    class AccountsTest {
        Accounts accounts = client.accounts();

        @Test
        void instanceCache() {
            assertThat(client.accounts()).isSameAs(accounts);
        }

        @Test
        void get() {
            assertThat(accounts.get("109258482743235692")).isNotNull().satisfies(account -> {
                log(account);
                assertThat(account.id()).isEqualTo("109258482743235692");
                assertThat(account.username()).isEqualTo("reinhapa");
            });
        }

        @Test
        void lists() {
            assertThat(accounts.lists("109258482743235692")).isNotNull().satisfies(lists -> {
                log(lists);
                assertThat(lists).hasAtLeastOneElementOfType(MList.class);
            });
        }

        @Test
        void statuses() {
            assertThat(accounts.statuses("109258482743235692")).isNotNull().satisfies(statuses -> {
                log(statuses);
                assertThat(statuses).hasAtLeastOneElementOfType(Status.class);
            });
        }

        @Test
        void followers() {
            assertThat(accounts.followers("109258482743235692")).isNotNull().satisfies(followers -> {
                log(followers);
                assertThat(followers).hasAtLeastOneElementOfType(Account.class);
            });
        }

        @Test
        void following() {
            assertThat(accounts.following("109258482743235692")).isNotNull().satisfies(following -> {
                log(following);
                assertThat(following).hasAtLeastOneElementOfType(Account.class);
            });
        }

        @Test
        void search() {
            assertThat(accounts.search("Patrick Reinhart")).isNotNull().satisfies(accounts -> {
                log(accounts);
                assertThat(accounts).hasAtLeastOneElementOfType(Account.class);
            });
        }
    }

    @Nested
    @DisplayName("statuses")
    class StatusesTest {
        Statuses statuses = client.statuses();

        @Test
        void instanceCache() {
            assertThat(client.statuses()).isSameAs(statuses);
        }

        @Test
        void get() {
            assertThat(statuses.get("109967065377609606")).isNotNull().satisfies(status -> {
                log(status);
                assertThat(status.id()).isEqualTo("109967065377609606");
                assertThat(status.content()).isEqualTo("<p>Finally got some " +
                        "<a href=\"https://mastodon.social/tags/vdz23\" class=\"mention hashtag\" rel=\"tag\">#" +
                        "<span>vdz23</span></a> preparations done to show on the wall. Still ways to go...\uD83D\uDE05</p>");
            });
        }
    }

    @Nested
    @DisplayName("streaming")
    class StreamingTest {
        Streaming streaming = client.streaming();

        @Test
        void instanceCache() {
            assertThat(client.streaming()).isSameAs(streaming);
        }

        @Test
        void health() {
            assertThat(streaming.health()).isEqualTo("OK");
        }

        @Test
        void stream() throws Exception {
            try (EventStream stream = streaming.stream()) {
                assertThat(stream).isNotNull();
                List<Event> eventList = new ArrayList<>();
//                stream.registerConsumer(eventList::add);
                stream.registerConsumer(event -> {
                    if ("notification".equals(event.event())) {
                        log(event);
                    }
                });
                Subscription subscription = Subscription.stream(true, accessToken, "public");
                assertThatNoException().isThrownBy(() -> stream.changeSubscription(subscription));
                TimeUnit.SECONDS.sleep(20);
            }
        }
    }

    @Nested
    @DisplayName("lists")
    class ListsTest {
        Lists lists = client.lists();

        @Test
        void instanceCache() {
            assertThat(client.lists()).isSameAs(lists);
        }

        @Test
        void get() {
            assertThat(lists.get()).isNotNull().satisfies(listEntries -> {
                log(listEntries);
                assertThat(listEntries).hasAtLeastOneElementOfType(MList.class);
            });
        }

        @Test
        void getOne() {
            assertThat(lists.get("27090")).isNotNull().satisfies(list -> {
                log(list);
                assertThat(list.id()).isEqualTo("27090");
                assertThat(list.title()).isEqualTo("Java Core Developers");
            });
        }

        @Test
        void accounts() {
            assertThat(lists.accounts("27090")).isNotNull().satisfies(list -> {
                log(list);
                assertThat(list).hasAtLeastOneElementOfType(Account.class);
            });
        }
    }

    @Nested
    @DisplayName("timelines")
    class TimelinesTest {
        Timelines timelines = client.timelines();

        @Test
        void instanceCache() {
            assertThat(client.timelines()).isSameAs(timelines);
        }

        @Test
        void pub() {
            assertThat(timelines.pub()).isNotNull().satisfies(statuses -> {
                log(statuses);
                assertThat(statuses).hasAtLeastOneElementOfType(Status.class);
            });
        }

        @Test
        void tag() {
            assertThat(timelines.tag("VDZ23")).isNotNull().satisfies(statuses -> {
                log(statuses);
                assertThat(statuses).hasAtLeastOneElementOfType(Status.class);
            });
        }

        @Test
        void home() {
            assertThat(timelines.home()).isNotNull().satisfies(statuses -> {
                log(statuses);
                assertThat(statuses).hasAtLeastOneElementOfType(Status.class);
            });
        }

        @Test
        void list() {
            assertThat(timelines.list("27090")).isNotNull().satisfies(statuses -> {
                log(statuses);
                assertThat(statuses).hasAtLeastOneElementOfType(Status.class);
            });
        }
    }

    void log(Object object) {
        if (System.getenv("GITHUB_ACTION") == null) {
            System.out.println(object);
        }
    }
}
