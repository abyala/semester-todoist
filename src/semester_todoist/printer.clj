(ns semester-todoist.printer
  (:require [clojure.java.io :as io]
            [clojure.data.csv :as csv]))

(def headers ["TYPE", "CONTENT", "DESCRIPTION", "PRIORITY", "INDENT", "AUTHOR", "RESPONSIBLE", "DATE", "DATE_LANG", "TIMEZONE", "DURATION", "DURATION_UNIT"])

(defn format-task [task]
  (let [{:keys [content priority description date date-lang indent]} task]
    ["task" content description priority indent "" "" date date-lang "" "" ""]))

(defn output [tasks file-name]
  (with-open [writer (io/writer file-name)]
    (csv/write-csv writer (into [headers] (map format-task tasks)))))
