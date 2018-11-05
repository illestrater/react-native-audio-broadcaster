using ReactNative.Bridge;
using System;
using System.Collections.Generic;
using Windows.ApplicationModel.Core;
using Windows.UI.Core;

namespace Audio.Broadcaster.RNAudioBroadcaster
{
    /// <summary>
    /// A module that allows JS to share data.
    /// </summary>
    class RNAudioBroadcasterModule : NativeModuleBase
    {
        /// <summary>
        /// Instantiates the <see cref="RNAudioBroadcasterModule"/>.
        /// </summary>
        internal RNAudioBroadcasterModule()
        {

        }

        /// <summary>
        /// The name of the native module.
        /// </summary>
        public override string Name
        {
            get
            {
                return "RNAudioBroadcaster";
            }
        }
    }
}
