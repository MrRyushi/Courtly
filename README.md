# Badminton Court Reservation System - Mobile App

## Overview

This mobile application is a **Court Reservation Platform** designed for badminton enthusiasts and businesses managing court rentals. Users can register, log in, browse available courts, and make reservations. The app also supports memberships, providing exclusive discounts to frequent users. Admins can monitor court availability and reservation data in real-time through the admin interface.

## Features

### **User Features**

- **Register**: Users can create an account by providing their name, email, and password.
- **Login**: Users can log in with their credentials to access app features.
- **View Court Availability**: Browse available courts, check current reservations, and see court status (available, reserved, or in use).
- **Reserve Courts**: Users can reserve up to two courts at a time for a specified duration.
- **Cancel Reservation**: Cancel a reservation at least one hour before the reserved time.
- **View Reservations**: Users can see their past and upcoming reservations.
- **Apply for Membership**: Access exclusive discounts on court reservations and equipment rentals by becoming a member.
- **Recommendations**: Get court reservation recommendations based on previous booking history.

### **Admin Features (Accessible via Admin View)**

- **Manage Courts**: Admins can view the court availability in real-time.
- **Monitor Reservations**: View all user reservations
- **Membership Management**: Track user memberships and view the number of active members.

## Technology Stack

- **Frontend**: Android Studio, Java
- **Backend**: Node.js, Express
- **Local Database**: SQLite (for storing user data, reservations, and memberships locally)
- **Remote Database**: Firebase (to store user preferences, suggest court reservations, and track reservation history)

## Prerequisites

Before running the mobile app, ensure you have the following installed:

- Android Studio

## Installation

1. **Clone the repository:**
   ```bash
   git clone https://github.com/MrRyushi/MOBDEVE-MCO.git
   ```
2. **Run the app**
   - Open the cloned repository using Android Studio and run the emulator

## Usage
- Register/Login: First-time users should register, while returning users can log in with their credentials.
- Browse and Reserve: Explore available courts, check availability, and reserve a court for your preferred time.
- Manage Reservations: Users can view, edit, or cancel their reservations.
- Apply for Membership: Become a member to enjoy discounts on court bookings and rentals.
