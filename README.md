# TaskOrganizer

TaskOrganizer is a Kotlin Application in which you can create boards with lists of tasks in order to organize your personal and work life.
The application is based on the Firebase products such as Authentication, Firestore Database and Storage.

Consists from:
- Splash Activity Tab, as a Motion layout for the application.
- Intro Activity Tab, for the user to sign in or sign up in the Firestore Database.
- Sign In Activity Tab, for signing in as an existing user to the application.
- Sign Up Activity Tab, for signing up as a new user to the application.
- Main Activity Tab, is the interface where the user has the list of boards that he created or he is assigned to. He is able to create a new board, access the existing boards or click the menu interface
- Drawer Layout, where the users can change their profile settings or sign out from the application
- Create Board Activity Tab, for creating a new Board of tasks.
- List of Tasks Activity Tab, where users can create new list of tasks and subtasks inside the board, access the subtask features or add signed up members to the board.
- Members Activity Tab, where there is a list of all the assigned members and a button to assign more members to the board.
- Card Activity Tab, for interacting with the card features for each subtask.


![main](https://github.com/ThanosArab/TaskOrganizer/assets/75016979/881ef375-6888-457b-b6c7-d8017623811a) ![drawer](https://github.com/ThanosArab/TaskOrganizer/assets/75016979/01a143ed-4be2-4a33-b7c1-0777231e880f)
![create_board](https://github.com/ThanosArab/TaskOrganizer/assets/75016979/b54354ee-dff2-4054-9ae8-bf96c1d5a59e) ![board](https://github.com/ThanosArab/TaskOrganizer/assets/75016979/0d167273-55d7-4f75-a7a9-59fbf681de62)
![members](https://github.com/ThanosArab/TaskOrganizer/assets/75016979/d5bf1765-57a4-4502-96aa-1e63026907d5) ![card](https://github.com/ThanosArab/TaskOrganizer/assets/75016979/f0d3f13e-f10f-4ff7-8dfd-7291c7eb1dd8)


## Dependencies
   
    implementation 'androidx.core:core-ktx:1.12.0'
    implementation 'androidx.appcompat:appcompat:1.6.1'
    implementation 'com.google.android.material:material:1.9.0'
    implementation 'androidx.constraintlayout:constraintlayout:2.1.4'
    testImplementation 'junit:junit:4.13.2'
    androidTestImplementation 'androidx.test.ext:junit:1.1.5'
    androidTestImplementation 'androidx.test.espresso:espresso-core:3.5.1'

    implementation 'androidx.constraintlayout:constraintlayout:2.2.0-alpha12'
    implementation 'androidx.constraintlayout:constraintlayout-compose:1.1.0-alpha12'

    implementation(platform("com.google.firebase:firebase-bom:32.3.1"))
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation 'com.google.firebase:firebase-auth:22.1.2'
    implementation 'com.google.firebase:firebase-firestore:24.8.1'
    implementation 'com.google.firebase:firebase-storage:20.2.1'

    implementation 'de.hdodenhof:circleimageview:3.1.0'

    implementation 'com.github.bumptech.glide:glide:4.16.0'

    implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4'

    implementation 'androidx.lifecycle:lifecycle-runtime-ktx:2.6.2'

## Launch

1) Download the zip file.
2) Access your Firebase account in order to link the application with your account.
3) Download the google json file in order to insert it to the project.
4) Start exploring the application.
