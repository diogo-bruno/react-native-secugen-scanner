/**
 * Sample React Native App
 *
 * adapted from App.js generated by the following command:
 *
 * react-native init example
 *
 * https://github.com/facebook/react-native
 */

import React, {Component} from 'react';
import {StyleSheet, Text, View, Button, Image} from 'react-native';
import SecugenScanner from 'react-native-secugen-scanner';

export default class App extends Component {
  state = {
    status: 'starting',
    message: '',
  };

  imgFingerPrint =
    'https://user-images.githubusercontent.com/11491923/96664556-73a96400-1329-11eb-90e8-b7e3d2c0a61f.jpg';

  msgs = {
    initializing: 'Initializing Secugen Reader',
    wait: 'Please Wait',
    attention: 'Attention',
    initialization_failed: 'Fingerprint device initialization failed.',
    initialization_success: 'Device opened successfully',
    error_device_not_found:
      'Either a fingerprint device is not attached or the attached fingerprint device is not supported.',
    permission_device_accepted:
      'Permission to connect to the device was accepted!',
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
  };

  componentDidMount() {
    this.setMessagesFP();
  }

  captureFP = async () => {
    const dataCapture = await SecugenScanner.captureFP(5000, 50);

    if (dataCapture.response.image) {
      this.imgFingerPrint = `data:image/png;base64,${dataCapture.response.image}`;
    }

    this.setState({
      status: 'native callback received',
      message: JSON.stringify(dataCapture).substring(1, 50),
    });
  };

  setMessagesFP = () => {
    SecugenScanner.setMessages(this.msgs);
  };

  render() {
    return (
      <View style={styles.container}>
        <Text style={styles.welcome}>☆SecugenScanner example☆</Text>
        <Text style={styles.instructions}>STATUS: {this.state.status}</Text>
        <Text style={styles.welcome}>☆NATIVE CALLBACK MESSAGE☆</Text>
        <Text style={styles.instructions}>{this.state.message}</Text>

        <Image style={styles.image} source={{uri: this.imgFingerPrint}} />

        <Button
          style={styles.margintop}
          title="Capture FP"
          onPress={this.captureFP}
        />
      </View>
    );
  }
}

const styles = StyleSheet.create({
  margintop: {
    marginTop: 20,
    marginBottom: 5,
  },
  image: {
    width: '100%',
    height: '50%',
    resizeMode: 'contain',
    borderWidth: 1,
    borderColor: 'red',
    padding: 10,
    margin: 10,
  },
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#F5FCFF',
  },
  welcome: {
    fontSize: 20,
    textAlign: 'center',
    margin: 10,
  },
  instructions: {
    textAlign: 'center',
    color: '#333333',
    marginBottom: 5,
  },
});
