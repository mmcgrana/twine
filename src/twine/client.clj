(ns twine.client
  (:import java.net.URI)
  (:import (com.surftools.BeanstalkClientImpl ClientImpl JobImpl))
  (:require [clojure.string :as str])
  (:refer-clojure :exclude [use peek]))

(def ^:private local-url
  "beanstalkd://127.0.0.1:11300")

(defn- ser [^String s]
  (.getBytes s "UTF-8"))

(defn- deser [^"[B" b]
  (String. b "UTF-8"))

(defn- unpack-job [^JobImpl j]
  (if j {:id (.getJobId j) :data (deser (.getData j))}))

(defn- unpack-map [m]
  (if m (into {} (map (fn [[k v]] [k (str/triml v)]) m))))

(defn- unpack-list [l]
  (seq l))

(defn init
  [& [{:keys [url] :as opts}]]
  (let [uri (URI. (or url local-url))
        host (.getHost uri)
        port (.getPort uri)]
    (ClientImpl. host port true)))

(defn use
  [^ClientImpl tw tube-name]
  (.useTube tw tube-name))

(defn put
  ([tw data]
    (put tw 0 0 0 data))
  ([^ClientImpl tw priority delay run data]
    (.put tw priority delay run (ser data))))

(defn watch
  [^ClientImpl tw tube-name]
  (.watch tw tube-name))

(defn ignore
  [^ClientImpl tw tube-name]
  (.ignore tw tube-name))

(defn reserve
  [^ClientImpl tw & [timeout]]
  (unpack-job (.reserve tw timeout)))

(defn delete
  [^ClientImpl tw id]
  (.delete tw id))

(defn release
  [^ClientImpl tw id priority delay]
  (.release tw id priority delay))

(defn bury
  [^ClientImpl tw id priority]
  (.bury tw id priority))

(defn touch
  [^ClientImpl tw id]
  (.touch tw id))

(defn peek
  [^ClientImpl tw id]
  (unpack-job (.peek tw id)))

(defn peek-ready
  [^ClientImpl tw]
  (unpack-job (.peekReady tw)))

(defn peek-delayed
  [^ClientImpl tw]
  (unpack-job (.peekDelayed tw)))

(defn peek-buried
  [^ClientImpl tw]
  (unpack-job (.peekBuried tw)))

(defn kick
  [^ClientImpl tw n]
  (.kick tw n))

(defn stats-job
  [^ClientImpl tw id]
  (unpack-map (.statsJob tw id)))

(defn stats-tube
  [^ClientImpl tw tube-name]
  (unpack-map (.statsTube tw tube-name)))

(defn stats
  [^ClientImpl tw]
  (unpack-map (.stats tw)))

(defn list-tubes
  [^ClientImpl tw]
  (unpack-list (.listTubes tw)))

(defn list-tube-used
  [^ClientImpl tw]
  (.listTubeUsed tw))

(defn list-tubes-watched
  [^ClientImpl tw]
  (unpack-list (.listTubesWatched tw)))
