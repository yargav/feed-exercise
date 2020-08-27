package com.lightricks.feedexercise.network

import io.reactivex.Single

/**
 * Mock feed API service that currently holds a single template (hard coded!)
 */

class MockFeedApiService : FeedApiService {
    override fun getFeedData(): Single<FeedData> {
        return Single.just(
            FeedData(
                listOf(
                    FeedData.Template(
                        "lensflare-unleash-the-power-of-nature.json",
                        "01E18PGE1RYB3R9YF9HRXQ0ZSD",
                        false,
                        true,
                        listOf(
                            "01DJ4TM160ETZR0NT4HA2M0ZTK",
                            "01DJ4TM161MRR86QFAXJTWP7NM"
                        ),
                        "lens-flare-template.json",
                        "UnleashThePowerOfNatureThumbnail.jpg"
                    )
                )
            )
        )
    }

}