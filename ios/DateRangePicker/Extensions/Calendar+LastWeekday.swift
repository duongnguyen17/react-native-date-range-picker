//
//  Calendar+LastWeekday.swift
//  DateRangePicker
//
//  Created by Datacom Vietnam on 18/07/2023.
//

import Foundation

extension Calendar {
    var lastWeekday: Int {
        let numDays = self.weekdaySymbols.count
        let res = (self.firstWeekday + numDays - 1) % numDays
        return res != 0 ? res : self.weekdaySymbols.count
    }
}

