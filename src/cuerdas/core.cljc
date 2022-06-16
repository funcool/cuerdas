;; Copyright (c) Andrey Antukh <niwi@niwi.nz>
;; All rights reserved.
;;
;; Redistribution and use in source and binary forms, with or without
;; modification, are permitted provided that the following conditions are met:
;;
;; * Redistributions of source code must retain the above copyright notice, this
;;   list of conditions and the following disclaimer.
;;
;; * Redistributions in binary form must reproduce the above copyright notice,
;;   this list of conditions and the following disclaimer in the documentation
;;   and/or other materials provided with the distribution.
;;
;; THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
;; AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
;; IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
;; DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
;; FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
;; DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
;; SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
;; CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
;; OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
;; OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

(ns cuerdas.core
  (:refer-clojure :exclude [contains? empty? repeat regexp?
                            replace reverse chars keyword concat
                            #?@(:clj [unquote format])])
  (:require [clojure.string :as str]
            [clojure.set :refer [map-invert]]
            [clojure.core :as c]

            [clojure.walk :refer [stringify-keys]]
            [cuerdas.regexp :as rx]

            #?(:cljs [goog.string :as gstr])
            #?(:cljs [cljs.reader :as edn]
               :clj  [clojure.edn :as edn]))
  #?(:clj (:import (java.util List Locale))))

#?(:cljs (def ^:private keyword* cljs.core/keyword)
   :clj  (def ^:private keyword* clojure.core/keyword))

#?(:clj (set! *warn-on-reflection* true))

;; => benchmarking: cljs.core/str
;; --> WARM:  100000
;; --> BENCH: 500000
;; --> TOTAL: 197.82ms
;; --> MEAN:  395.64ns
;; => benchmarking: cuerdas.core/concat
;; --> WARM:  100000
;; --> BENCH: 500000
;; --> TOTAL: 20.31ms
;; --> MEAN:  40.63ns

(declare repeat)

(defmacro concat
  "A macro variant of the clojure.core/str function that performs
  considerably faster string concatenation operation on CLJS (on
  JVM/CLJ it only applies basic simplification and then relies on the
  `clojure.core/str`)."
  [& params]
  (let [cljs?  (:ns &env)
        xform  (comp (partition-by string?)
                     (filter some?)
                     (mapcat (fn [part]
                               (if (string? (first part))
                                 [(apply c/str part)]
                                 (cond->> part
                                   cljs? (map (fn [o] (list 'js* "(~{} ?? \"\")" o))))))))
        params (into [] xform params)]
    (if cljs?
      (let [stmpl (apply c/str "\"\"" (repeat "+~{}" (count params)))]
        (cons 'js* (cons stmpl params)))
      (cons `c/str params))))

(defn empty?
  "Checks if a string is empty."
  [s]
  (and (string? s)
       (zero? (count s))))

(defn empty-or-nil?
  "Convenient helper for check emptines or if value is nil."
  [s]
  (or (nil? s)
      (empty? s)))

(defn includes?
  "Determines whether a string contains a substring."
  [s subs]
  (and (string? s)
       (string? subs)
       #?(:clj  (.contains ^String s subs)
          :cljs (gstr/contains s subs))))

#?(:clj
   (defn slice
     "Extracts a section of a string and returns a new string."
     ([s begin]
      (slice s begin (count s)))
     ([s ^long begin ^long end]
      (when (string? s)
        (let [end   (if (< end 0) (+ (count s) end) end)
              begin (if (< begin 0) (+ (count s) begin) begin)
              end   (if (> end (count s)) (count s) end)]
          (if (> begin end)
            ""
            (let [begin (if (< begin 0) 0 begin)
                  end (if (< end 0) 0 end)]
              (.substring ^String s begin end)))))))
   :cljs
   (defn slice
     "Extracts a section of a string and returns a new string."
     ([s begin]
      (when (string? s)
        (.slice s begin)))
     ([s begin end]
      (when (string? s)
        (.slice s begin end)))))

(defn starts-with?
  "Check if the string starts with prefix."
  [s prefix]
  (and (string? s)
       (or (string? prefix)
           (char? prefix))
       (or (zero? (count prefix))
           #?(:clj  (let [region (slice s 0 (count prefix))]
                      (= region prefix))
              :cljs (= (.lastIndexOf s prefix 0) 0)))))

(defn ends-with?
  "Check if the string ends with suffix."
  [s suffix]
  (and (string? s)
       (or (string? suffix)
           (char? suffix))
       (or (zero? (count suffix))
           #?(:clj (let [len (count s)
                         region (slice s (- len (count suffix)) len)]
                     (= region suffix))
              :cljs (let [l (- (count s) (count suffix))]
                      (and (>= l 0)
                           (= (.indexOf s suffix l) l)))))))

(defn lower
  "Converts string to all lower-case.

  This function works in strictly locale independent way,
  if you want a localized version, just use `locale-lower`"
  [s]
  (when (string? s)
    (.toLowerCase #?(:clj ^String s :cljs s))))

(defn upper
  "Converts string to all upper-case.

  This function works in strictly locale independent way,
  if you want a localized version, just use `locale-upper`"
  [s]
  (when (string? s)
    (.toUpperCase #?(:clj ^String s :cljs s))))

(defn blank?
  "Checks if a string is empty or contains only whitespace."
  [^String s]
  (and (string? s)
       (or (zero? (count s))
           (boolean (-> (re-pattern "(?u)^[\\s\\p{Z}]+$")
                        (re-matches s))))))

(defn alpha?
  "Checks if a string contains only alpha characters."
  [s]
  (and (string? s)
       (boolean (re-matches #"^[a-zA-Z]+$" s))))

(defn digits?
  "Checks if a string contains only digit characters."
  [s]
  (and (string? s)
       (boolean (re-matches #"^[0-9]+$" s))))

(defn alnum?
  "Checks if a string contains only alphanumeric characters."
  [s]
  (and (string? s)
       (boolean (re-matches #"^[a-zA-Z0-9]+$" s))))

(defn word?
  "Checks if a string contains only the word characters.
  This function will use all the unicode range."
  [s]
  (and (string? s)
       (boolean (re-matches (re-pattern "(?u)^[\\p{N}\\p{L}_-]+$") s))))

(defn letters?
  "Checks if string contains only letters.
  This function will use all the unicode range."
  [s]
  (and (string? s)
       (boolean (re-matches (re-pattern "(?u)^\\p{L}+$") s))))

(defn numeric?
  "Check if a string contains only numeric values."
  [s]
  (and (string? s)
       (boolean (re-matches #"^[+-]?([0-9]*\.?[0-9]+|[0-9]+\.?[0-9]*)([eE][+-]?[0-9]+)?$" s))))

(defn index-of
  ([s val]
   (when (and (string? s)
              (string? val))
     (str/index-of s val)))

  ([s val from]
   (when (and (string? s)
              (string? val))
     (str/index-of s val from))))

(defn last-index-of
  ([s val]
   (when (and (string? s)
              (string? val))
     (str/last-index-of s val)))

  ([s val from]
   (when (and (string? s)
              (string? val))
     (str/last-index-of s val from))))

(declare replace)

(defn trim
  "Removes whitespace or specified characters
  from both ends of string."
  ([s] (trim s "\n\t\f\r "))
  ([s chs]
   (when (string? s)
     (let [rxstr (str "[" (rx/escape chs) "]")
           rxstr (str "^" rxstr "+|" rxstr "+$")]
       (as-> (re-pattern rxstr) rx
         (replace s rx ""))))))

(defn rtrim
  "Removes whitespace or specified characters
  from right side of string."
  ([s] (rtrim s "\n\t\f\r "))
  ([s chs]
   (when (string? s)
     (let [rxstr (str "[" (rx/escape chs) "]")
           rxstr (str rxstr "+$")]
       (as-> (re-pattern rxstr) rx
         (replace s rx ""))))))

(defn ltrim
  "Removes whitespace or specified characters
  from left side of string."
  ([s] (ltrim s "\n\t\f\r "))
  ([s chs]
   (when (string? s)
     (let [rxstr (str "[" (rx/escape chs) "]")
           rxstr (str "^" rxstr "+")]
       (as-> (re-pattern rxstr) rx
         (replace s rx ""))))))

(defn clean
  "Trim and replace multiple spaces with
  a single space."
  [s]
  (-> (trim s)
      (replace (re-pattern "(?u)[\\s\\p{Z}]+") " ")))

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
  [^String s suffix]
  (if (ends-with? s suffix)
    (slice s 0 (- (count s) (count suffix)))
    s))

(declare join)

(defn repeat
  "Repeats string n times."
  ([s] (repeat s 1))
  ([s n]
   (when (string? s)
     #?(:clj  (join (clojure.core/repeat n s))
        :cljs (gstr/repeat s n)))))

#?(:cljs
   (defn- replace-all
     [s re replacement]
     (let [flags (.-flags re)
           flags (if (includes? flags "g")
                   flags
                   (str flags "g"))
           rx (js/RegExp. (.-source re) flags)]
       (.replace s rx replacement))))

#?(:cljs
   (defn- replace*
     [s match replacement]
     (cond
       (string? match)
       (str/replace s match replacement)

       (rx/regexp? match)
       (if (string? replacement)
         (replace-all s match replacement)
         (replace-all s match (#'str/replace-with replacement))))))

(defn replace
  "Replaces all instance of match with replacement in s.

  The replacement is literal (i.e. none of its characters are treated
  specially) for all cases above except pattern / string.

  In match is pattern instance, replacement can contain $1, $2, etc.
  will be substituted with string that matcher the corresponding
  parenthesized group in pattern.

  If you wish your replacement string to be used literary,
  use `(cuerdas.regexp/escape replacement)`.

  Example:
    (replace \"Almost Pig Latin\" #\"\\b(\\w)(\\w+)\\b\" \"$2$1ay\")
    ;; => \"lmostAay igPay atinLay\"
  "
  [s match replacement]
  (when (string? s)
    #?(:clj (str/replace s match replacement)
       :cljs (replace* s match replacement))))

(defn replace-first
  "Replaces first instance of match with replacement in s."
  [s match replacement]
  (when (string? s)
    (str/replace-first s match replacement)))

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
  number of times. The separator can be a string,
  character or Pattern (clj) / RegExp (cljs) instance."
  ([s] (split s #"\s+" #?(:cljs nil)))
  ([s ^Object sep]
   (cond
     (nil? s) s
     (rx/regexp? sep) (str/split s sep)
     (string? sep) (str/split s (re-pattern (rx/escape sep)))
     (char? sep) (str/split s (re-pattern (rx/escape (.toString sep))))
     :else (throw (ex-info "Invalid arguments" {:sep sep}))))
  ([s ^Object sep num]
   (cond
     (nil? s) s
     (rx/regexp? sep) (str/split s sep num)
     (string? sep) (str/split s (re-pattern (rx/escape sep)) num)
     (char? sep) (str/split s (re-pattern (rx/escape (.toString sep))) num)
     :else (throw (ex-info "Invalid arguments" {:sep sep})))))

(defn reverse
  "Return string reversed."
  [s]
  (when (string? s)
    (str/reverse s)))

(defn chars
  "Split a string in a seq of chars."
  [s]
  (when (string? s)
    #?(:clj  (vec (.split ^String s "(?!^)"))
       :cljs (js->clj (.split s "")))))

(defn lines
  "Return a list of the lines in the string."
  [s]
  (split s #"\n|\r\n"))

(defn unlines
  "Returns a new string joining a list of strings with a newline char (\\n)."
  [s]
  (when (sequential? s)
    (str/join "\n" s)))

(defn words
  "Returns a vector of the words in the string."
  ([s]
   (words s (re-pattern "(?u)[\\p{N}\\p{L}_-]+")))
  ([s re]
   (when (string? s)
     (vec (re-seq re s)))))

(defn- interpolate-format
  [s params]
  (letfn [(on-match [match]
            (let [val (edn/read-string
                       (if (= (subs match 0 1) "$")
                         (subs match 1)
                         (slice match 2 -2)))
                  val (if (symbol? val) (keyword* val) val)]
              (str (get params val ""))))]
    (as-> #"(?:%\([\d\w\:\_\-]+\)s|\$[\w\d\:\_\-]+)" $
      (replace s $ on-match))))

#?(:cljs
   (defn- indexed-format
     [s params]
     (let [params (clj->js (or params []))
           rx (js/RegExp. "%s" "g")]
       (replace s rx (fn [_]
                       (str (if (zero? (count params))
                              "%s"
                              (.shift params)))))))
   :clj
   (defn- indexed-format
     [s params]
     (let [params (java.util.ArrayList. ^List (vec params))]
       (replace s #"%s" (fn [_]
                          (str (if (.isEmpty params)
                                 "%s"
                                 (.remove params 0))))))))

(defn format
  "Simple string interpolation."
  [s & more]
  (when (string? s)
    (if (and (= (count more) 1) (associative? (first more)))
      (interpolate-format s (first more))
      (indexed-format s more))))

(def fmt
  "A shorter alias to `format` function."
  format)

(defn join
  "Joins strings together with given separator."
  ([coll]
   (apply str coll))
  ([separator coll]
   (apply str (interpose separator coll))))

(defn surround
  "Surround a string with another string or character."
  [s wrap]
  (when (string? s)
    (join #?(:cljs "") [wrap s wrap])))

(defn unsurround
  "Unsurround a string surrounded by another string or character."
  [s ^Object surrounding]
  (let [surrounding (.toString surrounding)
        length (count surrounding)
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
  ([s]
   (unsurround s "\""))
  ([s qchar]
   (unsurround s qchar)))

(defn- stylize-split
  [s]
  (let [re1 (re-pattern "(?u)(\\p{Lu}+[\\p{Ll}\\u0027\\p{Ps}\\p{Pe}]*)")
        re2 (re-pattern "(?u)[^\\p{L}\\p{N}\\u0027\\p{Ps}\\p{Pe}]+")]
    (some-> s
            (name)
            (replace re1 "-$1")
            (split re2)
            (seq))))

(defn- stylize-join
  ([coll every-fn join-with]
   (when (seq coll)
     (join join-with (map every-fn coll))))
  ([[fst & rst] first-fn rest-fn join-with]
   (when (string? fst)
     (join join-with (cons (first-fn fst) (map rest-fn rst))))))

(defn stylize
  ([s every-fn join-with]
   (stylize s every-fn every-fn join-with))
  ([s first-fn rest-fn join-with]
    (let [remove-empty #(seq (remove empty? %))]
      (some-> (stylize-split s)
              (remove-empty)
              (stylize-join first-fn rest-fn join-with)))))

(defn capital
  "Uppercases the first character of a string"
  [s]
  (if (empty-or-nil? s)
    s
    (str (upper (subs s 0 1)) (subs s 1 (count s)))))

(defn camel
  "Output will be: lowerUpperUpperNoSpaces
  accepts strings and keywords"
  [s]
  (stylize s lower capital ""))

(defn snake
  "Output will be: lower_cased_and_underscore_separated
  accepts strings and keywords"
  [s]
  (stylize s lower "_"))

(defn phrase
  "Output will be: Space separated with the first letter capitalized.
  accepts strings and keywords"
  [s]
  (stylize s capital lower " "))

(defn human
  "Output will be: lower cased and space separated
  accepts strings and keywords"
  [s]
  (stylize s lower " "))

(defn title
  "Output will be: Each Word Capitalized And Separated With Spaces
  accepts strings and keywords"
  [s]
  (stylize s capital " "))

(defn pascal
  "Output will be: CapitalizedAndTouchingTheNext
  accepts strings and keywords"
  [s]
  (stylize s capital ""))

(defn kebab
  "Output will be: lower-cased-and-separated-with-dashes
  accepts strings and keywords"
  [s]
  (stylize s lower "-"))

(defn js-selector
  "Output will be either:
     (js-selector \"-pascal-case-me\") ;; => PascalCaseMe
     (js-selector \"camel-case-me\") ;; => camelCaseMe

  accepts keywords and strings, with any standard delimiter"
  [s]
  (some-> (stylize-split s)
          (stylize-join identity capital "")))

(defn css-selector
  "Output will be either:
     (js-selector \"LeadingDash\") ;; => -leading-dash
     (js-selector \"noLeadingDash\") ;; => no-leading-dash

  accepts keywords and strings, with any standard delimiter"
  [s]
  (some-> (stylize-split s)
          (stylize-join lower "-")))

(def ^:private +slug-tr-map+
  (zipmap "ąàáäâãåæăćčĉęèéëêĝĥìíïîĵłľńňòóöőôõðøśșšŝťțŭùúüűûñÿýçżźž"
          "aaaaaaaaaccceeeeeghiiiijllnnoooooooossssttuuuuuunyyczzz"))

(defn slug
  "Transform text into a URL slug."
  [s]
  (some-> (lower s)
          (str/escape +slug-tr-map+)
          (replace #"[^\w\s]+" "")
          (replace #"\s+" "-")))

(defn uslug
  "Unicode friendly version of `slug` function."
  [s]
  (some-> (lower s)
          (replace (re-pattern "(?u)[^\\p{L}\\p{N}]+") " ")
          (replace (re-pattern "(?u)[\\p{Z}\\s]+") "-")))

(defn keyword
  "Safer version of clojure keyword, accepting a
  symbol for the namespace and kebab-casing the key"
  ([k]
   (keyword* (kebab k)))
  ([n k]
   (keyword* (str n) (kebab k))))

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
  ([s] (pad s nil))
  ([s {:keys [length padding type]
       :or {length 0 padding " " type :left}}]
   (when (string? s)
     (let [padding (slice padding 0 1)
           padlen  (- length (count s))
           padlen  (if (< padlen 0) 0 padlen)]
       (condp = type
         :right (str s (repeat padding padlen))
         :both  (let [first (repeat padding (Math/ceil (/ padlen 2)))
                      second (repeat padding (Math/floor (/ padlen 2)))]
                  (str first s second))
         :left  (str (repeat padding padlen) s))))))

(defn collapse-whitespace
  "Converts all adjacent whitespace characters
  to a single space."
  [s]
  (some-> s
          (replace (re-pattern "(?u)[\\p{Z}\\s]+") " ")
          (replace #"^\s+|\s+$" "")))

(defn escape-html
  [s]
  "Converts HTML special characters to their entity equivalents."
  (when s
    (-> s
        (replace "&"  "&amp;")
        (replace "<"  "&lt;")
        (replace ">"  "&gt;")
        (replace "\"" "&quot;"))))

(defn unescape-html
  "Converts entity characters to HTML equivalents."
  [s]
  (when s
    (-> s
        (replace "&amp;"  "&")
        (replace "&lt;" "<")
        (replace "&gt;" ">")
        (replace "&quot;" "\""))))

(defn- strip-tags-impl
  [s tags mappings]
  (let [kwdize (comp keyword lower name)
        tags (cond
               (nil? tags) tags
               (string? tags) (hash-set (kwdize tags))
               (sequential? tags) (set (map kwdize tags)))
        rx   (re-pattern "<\\/?([^<>]*)>")]
    (replace s rx (if (nil? tags)
                    (fn [[match tag]]
                      (let [tag (kwdize tag)]
                        (get mappings tag "")))
                    (fn [[match tag]]
                      (let [tag (kwdize tag)]
                        (if (tags tag)
                          (get mappings tag "")
                          match)))))))

(defn strip-tags
  "Remove html tags from string."
  ([s] (strip-tags-impl s nil {}))
  ([s tags]
   (if (map? tags)
     (strip-tags-impl s nil  tags)
     (strip-tags-impl s tags {}  )))
  ([s tags mapping]
   (strip-tags-impl s tags mapping)))

(defn substr-between
  "Find string that is nested in between two strings. Return first match"
  [s prefix suffix]
  (when (and (includes? s prefix)
             (includes? s suffix))
    (some-> (split s prefix)
            (second)
            (split suffix)
            (first))))

(defn <<-
  "Unindent multiline text. Uses either a supplied regex or the shortest
  beginning-of-line to non-whitespace distance"
  ([s]
   (let [all-indents (->> (rest (lines s)) ;; ignore the first line
                          (remove blank?)
                          (c/concat [(last (lines s))]) ;; in case all lines are indented
                          (map #(->> % (re-find #"^( +)") second count)))
         min-indent  (re-pattern (format "^ {%s}"
                                         (apply min all-indents)))]
     (<<- min-indent s)))
  ([r s] (->> s lines (map #(replace % r "")) unlines)))

;; --- String Interpolation

;; Copyright (c) 2009, 2016 Chas Emerick <chas@cemerick.com>
;; All rights reserved.
;;
;; Redistribution and use in source and binary forms, with or without
;; modification, are permitted provided that the following conditions are met:
;;
;; * Redistributions of source code must retain the above copyright notice, this
;;   list of conditions and the following disclaimer.
;;
;; * Redistributions in binary form must reproduce the above copyright notice,
;;   this list of conditions and the following disclaimer in the documentation
;;   and/or other materials provided with the distribution.
;;
;; THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
;; AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
;; IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
;; DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
;; FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
;; DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
;; SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
;; CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
;; OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
;; OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

;; Originally proposed/published at http://cemerick.com/2009/12/04/string-interpolation-in-clojure/
;; BSD Licensed version from https://gist.github.com/cemerick/116c56b9504152d59d3e60fff6d57ea7
;; Contains minor adaptations for make it work in cljs.

#?(:clj
   (defn- silent-read
     "Attempts to clojure.core/read a single form from the provided
     String, returning a vector containing the read form and a String
     containing the unread remainder of the provided String. Returns nil
     if no valid form can be read from the head of the String."
     [s]
     (try
       (let [r (-> s java.io.StringReader. java.io.PushbackReader.)]
         [(read r) (slurp r)])
       ;; this indicates an invalid form -- the head of s is just string data
       (catch Exception e))))

#?(:clj
   (defn- interpolate-istr
     "Yields a seq of Strings and read forms."
     ([s atom?]
      (lazy-seq
       (if-let [[form rest] (silent-read (subs s (if atom? 2 1)))]
         (cons form (interpolate-istr (if atom? (subs rest 1) rest)))
         (cons (subs s 0 2) (interpolate-istr (subs s 2))))))
     ([^String s]
      (if-let [start (->> ["~{" "~("]
                          (map #(.indexOf s ^String %))
                          (remove #(== -1 %))
                          sort
                          first)]
        (lazy-seq (cons
                   (subs s 0 start)
                   (interpolate-istr (subs s start) (= \{ (.charAt s (inc start))))))
        [s]))))

#?(:clj
(defmacro istr
  "A string formating macro that works LIKE ES6 template literals but
  using clojure construcs and symbols for interpolation delimiters.

  It accepts one or more strings; emits a `concat` invocation that
  concatenates the string data and evaluated expressions contained
  within that argument.

  Evaluation is controlled using ~{} and ~() forms. The former is used
  for simple value replacement using clojure.core/str; the latter can
  be used to embed the results of arbitrary function invocation into
  the produced string.

  Examples:

    user=> (def v 30.5)
    user=> (istr \"This trial required ~{v}ml of solution.\")
    \"This trial required 30.5ml of solution.\"
    user=> (istr \"There are ~(int v) days in November.\")
    \"There are 30 days in November.\"

    user=> (def m {:a [1 2 3]})
    user=> (istr \"The total for your order is $~(->> m :a (apply +)).\")
    \"The total for your order is $6.\"

    user=> (istr \"Just split a long interpolated string up into ~(-> m :a (get 0)), \"
                 \"~(-> m :a (get 1)), or even ~(-> m :a (get 2)) separate strings \"
                 \"if you don't want a << expression to end up being e.g. ~(* 4 (int v)) \"
                 \"columns wide.\")
    \"Just split a long interpolated string up into 1, 2, or even 3 separate strings if you don't want a << expression to end up being e.g. 120 columns wide.\"

    Note that quotes surrounding string literals within ~() forms must be
    escaped."
     [& strings]
     `(cuerdas.core/concat ~@(interpolate-istr (apply str strings))))
)

#?(:clj
(defmacro <<
  "A backward compatibility alias for `istr` macro."
  {:deprecated true}
  [& strings]
  `(cuerdas.core/concat ~@(interpolate-istr (apply str strings))))
)

#?(:clj
   (defn- interpolate-ffmt
     [s params]
     (loop [items  (->> (re-seq #"([^\%]+)*(\%{1,2}(\d+)?)?" s)
                        (remove (fn [[full seg]] (and (nil? seg) (not full)))))
            result []
            index  0]
       ;; (prn "interpolate-ffmt" items)
       (if-let [[full segment var? sidx] (first items)]
         (cond
           (and var? (= "%%" var?))
           (recur (rest items)
                  (conj result (str/replace full "%%" "%"))
                  index)

           (and var? sidx)
           (let [cidx (dec (edn/read-string sidx))]
             (recur (rest items)
                    (-> result
                        (conj segment)
                        (conj (nth params cidx)))
                    (inc index)))

           var?
           (recur (rest items)
                  (-> result
                      (conj segment)
                      (conj (nth params index)))
                  (inc index))

           :else
           (recur (rest items)
                  (conj result segment)
                  (inc index)))

         (remove nil? result)))))

#?(:clj
(defmacro ffmt
  "Alternative (to `istr`) string formating macro, that performs simple
  string formating on the compile time (this means that the string
  should be known at compile time). Internally it uses the fast string
  concatenation mechanism implemented in the `concat` macro.

  If you don't need the peculiarities of the `istr` macro, this macro
  should be prefered.

  It works with two basic forms: sequencial and indexed. Let seen an
  example:

    (str/ffmt \"url(%)\" my-url) ; sequential
    (str/ffmt \"url(%1)\" my-url) ; indexed
  "
  [s & params]
  (cons 'cuerdas.core/concat (interpolate-ffmt s (vec params))))
)
