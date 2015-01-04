(ns cuerdas.core
  (:refer-clojure :exclude [contains? empty? repeat replace chars reverse])
  (:require [clojure.string :as str]
            [clojure.set :refer [map-invert]]
            [goog.string :as gstr]))

(defn contains?
  "Determines whether a string contains a substring."
  [s subs]
  (when-not (nil? s)
    (not= (.indexOf s subs) -1)))

(defn regexp
  "Build or derive regexp instance."
  ([s]
   (if (regexp? s)
     s
     (js/RegExp. s)))
  ([s flags]
   (if (regexp? s)
     (js/RegExp. (.-source s) flags)
     (js/RegExp. s flags))))

(defn escape-regexp
  "Escapes characters in the string that are not safe
  to use in a RegExp."
  [s]
  (gstr/regExpEscape s))

(defn starts-with?
  "Check if the string starts with prefix."
  [s prefix]
  (when-not (nil? s)
    (= (.lastIndexOf s prefix 0) 0)))

(defn ends-with?
  "Check if the string ends with suffix."
  [s suffix]
  (let [l (- (count s) (count suffix))]
    (and (>= l 0)
         (= (.indexOf s suffix l) l))))

(def startswith? starts-with?)
(def endswith? ends-with?)

(defn lower
  "Converts string to all lower-case."
  [s]
  (when-not (nil? s)
    (.toLowerCase s)))

(defn upper
  "Converts string to all upper-case."
  [s]
  (when-not (nil? s)
    (.toUpperCase s)))

(declare replace)

(defn collapse-whitespace
  "Converts all adjacent whitespace characters
  to a single space."
  [s]
  (some-> s
          (replace #"[\s\xa0]+" " ")
          (replace #"^\s+|\s+$" "")))

(defn empty?
  "Checks if a string is empty."
  [s]
  (cond
   (nil? s) true
   (= (count s) 0) true
   :else false))

(defn blank?
  "Checks if a string is empty or contains only whitespaces."
  [s]
  (gstr/isEmptySafe s))

(defn repeat
  "Repeats string n times."
  ([s] (repeat s 1))
  ([s n]
   (when-not (nil? s)
     (gstr/repeat s n))))

(defn strip-newlines
  "Takes a string and replaces newlines with a space.
  Multiple lines are replaced with a single space."
  [s]
  (replace s #"(\r\n|\r|\n)+" " "))

(defn split
  "Splits a string on a separator a limited
  number of times. The separator can be a string
  or RegExp instance."
  ([s] (split s #"\s" nil))
  ([s sep] (split s sep nil))
  ([s sep num]
   (cond
     (nil? s) s
     (regexp? sep) (str/split s sep num)
     :else (str/split s (re-pattern sep) num))))

(defn lines
  "Return a list of the lines in the string."
  [s]
  (split s #"\n|\r\n"))

(defn chars
  "Split a string in a seq of chars."
  [s]
  (when-not (nil? s)
    (js->clj (.split s ""))))

(defn slice
  "Extracts a section of a string and returns a new string."
  ([s begin]
   (when-not (nil? s)
     (.slice s begin)))
  ([s begin end]
   (when-not (nil? s)
     (.slice s begin end))))

(defn replace
  "Replaces all instance of match with replacement in s."
  [s match replacement]
  (when-not (nil? s)
    (.replace s (regexp match "g") replacement)))

(defn ireplace
  "Replaces all instance of match with replacement in s."
  [s match replacement]
  (when-not (nil? s)
    (.replace s (regexp match "ig") replacement)))

(defn replace-first
  "Replaces first instance of match with replacement in s."
  [s match replacement]
  (when-not (nil? s)
    (.replace s (regexp match) replacement)))

(defn ireplace-first
  "Replaces first instance of match with replacement in s."
  [s match replacement]
  (when-not (nil? s)
    (.replace s (regexp match "i") replacement)))

(defn trim
  "Removes whitespace or specified characters
  from both ends of string."
  ([s] (trim s " "))
  ([s chs]
   (when-not (nil? s)
     (let [rxstr (str "[" (escape-regexp chs) "]")
           rx    (str "^" rxstr "+|" rxstr "+$")]
       (replace s rx "")))))

(defn rtrim
  "Removes whitespace or specified characters
  from right side of string."
  ([s] (rtrim s " "))
  ([s chs]
   (when-not (nil? s)
     (let [rxstr (str "[" (escape-regexp chs) "]")
           rx    (str rxstr "+$")]
       (replace s rx "")))))

(defn ltrim
  "Removes whitespace or specified characters
  from left side of string."
  ([s] (ltrim s " "))
  ([s chs]
   (when-not (nil? s)
     (let [rxstr (str "[" (escape-regexp chs) "]")
           rx    (str "^" rxstr "+")]
       (replace s rx "")))))

(def strip trim)
(def rstrip rtrim)
(def lstrip ltrim)

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
           template (if (.match (slice template (- (count template) 2)) #"\w\w")
                      (replace-first template #"\s*\S+$" "")
                      (rtrim (slice template 0 (dec (count template)))))]
       (if (> (count (str template subs)) (count s))
         s
         (str (slice s 0 (count template)) subs))))))

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
    (join "" [wrap s wrap])))

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

(declare dasherize)

(defn slugify
  "Transform text into a URL slug."
  [s]
  (let [from   "ąàáäâãåæăćčĉęèéëêĝĥìíïîĵłľńňòóöőôõðøśșšŝťțŭùúüűûñÿýçżźž"
        to     "aaaaaaaaaccceeeeeghiiiijllnnoooooooossssttuuuuuunyyczzz",
        regex  (str "[" (escape-regexp from) "]")]
    (-> (lower s)
        (replace regex (fn [c]
                         (let [index (.indexOf from c)
                               res   (.charAt to index)]
                           (if (empty? res) "-" res))))
        (replace #"[^\w\s-]" "")
        (dasherize))))

(defn strip-tags
  "Remove html tags from string."
  [s & [{:keys [tags replace-map]
                 :or {tags nil replace-map {}}}]]
  (let [kwdize (comp keyword lower)
        tags (cond
               (nil? tags) tags
               (string? tags) (hash-set (kwdize tags))
               (sequential? tags) (set (map kwdize tags)))
        rx   (re-pattern "<\\/?([^<>]*)>")
        replacer (if (nil? tags)
                   (fn [match tag]
                     (let [tag (kwdize tag)]
                       (get replace-map tag "")))
                   (fn [match tag]
                     (let [tag (kwdize tag)]
                       (if (tags tag)
                         (get replace-map tag "")
                         match))))]
    (replace s rx replacer)))

(defn clean
  "Trim and replace multiple spaces with
  a single space."
  [s]
  (-> (trim s)
      (replace #"\s+" " ")))

(def html-escape-chars
  {"lt" "<"
   "gt" ">"
   "quot" "\""
   "amp" "&"
   "apos" "'"})

(def reversed-html-escape-chars
  (map-invert html-escape-chars))

;; reversedEscapeChars["'"] = '#39';

(defn escape-html
  [s]
  "Converts HTML special characters to their entity equivalents."
  (let [escapechars (assoc reversed-html-escape-chars "'" "#39")
        rx (re-pattern "[&<>\"']")]
    (replace s rx (fn [x]
                    (str "&" (get escapechars x) ";")))))



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

(defn unescape-html
  "Converts entity characters to HTML equivalents."
  [s]
  (replace s #"\&(\w+);" (fn [x y]
                             (cond
                               (cljs.core/contains? html-escape-chars y)
                               (get html-escape-chars y)
                               :else y))))

(defn reverse
  "Return string reversed."
  [s]
  (when-not (nil? s)
    (let [cs (.split s "")
          cs (.reverse cs)]
      (.join cs ""))))

(defn- parse-number-impl
  [source]
  (or (* source 1) 0))

(defn parse-number
  "General purpose function for parse number like
  string to number. It works with both: integers
  and floats."
  ([s] (parse-number s 0))
  ([s precision]
   (if (nil? s)
     0
     (let [s  (trim s)
           rx #"^-?\d+(?:\.\d+)?$"]
       (if (.match s rx)
         (parse-number-impl (.toFixed (parse-number-impl s) precision))
         NaN)))))

(defn parse-float
  "Return the float value, wraps parseFloat."
  ([s] (js/parseFloat s))
  ([s precision]
   (if (nil? precision)
     (js/parseFloat s)
     (-> (js/parseFloat s)
         (.toFixed precision)
         (js/parseFloat)))))

(defn parse-int
  "Return the number value in integer form."
  [s]
  (let [rx (regexp "^\\s*-?0x" "i")]
    (if (.test rx s)
      (js/parseInt s 16)
      (js/parseInt s 10))))

(defn format
  "Simple string interpolation."
  [s & args]
  (if (and (= (count args) 1) (map? (first args)))
    (let [params (clj->js (first args))]
      (replace s #"%\(\w+\)s"
               (fn [match]
                 (str (aget params (slice match 2 -2))))))
    (let [params (clj->js args)]
      (replace s (regexp "%s" "g") (fn [_] (str (.shift params)))))))

(defn pad
  "Pads the str with characters until the total string
  length is equal to the passed length parameter. By
  default, pads on the left with the space char."
  [s & [{:keys [length padding type]
         :or {length 0 padding " " type :left}}]]
  (when-not (nil? s)
    (let [padding (aget padding 0)
          padlen  (- length (count s))]
      (condp = type
        :right (str s (repeat padding padlen))
        :both  (let [first (repeat padding (js/Math.ceil (/ padlen 2)))
                     second (repeat padding (js/Math.floor (/ padlen 2)))]
                 (str first s second))
        :left  (str (repeat padding padlen) s)))))

(defn capitalize
  "Converts first letter of the string to uppercase."
  [s]
  (when-not (nil? s)
    (str (upper (.charAt s 0)) (slice s 1))))

(defn camelize
  "Converts a string from selector-case to camelCase."
  [s]
  (some-> s
          (trim)
          (replace (regexp #"[-_\s]+(.)?" "g")
                   (fn [match c] (if c (upper c) "")))))

(defn dasherize
  "Converts a underscored or camelized string
  into an dasherized one."
  [s]
  (-> s
      (trim)
      (replace #"([A-Z])" "-$1")
      (replace #"[-_\s]+" "-")
      (lower)))

(defn underscored
  "Converts a camelized or dasherized string
  into an underscored one."
  [s]
  (some-> s
          (trim)
          (replace (regexp #"([a-z\d])([A-Z]+)" "g") "$1_$2")
          (replace (regexp #"[-\s]+", "g") "_")
          (lower)))

(defn humanize
  "Converts an underscored, camelized, or
  dasherized string into a humanized one."
  [s]
  (some-> s
          (underscored)
          (replace #"_id$", "")
          (replace (regexp "_" "g") " ")
          (capitalize)))

(defn titleize
  "Converts a string into TitleCase."
  ([s]
   (when-not (nil? s)
     (gstr/toTitleCase s)))
  ([s delimiters]
   (when-not (nil? s)
     (gstr/toTitleCase s delimiters))))

(defn classify
  "Converts string to camelized class name. First letter is always upper case."
  [s]
  (some-> s
          (str)
          (replace #"[\W_]" " ")
          (camelize)
          (replace #"\s" "")
          (capitalize)))
