(ns string.core-spec
  (:require-macros [speclj.core :refer [describe it should should= should-not run-specs]])
  (:require [speclj.core]
            [string.core :as str]))

(describe "String basic operations"
  (it "lower"
    (should (= (str/lower "FOO") "foo")))

  (it "upper"
    (should (= (str/upper "foo") "FOO")))

  (it "collapse-whitespace"
    (should= (str/collapse-whitespace "a  b\n c") "a b c"))

  (it "contains?"
    (should (str/contains? "abc" "ab")))

  (it "startswith?"
    (should (str/startswith? "abc" "ab"))
    (should-not (str/startswith? "abc" "cab")))

  (it "endswith?"
    (should (str/endswith? "abc" "bc"))
    (should-not (str/endswith? "abc" "bca")))

  (it "trim"
    (should= (str/trim " a ") "a")
    (should= (str/trim "-a-" "-") "a"))

  (it "ltrim"
    (should= (str/ltrim " a ") "a ")
    (should= (str/ltrim "-a-", "-") "a-"))

  (it "rtrim"
    (should= (str/rtrim " a ") " a")
    (should= (str/rtrim "-a-", "-") "-a"))

  (it "empty?"
    (should (str/empty? "  "))
    (should-not (str/empty? " s ")))

  (it "repeat"
    (should= (str/repeat "a") "a")
    (should= (str/repeat "a" 3) "aaa"))

  (it "strip-newlines"
    (should= (str/strip-newlines "a\n\nb") "a b"))

  (it "split"
    (should= (str/split "1 2 3") ["1" "2" "3"])
    (should= (str/split "1 2 3" " ") ["1" "2" "3"])
    (should= (str/split "1 2 3" #"\s") ["1" "2" "3"])
    (should= (str/split "1 2 3" #"\s" 2) ["1" "2 3"]))

  (it "replace-first"
    (should= (str/replace-first "aa bb aa" #"aa" "kk") "kk bb aa"))

  (it "replace"
    (should= (str/replace "aa bb aa" #"aa" "kk") "kk bb kk"))

  (it "prune"
    (should= (str/prune "Hello World" 8) "Hello..."))

  (it "quote"
    (should= (str/quote "a") "\"a\"")
    (should= (str/quote "\"") "\"\"\""))

  (it "unquote"
    (should= (str/unquote "\"\"\"") "\"")
    (should= (str/unquote "\"a\"") "a"))

  (it "slugify"
    (should= (str/slugify "Un éléphant à l'orée du bois")
             "un-elephant-a-loree-du-bois"))

  (it "strip-tags"
    (should= (str/strip-tags "<p>just <b>some</b> text</p>")
             "just some text")
    (should= (str/strip-tags "<p>just <b>some</b> text</p>" "p")
             "just <b>some</b> text"))

  (it "parse-number"
    (should= (str/parse-number "1.4") 1)
    (should= (str/parse-number "1.4" 1) 1.4)
    (should= (str/parse-number "1" 2) 1))

  (it "parse-float"
    (should= (str/parse-float "1.4") 1.4)
    (should= (str/parse-float "1") 1.0))

  (it "parse-int"
    (should= (str/parse-int "1.4") 1))

  (it "format"
    (should= (str/format "hello %s" "pepe") "hello pepe")
    (should= (str/format "hello %(name)s" {:name "pepe"}) "hello pepe"))

  (it "pad"
    (should= (str/pad "1" {:length 8}) "       1")
    (should= (str/pad "1" {:length 8 :padding "0"}) "00000001")
    (should= (str/pad "1" {:length 8 :padding "0" :type :right}) "10000000")
    (should= (str/pad "1" {:length 8 :padding "0" :type :both}) "00001000"))

  (it "capitalize"
    (should= "Foo" (str/capitalize "foo"))
    (should= "FooBar" (str/capitalize "fooBar")))

  (it "camelize"
    (should= "MozTransform" (str/camelize "-moz-transform"))
    (should= "mozTransform" (str/camelize "moz-transform")))

  (it "dasherize"
    (should= "-moz-transform" (str/dasherize "MozTransform")))

  (it "underscored"
    (should= "moz_transform" (str/underscored "MozTransform")))

  (it "humanize"
    (should= "Capitalize dash camel case underscore trim"
             (str/humanize "  capitalize dash-CamelCase_underscore trim  ")))

  (it "titleize"
    (should= "My Name Is Epeli" (str/titleize "my name is epeli")))

  (it "classify"
    (should= "SomeClassName" (str/classify "some_class_name")))
)
