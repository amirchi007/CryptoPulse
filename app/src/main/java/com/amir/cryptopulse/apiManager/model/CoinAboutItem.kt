package com.amir.cryptopulse.apiManager.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CoinAboutItem(
    var coinWebsite: String? = "no-data",
    var coinGithub: String? = "no-data",
    var coinTwitter: String? = "no-data",
    var coinDesc: String? = "no-data",
    var coinRedit: String? = "no-data"
) :Parcelable