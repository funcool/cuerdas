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

(defn slice
  "Extracts a section of a string and returns a new string."
  ([s begin]
   (.slice s begin))
  ([s begin end]
   (.slice s begin end)))

(defn replace
  [s match replacement]
  (cond
   (string? match)
   (.replace s (js/RegExp. (escape-regexp match) "g") replacement)

   (regexp? match)
   (.replace s (js/RegExp. (.-source match) "g") replacement)

   :else
   (throw (str "Invalid match arg: " match))))

(defn replace-first
  [s match replacement]
  (cond
   (string? match)
   (.replace s (js/RegExp. (escape-regexp match)) replacement)

   (regexp? match)
   (.replace s (js/RegExp. (.-source match)) replacement)

   :else
   (throw (str "Invalid match arg: " match))))

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

(defn dasherize
  "Converts a underscored or camelized string into an dasherized one."
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
        regex  (js/RegExp. (str "[" (escape-regexp from) "]"))]
    (-> (lower s)
        (replace regex (fn [c]
                         (let [index (.indexOf from c)
                               res   (.charAt to index)]
                           (if (empty? res) "-" res))))
        (replace #"[^\w\s-]" "")
        (dasherize))))

(defn regexp
  ([s] (regexp s ""))
  ([s flags]
   (if (regexp? s)
     (js/RegExp. (.-source s) flags)
     (js/RegExp. s flags))))

(defn strip-tags
  ([s] (strip-tags s ""))
  ([s & tags]
   (reduce (fn [acc tag]
             (let [rx (regexp (str "<\\/?" tag "[^<>]*>") "gi")]
               (replace acc rx "")))
           s
           tags)))

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
  (let [rx (regexp #"^\s*-?0x" "i")]
    (if (.test rx s)
      (js/parseInt s 16)
      (js/parseInt s 10))))

(defn format
  "Simple string interpolation."
  [s & args]
  (if (and (= (count args) 1) (map? (first args)))
    (let [params (clj->js (first args))]
      (replace s (regexp #"%\(\w+\)s" "g")
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
