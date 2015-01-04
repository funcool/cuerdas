(ns cuerdas.core
  (:refer-clojure :exclude [contains? empty? repeat replace reverse chars unquote format])
  (:require [clojure.string :as str]
            [clojure.walk :refer [stringify-keys]])
  (:import java.util.regex.Pattern))

(defn contains?
  "Determines whether a string contains a substring."
  [^String s ^String subs]
  (when-not (nil? s)
    (cond
      (nil? subs) false
      (>= 0 (.indexOf s subs)) true
      :else false)))

(declare slice)

(defn starts-with?
  "Check if the string starts with prefix."
  [^String s ^String prefix]
  (cond
    (nil? s) false
    (nil? prefix) false
    :else (let [region (slice s 0 (count prefix))]
            (= region prefix))))

(defn ends-with?
  "Check if the string ends with suffix."
  [^String s ^String suffix]
  (cond
    (nil? s) false
    (nil? suffix) false
    :else (let [len (count s)
                region (slice s (- len (count suffix)) len)]
            (= region suffix))))

(def startswith? starts-with?)
(def endswith? ends-with?)

(defn lower
  "Converts string to all lower-case."
  [^String s]
  (when-not (nil? s)
    (.toLowerCase s)))

(defn upper
  "Converts string to all upper-case."
  [s]
  (when-not (nil? s)
    (.toUpperCase s)))

(defn empty?
  "Checks if a string is empty or contains only whitespaces."
  [^String s]
  (cond
    (nil? s) true
    (= (count s) 0) true
    :else false))

(defn blank?
  "Checks if a string is empty or contains only whitespaces."
  [^String s]
  (cond
    (nil? s) true
    (= (count s) 0) true
    :else (let [rx #"^[\s\n]+$"]
            (if (re-matches rx s)
              true
              false))))

(declare escape-regexp)
(declare replace)

(defn trim
  "Removes whitespace or specified characters
  from both ends of string."
  ([s] (trim s " "))
  ([s chs]
   (when-not (nil? s)
     (let [rxstr (str "[" chs "]")
           rxstr (str "^" rxstr "+|" rxstr "+$")]
       (as-> (re-pattern rxstr) rx
         (replace s rx ""))))))

(defn rtrim
  "Removes whitespace or specified characters
  from right side of string."
  ([s] (rtrim s " "))
  ([s chs]
   (when-not (nil? s)
     (let [rxstr (str "[" chs "]")
           rxstr (str rxstr "+$")]
       (as-> (re-pattern rxstr) rx
         (replace s rx ""))))))

(defn ltrim
  "Removes whitespace or specified characters
  from left side of string."
  ([s] (ltrim s " "))
  ([s chs]
   (when-not (nil? s)
     (let [rxstr (str "[" chs "]")
           rxstr (str "^" rxstr)]
       (as-> (re-pattern rxstr) rx
         (replace s rx ""))))))

(declare join)

(defn repeat
  "Repeats string n times."
  ([^String s] (repeat s 1))
  ([^String s ^long n]
   (if (nil? s)
     s
     (join (clojure.core/repeat n s)))))

(defn slice
  "Extracts a section of a string and returns a new string."
  ([^String s ^long begin] (slice s begin (count s)))
  ([^String s ^long begin ^long end]
   (if (nil? s)
     s
     (let [end   (if (< end 0) (+ (count s) end) end)
           begin (if (< begin 0) (+ (count s) begin) begin)
           end   (if (> end (count s)) (count s) end)]
       (if (> begin end)
         ""
         (let [begin (if (< begin 0) 0 begin)
               end (if (< end 0) 0 end)]
           (.substring s begin end)))))))

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
  (when-not (nil? s)
    (str/replace s match replacement)))

(defn replace-first
  "Replaces first instance of match with replacement in s."
  [^String s match replacement]
  (when-not (nil? s)
    (str/replace-first s match replacement)))

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
   (cond
     (nil? s) s
     (instance? Pattern sep) (str/split s sep)
     :else (str/split s (re-pattern sep))))

  ([^String s sep ^long num]
   (cond
     (nil? s) s
     (instance? Pattern sep) (str/split s sep num)
     :else (str/split s (re-pattern sep) num))))

(defn reverse
  "Return string reversed."
  [^String s]
  (if (nil? s)
    s
    (let [sb (StringBuilder. s)]
      (.toString (.reverse sb)))))

(defn chars
  "Split a string in a seq of chars."
  [^String s]
  (when-not (nil? s)
    (into [] (.split s "(?!^)"))))

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

(defn join
  "Joins strings together with given separator."
  ([coll]
     (apply str coll))
  ([separator coll]
     (apply str (interpose separator coll))))

(defn surround
  "Surround a string with another string."
  [s wrap]
  (when-not (nil? s)
    (join [wrap s wrap])))

(defn unsurround
  "Unsurround a string surrounded by another."
  [s surrounding]
  (let [length (count surrounding)
        fstr (slice s 0 length)
        slength (count s)
        rightend (- slength length)
        lstr (slice s rightend slength)]
    (if (and (= fstr surrounding) (= lstr surrounding))
      (slice s length rightend)
      s)))

(defn quote
  "Quotes a string."
  ([s] (surround s "\""))
  ([s qchar] (surround s qchar)))

(defn unquote
  "Unquote a string."
  ([s] (unsurround s "\""))
  ([s qchar]
    (unsurround s qchar)))

(defn dasherize
  "Converts a underscored or camelized string
  into an dasherized one."
  [s]
  (-> s
      (trim)
      (replace #"([A-Z])" "-$1")
      (replace #"[-_\s]+" "-")
      (lower)))

(defn slugify
  "Transform text into a URL slug."
  [s]
  (let [from   "ąàáäâãåæăćčĉęèéëêĝĥìíïîĵłľńňòóöőôõðøśșšŝťțŭùúüűûñÿýçżźž"
        to     "aaaaaaaaaccceeeeeghiiiijllnnoooooooossssttuuuuuunyyczzz",
        regex  (re-pattern (str "[" (escape-regexp from) "]"))]
    (-> (lower s)
        (replace regex (fn [c]
                         (let [index (.indexOf from c)
                               res   (.charAt to index)
                               res   (String/valueOf res)]
                           (if (empty? res) "-" res))))
        (replace #"[^\w\s-]" "")
        (dasherize))))


;; (defn pad
;;   "Pads the str with characters until the total string
;;   length is equal to the passed length parameter. By
;;   default, pads on the left with the space char."
;;   [s & [{:keys [length padding type]
;;          :or {length 0 padding " " type :left}}]]
;;   (let [padding (aget padding 0)
;;         padlen  (- length (count s))]
;;     (condp = type
;;       :right (str s (repeat padding padlen))
;;       :both  (let [first (repeat padding (js/Math.ceil (/ padlen 2)))
;;                    second (repeat padding (js/Math.floor (/ padlen 2)))]
;;                (str first s second))
;;       :left  (str (repeat padding padlen) s))))

(defn capitalize
  "Converts first letter of the string to uppercase."
  [^String s]
  (when-not (nil? s)
    (let [firstc (-> (.charAt s 0)
                     (String/valueOf)
                     (upper))]
      (str firstc (slice s 1)))))

(defn camelize
  "Converts a string from selector-case to camelCase."
  [^String s]
  (some-> s
          (trim)
          (replace #"[-_\s]+(.)?" (fn [[match c]] (if c (upper c) "")))))

(defn underscored
  "Converts a camelized or dasherized string
  into an underscored one."
  [^String s]
  (some-> s
          (trim)
          (replace #"([a-z\d])([A-Z]+)" "$1_$2")
          (replace #"[-\s]+" "_")
          (lower)))

(defn humanize
  "Converts an underscored, camelized, or
  dasherized string into a humanized one."
  [^String s]
  (some-> s
          (underscored)
          (replace #"_id$", "")
          (replace "_" " ")
          (capitalize)))

(defn titleize
  "Converts a string into TitleCase."
  [^String s & [delimeters]]
  (when-not (nil? s)
    (let [delimeters (if delimeters
                       (escape-regexp delimeters)
                       "\\s")
          delimeters (str "|[" delimeters "]+")
          rx         (re-pattern (str "(^" delimeters ")([a-z])"))]
      (replace s rx (fn [[c1 _]]
                      (upper c1))))))

(defn classify
  "Converts string to camelized class name. First letter is always upper case."
  [^String s]
  (some-> s
          (str)
          (replace #"[\W_]" " ")
          (camelize)
          (replace #"\s" "")
          (capitalize)))

(defn parse-double
  "Return the double value from string."
  [^String s]
  (cond
    (nil? s) Double/NaN
    :else (Double/parseDouble s)))

(defn parse-long
  "Return the long value from string."
  [^String s]
  (cond
   (nil? s) Double/NaN
   :else (let [r (Double/parseDouble s)]
           (.longValue r))))

(defn pad
  "Pads the str with characters until the total string
  length is equal to the passed length parameter. By
  default, pads on the left with the space char."
  [s & [{:keys [length padding type]
         :or {length 0 padding " " type :left}}]]
  (when-not (nil? s)
    (let [padding (slice padding 0 1)
          padlen  (- length (count s))]
      (condp = type
        :right (str s (repeat padding padlen))
        :both  (let [first (repeat padding (Math/ceil (/ padlen 2)))
                     second (repeat padding (Math/floor (/ padlen 2)))]
                 (str first s second))
        :left  (str (repeat padding padlen) s)))))

(defn collapse-whitespace
  "Converts all adjacent whitespace characters
  to a single space."
  [s]
  (some-> s
          (replace #"[\s\xa0]+" " ")
          (replace #"^\s+|\s+$" "")))

(defn strip-tags
  "Remove html tags from string."
  ([s] (strip-tags s ""))
  ([s & tags]
   (reduce (fn [acc tag]
             (let [rx1  (-> (str "<\\/?" (lower tag) "[^<>]*>")
                            (re-pattern))
                   rx2  (-> (str "<\\/?" (upper tag) "[^<>]*>")
                            (re-pattern))]
               (-> acc
                   (replace rx1 "")
                   (replace rx2 ""))))
           s
           tags)))

(defn clean
  "Trim and replace multiple spaces with
  a single space."
  [s]
  (-> (trim s)
      (replace #"\s+" " ")))
