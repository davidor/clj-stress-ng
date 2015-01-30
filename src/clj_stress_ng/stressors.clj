(ns clj-stress-ng.stressors)

(def supported-stressors
  (into {} (sort
    {:affinity [:ops :rand]
     :aio [:ops :requests]
     :bseach [:ops :size]
     :cache [:ops :fence :flush]
     :clock [:ops]
     :cpu [:load :ops :method]
     :dentry [:ops :order]
     :dir [:ops]
     :eventfd [:ops]
     :fallocate [:ops]
     :fault [:ops]
     :fifo [:ops :readers]
     :flock [:ops :max]
     :fork [:ops :max]
     :fstat [:ops :dir]
     :futex [:ops]
     :get [:ops]
     :hdd [:bytes :noclean :ops]
     :hsearch [:ops :size]
     :inotify [:ops]
     :io [:ops]
     :kill [:ops]
     :lease [:breakers]
     :link [:ops]
     :lockf [:ops :nonblock]
     :lsearch [:ops :size]
     :memcpy [:ops]
     :mmap [:ops :async :bytes :file]
     :msg [:ops]
     :nice [:ops]
     :null [:ops]
     :open [:ops]
     :pipe [:ops]
     :poll [:ops]
     :procfs [:ops]
     :pthread [:ops :max]
     :qsort [:ops :size]
     :rdrand [:ops]
     :rename [:ops]
     :sendfile [:ops :size]
     :sem [:ops :procs]
     :sigq [:ops]
     :sigfpe [:ops]
     :sigsegv [:ops]
     :sock [:domain :ops]
     :stack [:ops]
     :switch [:ops]
     :symlink [:ops]
     :sysinfo [:ops]
     :timer [:ops :freq]
     :tsearch [:ops :size]
     :urandom [:ops]
     :utime [:ops :fsync]
     :vfork [:ops :max]
     :vm [:bytes :stride :hang :keep :ops :locked :method :populate]
     :wait [:ops]
     :yield [:ops]
     :zero [:ops]})))

