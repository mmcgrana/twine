# Twine

A Clojure client library to the [Beanstalkd](http://kr.github.com/beanstalkd/) queueing daemon.


## Usage

    ; require twine
    (require '[twine.client :as twine])

    ; producer puts jobs on a queue
    (def twp (twine/init))
    (twine/use twp "jobs")
    (twine/put twp "hello worker")
    
    ; consumer works jobs off a queue
    (def twc (twine/init))
    (twine/watch twc "jobs")
    (let [{:keys [id data]} (twine/reserve twc)]
      (println "reserved" id "with" data)
      (twine/delete twc id))

    ; observer checks stats
    (def two (twine/init))
    (twine/stats two)
    (twine/stats-tube two "jobs")

The library implements all of the [Beanstalkd commands](https://github.com/kr/beanstalkd/blob/v1.3/doc/protocol.txt); functions in this library are named after the commands.

## Installation

Depend on `[twine "0.0.1"]` in your `project.clj`.


## See also

The companion project [Spine](https://github.com/mmcgrana/spine) provides a higher-level `enqueue` / `work` interface that will be more appropriate for many applications.
