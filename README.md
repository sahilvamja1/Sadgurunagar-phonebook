# સદગુરુનગર ફોન બુક — Android project

This is a native Android starter app project (not a website). It includes Gujarati UI text, local contact storage, search, admin PIN, contact add/edit/delete, and a Call button that opens the phone dialer.

## Default admin password
`2468` — change it from Admin → Admin password બદલો after first launch.

## Build an APK with Android Studio
1. Install Android Studio on a computer.
2. Open this entire `SadgurunagarPhoneBook` folder in Android Studio.
3. Allow Gradle sync and install the Android SDK platform 35 if prompted.
4. Choose **Build → Build Bundle(s) / APK(s) → Build APK(s)**.
5. Android Studio will create the APK under `app/build/outputs/apk/debug/app-debug.apk`.

## Important limitations of this first version
- Contacts are saved locally on each phone. They do NOT automatically sync between villagers' phones.
- The admin PIN is a basic local app lock, not strong server-side security. Anyone who can clear app data can reset local contacts and the PIN.
- Back up the contact list before uninstalling or clearing app data.
- The app uses Android's dialer screen; it does not place calls automatically.

## Project details
- App label: સદગુરુનગર ફોન બુક
- Package: `in.sadgurunagar.phonebook`
- Minimum Android: 6.0 (API 23)
- Target SDK: 35


## Low-power phone option: build the APK in the cloud
This project includes a GitHub Actions workflow at `.github/workflows/build-apk.yml`.
It builds the APK on GitHub-hosted cloud runners, not on your phone.

1. Sign in to GitHub in your phone browser and create a new **public or private** repository.
2. Use **Add file → Upload files** and upload the contents of this project folder (including the hidden `.github` folder). Commit the files to the `main` branch.
3. Open the repository's **Actions** tab. The workflow named **Build Android APK** should run automatically.
4. When it finishes successfully, open the run and download the artifact named **Sadgurunagar-PhoneBook-APK**.
5. Extract the downloaded artifact ZIP. Tap `app-debug.apk` to install it.

Note: GitHub account sign-in and repository upload are required. The APK is compiled by the cloud workflow from this project's source; it is not precompiled in this ZIP.
