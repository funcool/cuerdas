(ns string.core
  (:require [clojure.string :as str]))

(defn lower
  "Converts string to all lower-case."
  [s]
  (str/lower-case s))

(defn upper
  "Converts string to all upper-case."
  [s]
  (str/upper-case s))
