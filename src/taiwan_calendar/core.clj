(ns taiwan-calendar.core
  (:require [java-time :as jt]
            [com.rpl.specter :as sp]
            [schema.core :as s]
            [dk.ative.docjure.spreadsheet :as ss]))

(def DateMap
  "A schema for a date-map data type"
  {:date java.time.LocalDate
   :holiday? s/Bool})

(defn some-element [pred coll]
  (some #(and (pred %) %) coll))

(defn labor-day [year]
  (jt/local-date year 5 1))

(defn after-work? [date-map date]
  (not (or (:holiday? date-map)
           (jt/before? (:date date-map) date))))
