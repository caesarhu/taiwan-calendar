(defproject taiwan-calendar "0.1.0"
  :description "Clojure匯入台灣人事行政總處行事曆相關程式庫"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [prismatic/schema "1.1.10"]
                 [clojure.java-time "0.3.2"]
                 [dk.ative/docjure "1.13.0"]
                 [com.rpl/specter "1.1.2"]]
  :repl-options {:init-ns taiwan-calendar.core}
  :profiles {:dev {:source-paths ["env/dev/clj"]}})
