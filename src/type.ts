export type DateRangePickerConfig = {
  title: string;
  allowToChooseNilDate?: boolean;
  minimumDate?: Date;
  maximumDate?: Date;
  textDone?: string;
  textCancel?: string;
} & (RangePickerConfig | DatePickerConfig);

type RangePickerConfig = {
  mode: DateRangePickerMode.Range;
  allowDateRangeChanges?: boolean;
  selectMonthOnHeaderTap?: boolean;
  initialValue?: RangeDate;
};

type DatePickerConfig = {
  mode: DateRangePickerMode.Single;
  initialValue?: Date;
};

export enum DateRangePickerMode {
  Range = 'range',
  Single = 'single',
}

export type RangeDate = {
  from: Date;
  to: Date;
};

export type ConfigNativeModule = Omit<
  DateRangePickerConfig,
  'minimumDate' | 'maximumDate' | 'initialValue'
> & {
  minimumDate?: number;
  maximumDate?: number;
  initialValue?: number | {from: number; to: number};
};
