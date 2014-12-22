(ns cuerdas.core-spec
  #+cljs
  (:require [speclj.core :as s :include-macros true]
            [cuerdas.core :as str])
  #+clj
  (:require [speclj.core :as s]
            [cuerdas.core :as str]))


(s/describe "String basic operations"
  (s/it "lower"
    (s/should (= (str/lower "FOO") "foo")))

  (s/it "upper"
    (s/should (= (str/upper "foo") "FOO")))

  (s/it "collapse-whitespace"
    (s/should= (str/collapse-whitespace "a  b\n c") "a b c"))

  (s/it "contains?"
    (s/should (str/contains? "abc" "ab"))
    (s/should (str/contains? "abc" "")))

  (s/it "startswith?"
    (s/should (str/startswith? "abc" "ab"))
    (s/should-not (str/startswith? "abc" "cab")))

  (s/it "endswith?"
    (s/should (str/endswith? "abc" "bc"))
    (s/should-not (str/endswith? "abc" "bca")))

  (s/it "trim"
    (s/should= (str/trim " a ") "a")
    (s/should= (str/trim "-a-" "-") "a"))

  (s/it "ltrim"
    (s/should= (str/ltrim " a ") "a ")
    (s/should= (str/ltrim "-a-", "-") "a-"))

  (s/it "rtrim"
    (s/should= (str/rtrim " a ") " a")
    (s/should= (str/rtrim "-a-", "-") "-a"))

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
    (s/should= (str/repeat "a") "a")
    (s/should= (str/repeat "a" 3) "aaa"))

  (s/it "slice"
    (s/should= (str/slice "abc" 1) "bc")
    (s/should= (str/slice "abcd" 1 3) "bc"))

  (s/it "strip-newlines"
    (s/should= "a b" (str/strip-newlines "a\n\nb")))

  (s/it "split"
    (s/should= (str/split "1 2 3") ["1" "2" "3"])
    (s/should= (str/split "1 2 3" " ") ["1" "2" "3"])
    (s/should= (str/split "1 2 3" #"\s") ["1" "2" "3"])
    (s/should= (str/split "1 2 3" #"\s" 2) ["1" "2 3"]))

  (s/it "replace-first"
    (s/should= "kk bb cc" (str/replace-first "aa bb cc" #"aa" "kk")))

  (s/it "surround"
    (s/should= "-aaa-" (str/surround "aaa" "-"))
    (s/should= "-^-aaa-^-" (str/surround "aaa" "-^-")))

  (s/it "unsurround"
    (s/should= "aaa" (str/unsurround "-aaa-" "-"))
    (s/should= "aaa" (str/unsurround "-^-aaa-^-" "-^-")))

  (s/it "chars"
    (s/should= ["a", "b"] (str/chars "ab")))

  (s/it "reverse"
    (s/should= "cba" (str/reverse "abc")))

  (s/it "replace"
    (s/should= (str/replace "aa bb aa" #"aa" "kk") "kk bb kk"))

  (s/it "prune"
    (s/should= "Hello..." (str/prune "Hello World" 8))
    (s/should= "Hello (...)"
             (str/prune "Hello World" 11 " (...)")))

  (s/it "join"
    (s/should= "ab" (str/join ["a" "b"])))

  (s/it "quote"
    (s/should= (str/quote "a") "\"a\"")
    (s/should= (str/quote "\"") "\"\"\""))

  (s/it "unquote"
    (s/should= (str/unquote "\"\"\"") "\"")
    (s/should= (str/unquote "\"a\"") "a"))

  (s/it "slugify"
    (s/should= (str/slugify "Un éléphant à l'orée du bois")
             "un-elephant-a-loree-du-bois"))

  #+cljs
  (s/it "strip-tags"
    (s/should= "just some text" (str/strip-tags "<p>just <b>some</b> text</p>"))
    (s/should= "just <b>some</b> text" (str/strip-tags "<p>just <b>some</b> text</p>" "p"))
    (s/should= "just <b>some</b> text" (str/strip-tags "<p>just <b>some</b> text</p>" "P")))

  #+cljs
  (s/it "parse-number"
    (s/should= (str/parse-number "1.4") 1)
    (s/should= (str/parse-number "1.4" 1) 1.4)
    (s/should= (str/parse-number "1" 2) 1))

  #+cljs
  (s/it "parse-float"
    (s/should= (str/parse-float "1.4") 1.4)
    (s/should= (str/parse-float "1") 1.0))

  #+clj
  (s/it "parse-double"
    (s/should= (str/parse-double "1.4") 1.4)
    (s/should= (str/parse-double "1") 1.0))

  #+cljs
  (s/it "parse-int"
    (s/should= (str/parse-int "1.4") 1))

  #+clj
  (s/it "parse-long"
    (s/should= (str/parse-long "1.4") 1))

  (s/it "format"
    (s/should= (str/format "hello %s" "pepe") "hello pepe")
    (s/should= (str/format "hello %(name)s" {:name "pepe"}) "hello pepe"))

  (s/it "pad"
    (s/should= (str/pad "1" {:length 8}) "       1")
    (s/should= (str/pad "1" {:length 8 :padding "0"}) "00000001")
    (s/should= (str/pad "1" {:length 8 :padding "0" :type :right}) "10000000")
    (s/should= (str/pad "1" {:length 8 :padding "0" :type :both}) "00001000"))

  (s/it "capitalize"
    (s/should= "Foo" (str/capitalize "foo"))
    (s/should= "FooBar" (str/capitalize "fooBar")))

  (s/it "camelize"
    (s/should= "MozTransform" (str/camelize "-moz-transform"))
    (s/should= "mozTransform" (str/camelize "moz-transform"))
    (s/should= "mozTransform" (str/camelize "moz transform")))

  (s/it "dasherize"
    (s/should= "-moz-transform" (str/dasherize "MozTransform")))

  (s/it "underscored"
    (s/should= "moz_transform" (str/underscored "MozTransform")))

  (s/it "humanize"
    (s/should= "Capitalize dash camel case underscore trim"
             (str/humanize "  capitalize dash-CamelCase_underscore trim  ")))

  (s/it "titleize"
    (s/should= "My Name Is Epeli" (str/titleize "my name is epeli")))

  (s/it "classify"
    (s/should= "SomeClassName" (str/classify "some_class_name")))

  (s/it "lines"
    (s/should= ["foo" "bar"] (str/lines "foo\nbar")))
)
