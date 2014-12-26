(ns cuerdas.core-spec
  #+cljs
  (:require [speclj.core :as s :include-macros true]
            [cuerdas.core :as str])
  #+clj
  (:require [speclj.core :as s]
            [cuerdas.core :as str]))

#+cljs
(defn nan?
  [s]
  (js/isNaN s))

#+clj
(defn nan?
  [s]
  (Double/isNaN s))


(s/describe "String basic operations"
  (s/it "lower"
    (s/should= nil (str/lower nil))
    (s/should= "foo" (str/lower "FOO")))

  (s/it "upper"
    (s/should= nil (str/upper nil))
    (s/should= "FOO" (str/upper "foo")))

  (s/it "collapse-whitespace"
    (s/should= nil (str/collapse-whitespace nil))
    (s/should= "a b c" (str/collapse-whitespace "a  b\n c")))

  (s/it "contains?"
    (s/should (str/contains? "abc" "ab"))
    (s/should (str/contains? "abc" ""))
    (s/should-not (str/contains? "abc" nil))
    (s/should-not (str/contains? nil nil)))

  (s/it "startswith?"
    (s/should (str/startswith? "abc" "ab"))
    (s/should-not (str/startswith? "abc" "cab"))
    (s/should-not (str/startswith? nil "ab"))
    (s/should-not (str/startswith? "abc" nil)))

  (s/it "endswith?"
    (s/should (str/endswith? "abc" "bc"))
    (s/should-not (str/endswith? "abc" "bca"))
    (s/should-not (str/endswith? nil "bc"))
    (s/should-not (str/endswith? "abc" nil)))

  (s/it "trim"
    (s/should= "a" (str/trim " a "))
    (s/should= nil (str/trim nil))
    (s/should= "a" (str/trim "-a-" "-")))

  (s/it "ltrim"
    (s/should= "a " (str/ltrim " a "))
    (s/should= nil (str/ltrim nil))
    (s/should= "a-" (str/ltrim "-a-", "-")))

  (s/it "rtrim"
    (s/should= " a" (str/rtrim " a "))
    (s/should= "a" (str/rtrim "a" "foo"))
    (s/should= nil (str/rtrim nil))
    (s/should= "-a" (str/rtrim "-a-", "-")))

  (s/it "empty?"
    (s/should (str/empty? ""))
    (s/should (str/empty? nil))
    (s/should-not (str/empty? " "))
    (s/should-not (str/empty? " s ")))

  (s/it "blank?"
    (s/should (str/blank? ""))
    (s/should (str/blank? " "))
    (s/should (str/blank? nil))
    (s/should-not (str/blank? " s ")))

  (s/it "repeat"
    (s/should= "a" (str/repeat "a"))
    (s/should= "aaa" (str/repeat "a" 3))
    (s/should= nil (str/repeat nil 3)))

  (s/it "slice"
    (s/should= "bc" (str/slice "abc" 1))
    (s/should= "bc" (str/slice "abcd" 1 3))
    (s/should= nil (str/slice nil 1)))

  (s/it "strip-newlines"
    (s/should= nil (str/strip-newlines nil))
    (s/should= "a b" (str/strip-newlines "a\n\nb")))

  (s/it "split"
    (s/should= nil (str/split nil))
    (s/should= ["1" "2" "3"] (str/split "1 2 3"))
    (s/should= ["1" "2" "3"] (str/split "1 2 3" " "))
    (s/should= ["1" "2" "3"] (str/split "1 2 3" #"\s"))
    (s/should= ["1" "2 3"] (str/split "1 2 3" #"\s" 2)))

  (s/it "replace-first"
    (s/should= nil (str/replace-first nil #"aa" "kk"))
    (s/should= "kk bb cc" (str/replace-first "aa bb cc" #"aa" "kk")))

  (s/it "surround"
    (s/should= nil (str/surround nil "-"))
    (s/should= "-aaa-" (str/surround "aaa" "-"))
    (s/should= "-^-aaa-^-" (str/surround "aaa" "-^-")))

  (s/it "unsurround"
    (s/should= nil (str/unsurround nil "-"))
    (s/should= "aaa" (str/unsurround "-aaa-" "-"))
    (s/should= "aaa" (str/unsurround "-^-aaa-^-" "-^-")))

  (s/it "chars"
    (s/should= nil (str/chars nil))
    (s/should= ["a", "b"] (str/chars "ab")))

  (s/it "reverse"
    (s/should= nil (str/reverse nil))
    (s/should= "cba" (str/reverse "abc")))

  (s/it "replace"
    (s/should= (str/replace "aa bb aa" #"aa" "kk") "kk bb kk"))

  (s/it "prune"
    (s/should= nil (str/prune nil 8))
    (s/should= "Hello..." (str/prune "Hello World" 8))
    (s/should= "Hello (...)"
             (str/prune "Hello World" 11 " (...)")))

  (s/it "join"
    (s/should= "ab" (str/join ["a" "b"])))

  (s/it "quote"
    (s/should= nil (str/quote nil))
    (s/should= "\"a\"" (str/quote "a"))
    (s/should= "\"\"\"" (str/quote "\"")))

  (s/it "unquote"
    (s/should= "\"" (str/unquote "\"\"\""))
    (s/should= "a" (str/unquote "\"a\"")))

  (s/it "slugify"
    (s/should= nil (str/slugify nil))
    (s/should= "un-elephant-a-loree-du-bois"
               (str/slugify "Un éléphant à l'orée du bois")))

  (s/it "clean"
    (s/should= nil (str/clean nil))
    (s/should= "a b" (str/clean " a   b  ")))

  #+cljs
  (s/it "escape-html"
    (s/should= "&lt;div&gt;Blah blah blah&lt;/div&gt;"
               (str/escape-html "<div>Blah blah blah</div>")))

  #+cljs
  (s/it "unescape-html"
    (s/should= "<div>Blah blah blah</div>"
               (str/unescape-html "&lt;div&gt;Blah blah blah&lt;/div&gt;")))

  (s/it "strip-tags"
    (s/should= nil (str/strip-tags nil))
    (s/should= "just some text" (str/strip-tags "<p>just <b>some</b> text</p>"))
    (s/should= "just <b>some</b> text" (str/strip-tags "<p>just <b>some</b> text</p>" "p"))
    (s/should= "just <b>some</b> text" (str/strip-tags "<p>just <b>some</b> text</p>" "P")))

  #+cljs
  (s/it "parse-number"
    (s/should= 0 (str/parse-number nil))
    (s/should= 1 (str/parse-number "1.4"))
    (s/should= 1.4 (str/parse-number "1.4" 1))
    (s/should= 1 (str/parse-number "1" 2)))

  #+cljs
  (s/it "parse-float"
    (s/should (nan? (str/parse-float nil)))
    (s/should= 1.4 (str/parse-float "1.4"))
    (s/should= 1.0 (str/parse-float "1")))

  #+clj
  (s/it "parse-double"
    (s/should (nan? (str/parse-double nil)))
    (s/should= 1.4 (str/parse-double "1.4"))
    (s/should= 1.0 (str/parse-double "1")))

  #+cljs
  (s/it "parse-int"
    (s/should (nan? (str/parse-int nil)))
    (s/should= 1 (str/parse-int "1.4")))

  #+clj
  (s/it "parse-long"
    (s/should (nan? (str/parse-long nil)))
    (s/should= 1 (str/parse-long "1.4")))

  (s/it "format"
    (s/should= nil (str/format nil "pepe"))
    (s/should= "hello pepe" (str/format "hello %s" "pepe"))
    (s/should= "hello pepe" (str/format "hello %(name)s" {:name "pepe"})))

  (s/it "pad"
    (s/should= nil (str/pad nil {:length 8}))
    (s/should= "       1" (str/pad "1" {:length 8}))
    (s/should= "00000001" (str/pad "1" {:length 8 :padding "0"}))
    (s/should= "10000000" (str/pad "1" {:length 8 :padding "0" :type :right}))
    (s/should= "00001000" (str/pad "1" {:length 8 :padding "0" :type :both})))

  (s/it "capitalize"
    (s/should= nil (str/capitalize nil))
    (s/should= "Foo" (str/capitalize "foo"))
    (s/should= "FooBar" (str/capitalize "fooBar")))

  (s/it "camelize"
    (s/should= nil (str/camelize nil))
    (s/should= "MozTransform" (str/camelize "-moz-transform"))
    (s/should= "mozTransform" (str/camelize "moz-transform"))
    (s/should= "mozTransform" (str/camelize "moz transform")))

  (s/it "dasherize"
    (s/should= nil (str/dasherize nil))
    (s/should= "-moz-transform" (str/dasherize "MozTransform")))

  (s/it "underscored"
    (s/should= nil (str/underscored nil))
    (s/should= "moz_transform" (str/underscored "MozTransform")))

  (s/it "humanize"
    (s/should= nil (str/humanize nil))
    (s/should= "Capitalize dash camel case underscore trim"
             (str/humanize "  capitalize dash-CamelCase_underscore trim  ")))

  (s/it "titleize"
    (s/should= nil (str/titleize nil))
    (s/should= "My Name Is Epeli" (str/titleize "my name is epeli")))

  (s/it "classify"
    (s/should= nil (str/classify nil))
    (s/should= "SomeClassName" (str/classify "some_class_name")))

  (s/it "lines"
    (s/should= nil (str/lines nil))
    (s/should= ["foo" "bar"] (str/lines "foo\nbar")))
)
