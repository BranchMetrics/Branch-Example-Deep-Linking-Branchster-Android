Branchster-Android
==================

### Add your app key to your project

After you register your app, your app key can be retrieved on the [Settings](https://dashboard.branch.io/#/settings) page of the dashboard. Now you need to add it to your project.

1. Open your res/values/strings.xml file
1. Add a new string entry with the name "bnc_app_key" and value as your app key
```xml
<resources>
    <!-- Other existing resources -->

    <!-- Add this string resource below, and change "your app key" to your app key -->
    <string name="bnc_app_key">"your app key"</string>
</resources>
```

1. Open your AndroidManifest.xml file
1. Add the following new meta-data
```xml
<application>
    <!-- Other existing entries -->

    <!-- Add this meta-data below; DO NOT changing the android:value -->
    <meta-data android:name="io.branch.sdk.ApplicationId" android:value="@string/bnc_app_key" />
</application>
```
