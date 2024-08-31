(ns semester-todoist.core
  (:require [java-time.api :as jt]
            [semester-todoist.parser :as parser]
            [semester-todoist.printer :as printer]))

(defn dates-of-classweeks [start-date off-weeks]
  (let [off-week-set (set off-weeks)]
    (->> start-date
         (iterate #(jt/plus % (jt/weeks 1)))
         (remove off-week-set)
         (take 14)
         vec)))

(defn week-task [week week-num]
  {:content  (str "Week " (inc week-num) ": " (:title week) " @plan @week" (inc week-num))
   :priority 4
   :indent   1})

(defn assignment-task [assignment week-num class-weeks]
  (let [{:keys [task-type name description]} assignment
        prefix ({:read    "📖 " :optional-read "(optional) 📖 "
                 :write   "✏️ " :optional-write "(optional) ✏️ "
                 :watch   "🎥 " :optional-watch "(optional) 🎥 "
                 :project "🏅 "} task-type)
        priority (get {:optional-read 4 :optional-write 4 :optional-watch 4 :project 2} task-type 3)]
    {:content     (str prefix name " @week" (inc week-num))
     :priority    priority
     :description description
     :date        (str (get class-weeks week-num))
     :date-lang   "en"
     :indent      2}))

(defn all-tasks-for-week [week week-num class-weeks]
  (concat [(week-task week week-num)]
          (map #(assignment-task % week-num class-weeks) (:assignments week))
          [(assignment-task {:task-type :watch :name "Video Lecture"} week-num class-weeks)]))

(defn all-tasks [course]
  (let [{:keys [start-date off-weeks weeks]} course
        class-weeks (dates-of-classweeks start-date off-weeks)]
    (apply concat (map-indexed (fn [week-num week] (all-tasks-for-week week week-num class-weeks))
                               weeks))))

(defn execute [input-file output-file]
  (if (= input-file output-file)
    (println "INVALID: Input file cannot be the same as the output file")
    (-> input-file parser/parse all-tasks (printer/output output-file))))