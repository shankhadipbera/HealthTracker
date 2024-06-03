
# HealthTracker

HealthTracker is a real-time health monitoring application that allows users to monitor their body temperature, SpO2 levels, and heart rate using data transmitted from an Arduino device via Bluetooth. The application provides an analysis report of the collected data and supports user authentication for secure data access. Users can also save and view data of other users through their unique UID for comparative analysis. The application is built using Java, Kotlin, and XML for Android, and it utilizes Firebase for backend services including real-time database functionality, user authentication, and cloud storage.

## Features

### User Authentication 

- Create and manage user accounts
- Secure login and logout functionality
### Real-Time Health Monitoring

- Connect to an Arduino device via Bluetooth
- Display real-time body temperature, SpO2, and heart rate
- Save data for future analysis
### Data Analysis

- Generate and view analysis reports of health data
- Visualize data through graphs and charts
### Multi-User Support

- Save and view data of other users through their unique UID
- Analyze health data of multiple users



## Technologies Used

- **Frontend:**  Kotlin,Java, XML
- **Backend:** Firebase Firestore
- **Authentication:** Firebase Authentication
- **Storage:** Firebase Storage, Mobile Intarnal Storage
- **Bluetooth Communication:** Android Bluetooth API



## Installation

To clone and run this application, you'll need [Git](https://git-scm.com), [Android Studio](https://developer.android.com/studio).

## Clone this repository
git clone https://github.com/shankhadipbera/HealthTracker

Open the project in Android Studio
    
## Usage/Examples

### Connecting to Arduino via Bluetooth
#### Enable Bluetooth

- Make sure Bluetooth is enabled on your Android device.
- Turn on the Arduino device and ensure it is discoverable.
#### Pair with Arduino

- Navigate to the Bluetooth settings on your Android device.
- Pair with the Arduino device using its Bluetooth module.
#### Connect to Arduino in the App

- Open the HealthTracker app.
- Navigate to the "Connect Device" section.
- Select the paired Arduino device from the list to establish a connection.
### Viewing Real-Time Health Data
#### View health metrics

- Once connected to the Arduino device, navigate to the "Health Data" section.
- View real-time data for body temperature, SpO2, and heart rate.
#### Save health data

- Save the current health data for future analysis by clicking the "Save Data" button.
### Analyzing Health Data
####View analysis reports
- Navigate to the "Analysis" section.
- Select a date range to view the analysis report.
- Visualize data through graphs and charts for better understanding.
### Multi-User Support
#### Save other user data

- Navigate to the "Manage Users" section.
- Enter the unique UID of another user to save their health data.
#### View and analyze other user data

- Select a user from the list to view their health data.
- Generate and view analysis reports for multiple users.

## Contributing

Contributions are always welcome!
- Fork the repository.
- Create a new feature branch (git checkout -b feature-name).
- Commit your changes (git commit -m 'Add some feature').
- Push to the branch (git push origin feature-name).
- Create a new Pull Request.


## Screenshots

![App Screenshot](https://blogger.googleusercontent.com/img/b/R29vZ2xl/AVvXsEhHrBy1QoIegQ0C08jTxLUueTDnCMW7N-I_qgSHQrhVrh52a7Tq5huhw1o6UGfWPMPOHvdGvhvSe1_Xu68xFp4IIYe1__5ma2h6YNtl2dLYFjPLdZ0WeI5GCVKLYYPoJ46ywKXjIeJHDgExnJRgCGPrTwFzgNuKUUVYvlH0B8DBgj1bVu_oBwegsrpJNBSD/s16000/1.png)
![App Screenshot](https://blogger.googleusercontent.com/img/b/R29vZ2xl/AVvXsEi2fFmao7Psipy7bsS1B7KvJoD-P9dK3oQYy48ycsv4zbWyXV2_obXxJIK50OHMZaWVqmIqT1sQW-lBlsnYR18rBXwHdxE0_F82mRnB39UOQ7GCqB35e-9gRAdEiCyzOjq7YAi2iXgDMs1TgFN4CkfLjdxvG3hIvLQQoXmj6sz3ahgavfnSW2nAVcCXBSby/s16000/2.png)
![App Screenshot](https://blogger.googleusercontent.com/img/b/R29vZ2xl/AVvXsEgAbK25iHY7XnQRhw9m6TO5wef827f3GtCHN4gqi7dkatpwTcEwDIsbXcyPFSCT-I2JZ3a8hbe_v-JGOMVlytJvLkllZ22wIhZVFcORTr570l2LcabrnFOJpcBlk6INXPCcXy5dCog4ls9bucOF4irLJQS0ZUUpkzVZjJbVlMz7r6lIzy8-gqrOrJSAC_Io/s16000/3.png)



## License

[MIT](https://choosealicense.com/licenses/mit/)

