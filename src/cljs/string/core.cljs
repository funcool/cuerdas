(ns string.core
  (:require [clojure.string :as str]
            [goog.string :as gstr]))

(defn lower
  "Converts string to all lower-case."
  [s]
  (str/lower-case s))

(defn upper
  "Converts string to all upper-case."
  [s]
  (str/upper-case s))

(defn capitalize
  "Converts first letter of the string to uppercase."
  [s]
  (str/capitalize s))

(defn collapse-whitespace
  "Converts all adjacent whitespace characters
  to a single space."
  [s]
  (gstr/collapseWhitespace s))

(defn contains?
  "Determines whether a string contains a substring."
  [s subs]
  (gstr/contains s subs))

(defn startswith?
  "Check if the string starts with prefix."
  [s prefix]
  (gstr/startsWith s prefix))

(defn endswith?
  "Check if the string ends with suffix."
  [s prefix]
  (gstr/endsWith s prefix))

(defn camel-case
  "Converts a string from selector-case to camelCase."
  [s]
  (gstr/toCamelCase s))

(defn selector-case
  "Converts a string from camelCase to selector-case."
  [s]
  (gstr/toSelectorCase s))

(defn title-case
  "Converts a string into TitleCase."
  ([s]
   (gstr/toTitleCase s))
  ([s delimiters]
   (gstr/toTitleCase s delimiters)))

(defn escape-regexp
  "Escapes characters in the string that are not safe
  to use in a RegExp."
  [s]
  (gstr/regExpEscape s))

(defn trim
  ([s] (trim s " "))
  ([s chs]
   (let [rxstr (str "[" (escape-regexp chs) "]")
         rx    (js/RegExp. (str "^" rxstr "+|" rxstr "+$") "g")]
     (.replace s rx ""))))
