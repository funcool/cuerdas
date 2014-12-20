(ns cuerdas.core
  (:refer-clojure :exclude [contains? empty? repeat replace])
  (:require [clojure.string :as str])
  (:import org.apache.commons.lang3.StringUtils))

(defn contains?
  "Determines whether a string contains a substring."
  [^String s ^String subs]
  (StringUtils/contains s subs))

(defn startswith?
  "Check if the string starts with prefix."
  [^String s ^String prefix]
  (StringUtils/startsWith s prefix))

(defn endswith?
  "Check if the string ends with suffix."
  [^String s ^String suffix]
  (StringUtils/endsWith s suffix))

(defn lower
  "Converts string to all lower-case."
  [^String s]
  (StringUtils/lowerCase s))

(defn upper
  "Converts string to all upper-case."
  [s]
  (str/upper-case s))

(defn empty?
  "Checks if a string is empty or contains only whitespaces."
  [^String s]
  (StringUtils/isEmpty s))

(defn blank?
  "Checks if a string is empty or contains only whitespaces."
  [^String s]
  (StringUtils/isBlank s))


(defn trim
  "Removes whitespace or specified characters
  from both ends of string."
  ([s] (trim s " "))
  ([s chs]
   (StringUtils/strip s chs)))

(defn rtrim
  "Removes whitespace or specified characters
  from right side of string."
  ([s] (rtrim s " "))
  ([s chs]
   (StringUtils/stripEnd s chs)))

(defn ltrim
  "Removes whitespace or specified characters
  from left side of string."
  ([s] (ltrim s " "))
  ([s chs]
   (StringUtils/stripStart s chs)))
