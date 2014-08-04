# Quick Circle Apps

**Quick Circle Apps** is an Android application made by Yoav Sternberg.
The application provide modules for the G3 Quick Circle Case.

[![Get it on Google Play](http://www.android.com/images/brand/get_it_on_play_logo_small.png)](https://play.google.com/store/apps/details?id=com.yoavst.quickapps)

## How it works?
The app uses LG Quick Circle SDK and QSlide SDK.  
* Torch - Enable/Disable camera flash.  
* Music - Register `NotificationListenerService` that implements `RemoteController.OnClientUpdateListener`.  
* Notifications - Register another `NotificationListenerService`.  
* Calendar - Reading events data from `CalendarContract.Events`.  
* Toggles - Each toggles use its permissions to change the state.  
* Stopwatch - Uses `TimerTask` that run every 10 milliseconds to update the clock.
* news - Use Feedly Cloud Api to receive the newest 20 articles from the user feed.  

## Acknowledgements
* LG for their Quick Circle and QSlide SDKs.  
* Excilys team for [Android annotations](https://github.com/excilys/androidannotations/wiki) that made my life a lot easier.  
* Joan Zapata for [android-iconify](https://github.com/JoanZapata/android-iconify).  
* Google for Android, Support V4, V13 packages and GSON.  
* Jake Wharton for [viewpagerindicator](http://viewpagerindicator.com/).  
* Pablo Fernandez For [Scribe-java](https://github.com/fernandezpablo85/scribe-java).  
* [Jeppe Foldager](http://www.blackbearblanc.dk/) for its wonderful icons.  

License
-------

    Copyright 2014 Yoav Sternberg

    Quick Circle Apps is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Quick Circle Apps is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Quick Circle Apps. If not, see <http://www.gnu.org/licenses/>.

---
