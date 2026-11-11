// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    id("com.android.library") version "8.7.3" apply false

    alias(libs.plugins.kotlin.android) apply false

    // MISE À JOUR RECOMMANDÉE : 2.50 -> 2.51
    id("com.google.dagger.hilt.android") version "2.60.1" apply false

    id("com.google.gms.google-services") version "4.4.0" apply false
    alias(libs.plugins.kotlin.compose) apply false
}