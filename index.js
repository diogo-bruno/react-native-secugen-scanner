import { NativeModules } from 'react-native';

const tryResponse = (messageResponse) => {
  let response = JSON.parse(messageResponse);
  if (response.typeResponse === 'JSON') {
    response.response = JSON.parse(response.response);
  }
  delete response.typeResponse;
  return response;
};

const SecugenScanner = {
  captureFP: async function (timeoutGetImage, qualityGetImage) {
    return new Promise((resolve, reject) => {
      NativeModules.SecugenScanner.captureFP(timeoutGetImage, qualityGetImage, (messageResponse) => {
        resolve(tryResponse(messageResponse));
      });
    });
  },
  setMessages: function (msgs) {
    NativeModules.SecugenScanner.setMessages(msgs);
  },
  matchImages: async function (base64Image1, base64Image2) {
    return new Promise((resolve, reject) => {
      NativeModules.SecugenScanner.matchImages(base64Image1, base64Image2, (messageResponse) => {
        resolve(tryResponse(messageResponse));
      });
    });
  },
  setLed: async function (option) {
    return new Promise((resolve, reject) => {
      NativeModules.SecugenScanner.setLed(option, (messageResponse) => {
        resolve(tryResponse(messageResponse));
      });
    });
  },
};

export default SecugenScanner;
