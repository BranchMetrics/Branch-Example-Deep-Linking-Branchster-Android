Branchster-Android
==================

## Configuring Keys and Secrets
This repository does not contain API keys so you need to define your own in order for the connected APIs to function. With the exception of the *Crashlytics ApiKey* (see the note below) the keys are defined as XML string resources and referenced at build-time. If you build the project as-is, you will get something like the following error:

```
Error: .. No resource found that matches the given name (at 'value' with value '@string/..').
```

To set up your own API keys and get rid of this error:

1. Open up **api_keys.xml** which exists in the */res/values* folder.
2. Insert your Branch App Key, Facebook ID and Twitter key/secret in this file.
3. Clean/Rebuild your project.

```XML
<?xml version="1.0" encoding="utf-8"?>
<resources>

    <!--
    Your Branch App Key Goes Here
    If you don't have one, see the Branch Android Quick-Start for how to get one:
    https://github.com/BranchMetrics/Branch-Integration-Guides/blob/master/android-quick-start.md
    -->
    <string name="bnc_app_key">YOUR BRANCH APP KEY</string>

    <!--
    Your Your Facebook App ID Goes Here
    If you don't have one, see the Facebook SDK for Android documentation:
    https://developers.facebook.com/docs/android
    -->
    <string name="facebook_app_id">YOUR FACEBOOK APP ID</string>

    <!--
    Your Twitter Key and Secret Goes Here
    If you don't have these, see the Twitter Kit for Android documentation:
    https://dev.twitter.com/twitter-kit/android
    -->
    <string name="twitter_key">YOUR TWITTER APP KEY</string>
    <string name="twitter_secret">YOUR TWITTER APP SECRET</string>

</resources>
```

### Fabric/Crashlytics (required for Twitter integration)

Twitter's Fabric framework doesn't currently allow the *com.crashlytics.ApiKey* meta-data to be specified as a String resource in the *ApplicationManifest.xml* file. If you try to add the key as a *@string/..* reference you will get a *Crashlytics Developer Tools error* at build time.

So to get Twitter integration working, you will need to insert your Crashyltics key directly in *AndroidManifest.xml* like so:

```XML
<manifest .. >
  <application .. />
  
    <activity .. />

    <meta-data 
      android:name="com.crashlytics.ApiKey"
      android:value="YOUR FABRIC/CRASHLYTICS ApiKey" />
      
      --
      
  </application>
</manifest>
```
