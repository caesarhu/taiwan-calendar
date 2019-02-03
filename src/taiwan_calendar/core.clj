(ns taiwan-calendar.core
  (:require [taiwan-calendar.sheet :refer :all]
            [java-time :as jt]
            [com.rpl.specter :as sp]
            [potpuri.core :as pop :refer [find-first if-all-let]]
            [schema.core :as s]
            [dk.ative.docjure.spreadsheet :as ss]))

(s/set-fn-validation! true)

(def calendar-2019-file "resources/108年辦公日曆表.xls")
(def calendar-2018-file-1 "resources/107年辦公日曆表.xls")
(def calendar-2018-file-2 "resources/107年辦公日曆表.xlsx")

(s/defschema CellMap
  {:value s/Int
   :holiday? s/Bool})

(s/defschema DateMap
  "A schema for a date-map data type"
  {:date java.time.LocalDate
   :holiday? s/Bool})

(s/defn cells->map :- [CellMap]
  [cells :- [Cell]]
  (->> (filter #(number? (ss/read-cell %)) cells)
       (map #(hash-map :value (int (ss/read-cell %))
                       ;:holiday? (contains? holiday-color (cell-color %))))))
                       :holiday? (contains? holiday-color (cell-colorcolor %))))))

(s/defn labor-day :- java.time.LocalDate
  [year :- s/Int]
  (jt/local-date year 5 1))

(s/defn after-work? :- s/Bool
  [date-map :- DateMap
   date :- java.time.LocalDate]
  (not (or (:holiday? date-map)
           (jt/before? (:date date-map) date))))
