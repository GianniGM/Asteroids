# Asteroids solution

## Api Key
1. Get your API key following this URL - https://api.nasa.gov/ 
2. Copy your API key `into app/src/main/java/com/udacity/asteroidradar/Constants.kt` class

```kotlin
object Constants {
    const val API_QUERY_DATE_FORMAT = "yyyy-MM-dd"
    const val DEFAULT_END_DATE_DAYS = 7
    const val BASE_URL = "https://api.nasa.gov/"
    const val API_KEY = ApiKey.INSERT_API_KEY_HERE
}
```