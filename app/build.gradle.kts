android {
    namespace = "com.chat.ai"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.chat.ai"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"
    }

}

dependencies {
    // Jetpack Compose & Lifecycle
    implementation(platform("androidx.compose:compose-bom:2024.02.00"))
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")

    // Retrofit & Gson (Network)
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
}