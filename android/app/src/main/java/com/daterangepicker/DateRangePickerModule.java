package com.daterangepicker;

import androidx.fragment.app.FragmentActivity;

import com.daterangepicker.base.AMCalendar;
import com.daterangepicker.interfaces.RangeSelectionListener;
import com.daterangepicker.interfaces.SingleSelectionListener;
import com.facebook.react.bridge.*;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class DateRangePickerModule extends ReactContextBaseJavaModule {

    private static final String TAG = "DateRangePickerModule";

    public DateRangePickerModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    public String getName() {
        return TAG;
    }

    @ReactMethod
    public void present(ReadableMap config, Callback handleDone) {
        String mode = config.getString("mode");

        Calendar fromValue = new GregorianCalendar();
        Calendar toValue = new GregorianCalendar();

        Calendar minimumDate = new GregorianCalendar();
        Calendar maximumDate = new GregorianCalendar();

        String title = "";
        String textCancel = "";
        String textDone = "";

        // Kiểm tra xem tham số 'initValue' có tồn tại không
        if (config.hasKey("initialValue")) {
            ReadableType initValueType = config.getType("initialValue");
            if (initValueType == ReadableType.Number) {
                // Trường hợp tham số 'initValue' là một số duy nhất (kiểu double)
                fromValue.setTimeInMillis((long) config.getDouble("initialValue"));
            } else if (initValueType == ReadableType.Map) {
                // Trường hợp tham số 'initValue' là một phạm vi (kiểu {from: double, to: double})
                ReadableMap range = config.getMap("initialValue");
                if (range.hasKey("from") && range.hasKey("to")) {
                    fromValue.setTimeInMillis((long) config.getDouble("from"));
                    toValue.setTimeInMillis((long) config.getDouble("to"));
                }
            }
        }

        if (config.hasKey("minimumDate")) {
            minimumDate.setTimeInMillis((long) config.getDouble("minimumDate"));
        }

        if (config.hasKey("maximumDate")) {
            maximumDate.setTimeInMillis((long) config.getDouble("maximumDate"));
        }

        if (config.hasKey("textCancel")) {
            textCancel =config.getString("textCancel");
        }

        if (config.hasKey("textDone")) {
            textDone =config.getString("textDone");
        }

        if (config.hasKey("title")) {
            title =config.getString("title");
        }

        if (mode.equals(CalendarType.RANGE.getValue())) {
            // Gọi RangeAMCalendar nếu mode là 'range'
            AMCalendar.RangeAMCalendar rangeAMCalendar = AMCalendar.Companion.rangeSelect((FragmentActivity) getCurrentActivity(),

                    new RangeSelectionListener() {
                        @Override
                        public void onRangeSelect(Calendar cal, Calendar calEnd) {
                            if (cal != null && calEnd != null) {
                                WritableArray selectedDates = Arguments.createArray();
                                selectedDates.pushInt(cal.get(Calendar.YEAR));
                                selectedDates.pushInt(cal.get(Calendar.MONTH));
                                selectedDates.pushInt(cal.get(Calendar.DAY_OF_MONTH));
                                selectedDates.pushInt(calEnd.get(Calendar.YEAR));
                                selectedDates.pushInt(calEnd.get(Calendar.MONTH));
                                selectedDates.pushInt(calEnd.get(Calendar.DAY_OF_MONTH));
                                WritableMap result = Arguments.createMap();
                                result.putArray("selectedDates", selectedDates);

                                handleDone.invoke(result);
                            }
                        }

                    });

            if (config.hasKey("initialValue")) {
            rangeAMCalendar.preselect(fromValue, toValue);
            }

            if (config.hasKey("allowDateRangeChanges")) {
                rangeAMCalendar.setAllowDateRangeChanges((config.getBoolean("allowDateRangeChanges")));
            }

            if (config.hasKey("textCancel")) {
                rangeAMCalendar.setTextCancel(textCancel);
            }

            if (config.hasKey("textDone")) {
                rangeAMCalendar.setTextSelect(textDone);
            }

            if (config.hasKey("title")) {
                rangeAMCalendar.setTitle(title);
            }

            rangeAMCalendar.show();

        } else {
            // Gọi SingleAMCalendar nếu mode là 'single'
            AMCalendar.SingleAMCalendar singleAMCalendar = AMCalendar.Companion.singleSelect((FragmentActivity) getCurrentActivity(),
                    new SingleSelectionListener() {
                        @Override
                        public void onSingleSelect(Calendar cal) {
                            if (cal != null) {
                                WritableArray selectedDates = Arguments.createArray();
                                selectedDates.pushInt(cal.get(Calendar.YEAR));
                                selectedDates.pushInt(cal.get(Calendar.MONTH));
                                selectedDates.pushInt(cal.get(Calendar.DAY_OF_MONTH));
                                WritableMap result = Arguments.createMap();
                                result.putArray("selectedDates", selectedDates);

                                handleDone.invoke(result);
                            }
                        }
                    });

            if (config.hasKey("initialValue")) {
            singleAMCalendar.preselect(fromValue);
            }

            if (config.hasKey("textCancel")) {
                singleAMCalendar.setTextCancel(textCancel);
            }

            if (config.hasKey("textDone")) {
                singleAMCalendar.setTextSelect(textDone);
            }

            if (config.hasKey("title")) {
                singleAMCalendar.setTitle(title);
            }

            singleAMCalendar.show();
        }
    }
}
