# Weather App - Kotlin & Jetpack Compose

A feature-rich, modern Android weather application built with Kotlin and Jetpack Compose. This app demonstrates modern Android development best practices, including a clean MVVM architecture, dependency injection with Hilt, state-driven UI, and robust user management features.


## Core Features

-   **Dynamic Weather Details:** Get real-time weather data for any city.
-   **User Authentication:** Secure login and registration system.
-   **Auto-Login:** Remembers the user's session for a seamless experience.
-   **City Management:** Users can save and manage a list of their favorite cities.
-   **Dynamic City Search:** An API-powered search bar with auto-suggestions for finding cities worldwide.
-   **5-Day Forecast:** Plan ahead with a detailed 5-day weather forecast.
-   **Hourly Breakdown:** See temperature and weather changes on an hourly basis.
-   **Dark Mode Support:** A sleek, theme-aware UI that adapts to system settings.
-   **Temperature Unit Toggle:** Switch between Celsius and Fahrenheit.
-   **State Preservation:** UI state, including focus and cursor position, is preserved during screen rotations.

## Technical Features

-   **100% Kotlin & Jetpack Compose:** Built entirely with modern Android tools.
-   **MVVM Architecture:** A clean and scalable Model-View-ViewModel architecture.
-   **Repository Pattern:** A single source of truth for all application data.
-   **Dependency Injection with Hilt:** Manages dependencies efficiently across the app.
-   **Retrofit & OkHttp:** For robust and efficient networking.
-   **Room Database:** Persists user data and saved cities locally.
-   **Jetpack Navigation:** For navigating between composable screens.
-   **Coroutines & Flow:** For handling asynchronous operations and managing state.
-   **State-Driven UI:** UI components react to state changes from the ViewModel.

## Screens

### 1. Login & Registration

-   Users can create an account or log in.
-   Features robust input validation for username, password, and city.
-   The session is persisted, enabling auto-login when the app is relaunched.

### 2. Home Screen

-   Displays the current weather for the user's primary saved city.
-   Shows a summary of the 5-day forecast.
-   Allows users to navigate to the City Management and Weather Details screens.
-   Features the Celsius/Fahrenheit toggle.

### 3. City List Screen

-   Displays a list of the user's saved cities.
-   Users can add a city by selecting a suggestion or by typing a full city name.
-   Users can delete cities from their list.
-   Shows a list of popular cities to easily add them.

### 4. Weather Details Screen

-   Provides a detailed weather report for a selected city and date.
-   **Hourly Breakdown:** A scrollable horizontal list of weather conditions for the next few hours.
-   **Detailed Stats Grid:** Displays information like Humidity, Wind Speed, Pressure, Visibility, and Cloudiness.
-   Sunrise and Sunset times.

## Architecture & Flow

This project follows modern Android architecture principles to create a scalable and maintainable codebase.

1.  **UI Layer (Composables):** The UI is built entirely with Jetpack Compose. Screens are "state-driven," meaning they are stateless composables that observe a `StateFlow` from a ViewModel. User interactions are sent to the ViewModel as events.

2.  **ViewModel Layer (Hilt ViewModels):** Each screen has a corresponding ViewModel that holds the UI state and business logic. It receives events from the UI, processes them (often using coroutines), and updates the UI state. ViewModels never know about the specifics of the UI.

3.  **Repository Layer:** The ViewModels communicate with Repositories, which are the single source of truth for app data. The repositories abstract away the origin of the data—whether it comes from a network API or a local database.

4.  **Data Layer (Room & Retrofit):**
    -   **Networking:** Retrofit is used to fetch live weather data (from OpenWeatherMap).
    -   **Local Persistence:** A Room database is used to store user credentials, login sessions, and the list of saved cities.

## Setup

To build and run this project, you will need to provide your own API keys.

1.  **Clone the repository:** git clone https://github.com/GitWithMithunS/Weather-app-Kotlin.git
2.  **Provide your own API KEY:** OPEN_WEATHER_API_KEY="YOUR_OPENWEATHERMAP_API_KEY"
    
## Architectural  Diagram
<img width="8191" height="3254" alt="Untitled diagram-2026-02-03-175514" src="https://github.com/user-attachments/assets/5937522b-9268-48c3-afcb-bc9e564088e1" />

    


