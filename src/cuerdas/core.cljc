(ns cuerdas.core
  (:refer-clojure :exclude [contains? empty? repeat regexp?
                            replace reverse chars keyword
                            #?@(:clj [unquote format])])
  (:require [clojure.string :as str]
            [clojure.set :refer [map-invert]]
            [clojure.walk :refer [stringify-keys]]
            #?(:cljs [goog.string :as gstr])
            #?(:cljs [cljs.reader :as edn]
               :clj  [clojure.edn :as edn]))
  #?(:clj (:import java.util.regex.Pattern
                   java.util.List
                   java.util.Locale)))

(def ^:dynamic *unicode*
  "Bind to truthy value to make cuerdas behave locale specifically
  for unicode acknowledgement, bind to a truthy value, like :yes or true

  To respect locale bind a map with it's :locale value set to
  an instance of java.util.Locale on the jvm or
  TODO: locale handling for JS"
  nil)

#?(:cljs (def ^:private keyword* cljs.core/keyword)
   :clj  (def ^:private keyword* clojure.core/keyword))

(defn- regexp?
  "Return `true` if `x` is a regexp pattern
  instance."
  [x]
  #?(:cljs (cljs.core/regexp? x)
     :clj (instance? Pattern x)))

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

#?(:clj
   (defn slice
     "Extracts a section of a string and returns a new string."
     ([s begin]
      (slice s begin (count s)))
     ([s ^long begin ^long end]
      (if (nil? s)
        s
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
      (when-not (nil? s)
        (.slice s begin)))
     ([s begin end]
      (when-not (nil? s)
        (.slice s begin end)))))

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
    (if *unicode*
      #?(:clj ^String (if (instance? Locale (:locale *unicode*))
                        (.toLowerCase s (:locale *unicode*))
                        (.toLowerCase s))
         :cljs (.toLocaleLowerCase s))
      (.toLowerCase #?(:clj ^String s :cljs s)))))

(defn upper
  "Converts string to all upper-case."
  [s]
  (when-not (nil? s)
    (if *unicode*
      #?(:clj ^String (if (instance? Locale (:locale *unicode*))
                        (.toUpperCase s (:locale *unicode*))
                        (.toUpperCase s))
         :cljs (.toLocaleUpperCase s))
      (.toUpperCase #?(:clj ^String s :cljs s)))))

(defn caseless=
  "compares strings case-insensitively"
  [s1 s2]
  (when-not (and (string? s1) (string? s2))
    (if *unicode*
      (= (lower s1) (lower s2))
      #?(:clj  (.equalsIgnoreCase s1 s2)
         :cljs (= (lower s1) (lower s2))))))

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

(def posix-alphas?
  "Checks if a string contains only characters matched by [:alpha:]"
  (char-range-check #"^[a-zA-Z]+$"))

(def posix-digits?
  "Checks if a string contains only characters matched by [:digit:]"
  (char-range-check #"^[0-9]+$"))

(def posix-alnums?
  "Checks if a string contains only characters matched by [:alnum:]"
  (char-range-check #"^[a-zA-Z0-9]+$"))

(def posix-word?
  "Checks if a string contains only the
  (unofficial) posix [:word:] characters"
  (char-range-check #"^[a-zA-Z0-9_]+$"))

(def posix-digits?
  "Checks if a string contains only characters matched by [:digit:]"
  (char-range-check #"^[0-9]+$"))

(defn digits?
  "Checks if the string contains only digits

  This won't match unicode digits because clojure
  doesn't do arithmetic on unicode digits."
  [s]
  (when (string? s)
    (re-matches #"^[0-9]+$" s))) ;;TODO: clojure won't do math on other digits, should this identify them?

(defn letters?
  "Checks if string contains only letters"
  [s]
  (when (string? s)
    (if *unicode*
      #?(:clj  (re-matches #"^\p{L}+$" s)
         :cljs (re-matches #"^[a-zA-Z]+$" s)) ;; TODO: match unicode
      (re-matches #"^[a-zA-Z]+$" s))))

(defn word?
  "Checks if string represents a word"
  [s]
  (when (string? s)
    (if *unicode*
      #?(:clj  (re-matches #"^[\p{L}_-]+$" s)
         :cljs (re-matches #"^[a-zA-Z]_-+$" s)) ;; TODO: match unicode
      (re-matches #"^[a-zA-Z]+$" s))))

(defn numeric?
  "is the string a a number"
  [s]
  (and (string? s)
       (re-matches #"^-?\d+\.?\d*$" s))) ;;TODO: should this identify 1/3 1M and 1N?

(declare escape-regexp)
(declare replace)

(defn trim
  "Removes whitespace or specified characters
  from both ends of string."
  ([s] (trim s "\n\t\f\r "))
  ([s chs]
   (when-not (nil? s)
     (let [rxstr (str "[" (escape-regexp chs) "]")
           rxstr (str "^" rxstr "+|" rxstr "+$")]
       (as-> (re-pattern rxstr) rx
         (replace s rx ""))))))

(defn rtrim
  "Removes whitespace or specified characters
  from right side of string."
  ([s] (rtrim s "\n\t\f\r "))
  ([s chs]
   (when-not (nil? s)
     (let [rxstr (str "[" (escape-regexp chs) "]")
           rxstr (str rxstr "+$")]
       (as-> (re-pattern rxstr) rx
         (replace s rx ""))))))

(defn ltrim
  "Removes whitespace or specified characters
  from left side of string."
  ([s] (ltrim s "\b\t\f\r "))
  ([s chs]
   (when-not (nil? s)
     (let [rxstr (str "[" (escape-regexp chs) "]")
           rxstr (str "^" rxstr "+")]
       (as-> (re-pattern rxstr) rx
         (replace s rx ""))))))

(defn clean
  "Trim and replace multiple spaces with
  a single space."
  [s]
  (-> (trim s)
      (replace #"\s+" " ")))

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

(defn includes?
  [s v]
  (when-not (nil? s)
    (str/includes? s v)))

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

       (regexp? match)
       (if (string? replacement)
         (replace-all s match replacement)
         (replace-all s match (str/replace-with replacement))))))

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
    #?(:clj (str/replace s match replacement)
       :cljs (replace* s match replacement))))

(defn replace-first
  "Replaces first instance of match with replacement in s."
  [s match replacement]
  (when-not (nil? s)
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
  number of times. The separator can be a string
  or Pattern (clj) / RegExp (cljs) instance."
  ([s] (split s #"\s" #?(:cljs nil)))
  ([s sep]
   (cond
     (nil? s) s
     (regexp? sep) (str/split s sep)
     (string? sep) (str/split s (re-pattern (escape-regexp sep)))
     :else (throw (ex-info "Invalid arguments" {:sep sep}))))
  ([s sep num]
   (cond
     (nil? s) s
     (regexp? sep) (str/split s sep num)
     (string? sep) (str/split s (re-pattern (escape-regexp sep)) num)
     :else (throw (ex-info "Invalid arguments" {:sep sep})))))

(defn reverse
  "Return string reversed."
  [s]
  (when-not (nil? s)
    (str/reverse s)))

(defn chars
  "Split a string in a seq of chars."
  [s]
  (when-not (nil? s)
    #?(:clj  (vec (.split ^String s "(?!^)"))
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
  ([s]
   (unsurround s "\""))
  ([s qchar]
   (unsurround s qchar)))

(defn- stylize-split
  [s]
  (some-> s
          (name)
          (replace #"[A-Z]+[a-z]*" #(str "-" %))
          (lower)
          (split #"[^a-zA-Z]+")
          (seq)))

(defn- stylize-join
  ([coll every-fn join-with] (stylize-join coll every-fn every-fn join-with))
  ([[fst & rst] first-fn rest-fn join-with]
    (when-not (nil? fst)
      (join join-with
            (into [(first-fn fst)] (map rest-fn rst))))))

(defn stylize
  ([s every-fn join-with] (stylize s every-fn every-fn join-with))
  ([s first-fn rest-fn join-with]
    (let [remove-empty #(seq (remove (partial = "") %))]
      (some-> s
              (stylize-split)
              (remove-empty)
              (stylize-join first-fn rest-fn join-with)))))

(defn- update-range
  [s [lower upper] update-fn]
  (when (and (string? s) (not-empty s))
    (let [length (count s)
          start  (max 0 lower)
          end    (min length upper)]
      (str (subs s 0 start)
           (update-fn (subs s start end))
           (subs s end)))))

(defn capitalize
  "Uppercases the first character of a string or keyword"
  [s]
  (when s
    (update-range (name s) [0 1] upper)))

(defn camel
  "Output will be: lowerUpperUpperNoSpaces
  accepts strings and keywords"
  [s]
  (stylize s lower capitalize ""))

(defn snake
  "Output will be: lower_cased_and_underscore_separated
  accepts strings and keywords"
  [s]
  (stylize s lower "_"))

(defn phrase
  "Output will be: Space separated with the first letter capitalized.
  accepts strings and keywords"
  [s]
  (stylize s capitalize lower " "))

(defn human
  "Output will be: lower cased and space separated
  accepts strings and keywords"
  [s]
  (stylize s lower " "))

(defn title
  "Output will be: Each Word Capitalized And Separated With Spaces
  accepts strings and keywords"
  [s]
  (stylize s capitalize " "))

(defn pascal
  "Output will be: CapitalizedAndTouchingTheNext
  accepts strings and keywords"
  [s]
  (stylize s capitalize ""))

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
  (some-> s
          (stylize-split)
          (stylize-join capitalize "")))

(defn css-selector
  "Output will be either:
     (js-selector \"LeadingDash\") ;; => -leading-dash
     (js-selector \"noLeadingDash\") ;; => no-leading-dash

  accepts keywords and strings, with any standard delimiter"
  [s]
  (some-> s
          (stylize-split)
          (stylize-join lower "-")))

(defn slug
  "Transform text into a URL slug."
  [s]
  (when s
    (-> (lower s)
        (name)
        (str/escape (zipmap "ąàáäâãåæăćčĉęèéëêĝĥìíïîĵłľńňòóöőôõðøśșšŝťțŭùúüűûñÿýçżźž"
                            "aaaaaaaaaccceeeeeghiiiijllnnoooooooossssttuuuuuunyyczzz"))
        (replace #"[^\w\s]+" "")
        (kebab))))

(defn keyword
  "Safer version of clojure keyword, accepting a
  symbol for the namespace and kebab-casing the key"
  ([k]
   (keyword* (kebab k)))
  ([n k]
   (keyword* (str n) (kebab k))))

(defn parse-number
  "General purpose function for parse number like
  string to number. It works with both integers
  and floats."
  [s]
  (if (nil? s)
    #?(:cljs NaN :clj Double/NaN)
    (let [s (trim s)
          rx #"^-?\d+(?:\.\d+)?$"]
      (if (re-matches rx s)
        (edn/read-string s)
        #?(:cljs NaN :clj Double/NaN)))))

(defn parse-double
  "Return the double value from string."
  [^String s]
  #?(:cljs (js/parseFloat s)
     :clj  (cond
             (nil? s) Double/NaN
             :else (Double/parseDouble s))))

(defn parse-int
  "Return the number value in integer form."
  [s]
  (if (and (string? s)
           (re-matches #"-?\d+(\.\d+)?" s))
    #?(:clj (.longValue (Double. ^String s))
       :cljs (js/parseInt s 10))
    #?(:clj Double/NaN
       :cljs js/NaN)))

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

(defn <<-
  "Unindent multiline text.
  Uses either a supplied regex or the shortest
  beginning-of-line to non-whitespace distance"
  ([s]
   (let [all-indents (->> (rest (lines s)) ;; ignore the first line
                          (remove blank?)
                          (concat [(last (lines s))]) ;; in case all lines are indented
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
   (defn- interpolate
     "Yields a seq of Strings and read forms."
     ([s atom?]
      (lazy-seq
       (if-let [[form rest] (silent-read (subs s (if atom? 2 1)))]
         (cons form (interpolate (if atom? (subs rest 1) rest)))
         (cons (subs s 0 2) (interpolate (subs s 2))))))
     ([^String s]
      (if-let [start (->> ["~{" "~("]
                          (map #(.indexOf s ^String %))
                          (remove #(== -1 %))
                          sort
                          first)]
        (lazy-seq (cons
                   (subs s 0 start)
                   (interpolate (subs s start) (= \{ (.charAt s (inc start))))))
        [s]))))

#?(:clj
   (defmacro <<
     "Accepts one or more strings; emits a `str` invocation that
     concatenates the string data and evaluated expressions contained
     within that argument.  Evaluation is controlled using ~{} and ~()
     forms. The former is used for simple value replacement using
     clojure.core/str; the latter can be used to embed the results of
     arbitrary function invocation into the produced string.

     Examples:

         user=> (def v 30.5)
         #'user/v
         user=> (<< \"This trial required ~{v}ml of solution.\")
         \"This trial required 30.5ml of solution.\"
         user=> (<< \"There are ~(int v) days in November.\")
         \"There are 30 days in November.\"
         user=> (def m {:a [1 2 3]})
         #'user/m
         user=> (<< \"The total for your order is $~(->> m :a (apply +)).\")
         \"The total for your order is $6.\"
         user=> (<< \"Just split a long interpolated string up into ~(-> m :a (get 0)), \"
                  \"~(-> m :a (get 1)), or even ~(-> m :a (get 2)) separate strings \"
                  \"if you don't want a << expression to end up being e.g. ~(* 4 (int v)) \"
                  \"columns wide.\")
         \"Just split a long interpolated string up into 1, 2, or even 3 separate strings if you don't want a << expression to end up being e.g. 120 columns wide.\"

     Note that quotes surrounding string literals within ~() forms must be
     escaped."
     [& strings]
     `(str ~@(interpolate (apply str strings)))))

;; --- End String Interpolation
