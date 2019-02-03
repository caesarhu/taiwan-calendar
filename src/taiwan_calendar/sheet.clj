(ns taiwan-calendar.sheet
  (:require [dk.ative.docjure.spreadsheet :as ss]
            [schema.core :as s]))

(def sheet-class #{org.apache.poi.hssf.usermodel.HSSFSheet org.apache.poi.xssf.usermodel.XSSFSheet})
(def Sheet (s/if (partial instance? org.apache.poi.xssf.usermodel.XSSFSheet)
                 org.apache.poi.xssf.usermodel.XSSFSheet
                 org.apache.poi.hssf.usermodel.HSSFSheet))
(def cell-class #{org.apache.poi.xssf.usermodel.XSSFCell org.apache.poi.hssf.usermodel.HSSFCell})
(def Cell (s/if (partial instance? org.apache.poi.xssf.usermodel.XSSFCell)
                org.apache.poi.xssf.usermodel.XSSFCell
                org.apache.poi.hssf.usermodel.HSSFCell))
(def xlsx-color-class org.apache.poi.xssf.usermodel.XSSFColor)
(def holiday-color #{"FFFF99FF" "FFFF:9999:CCCC"})

(def column-of-month (for [c "D" r (range 1 55)] (str c r)))
(def jan-str "一")
(def apr-str "四")
(def jul-str "七")
(def oct-str "十")
(def month-str #{jan-str apr-str jul-str oct-str})
(def month-column ["BCDEFGH" "IJKLMNO" "PQRSTUV"])
(def year-cell-addr "B2")
(def re-year #"中華民國(\d+)年")
(def column-of-month (for [c "D" r (range 1 55)] (str c r)))
(def jan-str "一")
(def apr-str "四")
(def jul-str "七")
(def oct-str "十")
(def month-str #{jan-str apr-str jul-str oct-str})

(s/defn str->int :- (s/maybe s/Int)
  [number-string :- s/Str]
  (try (Integer/parseInt number-string)
    (catch Exception e nil)))

(s/defn read-sheet :- Sheet
  [file  :- s/Str
   sheet :- s/Str]
  (->> (ss/load-workbook file)
       (ss/select-sheet sheet)))

(s/defn parse-year :- s/Int
  [sheet :- Sheet]
  (->> (ss/select-cell year-cell-addr sheet)
       ss/read-cell
       (re-find re-year)
       last
       str->int
       (+ 1911)))

(s/defn cell-colorcolor :- s/Str
  [cell :- Cell]
  (when-let [color (.getFillForegroundColorColor (.getCellStyle cell))]
    (if (instance? xlsx-color-class)
      (.getARGBHex color)
      (.getHexString color))))

(s/defn month-cells-addr :- [s/Str]
  [row :- [s/Int]
   column :- s/Str]
  (for [r row
        c column]
       (str c r)))

(s/defn month-cells :- [Cell]
  [sheet :- Sheet
   addr-coll :- [s/Str]]
  (map #(ss/select-cell % sheet) addr-coll))

(s/defn filter-month-str :- [Cell]
  [sheet :- Sheet]
  (let [cells (map #(ss/select-cell % sheet) column-of-month)]
    (filter #(contains? month-str (ss/read-cell %)) cells)))

(s/defn ->month-row :- [s/Int]
  [n :- s/Int]
  (range n (+ 11 n) 2))

(s/defn month-row :- [[s/Int]]
  [sheet :- Sheet]
  (->> (filter-month-str sheet)
       (map #(.getRowIndex %))
       (map #(+ 3 %))
       (map ->month-row)))

(s/defn year-cells :- [[Cell]]
  [sheet :- Sheet]
  (->> (for [r (month-row sheet)
             c month-column]
         (month-cells-addr r c))
       (map #(month-cells sheet %))))
