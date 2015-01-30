(ns clj-stress-ng.core-test
  (:use midje.sweet
        [midje.util :only [testable-privates]]
        [clojure.java.shell :only [sh]])
  (:require [clj-stress-ng.core :refer :all]))

(testable-privates
  clj-stress-ng.core
  workers-for-stress-ng-command
  option-for-stress-ng-command
  options-for-stress-ng-command
  stress-ng-command
  stress-ng-output
  output-line
  stress-ng-results
  supported?)

(def stress-ng-example-output
  "stress-ng: info: [11] dispatching hogs: 1 cpu
stress-ng: info: [11] successful run completed in 2.01
stress-ng: info: [11] stressor bogo ops real time usr time sys time bogo ops/s bogo ops/s
stress-ng: info: [11]           (secs)   (secs)   (secs)  (real time) (usr+sys time)
stress-ng: info: [11]   cpu      469      2.01     2.01     0.00     233.52     233.33
stress-ng: info: [11]  cache     191      2.00     1.72     0.00     95.50      111.05")

(facts
  "about `stressor-options-for-stress-ng-command`"
  (let [stressor (first {:cpu {:workers 2}})]
    (workers-for-stress-ng-command stressor) => ["--cpu" "2"]))

(facts
  "about `option-for-stress-ng-command`"
  (let [stressor-name "cpu"
        option-name "method"
        option-value "double"]
    (option-for-stress-ng-command stressor-name option-name option-value) =>
    ["--cpu-method" "double"]))

(facts
  "about `options-for-stress-ng-command`"
  (let [stressor (first {:cpu {:workers 2 :method "double" :ops 1000}})]
    (options-for-stress-ng-command stressor) =>
    ["--cpu-method" "double" "--cpu-ops" "1000"]))

(facts
  "about `stress-ng-command`"
  (let [one-stressor {:cpu {:workers 2 :method "double"}}
        two-stressors {:cpu {:workers 1} :cache {:workers 2}}
        seconds 10]
    (stress-ng-command one-stressor seconds) =>
    ["stress-ng" "--cpu" "2" "--cpu-method" "double"
     "--metrics-brief" "--timeout" "10s"]
    (stress-ng-command two-stressors seconds) =>
    ["stress-ng" "--cache" "2" "--cpu" "1"
     "--metrics-brief" "--timeout" "10s"]))

(facts
  "about `stress-ng-output`"
  (let [stressors {:cpu {:workers 1}}
        seconds 1]
    (stress-ng-output stressors seconds) => "output"
    (provided
      (sh "stress-ng" "--cpu" "1" "--metrics-brief" "--timeout" "1s") =>
      {:exit 0 :out "output" :err ""})
    (stress-ng-output stressors seconds) => (throws Exception)
    (provided
      (sh "stress-ng" "--cpu" "1" "--metrics-brief" "--timeout" "1s") =>
      {:exit 0 :out "output" :err "Ugly Error"})))

(facts
  "about `output-line`"
  (let [stressor-name "cache"
        output-lines (clojure.string/split-lines stress-ng-example-output)]
    (output-line stressor-name output-lines) => (output-lines 5)))

(facts
  "about `stress-ng-results`"
  (let [stressor-name "cpu"]
    (stress-ng-results stressor-name stress-ng-example-output) =>
    {:bogo-ops 469 :real-time 2.01 :usr-time 2.01 :sys-time 0.00}))

(facts
  "about `supported`"
  (let [supported-stressors '(:cpu :cache)
        contains-unsupported-stressors '(:cpuuu :cache)]
    (supported? supported-stressors) => true
    (supported? contains-unsupported-stressors) => false))

(facts
  "about `run`"
  (let [stressors {:cpu {:workers 1} :cache {:workers 1}}
        invalid-stressors {:cpuuu {:workers 1}}
        seconds 10]
    (run stressors seconds) =>
    {:cpu {:bogo-ops 469 :real-time 2.01 :usr-time 2.01 :sys-time 0.00}
     :cache {:bogo-ops 191 :real-time 2.00 :usr-time 1.72 :sys-time 0.00}}
    (provided
      (#'clj-stress-ng.core/stress-ng-output stressors seconds) =>
      stress-ng-example-output)
    (run invalid-stressors seconds) => (throws Exception)))

(facts "about `stressors`"
  (count available-stressors) => 58)
  (take 5 available-stressors) => '(:affinity :aio :bsearch :cache :clock)
