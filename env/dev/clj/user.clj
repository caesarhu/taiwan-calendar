(ns user
  (:require [taiwan-calendar.core :refer :all]
            [taiwan-calendar.sheet :refer :all]
            [java-time :as jt]
            [com.rpl.specter :as sp]
            [potpuri.core :as pop :refer [find-first]]
            [schema.core :as s]
            [dk.ative.docjure.spreadsheet :as ss]))

(s/set-fn-validation! true)
