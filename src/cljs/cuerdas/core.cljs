(ns cuerdas.core
  (:refer-clojure :exclude [contains? empty? repeat replace])
  (:require [clojure.string :as str]
            [goog.string :as gstr]))

(defn contains?
  "Determines whether a string contains a substring."
  [s subs]
  (gstr/contains s subs))

(defn- derive-regexp
  [rx flags]
  (let [gflag (if (and (not (contains? flags "-g"))
                       (or (.-global rx) (contains? flags "g")))
                "g" "")
        iflag (if (and (not (contains? flags "-i"))
                       (or (.-ignoreCase rx) (contains? flags "i")))
                "i" "")
        mflag (if (and (not (contains? flags "-m"))
                       (or (.-multiline rx) (contains? flags "m")))
                "m" "")]
    (js/RegExp. (.-source rx) (str gflag iflag mflag))))

(defn regexp
  "Build or derive regexp instance."
  ([s] (regexp s ""))
  ([s flags]
   (if (regexp? s)
     (derive-regexp s flags)
     (js/RegExp. s flags))))

(defn escape-regexp
  "Escapes characters in the string that are not safe
  to use in a RegExp."
  [s]
  (gstr/regExpEscape s))

(defn startswith?
  "Check if the string starts with prefix."
  [s prefix]
  (gstr/startsWith s prefix))

(defn endswith?
  "Check if the string ends with suffix."
  [s prefix]
  (gstr/endsWith s prefix))

(defn lower
  "Converts string to all lower-case."
  [s]
  (str/lower-case s))

(defn upper
  "Converts string to all upper-case."
  [s]
  (str/upper-case s))

(defn collapse-whitespace
  "Converts all adjacent whitespace characters
  to a single space."
  [s]
  (gstr/collapseWhitespace s))

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
  ([s sep] (split s sep nil))
  ([s sep num]
   (if (regexp? sep)
     (str/split s sep num)
     (str/split s (regexp sep) num))))

(defn lines
  "Return a list of the lines in the string."
  [s]
  (split s #"\n|\r\n"))

(defn chars
  "Split a string in a seq of chars."
  [s]
  (js->clj (.split s "")))

(defn slice
  "Extracts a section of a string and returns a new string."
  ([s begin]
   (.slice s begin))
  ([s begin end]
   (.slice s begin end)))

(defn replace
  "Replaces all instance of match with replacement in s."
  [s match replacement]
  (.replace s (regexp match "g") replacement))

(defn ireplace
  "Replaces all instance of match with replacement in s."
  [s match replacement]
  (.replace s (regexp match "ig") replacement))

(defn replace-first
  "Replaces first instance of match with replacement in s."
  [s match replacement]
  (.replace s (regexp match) replacement))

(defn ireplace-first
  "Replaces first instance of match with replacement in s."
  [s match replacement]
  (.replace s (regexp match "i") replacement))

(defn trim
  "Removes whitespace or specified characters
  from both ends of string."
  ([s] (trim s " "))
  ([s chs]
   (let [rxstr (str "[" (escape-regexp chs) "]")
         rx    (str "^" rxstr "+|" rxstr "+$")]
     (replace s rx ""))))

(defn rtrim
  "Removes whitespace or specified characters
  from right side of string."
  ([s] (rtrim s " "))
  ([s chs]
   (let [rxstr (str "[" (escape-regexp chs) "]")
         rx    (str rxstr "+$")]
     (replace s rx ""))))

(defn ltrim
  "Removes whitespace or specified characters
  from left side of string."
  ([s] (ltrim s " "))
  ([s chs]
   (let [rxstr (str "[" (escape-regexp chs) "]")
         rx    (str "^" rxstr "+")]
     (replace s rx ""))))

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
  (join "" [wrap s wrap]))

(defn quote
  "Quotes a string."
  ([s] (surround s "\""))
  ([s qchar] (surround s qchar)))

(defn unquote
  "Unquote a string."
  ([s] (unquote s "\""))
  ([s qchar]
   (let [length (count s)
         fchar (aget s 0)
         lchar (aget s (dec length))]
     (if (and (= fchar qchar) (= lchar qchar))
       (slice s 1 (dec length))
       s))))

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
  ([s] (strip-tags s ""))
  ([s & tags]
   (reduce (fn [acc tag]
             (let [rx (str "<\\/?" tag "[^<>]*>")]
               (ireplace acc rx "")))
           s
           tags)))

(defn reverse
  "Return string reversed."
  [s]
  ;; Uses bare js implementation
  ;; for performance reasons.
  (let [cs (.split s "")
        cs (.reverse cs)]
    (.join cs "")))

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
  (let [padding (aget padding 0)
        padlen  (- length (count s))]
    (condp = type
      :right (str s (repeat padding padlen))
      :both  (let [first (repeat padding (js/Math.ceil (/ padlen 2)))
                   second (repeat padding (js/Math.floor (/ padlen 2)))]
               (str first s second))
      :left  (str (repeat padding padlen) s))))

(defn capitalize
  "Converts first letter of the string to uppercase."
  [s]
  (str (upper (.charAt s 0)) (slice s 1)))

(defn camelize
  "Converts a string from selector-case to camelCase."
  [s]
  (-> (trim s)
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
  (-> (trim s)
      (replace (regexp #"([a-z\d])([A-Z]+)" "g") "$1_$2")
      (replace (regexp #"[-\s]+", "g") "_")
      (lower)))

(defn humanize
  "Converts an underscored, camelized, or
  dasherized string into a humanized one."
  [s]
  (-> (underscored s)
      (replace #"_id$", "")
      (replace (regexp "_" "g") " ")
      (capitalize)))

(defn titleize
  "Converts a string into TitleCase."
  ([s]
   (gstr/toTitleCase s))
  ([s delimiters]
   (gstr/toTitleCase s delimiters)))

(defn classify
  "Converts string to camelized class name. First letter is always upper case."
  [s]
  (-> (str s)
      (replace #"[\W_]" " ")
      (camelize)
      (replace #"\s" "")
      (capitalize)))
