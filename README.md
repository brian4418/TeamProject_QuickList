# Quick List: Task Management App

Welcome to the Quick List app repository! Quick List is an intuitive, streamlined to-do list Android application developed to solve common issues such as poor time management and disorganized task planning. The app is particularly useful for students, professionals, or anyone looking for an efficient and user-friendly task management solution.

## Table of Contents

* [About the Project](#about-the-project)
* [Features](#features)
* [Getting Started](#getting-started)
* [Installation](#installation)
* [Usage](#usage)
* [Technologies Used](#technologies-used)
* [Team and Contributors](#team-and-contributors)
* [Challenges and Solutions](#challenges-and-solutions)
* [Future Work](#future-work)
* [Useful Links](#useful-links)

## About the Project

Quick List addresses everyday struggles in managing various responsibilities, like work, academics, and personal life, by minimizing missed deadlines and forgotten tasks. It allows users to effortlessly add, manage, and prioritize their daily tasks, complemented by intelligent scheduling and timely push notifications.

The vibrant and user-friendly UI leverages the iconic UW-Madison red, while Firebase integration ensures seamless cross-device synchronization and data persistence.

## Features

* **Firebase Authentication & Database**: Allows users to securely log in and manage tasks across multiple devices.
* **Gesture-Based Interactions**: Easily edit or delete tasks with simple swipe gestures.
* **Push Notifications**: Get timely reminders for task deadlines and important notifications directly on your mobile device.
* **Color-Coded Priorities**: Quickly visualize tasks based on their priority level.
* **Compact & Responsive UI**: A clean interface optimized for readability and usability across all mobile screens.
* **Flexible Task Management**: Supports adding tasks with details like title, deadline, priority level, and recurrence (daily, weekly, monthly).

## Getting Started

Follow these instructions to get Quick List running on your local Android development environment.

### Prerequisites

* Android Studio
* Kotlin support enabled
* Firebase Account

### Installation

1. Clone the repository:

```bash
git clone https://github.com/brian4418/TeamProject_QuickList.git
```

2. Open the project in Android Studio.
3. Set up Firebase:

   * Create a Firebase project.
   * Add the Android app to your Firebase project.
   * Download and add `google-services.json` to your app module.

### Running the App

1. Build and run the application on your emulator or physical Android device.

## Usage

* **Login & Sign Up**: Authenticate using your email and password.
* **Task Management**: Add, edit, delete, or search for tasks efficiently.
* **Settings Management**: Customize notification preferences and manage your account.

## Technologies Used

* **Languages**: Kotlin, XML
* **Frameworks & Libraries**: Android SDK, Firebase Authentication, Firebase Realtime Database, Android WorkManager (for notifications)
* **Development Tools**: Android Studio, Firebase Console

## Challenges and Solutions

* **Database Integration**: Initially struggled due to limited previous experience; successfully integrated Firebase for robust task storage and synchronization.
* **Task Organization**: Merged weekly and monthly task views into a unified, efficient design.

## Future Work

* **Filtering**: Implement advanced task filters by date, priority, and category.
* **Categorization**: Introduce categories for tasks (e.g., Assignments, Housework).
* **Enhanced Views**: Add different visualization modes such as Card View or Calendar View.
