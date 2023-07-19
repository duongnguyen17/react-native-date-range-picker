//
//  DateRangePickerModule.swift
//  DateRangePicker
//
//  Created by Datacom Vietnam on 18/07/2023.
//

import Foundation
import UIKit
import React

@objc(DateRangePickerModule)
class DateRangePickerModule: NSObject {
  
  @objc
  func present(_ config: NSDictionary, doneHandler: @escaping RCTResponseSenderBlock
               //               , dismissHandler: @escaping RCTResponseSenderBlock
  ) {
    DispatchQueue.main.async {
      guard let rootViewController = UIApplication.shared.windows.first?.rootViewController else {
        return
      }
      
      let fastisController = self.getFastisController(cf: config)
      
      if case let .range(rangeController) = fastisController {
        // Handle rangeController
        rangeController.doneHandler = { result in
          let object = [
            "from": result!.fromDate.timeIntervalSince1970 * 1000 ,
            "to": result!.toDate.timeIntervalSince1970 * 1000 ,
          ]
          
          doneHandler([object])
          
        }
        
        //        rangeController.dismissHandler = {
        //          dismissHandler([])
        //        }
        rangeController.present(above: rootViewController)
        //        rootViewController.present(rangeController, animated: true, completion: nil)
      } else if case let .date(dateController) = fastisController {
        // Handle dateController
        dateController.doneHandler = { result in
          doneHandler([result!.timeIntervalSince1970 * 1000])
        }
        
        //        dateController.dismissHandler = {
        //          dismissHandler([])
        //        }
        dateController.present(above: rootViewController)
        //        rootViewController.present(dateController, animated: true, completion: nil)
      }
    }
  }
  
  enum FastisControllerType {
    case range(FastisController<FastisRange>)
    case date(FastisController<Date>)
  }
  
  private func getFastisController(cf: NSDictionary) ->FastisControllerType{
    // Cấu hình các thuộc tính tùy chỉnh cho fastisConfig từ configÏ
    let mode = cf["mode"] as! String
    
    var customConfig = FastisConfig.default
    
    if let textCancel = cf["textCancel"] as? String {
      customConfig.controller.cancelButtonTitle = textCancel
    }
    
    if let textDone = cf["textDone"] as? String {
      customConfig.controller.doneButtonTitle = textDone
    }
    
    
    if mode == "range" {
      let rangeController = FastisController<FastisRange>(mode: .range,config: customConfig)
      
      if let title = cf["title"] as? String {
        rangeController.title = title
      }
      
      if let allowDateRangeChanges = cf["allowDateRangeChanges"] as? Bool {
        rangeController.allowDateRangeChanges = allowDateRangeChanges
      }
      
      if let selectMonthOnHeaderTap = cf["selectMonthOnHeaderTap"] as? Bool {
        rangeController.selectMonthOnHeaderTap = selectMonthOnHeaderTap
      }
      
      if let allowToChooseNilDate = cf["allowToChooseNilDate"] as? Bool {
        rangeController.allowToChooseNilDate = allowToChooseNilDate
      }
      
      if let initialValue = cf["initialValue"] as? NSDictionary,
         let fromTimestamp = initialValue["from"] as? Double,
         let toTimestamp = initialValue["to"] as? Double{
        let fromDate =  Date(timeIntervalSince1970: fromTimestamp / 1000)
        let toDate =  Date(timeIntervalSince1970: toTimestamp / 1000)
        let initialRange = FastisRange(from: fromDate, to: toDate)
        rangeController.initialValue = initialRange
      }
      
      if let maximumDateTimestamp = cf["maximumDate"] as? Double{
        rangeController.maximumDate = Date(timeIntervalSince1970: maximumDateTimestamp / 1000)
      }
      
      if let minimumDateTimestamp = cf["minimumDate"] as? Double {
        rangeController.minimumDate = Date(timeIntervalSince1970: minimumDateTimestamp / 1000)
      }
      
      return .range(rangeController)
    } else {
      let dateController = FastisController<Date>(mode: .single,config: customConfig)
      
      if let title = cf["title"] as? String {
        dateController.title = title
      }
      
      if let allowToChooseNilDate = cf["allowToChooseNilDate"] as? Bool {
        dateController.allowToChooseNilDate = allowToChooseNilDate
      }
      
      if let initialValueTimestamp = cf["initialValue"] as? Double {
        dateController.initialValue = Date(timeIntervalSince1970: initialValueTimestamp / 1000)
      }
      
      if let maximumDateTimestamp = cf["maximumDate"] as? Double{
        dateController.maximumDate = Date(timeIntervalSince1970: maximumDateTimestamp / 1000)
      }
      
      if let minimumDateTimestamp = cf["minimumDate"] as? Double{
        dateController.minimumDate = Date(timeIntervalSince1970: minimumDateTimestamp / 1000)
      }
      
      return .date(dateController)
    }
  }
  
  
  @objc
  static func requiresMainQueueSetup() -> Bool {
    return true
  }
}

