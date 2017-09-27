# Mine-Field-Alarm-Android

## Application and the idea

This project started as an idea of [Organisation of Amputees UDAS Republike Srpske](https://en.wikipedia.org/wiki/The_Organization_of_Amputees_UDAS_Republike_Srpske)". 

Since Bosnia and Herzegovina has a problem with landmine pollution, UDAS decided to develop an application that will trigger an alarm on the android device when carrier comes near the minefield.

There are still around 12 000 minefields in Bosnia and Herzegovina. There are more than 1500 post-war victims of landmines in Bosnia.

The database with coordinates of existing minefields currently exist handwritten on papers.

As an volounteer in organisation UDAS, I began implementing this application for the University project.

Because I don't have much time to develop whole application myself, and since that this application can be useful 
in any other country or area that has the same problem as Bosnia, I decided to make this project an open source so I 
am calling any java / android programmer, UI designer or anyone with any good idea for improvement and application 
develompent to participate and contribute in one good and human idea - to possibly save a life.

## What do we have right now?

At this stage the app has basic interface. When you start the app it opens google map and draws mine fields in radius. It connects to Google Play Services API to receive updates of current geolocation of the user and uses geofences to compare locations and possibly trigger the alarm if user apreaches that radius. 

## What do we need?

### Database
We have to decide which database will be used and how the minefields will be managed. We have to think of good idea to update 
minefield DB because some minefields (hopefully all at one moment) will be cleared during the time. There are some decisions to be made when it comes to design of the app, possibly a new branch for 2 different approaches.

### Support for multiple devices/screens
Right now there is only vertical orientation supported, it should be changed.

### Custom Geofence class
Since I don't want to rely on external API for this, we need to develop custom geofence class to manage users approaching. Already opened an [issue](https://github.com/Ladislav92/Mine-Field-Alarm-Android/issues/2) about this.

### Polygon painter
We need a class that will paint polygons instead of circles that are representing minefields at the moment.

### Ideas !
And dont forget to check out [contributing](https://github.com/Ladislav92/Mine-Field-Alarm-Android/blob/master/CONTRIBUTING.md) file.


This project is under [GNU General Public License v3.0](https://github.com/Ladislav92/Mine-Field-Alarm-Android/blob/master/LICENSE).

