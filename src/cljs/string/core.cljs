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
  "Removes whitespace or specified characters
  from both ends of string."
  ([s] (trim s " "))
  ([s chs]
   (let [rxstr (str "[" (escape-regexp chs) "]")
         rx    (js/RegExp. (str "^" rxstr "+|" rxstr "+$") "g")]
     (.replace s rx ""))))

(defn rtrim
  "Removes whitespace or specified characters
  from right side of string."
  ([s] (rtrim s " "))
  ([s chs]
   (let [rxstr (str "[" (escape-regexp chs) "]")
         rx    (js/RegExp. (str rxstr "+$"))]
     (.replace s rx ""))))

(defn ltrim
  "Removes whitespace or specified characters
  from left side of string."
  ([s] (ltrim s " "))
  ([s chs]
   (let [rxstr (str "[" (escape-regexp chs) "]")
         rx    (js/RegExp. (str "^" rxstr "+"))]
     (.replace s rx ""))))

(defn empty?
  "Checks if a string is empty or contains only whitespaces."
  [s]
  (gstr/isEmpty s))

(defn repeat
  "Repeats string n times."
  ([s] (repeat s 1))
  ([s n]
   (gstr/repeat s n)))

(defn strip-newlines
  "Takes a string and replaces newlines with a space.
  Multiple lines are replaced with a single space."
  [s]
  (gstr/stripNewlines s))

(defn split
  "Splits a string on a separator a limited
  number of times. The separator can be a string
  or RegExp instance."
  ([s] (split s #"\s" nil))
  ([s sep] (split s sep 0))
  ([s sep num]
   (if (regexp? sep)
     (str/split s sep num)
     (str/split s (re-pattern sep) num))))

(defn replace-all
  [s match replacement]
  (cond
   (string? match)
   (.replace s (js/RegExp. (escape-regexp match) "g") replacement)

   (.hasOwnProperty match "source")
   (.replace s (js/RegExp. (.-source match) "g") replacement)

   :else
   (throw (str "Invalid match arg: " match))))

(defn replace-first
  [s match replacement]
  (cond
   (string? match)
   (.replace s (js/RegExp. (escape-regexp match)) replacement)

   (.hasOwnProperty match "source")
   (.replace s (js/RegExp. (.-source match)) replacement)

   :else
   (throw (str "Invalid match arg: " match))))

(defn prune
  "Truncates a string to a certain length and adds '...'
  if necessary."
  ([s num] (prune s num "..."))
  ([s num subs]
   (if (< (.-length s) num)
     s
     (let [tmpl (fn [c] (if (not= (upper c) (lower c)) "A" " "))
           template (-> (.slice s 0 (inc (.-length s)))
                        (replace-all #".(?=\W*\w*$)" tmpl))
           template (if (.match (.slice template (- (.-length template) 2)) #"\w\w")
                      (replace-first template #"\s*\S+$" "")
                      (rtrim (.slice template 0 (dec (.-length template)))))]
       (if (> (.-length (str template subs)) (.-length s))
         s
         (str (.slice s 0 (.-length template)) subs))))))
