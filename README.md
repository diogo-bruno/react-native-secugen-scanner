# react-native-secugen-scanner

![react-secugen](https://user-images.githubusercontent.com/11491923/96663907-119c2f00-1328-11eb-8251-31868d192d5e.jpg)

## Install

```
yarn add https://github.com/diogo-bruno/react-native-secugen-scanner --save
```

## Usage

```javascript
import SecugenScanner from 'react-native-secugen-scanner';

SecugenScanner.setMessages({
  initializing: 'Initializing Secugen Reader',
  wait: 'Please Wait',
  attention: 'Attention',
  initialization_failed: 'Fingerprint device initialization failed.',
  initialization_success: 'Device opened successfully',
  error_device_not_found: 'Either a fingerprint device is not attached or the attached fingerprint device is not supported.',
  permission_device_accepted: 'Permission to connect to the device was accepted!',
  permission_device_denied: 'Permission to connect to the device was denied!',
  alert_two_images_match: 'Two images are required for the match',
  init_device_error: 'Error init device',
  alert_capture_digital: 'Capture in the coming',
  seconds: 'Seconds',
  quality_fingerprint: 'Quality of the fingerprint is less than',
  quality_error_code: 'Quality Error Code',
  error_during_capture: 'Error when capturing digital ',
  capture_error_code: 'Capture Error Code ',
  error_exception_capture: 'Exception Capture - ',
});

captureFP = async () => {
  const dataCapture = await SecugenScanner.captureFP(5000, 50);
  if (dataCapture.response.image) {
    imgFingerPrint = `data:image/png;base64,${dataCapture.response.image}`;
  }
  console.log(dataCapture);
};

matchImages = async () => {
  const base64Image1 = 'data:image/png;base64,...';
  const base64Image2 = 'data:image/png;base64,...';
  const match = await SecugenScanner.matchImages(base64Image1, base64Image2);
};

setLed = async (ledOn) => {
  await SecugenScanner.setLed(ledOn); // true or false
};
```
