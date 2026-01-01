# Firebase Setup Instructions

This app requires Firebase to be configured. Follow these steps to set up Firebase:

## 1. Create a Firebase Project

1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Click "Add project" and follow the setup wizard
3. Enable Google Analytics (optional but recommended)

## 2. Add Android App to Firebase

1. In Firebase Console, click "Add app" and select Android
2. Enter package name: `com.example.jobnest`
3. Download the `google-services.json` file
4. Place the file in: `app/google-services.json`

## 3. Enable Firebase Services

### Authentication
1. Go to Authentication → Sign-in method
2. Enable "Email/Password" authentication
3. Optionally enable "Google" sign-in

### Firestore Database
1. Go to Firestore Database → Create database
2. Start in **test mode** (you can change security rules later)
3. Choose a location close to your users

### Security Rules (Important!)

Update your Firestore security rules to:

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Users collection
    match /users/{userId} {
      allow read: if request.auth != null;
      allow write: if request.auth != null && request.auth.uid == userId;
    }
    
    // Jobs collection
    match /jobs/{jobId} {
      allow read: if request.auth != null;
      allow create: if request.auth != null;
      allow update, delete: if request.auth != null && 
        resource.data.ownerId == request.auth.uid;
    }
    
    // Saved jobs collection
    match /savedJobs/{savedJobId} {
      allow read: if request.auth != null && 
        resource.data.userId == request.auth.uid;
      allow create: if request.auth != null && 
        request.resource.data.userId == request.auth.uid;
      allow delete: if request.auth != null && 
        resource.data.userId == request.auth.uid;
    }
  }
}
```

## 4. Build and Run

After completing the setup:
1. Sync your project with Gradle files
2. Build and run the app
3. The app will automatically connect to Firebase

## Important Notes

- The `google-services.json` file should NOT be committed to version control if it contains sensitive data
- Make sure your app's package name matches the one in Firebase Console
- Test mode in Firestore allows read/write for 30 days - update rules before production

