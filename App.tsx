/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 */

import React from 'react';
import {
  Button,
  SafeAreaView,
  StatusBar,
  useColorScheme,
  View,
} from 'react-native';

import {Colors} from 'react-native/Libraries/NewAppScreen';
import {DateRangePicker} from './src';
import {DateRangePickerMode, RangeDate} from './src/type';

function App(): JSX.Element {
  const isDarkMode = useColorScheme() === 'dark';

  const backgroundStyle = {
    backgroundColor: isDarkMode ? Colors.darker : Colors.lighter,
  };

  const handleDone = (result: RangeDate | Date) => {
    // Xử lý kết quả trả về từ Native Module
    console.log('Result:', result);
  };

  const handleShowAction = async () => {
    // _refAction.current?.show();
    DateRangePicker.present(
      {
        title: 'Chọn ngày',
        mode: DateRangePickerMode.Range,
        allowDateRangeChanges: false,
        allowToChooseNilDate: false,
        textDone: 'TEst Done',
        textCancel: 'test Cancel',
        // maximumDate: new Date('2023-08-17'),
        // minimumDate: new Date('2023-06-17'),
        // initialValue: {
        //   from: new Date('2023-06-20'),
        //   to: new Date('2023-06-24'),
        // },
      },
      handleDone,
    );
  };

  return (
    <SafeAreaView style={[backgroundStyle, {flex: 1}]}>
      <StatusBar
        barStyle={isDarkMode ? 'light-content' : 'dark-content'}
        backgroundColor={backgroundStyle.backgroundColor}
      />
      <View style={{flex: 1, justifyContent: 'center', alignItems: 'center'}}>
        <Button title="picker" onPress={handleShowAction} />
      </View>
    </SafeAreaView>
  );
}

export default App;
