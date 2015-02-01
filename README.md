# clj-stress-ng

A clojure wrapper for [stress-ng](http://kernel.ubuntu.com/~cking/stress-ng/).

[![Clojars Project](http://clojars.org/clj-stress-ng/latest-version.svg)](http://clojars.org/clj-stress-ng)

[![Travis](https://travis-ci.org/davidor/clj-stress-ng.svg?branch=master)](https://travis-ci.org/davidor/clj-stress-ng)

## Usage

To use this library, you need to have stress-ng installed in your system.
The instructions can be found [here](http://kernel.ubuntu.com/~cking/stress-ng/).

Add the following to your project.clj:
```clojure
[clj-stress-ng "0.1.2"]
```

Use the library:
```clojure
(require '[clj-stress-ng.core :as stress-ng])
```

Execute stress-ng using the cpu stressor and one worker during 10 seconds:
```clojure
;; equivalent stress-ng command: stress-ng --cpu 1 --timeout 10s
(stress-ng/run {:cpu {:workers 1}} 10)
```

Execute stress-ng with several stressors and during 5 seconds:
```clojure
;; stress-ng --cpu 1 --cache 2 --fifo 3 --timeout 5s
(stress-ng/run {:cpu {:workers 1}
                :cache {:workers 2}
                :fifo {:workers 3}} 5)
```

The only required option for the stressors is the number of workers.
Some stressors can be used with different options. For example,
the cpu stressor can be executed with the options --cpu-ops and
--cpu-method:
```clojure
;; stress-ng --cpu 1 --cpu-ops 10000 --cpu-method float --timeout 10s
(stress-ng/run {:cpu {:workers 1 :ops 10000 :method "float"}} 10)
```

The stress-ng function returns a map with 4 fields like this one:
```clojure
{:cpu   {:bogo-ops 989, :sys-time 0.0, :usr-time 4.78, :real-time 5.03}
 :cache {:bogo-ops 779, :sys-time 0.0, :usr-time 8.05, :real-time 5.0}}
```

Get a map with the available stressors and the options that they support:
```clojure
(println stress-ng/available-stressors)
```

This library has been tested with stress-ng 0.03.11.

## License

Copyright (C) 2015 David Ortiz (Barcelona Supercomputing Center).
Distributed under the Apache License.
