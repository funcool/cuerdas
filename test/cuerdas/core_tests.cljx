(ns cuerdas.core-tests
  #+cljs
  (:require [cljs-testrunners.node :as node]
            [cljs.test :as t]
            [cuerdas.core :as str])
  #+clj
  (:require [clojure.test :as t]
            [cuerdas.core :as str]))

(defn nan?
  [s]
  #+cljs (js/isNaN s)
  #+clj (Double/isNaN s))

(t/deftest cuerdas-tests
  (t/testing "lower"
    (t/is (= nil (str/lower nil)))
    (t/is (= "foo" (str/lower "FOO"))))

  (t/testing "upper"
    (t/is (= nil (str/upper nil)))
    (t/is (= "FOO" (str/upper "foo"))))

  (t/testing "collapse-whitespace"
    (t/is (= nil (str/collapse-whitespace nil)))
    (t/is (= "a b c" (str/collapse-whitespace "a  b\n c"))))

  (t/testing "contains?"
    (t/is (str/contains? "abc" "ab"))
    (t/is (str/contains? "abc" ""))
    (t/is (not (str/contains? "abc" "cba")))
    (t/is (not (str/contains? "abc" nil)))
    (t/is (not (str/contains? nil nil))))

  (t/testing "startswith?"
    (t/is (str/startswith? "abc" "ab"))
    (t/is (not (str/startswith? "abc" "cab")))
    (t/is (not (str/startswith? nil "ab")))
    (t/is (not (str/startswith? "abc" nil))))

  (t/testing "endswith?"
    (t/is (str/endswith? "abc" "bc"))
    (t/is (not (str/endswith? "abc" "bca")))
    (t/is (not (str/endswith? nil "bc")))
    (t/is (not (str/endswith? "abc" nil))))

  (t/testing "trim"
    (t/is (= "a" (str/trim " a ")))
    (t/is (= nil (str/trim nil)))
    (t/is (= "a" (str/trim "-a-" "-"))))

  (t/testing "ltrim"
    (t/is (= "a " (str/ltrim " a ")))
    (t/is (= nil (str/ltrim nil)))
    (t/is (= "a-" (str/ltrim "-a-", "-"))))

  (t/testing "rtrim"
    (t/is (= " a" (str/rtrim " a ")))
    (t/is (= "a" (str/rtrim "a" "foo")))
    (t/is (= nil (str/rtrim nil)))
    (t/is (= "-a" (str/rtrim "-a-", "-"))))

  (t/testing "empty?"
    (t/is (str/empty? ""))
    (t/is (str/empty? nil))
    (t/is (not (str/empty? " ")))
    (t/is (not (str/empty? " s "))))

  (t/testing "blank?"
    (t/is (str/blank? ""))
    (t/is (str/blank? " "))
    (t/is (str/blank? nil))
    (t/is (not (str/blank? " s "))))

  (t/testing "repeat"
    (t/is (= "a" (str/repeat "a")))
    (t/is (= "aaa" (str/repeat "a" 3)))
    (t/is (= nil (str/repeat nil 3))))

  (t/testing "slice"
    (t/is (= "bc" (str/slice "abc" 1)))
    (t/is (= "bc" (str/slice "abcd" 1 3)))
    (t/is (= nil (str/slice nil 1))))

  (t/testing "strip-newlines"
    (t/is (= nil (str/strip-newlines nil)))
    (t/is (= "a b" (str/strip-newlines "a\n\nb"))))

  (t/testing "split"
    (t/is (= nil (str/split nil)))
    (t/is (= ["1" "2" "3"] (str/split "1 2 3")))
    (t/is (= ["1" "2" "3"] (str/split "1 2 3" " ")))
    (t/is (= ["1" "2" "3"] (str/split "1 2 3" #"\s")))
    (t/is (= ["1" "2 3"] (str/split "1 2 3" #"\s" 2))))

  (t/testing "replace-first"
    (t/is (= nil (str/replace-first nil #"aa" "kk")))
    (t/is (= "kk bb cc" (str/replace-first "aa bb cc" #"aa" "kk"))))

  (t/testing "surround"
    (t/is (= nil (str/surround nil "-")))
    (t/is (= "-aaa-" (str/surround "aaa" "-")))
    (t/is (= "-^-aaa-^-" (str/surround "aaa" "-^-"))))

  (t/testing "unsurround"
    (t/is (= nil (str/unsurround nil "-")))
    (t/is (= "aaa" (str/unsurround "-aaa-" "-")))
    (t/is (= "aaa" (str/unsurround "-^-aaa-^-" "-^-"))))

  (t/testing "chars"
    (t/is (= nil (str/chars nil)))
    (t/is (= ["a", "b"] (str/chars "ab"))))

  (t/testing "reverse"
    (t/is (= nil (str/reverse nil)))
    (t/is (= "cba" (str/reverse "abc"))))

  (t/testing "replace"
    (t/is (= (str/replace "aa bb aa" #"aa" "kk") "kk bb kk")))

  (t/testing "prune"
    (t/is (= nil (str/prune nil 8)))
    (t/is (= "Hello..." (str/prune "Hello World" 8)))
    (t/is (= "Hello (...)"
             (str/prune "Hello World" 11 " (...)"))))

  (t/testing "join"
    (t/is (= "ab" (str/join ["a" "b"]))))

  (t/testing "quote"
    (t/is (= nil (str/quote nil)))
    (t/is (= "\"a\"" (str/quote "a")))
    (t/is (= "\"\"\"" (str/quote "\""))))

  (t/testing "unquote"
    (t/is (= "\"" (str/unquote "\"\"\"")))
    (t/is (= "a" (str/unquote "\"a\""))))

  (t/testing "slugify"
    (t/is (= nil (str/slugify nil)))
    (t/is (= "un-elephant-a-loree-du-bois"
             (str/slugify "Un éléphant à l'orée du bois"))))

  (t/testing "clean"
    (t/is (= nil (str/clean nil)))
    (t/is (= "a b" (str/clean " a   b  ")))
    (t/is (= "23.12.2014 10:09:19" (str/clean "23.12.2014    10:09:19"))))

  #+cljs
  (t/testing "escape-html"
    (t/is (= "&lt;div&gt;Blah blah blah&lt;/div&gt;"
             (str/escape-html "<div>Blah blah blah</div>"))))

  #+cljs
  (t/testing "unescape-html"
    (t/is (= "<div>Blah blah blah</div>"
             (str/unescape-html "&lt;div&gt;Blah blah blah&lt;/div&gt;"))))

  (t/testing "strip-tags"
    (t/is (= nil (str/strip-tags nil)))
    (t/is (= "just\ntext"
             (str/strip-tags "just<br>text" {:br "\n"})))
    (t/is (= "just some text"
             (str/strip-tags "<p>just <b>some</b> text</p>")))
    (t/is (= "just <b>some</b> text"
             (str/strip-tags "<p>just <b>some</b> text</p>" ["p"])))
    (t/is (= "just <b>some</b> text"
             (str/strip-tags "<p>just <b>some</b> text</p>" "P"))))


  #+cljs
  (t/testing "parse-number"
    (t/is (= 0 (str/parse-number nil)))
    (t/is (= 1 (str/parse-number "1.4")))
    (t/is (= 1.4 (str/parse-number "1.4" 1)))
    (t/is (= 1 (str/parse-number "1" 2))))

  #+cljs
  (t/testing "parse-float"
    (t/is (nan? (str/parse-float nil)))
    (t/is (= 1.4 (str/parse-float "1.4")))
    (t/is (= 1.0 (str/parse-float "1"))))

  #+clj
  (t/testing "parse-double"
    (t/is (nan? (str/parse-double nil)))
    (t/is (= 1.4 (str/parse-double "1.4")))
    (t/is (= 1.0 (str/parse-double "1"))))

  #+cljs
  (t/testing "parse-int"
    (t/is (nan? (str/parse-int nil)))
    (t/is (= 1 (str/parse-int "1.4"))))

  #+clj
  (t/testing "parse-long"
    (t/is (nan? (str/parse-long nil)))
    (t/is (= 1 (str/parse-long "1.4"))))

  (t/testing "format"
    (t/is (= nil (str/format nil "pepe")))
    (t/is (= "hello pepe" (str/format "hello %s" "pepe")))
    (t/is (= "hello pepe" (str/format "hello %(name)s" {:name "pepe"}))))

  (t/testing "pad"
    (t/is (= nil (str/pad nil {:length 8})))
    (t/is (= "       1" (str/pad "1" {:length 8})))
    (t/is (= "00000001" (str/pad "1" {:length 8 :padding "0"})))
    (t/is (= "10000000" (str/pad "1" {:length 8 :padding "0" :type :right})))
    (t/is (= "00001000" (str/pad "1" {:length 8 :padding "0" :type :both}))))

  (t/testing "capitalize"
    (t/is (= nil (str/capitalize nil)))
    (t/is (= "Foo" (str/capitalize "foo")))
    (t/is (= "FooBar" (str/capitalize "fooBar"))))

  (t/testing "strip-prefix"
    (t/is (= "ab" (str/strip-prefix "ab" nil)))
    (t/is (= nil (str/strip-prefix nil nil)))
    (t/is (= "a" (str/strip-prefix "-=a" "-=")))
    (t/is (= "=-a" (str/strip-prefix "=-a" "-="))))

  (t/testing "strip-suffix"
    (t/is (= "ab" (str/strip-suffix "ab" nil)))
    (t/is (= nil (str/strip-suffix nil nil)))
    (t/is (= "a" (str/strip-suffix "a=-" "=-")))
    (t/is (= "a-=" (str/strip-suffix "a-=" "=-"))))

  (t/testing "camelize"
    (t/is (= nil (str/camelize nil)))
    (t/is (= "MozTransform" (str/camelize "-moz-transform")))
    (t/is (= "mozTransform" (str/camelize "moz-transform")))
    (t/is (= "mozTransform" (str/camelize "moz transform"))))

  #+clj
  (t/testing "strip-suffix"
    (t/is (= nil (str/strip-suffix nil "foo")))
    (t/is (= "foobar" (str/strip-suffix "foobar-" "-"))))

  (t/testing "dasherize"
    (t/is (= nil (str/dasherize nil)))
    (t/is (= "moz" (str/dasherize "MOZ")))
    (t/is (= "moz-transform" (str/dasherize "MozTransform"))))

  (t/testing "underscored"
    (t/is (= nil (str/underscored nil)))
    (t/is (= "moz_transform" (str/underscored "MozTransform"))))

  (t/testing "humanize"
    (t/is (= nil (str/humanize nil)))
    (t/is (= "Capitalize dash camel case underscore trim"
             (str/humanize "  capitalize dash-CamelCase_underscore trim  "))))

  (t/testing "titleize"
    (t/is (= nil (str/titleize nil)))
    (t/is (= "My Name Is Epeli" (str/titleize "my name is epeli"))))

  (t/testing "classify"
    (t/is (= nil (str/classify nil)))
    (t/is (= "SomeClassName" (str/classify "some_class_name"))))

  (t/testing "lines"
    (t/is (= nil (str/lines nil)))
    (t/is (= ["foo" "bar"] (str/lines "foo\nbar"))))

  (t/testing "unlines"
    (t/is (= nil (str/unlines nil)))
    (t/is (= "foo\nbar" (str/unlines ["foo" "bar"])))
    (t/is (= "" (str/unlines []))))

  (t/testing "substr-between"
    (t/is (= nil (str/substr-between nil "" "")))
    (t/is (= nil (str/substr-between "" nil "")))
    (t/is (= nil (str/substr-between "" "" nil)))
    (t/is (= nil (str/substr-between "---foo>>bar" "<<" ">>")))
    (t/is (= nil (str/substr-between "---foo>>bar" "---" "<<")))
    (t/is (= "foo" (str/substr-between "---foo>>bar" "---" ">>")))
    (t/is (= "foo" (str/substr-between "---foo>>bar--foo1>>bar" "---" ">>"))))
  )

#+cljs
(set! *main-cli-fn* #(node/run-tests))
