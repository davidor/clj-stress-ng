(ns clj-stress-ng.core-test
  (:use midje.sweet
        [clojure.java.shell :only [sh]])
  (:require [clj-stress-ng.core :refer :all]))

(def stress-ng-example-output-1-stressor
  "stress-ng: info: [1245] dispatching hogs: 1 cpu
stress-ng: info: [1245] successful run completed in 10.02s
stress-ng: info: [1245] stressor      bogo ops real time  usr time  sys time   bogo ops/s   bogo ops/s
stress-ng: info: [1245]                          (secs)    (secs)    (secs)   (real time) (usr+sys time)
stress-ng: info: [1245] cpu             1411     10.02      9.90      0.02       140.84       142.24")

(def stress-ng-example-output-2-stressors
  "stress-ng: info: [1248] dispatching hogs: 1 cache, 1 cpu
stress-ng: info: [1248] successful run completed in 10.08s
stress-ng: info: [1248] stressor      bogo ops real time  usr time  sys time   bogo ops/s   bogo ops/s
stress-ng: info: [1248]                          (secs)    (secs)    (secs)   (real time) (usr+sys time)
stress-ng: info: [1248] cache             534     10.02      7.72      0.35        53.28        66.17
stress-ng: info: [1248] cpu              1032     10.08      6.73      0.44       102.36       143.93")

(fact "clj-stress-ng works when it is called with 1 stressor"
  (let [stressor {:cpu {:workers 1 :method "double"}}
        seconds 10]
    (run stressor seconds) =>
      {:cpu {:bogo-ops 1411 :real-time 10.02 :usr-time 9.90 :sys-time 0.02}}
    (provided 
      (sh "stress-ng" "--cpu" "1" "--cpu-method" "double"
          "--metrics-brief" "--timeout" "10s") =>
        {:exit 0 :out stress-ng-example-output-1-stressor :err ""})))

(fact "clj-stress-ng works when it is called with 2 stressors"
  (let [stressors {:cache {:workers 1} :cpu {:workers 1} }
        seconds 10]
    (run stressors seconds) => 
      {:cpu {:bogo-ops 1032 :real-time 10.08 :usr-time 6.73 :sys-time 0.44}
       :cache {:bogo-ops 534 :real-time 10.02 :usr-time 7.72 :sys-time 0.35}}
    (provided
      (sh "stress-ng" "--cache" "1" "--cpu" "1"
          "--metrics-brief" "--timeout" "10s") =>
      {:exit 0 :out stress-ng-example-output-2-stressors :err ""})))      

(fact "clj-stress-ng works well when the given stressors are not ordered"
  (let [stressors {:cpu {:workers 1} :cache {:workers 1}}
        seconds 10]
    (run stressors seconds) =>
      {:cpu {:bogo-ops 1032 :real-time 10.08 :usr-time 6.73 :sys-time 0.44}
       :cache {:bogo-ops 534 :real-time 10.02 :usr-time 7.72 :sys-time 0.35}}
    (provided
      (sh "stress-ng" "--cache" "1" "--cpu" "1"
          "--metrics-brief" "--timeout" "10s") =>
      {:exit 0 :out stress-ng-example-output-2-stressors :err ""})))

(fact "clj-stress-ng raises an exception when it receives an invalid stressor"
  (let [invalid-stressor {:cpuuu {:workers 1}}
        seconds 10]
    (run invalid-stressor seconds) => (throws Exception)))

(fact "clj-stress-ng raises an exception when stress-ng fails"
  (let [stressors {:cpu {:workers 1} :cache {:workers 1}}
        seconds 10]
    (run stressors seconds) => (throws Exception)
    (provided
      (sh "stress-ng" "--cache" "1" "--cpu" "1"
          "--metrics-brief" "--timeout" "10s") =>
      {:exit 0 :out "" :err "Ugly error"})))

(fact "the list of stressors can be retrieved correctly" 
  (count available-stressors) => 58 
  (sort (keys (take 5 available-stressors))) => 
    '(:affinity :aio :bsearch :cache :clock))