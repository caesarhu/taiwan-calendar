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
(def sheet-2019 (read-sheet calendar-2019-file "Sheet1"))
(def sheet-2018 (read-sheet calendar-2018-file-2 "Sheet1"))

(s/defschema DateMap
  "A schema for a date-map data type"
  {:date java.time.LocalDate
   :holiday? s/Bool})

(s/defn cell->map :- (s/maybe DateMap)
  [year :- s/Int
   month :- s/Int
   cell :- Cell]
  (if-all-let [value (ss/read-cell cell)
               v (and (number? value) (int value))]
    (hash-map :date (jt/local-date year month v)
              :holiday? (contains? holiday-color (cell-colorcolor cell)))))

(s/defn sheet->map :- [DateMap]
  [sheet :- Sheet]
  (let [year (parse-year sheet)
        months (range 1 13)
        cells (year-cells sheet)]
    (->> (mapcat (fn [month coll]
                   (map #(cell->map year month %) coll))
                 months
                 cells)
         (filter some?))))

(s/defn labor-day :- java.time.LocalDate
  [year :- s/Int]
  (jt/local-date year 5 1))

(s/defn after-work? :- s/Bool
  [date-map :- DateMap
   date :- java.time.LocalDate]
  (not (or (:holiday? date-map)
           (jt/before? (:date date-map) date))))

(s/defn set-holiday :- (s/maybe [DateMap])
  [calendar :- [DateMap]
   date :- java.time.LocalDate]
  (when-let [holiday (find-first calendar #(after-work? % date))]
    (sp/transform [sp/ALL (sp/pred= holiday) :holiday?] not calendar)))

(s/defn find-make-up :- [DateMap]
  [calendar :- [DateMap]]
  (sp/select [sp/ALL #(and (jt/weekend? (:date %)) (false? (:holiday? %)))] calendar))

(s/defn find-long-weekend :- [[DateMap]]
  [calendar :- [DateMap]]
  (sp/select [sp/ALL #(and (< 2 (count %)) (:holiday? (first %)))] (partition-by :holiday? calendar)))

(s/defn year-calendar :- {:calendar [DateMap]
                          :year s/Int
                          :make-up [DateMap]
                          :long-weekend [[DateMap]]}
  ([file :- s/Str
    labor? :- s/Bool]
   (if-all-let [sheet (read-sheet file "Sheet1")
                year (parse-year sheet)
                lday (labor-day year)
                cal (sheet->map sheet)
                cal2 (if labor?
                       (set-holiday cal lday)
                       cal)]
     (hash-map :calendar cal2
               :year year
               :make-up (find-make-up cal2)
               :long-weekend (find-long-weekend cal2))))
  ([file :- s/Str]
   (year-calendar file true)))
