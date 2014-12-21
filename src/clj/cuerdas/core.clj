(ns cuerdas.core
  (:refer-clojure :exclude [contains? empty? repeat replace reverse chars])
  (:require [clojure.string :as str]
            [clojure.walk :refer [stringify-keys]])
  (:import org.apache.commons.lang3.StringUtils
           java.util.regex.Pattern))

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

(defn repeat
  "Repeats string n times."
  ([^String s] (repeat s 1))
  ([^String s ^long n]
   (StringUtils/repeat s n)))

(defn slice
  "Extracts a section of a string and returns a new string."
  ([^String s ^long begin]
   (StringUtils/substring s begin))
  ([^String s ^long begin ^long end]
   (StringUtils/substring s begin end)))

(defn escape-regexp
  "Java specific pattern quoting."
  [^String s]
  (Pattern/quote s))

(defn replace
  "Replaces all instance of match with replacement in s.

  The replacement is literal (i.e. none of its characters are treated
  specially) for all cases above except pattern / string.

  In match is pattern instance, replacement can contain $1, $2, etc.
  will be substituted with string that matcher the corresponding
  parenthesized group in pattern.

  If you wish your replacement string to be used literary,
  use `(escape-regexp replacement)`.

  Example:
    (replace \"Almost Pig Latin\" #\"\\b(\\w)(\\w+)\\b\" \"$2$1ay\")
    ;; => \"lmostAay igPay atinLay\"
  "
  [^String s match ^String replacement]
  (str/replace s match replacement))

(defn replace-first
  "Replaces first instance of match with replacement in s."
  [s match replacement]
  (str/replace-first s match replacement))

(defn prune
  "Truncates a string to a certain length and adds '...'
  if necessary."
  ([s num] (prune s num "..."))
  ([s num subs]
   (if (< (count s) num)
     s
     (let [tmpl (fn [c] (if (not= (upper c) (lower c)) "A" " "))
           template (-> (slice s 0 (inc (count s)))
                        (replace #".(?=\W*\w*$)" tmpl))
           template (if (.matches (slice template (- (count template) 2)) "\\w\\w")
                      (replace-first template #"\s*\S+$" "")
                      (rtrim (slice template 0 (dec (count template)))))]
       (if (> (count (str template subs)) (count s))
         s
         (str (slice s 0 (count template)) subs))))))

(defn strip-newlines
  "Takes a string and replaces newlines with a space.
  Multiple lines are replaced with a single space."
  [^String s]
  (replace s #"[\n\r|\n]+" " "))

(defn split
  "Splits a string on a separator a limited
  number of times. The separator can be a string
  or Pattern instance."
  ([^String s] (split s #"\s"))
  ([^String s sep]
   (if (instance? Pattern sep)
     (str/split s sep)
     (str/split s (re-pattern sep))))
  ([^String s sep ^long num]
   (if (instance? Pattern sep)
     (str/split s sep num)
     (str/split s (re-pattern sep) num))))

(defn reverse
  "Return string reversed."
  [^String s]
  (StringUtils/reverse s))

(defn chars
  "Split a string in a seq of chars."
  [^String s]
  (into [] (.split s "(?!^)")))

(defn lines
  "Return a list of the lines in the string."
  [s]
  (split s #"\n|\r\n"))

(defn format
  "Simple string interpolation."
  [s & args]
  (if (and (= (count args) 1) (map? (first args)))
    (let [params (stringify-keys (first args))]
      (replace s #"%\(\w+\)s"
               (fn [match]
                 (let [substr (slice match 2 -2)]
                   (get params substr)))))
    (let [params (java.util.ArrayList. args)]
      (replace s #"%s" (fn [_] (str (.remove params 0)))))))

;; (defn parse-int
;;   "Return the number value in integer form."
;;   [s]
;;   (let [rx (regexp "^\\s*-?0x" "i")]
;;     (if (.test rx s)
;;       (js/parseInt s 16)
;;       (js/parseInt s 10))))

