
# Flickr Image Searcher

An **Android app** that allows users to search for images using the **Flickr API** and display the results in a **Jetpack Compose list** with pagination.
  
---  

## Getting Started

### Set Up Your Flickr API Key
To use this project, you **must** have a **Flickr API key**.

🔹 **Get a Flickr API Key:**
1. Go to **[Flickr API Key Registration](https://www.flickr.com/services/api/misc.api_keys.html)**
2. Sign in and create an API key.
3. Copy the generated key.

🔹 **Add API Key to `local.properties`:**
1. Open your **`local.properties`** file (in the root of your project).
2. Add the following line:  
   FLICKR_API_KEY=your_api_key_here
3. The app will automatically pick it up.

---  

## Technologies Used

### Android Tech Stack
**Kotlin** – The main language used in the app.    
**Jetpack Compose** – Used for building UI.    
**MVVM Architecture** – For separation of concerns.    
**Paging 3** – For handling infinite scrolling.    
**Koin Annotation** – Dependency Injection.    
**Mockk** – For mocking objects and classes.    
**Retrofit + OkHttp** – For making network requests.     
**Room Database** – To save search history.    
**Timber** – For logging.
  
---  

##  Tests

This project includes **unit/Instrumentation tests** to ensure correctness.


## Further Improvements
- UI tests
- Auto complete for search
- Increase test coverage