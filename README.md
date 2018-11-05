
# react-native-audio-broadcaster

## Getting started

`$ npm install react-native-audio-broadcaster --save`

### Mostly automatic installation

`$ react-native link react-native-audio-broadcaster`

### Manual installation


#### iOS

1. In XCode, in the project navigator, right click `Libraries` ➜ `Add Files to [your project's name]`
2. Go to `node_modules` ➜ `react-native-audio-broadcaster` and add `RNAudioBroadcaster.xcodeproj`
3. In XCode, in the project navigator, select your project. Add `libRNAudioBroadcaster.a` to your project's `Build Phases` ➜ `Link Binary With Libraries`
4. Run your project (`Cmd+R`)<

#### Android

1. Open up `android/app/src/main/java/[...]/MainActivity.java`
  - Add `import com.reactlibrary.RNAudioBroadcasterPackage;` to the imports at the top of the file
  - Add `new RNAudioBroadcasterPackage()` to the list returned by the `getPackages()` method
2. Append the following lines to `android/settings.gradle`:
  	```
  	include ':react-native-audio-broadcaster'
  	project(':react-native-audio-broadcaster').projectDir = new File(rootProject.projectDir, 	'../node_modules/react-native-audio-broadcaster/android')
  	```
3. Insert the following lines inside the dependencies block in `android/app/build.gradle`:
  	```
      compile project(':react-native-audio-broadcaster')
  	```

#### Windows
[Read it! :D](https://github.com/ReactWindows/react-native)

1. In Visual Studio add the `RNAudioBroadcaster.sln` in `node_modules/react-native-audio-broadcaster/windows/RNAudioBroadcaster.sln` folder to their solution, reference from their app.
2. Open up your `MainPage.cs` app
  - Add `using Audio.Broadcaster.RNAudioBroadcaster;` to the usings at the top of the file
  - Add `new RNAudioBroadcasterPackage()` to the `List<IReactPackage>` returned by the `Packages` method


## Usage
```javascript
import RNAudioBroadcaster from 'react-native-audio-broadcaster';

// TODO: What to do with the module?
RNAudioBroadcaster;
```
  