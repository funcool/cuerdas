(ns cuerdas.core
  (:refer-clojure :exclude [contains? empty? repeat replace reverse chars
                            #?@(:clj [unquote format])])
  (:require [clojure.string :as str]
            #?(:cljs [goog.string :as gstr])
            [clojure.set :refer [map-invert]]
            #?(:cljs [cljs.reader :as edn]
               :clj  [clojure.edn :as edn])
            [clojure.walk :refer [stringify-keys]])
  #?(:clj (:import java.util.regex.Pattern
                   java.util.List)))

(defn empty?
  "Checks if a string is empty."
  [^String s]
  (if (nil? s)
    false
    (= (count s) 0)))

(defn contains?
  "Determines whether a string contains a substring."
  [s subs]
  (when-not (nil? s)
    #?(:clj  (cond
               (nil? subs) false
               (empty? subs) true
               (>= (.indexOf ^String s ^String subs) 0) true
               :else false)
       :cljs (not= (.indexOf s subs) -1))))

(defn slice
  "Extracts a section of a string and returns a new string."
  ([s begin]
   #?(:clj  (slice s begin (count s))
      :cljs (when-not (nil? s)
              (.slice s begin))))
  ([s #?@(:clj [^long begin ^long end] :cljs [begin end])]
   #?(:clj (if (nil? s)
             s
             (let [end   (if (< end 0) (+ (count s) end) end)
                   begin (if (< begin 0) (+ (count s) begin) begin)
                   end   (if (> end (count s)) (count s) end)]
               (if (> begin end)
                 ""
                 (let [begin (if (< begin 0) 0 begin)
                       end (if (< end 0) 0 end)]
                   (.substring ^String s begin end)))))
      :cljs (when-not (nil? s)
              (.slice s begin end)))))

#?(:cljs
   (defn- regexp
     "Build or derive regexp instance."
     ([s]
      (if (regexp? s)
        s
        (js/RegExp. s)))
     ([s flags]
      (if (regexp? s)
        (js/RegExp. (.-source s) flags)
        (js/RegExp. s flags)))))

(defn starts-with?
  "Check if the string starts with prefix."
  [s prefix]
  #?(:clj (when-not (nil? s)
            (cond
              (nil? prefix) false
              (empty? prefix) true
              :else (let [region (slice s 0 (count prefix))]
                      (= region prefix))))
     :cljs (when-not (nil? s)
             (= (.lastIndexOf s prefix 0) 0))))

(defn ends-with?
  "Check if the string ends with suffix."
  [s suffix]
  #?(:clj (when-not (nil? s)
            (nil? s) false
            (empty? suffix) true
            :else (let [len (count s)
                        region (slice s (- len (count suffix)) len)]
                    (= region suffix)))
     :cljs (when-not (nil? s)
             (let [l (- (count s) (count suffix))]
               (and (>= l 0)
                    (= (.indexOf s suffix l) l))))))

(def startswith? starts-with?)
(def endswith? ends-with?)

(defn lower
  "Converts string to all lower-case."
  [s]
  (when-not (nil? s)
    (.toLowerCase #?(:clj ^String s :cljs s))))

(defn upper
  "Converts string to all upper-case."
  [s]
  (when-not (nil? s)
    (.toUpperCase #?(:clj ^String s :cljs s))))

(defn blank?
  "Checks if a string is empty or contains only whitespace."
  [^String s]
  #?(:clj (cond
            (nil? s) true
            (= (count s) 0) true
            :else (let [rx #"^[\s\n]+$"]
                    (if (re-matches rx s)
                      true
                      false)))
     :cljs (gstr/isEmptySafe s)))

(defn- char-range-check
  [re]
  (fn [^String s]
    (if (nil? s)
      false
      (re-matches re s))))

(def alpha?
  "Checks if a string contains only alpha characters."
  (char-range-check #"^[a-zA-Z]+$"))

(def numeric?
  "Checks if a string contains only numeric characters."
  (char-range-check #"^[0-9]+$"))

(def alpha-numeric?
  "Checks if a string contains only alphanumeric characters."
  (char-range-check #"^[a-zA-Z0-9]+$"))

(declare escape-regexp)
(declare replace)

(defn trim
  "Removes whitespace or specified characters
  from both ends of string."
  ([s] (trim s " "))
  ([s chs]
   (when-not (nil? s)
     (let [rxstr (str "[" #?(:clj chs :cljs (escape-regexp chs)) "]")
           rxstr (str "^" rxstr "+|" rxstr "+$")]
       (as-> (re-pattern rxstr) rx
         (replace s rx ""))))))

(defn rtrim
  "Removes whitespace or specified characters
  from right side of string."
  ([s] (rtrim s " "))
  ([s chs]
   (when-not (nil? s)
     (let [rxstr (str "[" #?(:clj chs :cljs (escape-regexp chs)) "]")
           rxstr (str rxstr "+$")]
       (as-> (re-pattern rxstr) rx
         (replace s rx ""))))))

(defn ltrim
  "Removes whitespace or specified characters
  from left side of string."
  ([s] (ltrim s " "))
  ([s chs]
   (when-not (nil? s)
     (let [rxstr (str "[" #?(:clj chs :cljs (escape-regexp chs)) "]")
           rxstr (str "^" rxstr "+")]
       (as-> (re-pattern rxstr) rx
         (replace s rx ""))))))

(def strip trim)
(def rstrip rtrim)
(def lstrip ltrim)

(defn strip-prefix
  "Strip prefix in more efficient way."
  [^String s ^String prefix]
  (if (starts-with? s prefix)
    (slice s (count prefix) (count s))
    s))

(defn strip-suffix
  "Strip suffix in more efficient way."
  [^String s ^String prefix]
  (if (ends-with? s prefix)
    (slice s 0 (- (count s) (count prefix)))
    s))

(declare join)

(defn repeat
  "Repeats string n times."
  ([s] (repeat s 1))
  ([s n]
   (when-not (nil? s)
     #?(:clj  (join (clojure.core/repeat n s))
        :cljs (gstr/repeat s n)))))

(defn escape-regexp
  "Escapes characters in the string that are not safe
   to use in a RegExp."
  [s]
  #?(:clj  (Pattern/quote ^String s)
     :cljs (gstr/regExpEscape s)))

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
  [s match replacement]
  (when-not (nil? s)
    #?(:clj  (str/replace s match replacement)
       :cljs (.replace s (regexp match "g") replacement))))

#?(:cljs
   (defn ireplace
     "Replaces all instance of match with replacement in s."
     [s match replacement]
     (when-not (nil? s)
       (.replace s (regexp match "ig") replacement))))

(defn replace-first
  "Replaces first instance of match with replacement in s."
  [^String s match replacement]
  (when-not (nil? s)
    #?(:clj  (str/replace-first s match replacement)
       :cljs (.replace s (regexp match) replacement))))

#?(:cljs
   (defn ireplace-first
     "Replaces first instance of match with replacement in s."
     [s match replacement]
     (when-not (nil? s)
       (.replace s (regexp match "i") replacement))))

(defn prune
  "Truncates a string to a certain length and adds '...'
  if necessary."
  ([s num] (prune s num "..."))
  ([s num subs]
   (if (<= (count s) num)
     s
     (let [tmpl (fn [c]
                  (if (not= (upper c) (lower c)) "A" " "))
           template (-> (slice s 0 (inc num))
                        (replace #".(?=\W*\w*$)" tmpl))
           tmp (slice template (- (count template) 2))
           template (if #?(:clj  (.matches ^String tmp "\\w\\w")
                           :cljs (.match tmp #"\w\w"))
                      (replace-first template #"\s*\S+$" "")
                      (rtrim (slice template 0 (dec (count template)))))]
       (if (> (count (str template subs)) (count s))
         s
         (str (slice s 0 (count template)) subs))))))

(defn strip-newlines
  "Takes a string and replaces newlines with a space.
  Multiple lines are replaced with a single space."
  [^String s]
  (replace s #?(:clj #"[\n\r|\n]+" :cljs #"(\r\n|\r|\n)+") " "))

(defn split
  "Splits a string on a separator a limited
  number of times. The separator can be a string
  or Pattern (clj) / RegExp (cljs) instance."
  ([s] (split s #"\s" #?(:cljs nil)))
  ([s sep]
   #?(:clj  (cond
              (nil? s) s
              (instance? Pattern sep) (str/split s sep)
              :else (str/split s (re-pattern sep)))
      :cljs (split s sep nil)))
  ([s sep num]
   (cond
     (nil? s) s
     #?(:clj  (instance? Pattern sep)
        :cljs (regexp?           sep)) (str/split s sep num)
     :else (str/split s (re-pattern sep) num))))

(defn reverse
  "Return string reversed."
  [s]
  (when-not (nil? s)
    #?(:clj (let [sb (StringBuilder. ^String s)]
              (.toString (.reverse sb)))
       :cljs (-> s (.split "") (.reverse) (.join "")))))

(defn chars
  "Split a string in a seq of chars."
  [s]
  (when-not (nil? s)
    #?(:clj  (into [] (.split ^String s "(?!^)"))
       :cljs (js->clj (.split s "")))))

(defn lines
  "Return a list of the lines in the string."
  [s]
  (split s #"\n|\r\n"))

(defn unlines
  "Returns a new string joining a list of strings with a newline char (\\n)."
  [s]
  (if (nil? s)
    s
    (str/join "\n" s)))

(defn words
  "Returns a vector of the words in the string."
  ([^String s word-re]
   (if (nil? s)
     []
     (vec (re-seq word-re s))))
  ([^String s]
   (words s #"[a-zA-Z0-9_-]+")))

(defn- interpolate-format
  [s params]
  (letfn [(on-match [match]
            (let [val (edn/read-string
                       (if (= (subs match 0 1) "$")
                         (subs match 1)
                         (slice match 2 -2)))
                  val (if (symbol? val) (keyword val) val)]
              (str (get params val ""))))]
    (as-> #"(?:%\([\d\w\:\_\-]+\)s|\$[\w\d\:\_\-]+)" $
      (replace s $ on-match))))

(defn- indexed-format
  [s params]
  (let [params #?(:clj (java.util.ArrayList. ^List (vec params))
                  :cljs (clj->js (or params [])))]
    (replace s #?(:clj #"%s" :cljs (regexp "%s" "g"))
             (fn [_] (str #?(:clj (if (.isEmpty params)
                                    "%s"
                                    (.remove params 0))
                             :cljs (if (zero? (count params))
                                     "%s"
                                     (.shift params))))))))

(defn format
  "Simple string interpolation."
  [s & more]
  (when (string? s)
    (if (and (= (count more) 1) (associative? (first more)))
      (interpolate-format s (first more))
      (indexed-format s more))))

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
    (join #?(:cljs "") [wrap s wrap])))

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
  (some-> s
          (trim)
          (replace #"([A-Z]+)" "-$1")
          (replace #"[-_\s]+" "-")
          (strip-prefix "-")
          (lower)))

(defn slugify
  "Transform text into a URL slug."
  [s]
  (when s
    (let [from  "ąàáäâãåæăćčĉęèéëêĝĥìíïîĵłľńňòóöőôõðøśșšŝťțŭùúüűûñÿýçżźž"
          to    "aaaaaaaaaccceeeeeghiiiijllnnoooooooossssttuuuuuunyyczzz"
          regex (re-pattern (str "[" (escape-regexp from) "]"))]
      (-> (lower s)
          (replace regex (fn [^String c]
                           (let [index (.indexOf from c)
                                 res   #?(:clj  (String/valueOf (.charAt to index))
                                          :cljs (.charAt to index))]
                             (if (empty? res) "-" res))))
          (replace #"[^\w\s-]" "")
          (dasherize)))))

(defn capitalize
  "Converts first letter of the string to uppercase."
  [s]
  (when-not (nil? s)
    (-> (.charAt ^String s 0)
        #?(:clj (String/valueOf))
        (upper)
        (str (slice s 1)))))

(defn camelize
  "Converts a string from selector-case to camelCase."
  [s]
  (some-> s
          (trim)
          (replace #?(:clj  #"[-_\s]+(.)?"
                      :cljs (regexp #"[-_\s]+(.)?" "g"))
                   (fn [[match c]] (if c (upper c) "")))))

(defn underscored
  "Converts a camelized or dasherized string
  into an underscored one."
  [s]
  (some-> s
          (trim)
          (replace #?(:clj  #"([a-z\d])([A-Z]+)"
                      :cljs (regexp #"([a-z\d])([A-Z]+)" "g"))"$1_$2")
          (replace #?(:clj  #"[-\s]+"
                      :cljs (regexp #"[-\s]+", "g")) "_")
          (lower)))

(defn humanize
  "Converts an underscored, camelized, or
  dasherized string into a humanized one."
  [s]
  (some-> s
          (underscored)
          (replace #"_id$", "")
          (replace #?(:clj "_" :cljs (regexp "_" "g")) " ")
          (capitalize)))

(defn titleize
  "Converts a string into TitleCase."
  ([s]
   #?(:clj  (titleize s nil)
      :cljs (when-not (nil? s)
              (gstr/toTitleCase s))))
  ([s delimeters]
   #?(:clj
      (when-not (nil? s)
        (let [delimeters (if delimeters
                           (escape-regexp delimeters)
                           "\\s")
              delimeters (str "|[" delimeters "]+")
              rx         (re-pattern (str "(^" delimeters ")([a-z])"))]
          (replace s rx (fn [[c1 _]]
                          (upper c1)))))
      :cljs (gstr/toTitleCase s delimeters))))

(defn classify
  "Converts string to camelized class name. First letter is always upper case."
  [s]
  (some-> s
          (str)
          (replace #"[\W_]" " ")
          (camelize)
          (replace #"\s" "")
          (capitalize)))

#?(:cljs
   (defn- parse-number-impl
     [source]
     (or (* source 1) 0)))

#?(:cljs
   (defn parse-number
     "General purpose function for parse number like
  string to number. It works with both integers
  and floats."
     ([s] (parse-number s 0))
     ([s precision]
      (if (nil? s)
        0
        (let [s  (trim s)
              rx #"^-?\d+(?:\.\d+)?$"]
          (if (.match s rx)
            (parse-number-impl (.toFixed (parse-number-impl s) precision))
            NaN))))))

#?(:cljs
   (defn parse-float
     "Return the float value, wraps parseFloat."
     ([s] (js/parseFloat s))
     ([s precision]
      (if (nil? precision)
        (js/parseFloat s)
        (-> (js/parseFloat s)
            (.toFixed precision)
            (js/parseFloat))))))

#?(:clj
   (defn parse-double
     "Return the double value from string."
     [^String s]
     (cond
       (nil? s) Double/NaN
       :else (Double/parseDouble s)))
   )

(defn parse-int
  "Return the number value in integer form."
  [s]
  #?(:cljs
    (let [rx (regexp "^\\s*-?0x" "i")]
      (if (.test rx s)
        (js/parseInt s 16)
        (js/parseInt s 10)))
    :clj
    (if (and (string? s) (re-matches #"-?\d+(\.\d+)?" s))
      (.longValue (Double. s))
      Double/NaN)))

#?(:clj
   (defn parse-long
     "Return the long value from string."
     [^String s]
     (cond
       (nil? s) Double/NaN
       :else (let [r (Double. (Double/parseDouble s))]
               (.longValue ^java.lang.Double r))))
   )

(defn one-of?
  "Returns true if s can be found in coll."
  [coll ^String s]
  (boolean (some #(= % s) coll)))

(defn to-bool
  "Returns true for 1/on/true/yes string values (case-insensitive),
  false otherwise."
  [^String s]
  (one-of? ["1" "on" "true" "yes"] (lower s)))

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

#?(:cljs
   (def html-escape-chars
     {"lt" "<"
      "gt" ">"
      "quot" "\""
      "amp" "&"
      "apos" "'"}))

#?(:cljs
   (def reversed-html-escape-chars
     (map-invert html-escape-chars)))

;; reversedEscapeChars["'"] = '#39';

#?(:cljs
   (defn escape-html
     [s]
     "Converts HTML special characters to their entity equivalents."
     (let [escapechars (assoc reversed-html-escape-chars "'" "#39")
           rx (re-pattern "[&<>\"']")]
       (replace s rx (fn [x]
                       (str "&" (get escapechars x) ";"))))))

;; Complete logic for unescape-html
;;   if (entityCode in escapeChars) {
;;     return escapeChars[entityCode];
;;   } else if (match = entityCode.match(/^#x([\da-fA-F]+)$/)) {
;;     return String.fromCharCode(parseInt(match[1], 16));
;;   } else if (match = entityCode.match(/^#(\d+)$/)) {
;;     return String.fromCharCode(~~match[1]);
;;   } else {
;;     return entity;
;;   }

;; TODO: basic implementation

#?(:cljs
   (defn unescape-html
     "Converts entity characters to HTML equivalents."
     [s]
     (replace s #"\&(\w+);" (fn [x y]
                              (cond
                                (cljs.core/contains? html-escape-chars y)
                                (get html-escape-chars y)
                                :else y)))))

(defn- strip-tags-impl
  [s tags mappings]
  (let [kwdize (comp keyword lower name)
        tags (cond
               (nil? tags) tags
               (string? tags) (hash-set (kwdize tags))
               (sequential? tags) (set (map kwdize tags)))
        rx   (re-pattern "<\\/?([^<>]*)>")
        replacer (if (nil? tags)
                   (fn #?(:clj [[match tag]] :cljs [match tag])
                     (let [tag (kwdize tag)]
                       (get mappings tag "")))
                   (fn #?(:clj [[match tag]] :cljs [match tag])
                     (let [tag (kwdize tag)]
                       (if (tags tag)
                         (get mappings tag "")
                         match))))]
    (replace s rx replacer)))

(defn strip-tags
  "Remove html tags from string."
  ([s] (strip-tags-impl s nil {}))
  ([s tags]
   (if (map? tags)
     (strip-tags-impl s nil  tags)
     (strip-tags-impl s tags {}  )))
  ([s tags mapping]
   (strip-tags-impl s tags mapping)))

(defn clean
  "Trim and replace multiple spaces with
  a single space."
  [s]
  (-> (trim s)
      (replace #"\s+" " ")))

(defn substr-between
  "Find string that is nested in between two strings. Return first match"
  [s prefix suffix]
  (cond
    (nil? s) nil
    (nil? prefix) nil
    (nil? suffix) nil
    (not (contains? s prefix)) nil
    (not (contains? s suffix)) nil
    :else
    (some-> s
            (split prefix)
            second
            (split suffix)
            first)))
