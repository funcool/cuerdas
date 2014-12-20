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

  #+cljs
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

  #+cljs
  (s/it "repeat"
    (s/should= (str/repeat "a") "a")
    (s/should= (str/repeat "a" 3) "aaa"))

  #+cljs
  (s/it "strip-newlines"
    (s/should= (str/strip-newlines "a\n\nb") "a b"))

  #+cljs
  (s/it "split"
    (s/should= (str/split "1 2 3") ["1" "2" "3"])
    (s/should= (str/split "1 2 3" " ") ["1" "2" "3"])
    (s/should= (str/split "1 2 3" #"\s") ["1" "2" "3"])
    (s/should= (str/split "1 2 3" #"\s" 2) ["1" "2 3"]))

  #+cljs
  (s/it "replace-first"
    (s/should= "kk bb aa" (str/replace-first "aa bb aa" #"aa" "kk"))
    (s/should= "kk bb aa" (str/replace-first "aa bb aa" (str/regexp #"aa" "g") "kk")))

  #+cljs
  (s/it "replace"
    (s/should= (str/replace "aa bb aa" #"aa" "kk") "kk bb kk"))

  #+cljs
  (s/it "prune"
    (s/should= "Hello..." (str/prune "Hello World" 8))
    (s/should= "Hello (...)"
             (str/prune "Hello World" 11 " (...)")))

  #+cljs
  (s/it "quote"
    (s/should= (str/quote "a") "\"a\"")
    (s/should= (str/quote "\"") "\"\"\""))

  #+cljs
  (s/it "unquote"
    (s/should= (str/unquote "\"\"\"") "\"")
    (s/should= (str/unquote "\"a\"") "a"))

  #+cljs
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

  #+cljs
  (s/it "parse-int"
    (s/should= (str/parse-int "1.4") 1))

  #+cljs
  (s/it "format"
    (s/should= (str/format "hello %s" "pepe") "hello pepe")
    (s/should= (str/format "hello %(name)s" {:name "pepe"}) "hello pepe"))

  #+cljs
  (s/it "pad"
    (s/should= (str/pad "1" {:length 8}) "       1")
    (s/should= (str/pad "1" {:length 8 :padding "0"}) "00000001")
    (s/should= (str/pad "1" {:length 8 :padding "0" :type :right}) "10000000")
    (s/should= (str/pad "1" {:length 8 :padding "0" :type :both}) "00001000"))

  #+cljs
  (s/it "capitalize"
    (s/should= "Foo" (str/capitalize "foo"))
    (s/should= "FooBar" (str/capitalize "fooBar")))

  #+cljs
  (s/it "camelize"
    (s/should= "MozTransform" (str/camelize "-moz-transform"))
    (s/should= "mozTransform" (str/camelize "moz-transform"))
    (s/should= "mozTransform" (str/camelize "moz transform")))

  #+cljs
  (s/it "dasherize"
    (s/should= "-moz-transform" (str/dasherize "MozTransform")))

  #+cljs
  (s/it "underscored"
    (s/should= "moz_transform" (str/underscored "MozTransform")))

  #+cljs
  (s/it "humanize"
    (s/should= "Capitalize dash camel case underscore trim"
             (str/humanize "  capitalize dash-CamelCase_underscore trim  ")))

  #+cljs
  (s/it "titleize"
    (s/should= "My Name Is Epeli" (str/titleize "my name is epeli")))

  #+cljs
  (s/it "classify"
    (s/should= "SomeClassName" (str/classify "some_class_name")))

  #+cljs
  (s/it "lines"
    (s/should= ["foo" "bar"] (str/lines "foo\nbar")))
)
