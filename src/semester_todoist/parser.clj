(ns semester-todoist.parser
  (:require [clojure.edn :as edn]
            [clojure.spec.alpha :as s]
            [java-time.api :as jt]))

(def date-regex #"(\d{4})-(\d{2})-(\d{2})")
(s/def :course/spec (s/keys :req-un [:course/id :course/name :course/start-date :course/off-weeks :course/weeks]))
(s/def :course/id pos-int?)
(s/def :course/name string?)
(s/def :course/start-date (s/and string? #(re-matches date-regex %)))
(s/def :course/off-weeks (s/coll-of :course/start-date))
(s/def :course/weeks (s/coll-of :course/week))
(s/def :course/title string?)
(s/def :course/week (s/cat :title (s/? :course/title)
                           :assignments (s/coll-of :assignment/spec)))
(s/def :assignment/name string?)
(s/def :assignment/description string?)

(s/def :assignment/spec (s/cat :task-type #{:read :write :watch :project}
                               :name string?
                               :description (s/? :assignment/description)))

(defn parse-date [s] (jt/local-date "yyyy-MM-dd" s))
(defn reformat [data]
  (-> data
      (update :start-date parse-date)
      (update :off-weeks #(map parse-date %))))

(defn parse [filename]
  (let [parsed (edn/read-string (slurp filename))
        conformed (s/conform :course/spec parsed)]
    (assert (not= conformed :clojure.spec.alpha/invalid) (str "File is in an invalid format: " filename))
    (reformat conformed)))