import {NativeModules} from 'react-native';
import {DateRangePickerConfig, DateRangePickerMode, RangeDate} from './type';
import moment from 'moment';

export const DateRangePicker = {
  present: (
    config: DateRangePickerConfig,
    handleDone?: (value: RangeDate | Date) => void,
    // dismissHandler?: () => void,
  ) => {
    const minimumDate = config.minimumDate
      ? moment(config.minimumDate).valueOf()
      : undefined;

    const maximumDate = config.maximumDate
      ? moment(config.maximumDate).valueOf()
      : undefined;

    let initialValue: number | {from: number; to: number} | undefined;
    if (config.initialValue) {
      if (config.mode === DateRangePickerMode.Range) {
        initialValue = {
          from: moment(config.initialValue.from).valueOf(),
          to: moment(config.initialValue.to).valueOf(),
        };
      } else {
        initialValue = moment(config.initialValue).valueOf();
      }
    }

    const _handleDone = (result: any) => {
      if (result?.from) {
        handleDone?.({from: new Date(result.from), to: new Date(result.to)});
      } else {
        handleDone?.(new Date(result));
      }
    };

    NativeModules.DateRangePickerModule.present(
      {
        ...config,
        minimumDate,
        maximumDate,
        initialValue,
      },
      _handleDone,
      // dismissHandler,
    );
  },
};
