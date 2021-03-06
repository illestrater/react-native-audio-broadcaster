
import {
  NativeModules,
  DeviceEventEmitter,
  NativeAppEventEmitter,
  Platform
} from 'react-native';
import async from 'async';
import EventEmitter from 'eventemitter3';

const { RNAudioBroadcaster } = NativeModules;

class Broadcaster extends EventEmitter {
  constructor() {
    super();

    let appEventEmitter = Platform.OS === 'ios' ? NativeAppEventEmitter : DeviceEventEmitter;
  }

  init(settings, callback = _.noop) {
    RNAudioBroadcaster.init(settings, callback);
  }

  levels(callback = _.noop) {
    RNAudioBroadcaster.levels(callback);
  }

  stop() {
    RNAudioBroadcaster.stop();
  }

  get test() { return 'hi'; }
}

export { Broadcaster };
