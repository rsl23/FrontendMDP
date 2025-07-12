# WeCycle - Android Marketplace App

<div align="center">
  <img src="https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white" />
  <img src="https://img.shields.io/badge/Kotlin-0095D5?style=for-the-badge&logo=kotlin&logoColor=white" />
  <img src="https://img.shields.io/badge/Jetpack_Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white" />
  <img src="https://img.shields.io/badge/Firebase-FFCA28?style=for-the-badge&logo=firebase&logoColor=black" />
</div>

## 📱 Overview

WeCycle is a modern Android marketplace application that enables users to buy and sell products with a focus on sustainability and recycling. The app is built using cutting-edge technologies such as Jetpack Compose, Firebase, and implements MVVM architecture with Hilt for dependency injection.

## ✨ Features

### 🔐 Authentication
- **Login & Register** with email/password
- **Google Sign-In** integration
- **Auto-login** functionality
- **Firebase Authentication** backend

### 🏪 Product Management
- **Browse Products** with search and filter
- **Product Details** with complete information
- **Create/Edit Products** with image upload
- **Category-based filtering**
- **Product Image Management** with Coil loading

### 💬 Communication
- **Real-time Chat** system between users
- **Chat List** to view all conversations
- **WhatsApp-like UI** design
- **Message status indicators**

### 💳 Payment System
- **Midtrans Payment Gateway** integration
- **WebView-based** payment process
- **Transaction History** tracking
- **Payment Status** monitoring
- **Sandbox testing** environment

### 📊 Analytics & Reporting
- **Sales Analytics** for sellers
- **Transaction Statistics**
- **Revenue Tracking**
- **Product Performance** metrics

### 👤 User Management
- **Profile Management** with photo upload
- **Edit Profile** functionality
- **User Dashboard** with product management

## 🏗️ Architecture

This application uses **Clean Architecture** with the following components:

```
app/
├── data/
│   ├── model/          # Data models
│   ├── repository/     # Repository implementations
│   └── source/         # Data sources (local & remote)
├── navigation/         # Navigation components
├── ui/
│   ├── components/     # Reusable UI components
│   ├── module/         # Feature modules
│   └── theme/          # App theming
└── Application.kt      # Application class
```

### 🎯 Key Architectural Patterns
- **MVVM (Model-View-ViewModel)**
- **Repository Pattern**
- **Dependency Injection** with Hilt
- **Single Activity** with Jetpack Compose Navigation
- **State Management** with StateFlow and Compose State

## 🛠️ Tech Stack

### Core
- **Kotlin** - Programming language
- **Jetpack Compose** - Modern UI toolkit
- **Android Gradle Plugin** - Build system
- **Hilt** - Dependency injection

### UI & UX
- **Material Design 3** - Design system
- **Coil** - Image loading library
- **Navigation Compose** - Navigation framework
- **Compose Foundation** - UI components

### Backend & Data
- **Firebase Auth** - Authentication service
- **Firebase Firestore** - NoSQL database
- **Room Database** - Local database
- **Retrofit + Moshi** - Network layer
- **Midtrans** - Payment gateway

### Testing
- **JUnit** - Unit testing framework
- **Mockito** - Mocking framework
- **Espresso** - UI testing
- **Truth** - Assertion library

## 📋 Prerequisites

- **Android Studio** Arctic Fox or newer
- **Android SDK** level 24 (Android 7.0) or higher
- **Kotlin** 1.8.0 or newer
- **Java** 8 or newer

## 🚀 Installation

1. **Clone repository**
   ```bash
   git clone https://github.com/rsl23/FrontendMDP.git
   cd FrontendMDP
   ```

2. **Setup Firebase**
   - Create a new project in [Firebase Console](https://console.firebase.google.com/)
   - Add Android app with package name: `com.example.projectmdp`
   - Download `google-services.json` and place it in `app/` folder
   - Enable Authentication and Firestore Database

3. **Setup Midtrans**
   - Register at [Midtrans Dashboard](https://dashboard.midtrans.com/)
   - Get Server Key and Client Key
   - Configure in the application (see Configuration section)

4. **Build and Run**
   ```bash
   ./gradlew assembleDebug
   ```

## ⚙️ Configuration

### Firebase Setup
Make sure the `google-services.json` file is in the `app/` folder and Firebase services are configured:

```kotlin
// Firebase Authentication rules
allow read, write: if request.auth != null;

// Firestore security rules
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /{document=**} {
      allow read, write: if request.auth != null;
    }
  }
}
```

### Network Security Config
The application uses `network_security_config.xml` for network security configuration, especially for Midtrans payment gateway.

## 📱 App Modules

### 🏠 User Dashboard
- Product grid dengan lazy loading
- Search dan filter functionality
- User profile management
- Quick access to all features

### 🛍️ Product Management
- **Create Product**: Form to add new products
- **Edit Product**: Update product information
- **Product Details**: Complete product view with action buttons
- **Image Upload**: Using device gallery

### 💬 Chat System
- **Chat List**: List of all conversations
- **Chat Screen**: Real-time conversation interface
- **Message Status**: Delivered/read indicators
- **User Profile**: User information in chat header

### 📊 Analytics
- Sales performance metrics
- Revenue tracking
- Product statistics
- Transaction analytics

### 💳 Payment (Midtrans)
- Product selection and quantity
- Payment method selection
- WebView-based payment flow
- Payment status confirmation

## 🧪 Testing

### Running Tests
```bash
# Unit tests
./gradlew testDebugUnitTest

# Instrumentation tests
./gradlew connectedAndroidTest

# Generate test coverage report
./gradlew jacocoTestReport
```

### Test Coverage
- **Repository Layer**: ✅ Covered
- **ViewModel Layer**: ✅ Covered
- **UI Components**: 🔄 In Progress
- **Integration Tests**: 🔄 Planned

## 📦 Build Types

### Debug
- Debugging enabled
- Logging enabled
- Midtrans sandbox environment

### Release
- Code obfuscation with ProGuard
- Optimized performance
- Production Midtrans environment

## 🤝 Contributing

1. Fork repository
2. Create feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit changes (`git commit -m 'Add some AmazingFeature'`)
4. Push branch (`git push origin feature/AmazingFeature`)
5. Create Pull Request

### Coding Guidelines
- Follow [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html)
- Use meaningful variable and function names
- Add comments for complex logic
- Write unit tests for new features

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 📞 Contact & Support

- **Developer**: RSL23 Team
- **Repository**: [GitHub Repository](https://github.com/rsl23/FrontendMDP)
- **Issues**: [Report Issues](https://github.com/rsl23/FrontendMDP/issues)

## 🙏 Acknowledgments

- **Google** for Android Jetpack libraries
- **Firebase** for backend services
- **Midtrans** for payment gateway
- **Coil** for image loading solution
- **Material Design** for design guidelines

---

<div align="center">
  <p>Made with ❤️ for sustainable marketplace solutions</p>
  <p>🌱 <strong>WeCycle</strong> - Reduce, Reuse, Recycle 🌱</p>
</div>
