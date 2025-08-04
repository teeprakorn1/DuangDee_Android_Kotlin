# 🔮 DuangDee Android App (Kotlin)

**DuangDee** is a fortune-telling Android application developed using **Kotlin**, designed to deliver various types of spiritual predictions including **palmprint reading**, **tarot card reading**, and **birthday-based forecasts**. The app also provides **daily horoscope results** with a user-friendly interface.

This project was developed as part of a university assignment in **Year 3, Semester 1**.

---

## 🚀 Key Features

- ✋ **Palmprint Prediction** – Capture palm images and get predictions using a machine learning model
- 🃏 **Tarot Card Reading** – Draw cards and receive spiritual insights
- 🎂 **Birthday Horoscope** – Analyze zodiac-based predictions using birth date
- 📅 **Daily Horoscope** – Daily astrological summary personalized for the user
- 👤 User authentication and profile system with **Firebase OAuth login**
- 🔐 Token encryption for every API request to enhance security
- 📧 Password reset via email functionality
- 🛡️ Rate limiting and XSS protection implemented in the Node.js backend
- 🔗 Connects to backend APIs and ML models

---

## ⚙️ System Architecture

The DuangDee system is divided into **4 major components**, each responsible for a specific part of the application:

| Component           | Description |
|---------------------|-------------|
| **📱 Mobile Frontend (Kotlin)** | Android application developed using Kotlin and Jetpack libraries. Responsible for user interaction, UI display, with backend and AI services. |
| **🌐 Backend API (Node.js)** | RESTful API server built with Node.js and Express. Handles user authentication (including Firebase OAuth), profile management, tarot logic, birthday horoscope, and daily fortune content. Implements token encryption, rate limiting, and XSS protection. |
| **🧠 AI Engine (Python)** | Python-based microservice (Flask) that processes palmprint images using an ML model (OpenCV / TensorFlow). Receives images from mobile app and returns predictions. |
| **🖥️ Web Admin (React.js)** | Web-based admin dashboard built with React. Allows admins to manage users, tarot cards, and horoscope content. Node.js backend. |

> All components are loosely coupled and communicate via REST APIs.

---

## 🧰 Tech Stack

### 💻 Frontend (Android)

- Kotlin
- ViewModel, LiveData, Retrofit2
- Coroutines for asynchronous tasks
- Glide for image loading
- Material Design UI components
- **Designed with Figma**

### 🌐 Backend (API)

- Node.js + Express
- Firebase OAuth Authentication integration
- JWT Token encryption for every request
- Password reset email system
- Rate limiting to prevent abuse
- XSS attack protection
- MySQL
- REST API for horoscope, tarot, and user handling

## 🖥️ Web Admin Panel

An additional **Admin Dashboard** is developed using **React.js** to manage the backend data:

- Manage users, horoscopes, and tarot card sets
- View palmprint submission logs
- Admin authentication
- Built using React + Tailwind CSS

## 🎓 Academic Context

This full system was developed as part of a **third-year university project (Semester 1)**.  
It demonstrates skills in:

- Cross-platform system design
- Full-stack development
- Mobile-to-ML integration
- UI/UX design with Figma
- Automated testing with Robot Framework

### 🧠 AI Module

- Python (Flask)
- OpenCV or TensorFlow/Keras (for palmprint analysis)
- Trained model hosted separately from backend server

## 🧪 Testing

The system is tested using the following tools:

- 🤖 **Robot Framework** – Used for writing acceptance tests in a readable syntax
- 🌐 **SeleniumLibrary** – Automates UI testing for both mobile and web interfaces
- ✅ Tests cover:
  - User login & registration flows
  - Fortune prediction flows (palmprint, tarot, birthday)
  - UI navigation and backend API response validation

> All automated tests are written in `.robot` files and can be run with a single command.

---

## 📸 Screenshots (Placeholder)

> Add real UI screenshots here from the app (designed in Figma and implemented in Android)

| Main Screen | Profile Screen | Menu Screen | Game Screen | Horoscope Screen | Palmprint Screen |
|-----------|--------------|--------|-----------|-----------|-----------|
| ![](screenshots/main_screen.jpg) | ![](screenshots/profile_screen.jpg) | ![](screenshots/menu_screen.jpg) | ![](screenshots/game_screen.jpg) | ![](screenshots/horoscope_screen.jpg) | ![](screenshots/palm_screen.jpg) | 
