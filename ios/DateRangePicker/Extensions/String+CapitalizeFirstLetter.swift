//
//  String+CapitalizeFirstLetter.swift
//  DateRangePicker
//
//  Created by Datacom Vietnam on 18/07/2023.
//

import Foundation

extension String {

    func capitalizingFirstLetter() -> String {
        prefix(1).capitalized + dropFirst()
    }

    mutating func capitalizeFirstLetter() {
        self = self.capitalizingFirstLetter()
    }

}

