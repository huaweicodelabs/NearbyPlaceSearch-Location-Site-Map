##  HMSMultiKits


## Table of Contents

 * [Introduction](#introduction)
 * [Installation](#installation)
 * [Supported Environments](#supported-environments)
 * [Sample Code](#Sample-Code)
 * [License](#license)


## Introduction
    HMSMultiKits sample code encapsulates APIs of the HUAWEI Location Kit、Map Kit and Site Kit
    In this Codelab, an Android application is created based on Huawei location service and location service and map service. The location service helps developers quickly and accurately obtain user location information. The location service helps developers build products based on location service to obtain the surrounding locations of the current location, mapService helps developers display the map of the current location and plan the path from the current location to the target location.
    · Current location: Use the location service to obtain the current 
      location information.
    · Surrounding search: Search for locations based on the current 
      location.
    · Location details: Query more detailed information about a location.
    · Path planning: Plan the path from the current location to the target 
      location, including driving, walking, and cycling.


## Installation
    Before using HMSMultiKits sample code, check whether the Android Studio environment has been installed. 
    Decompress the HMSMultiKits sample code package.
    Download  HMSMultiKits.zip.
    Decompress the HMSMultiKits.zip.
    Open HMSMultiKits with Android studio.

    
## Supported Environments
	Android Studio
	Java


## Sample Code

    1). Assigning App Permissions
    You need to apply for the permissions in the Manifest file.
    Code:HMSMultiKits/app/src/AndroidManifest.xml
    
    2). Locating function development.
    Obtains the current location information.
    Code:HMSMultiKits/app/src/main/java/com/huawei/multikits/java/activity/MainActivity.java
    Code:HMSMultiKits/app/src/main/java/com/huawei/multikits/kotlin/activity/MainActivity.kt

    3). Get the list of surrounding locations.
    Using the NearbySearchRequest object to obtain the list of surrounding locations.
    Code:HMSMultiKits/app/src/main/java/com/huawei/multikits/java/activity/AddressListActivity.java
    Code:HMSMultiKits/app/src/main/java/com/huawei/multikits/kotlin/activity/AddressListActivity.kt

    4). Obtaining Location Details.
    Using the DetailSearchRequest object to obtain location details.
    Code:HMSMultiKits/app/src/main/java/com/huawei/multikits/java/activity/AddressDetailActivity.java
    Code:HMSMultiKits/app/src/main/java/com/huawei/multikits/kotlin/activity/AddressDetailActivity.kt

    5). Path Planning.
    Plan the paths for obtaining capabilities provided by Huawei Map..
    Code:HMSMultiKits/app/src/main/java/com/huawei/multikits/java/activity/MapDetailActivity.java
    Code:HMSMultiKits/app/src/main/java/com/huawei/multikits/kotlin/activity/MapDetailActivity.kt


##  License
    HUAWEI Location kit sample is licensed under the [Apache License, version 2.0](http://www.apache.org/licenses/LICENSE-2.0).

