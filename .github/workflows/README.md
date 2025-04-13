# GitHub Workflow Setup

## Workflows Overview

### 1. Android CI (android.yml)
This workflow handles the main Android build process with Firebase integration.

**Triggers:**
- Push to `main` branch
- Pull requests to `main` branch

**Key Steps:**
- Sets up JDK 17
- Creates `google-services.json` from repository secrets
- Builds the Android app
- Uploads the debug APK as an artifact

**Downloading Build Artifacts:**
1. Go to the Actions tab in your GitHub repository
2. Click on the completed Android CI workflow run
3. Scroll down to the Artifacts section
4. Click on the "app-debug" artifact to download the APK
5. The downloaded file will contain the debug APK from the build

### 2. Code Quality (code-quality.yml)
This workflow runs code quality checks using ktlint.

**Triggers:**
- Push to `main` branch
- Pull requests to `main` branch

**Key Steps:**
- Sets up JDK 17
- Configures Gradle
- Runs ktlint checks using `./gradlew ktlintCheck`

## Setting up the GOOGLE_SERVICES_JSON Secret

To use the GitHub workflow for building the Android app, you need to add the `google-services.json` file as a repository secret. Here's how to do it:

1. Go to your Firebase Console (https://console.firebase.google.com/)
2. Select your project
3. Go to Project Settings (gear icon)
4. In the "Your apps" section, find your Android app
5. Download the `google-services.json` file
6. Open the file in a text editor and copy its entire contents
7. Go to your GitHub repository
8. Click on "Settings" tab
9. In the left sidebar, click on "Secrets and variables" > "Actions"
10. Click on "New repository secret"
11. Name: `GOOGLE_SERVICES_JSON`
12. Value: Paste the entire contents of the `google-services.json` file
13. Click "Add secret"

Now, when the GitHub workflow runs, it will automatically create the `google-services.json` file in the app directory using the contents of this secret.

## Security Note

The `google-services.json` file contains sensitive information about your Firebase project. By storing it as a GitHub secret, you ensure that:

1. The file is not committed to your repository
2. The file is only accessible to users with appropriate permissions
3. The file is securely used during the build process

Never commit the actual `google-services.json` file to your repository. 