(ns cuerdas.core-test
  (:require [clojure.test :as t]
            [cuerdas.core :as str :include-macros true])
  #?(:clj (:import (java.util Locale))))

(defn nan?
  [s]
  #?(:cljs (js/isNaN s) :clj (Double/isNaN s)))

(defn empty-tests
  [fn?]
  (not (or (fn? nil) (fn? "") (fn? " "))))

(t/deftest cuerdas-tests
  (t/testing "lower"
    (t/is (= nil (str/lower nil)))
    (t/is (= "" (str/lower "")))
    (t/is (= "foo" (str/lower "FOO"))))

  (t/testing "upper"
    (t/is (= nil (str/upper nil)))
    (t/is (= "" (str/upper "")))
    (t/is (= "FOO" (str/upper "foo"))))

  (t/testing "collapse-whitespace"
    (t/is (= nil (str/collapse-whitespace nil)))
    (t/is (= "a b" (str/collapse-whitespace "a\u2003b")))
    (t/is (= "a b" (str/collapse-whitespace "a\u3000b")))
    (t/is (= "a b c" (str/collapse-whitespace "a  b\n c"))))

  (t/testing "index-of"
    (t/is (= nil (str/index-of nil nil)))
    (t/is (= nil (str/index-of "foo" nil)))
    (t/is (= 1   (str/index-of "abc" "b"))))

  (t/testing "last-index-of"
    (t/is (= nil (str/last-index-of nil nil)))
    (t/is (= nil (str/last-index-of "foo" nil)))
    (t/is (= 1   (str/last-index-of "abc" "b"))))

  (t/testing "includes?"
    (t/is (= true (str/includes? "abc" "ab")))
    (t/is (= true (str/includes? "abc" "")))
    (t/is (= false (str/includes? "abc" "cba")))
    (t/is (= false (str/includes? "abc" nil)))
    (t/is (= false (str/includes? nil nil)))
    (t/is (= false (str/includes? "abc" nil))))

  (t/testing "starts-with?"
    (t/is (= false (str/starts-with? nil nil)))
    (t/is (= false (str/starts-with? nil "ab")))
    (t/is (= false (str/starts-with? "" "ab")))
    (t/is (= true (str/starts-with? "" "")))
    (t/is (= false (str/starts-with? "abc" nil)))
    (t/is (= true (str/starts-with? "abc" "ab")))
    (t/is (= false (str/starts-with? "abc" "cab")))
    (t/is (= true (str/starts-with? "abc" ""))))

  (t/testing "ends-with?"
    (t/is (= false (str/ends-with? nil nil)))
    (t/is (= false (str/ends-with? nil "bc")))
    (t/is (= false (str/ends-with? "" "bc")))
    (t/is (= true (str/ends-with? "" "")))
    (t/is (= false (str/ends-with? "abc" nil)))
    (t/is (= true (str/ends-with? "abc" "bc")))
    (t/is (= false (str/ends-with? "abc" "bca")))
    (t/is (= true (str/ends-with? "abc" ""))))

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
    (t/is (not (str/empty? nil)))
    (t/is (not (str/empty? " ")))
    (t/is (not (str/empty? " s "))))

  (t/testing "empty-or-nil?"
    (t/is (str/empty-or-nil? ""))
    (t/is (str/empty-or-nil? nil))
    (t/is (not (str/empty-or-nil? " ")))
    (t/is (not (str/empty-or-nil? " s "))))

  (t/testing "blank?"
    (t/is (str/blank? ""))
    (t/is (str/blank? " "))
    (t/is (str/blank? " "))
    (t/is (str/blank? " "))
    (t/is (not (str/blank? nil)))
    (t/is (not (str/blank? " s "))))

  (t/testing "alpha?"
    (t/is (empty-tests str/alpha?))
    (t/is (not (str/alpha? "test1")))
    (t/is (not (str/alpha? "test.")))
    (t/is (not (str/alpha? "test\ntest")))
    (t/is (str/alpha? "Test")))

  (t/testing "digits?"
    (t/is (empty-tests str/digits?))
    (t/is (not (str/digits? "test1")))
    (t/is (not (str/digits? "1.1")))
    (t/is (not (str/digits? "1\n1")))
    (t/is (str/digits? "0123")))

  (t/testing "numeric?"
    (t/is (str/numeric? "1"))
    (t/is (str/numeric? "+1"))
    (t/is (str/numeric? "-1"))
    (t/is (str/numeric? "1.1"))
    (t/is (str/numeric? "+1.1"))
    (t/is (str/numeric? "+1.1e2"))
    (t/is (not (str/numeric? "_1.1")))
    (t/is (not (str/numeric? "test"))))

  (t/testing "letters?"
    (t/is (str/letters? "hello"))
    (t/is (str/letters? "Русский"))
    (t/is (str/letters? "日本語"))
    (t/is (str/letters? "العربية")))

  (t/testing "word?"
    (t/is (str/word? "world2000"))
    (t/is (str/word? "world⑤"))
    (t/is (str/word? "he_ll-o"))
    (t/is (str/word? "Русс_кий"))
    (t/is (str/word? "日_本-語"))
    (t/is (str/word? "ال-ع_ربية")))

  (t/testing "uslug"
    (t/is (nil? (str/uslug nil)))
    (t/is (= "русский-001-foobar" (str/uslug "Русский 001   foobar"))))

  (t/testing "alnum?"
    (t/is (empty-tests str/alnum?))
    (t/is (str/alnum? "test1"))
    (t/is (not (str/alnum? "test.1")))
    (t/is (not (str/alnum? "test\n1")))
    (t/is (str/alnum? "0A1B2C")))

  (t/testing "repeat"
    (t/is (= "a" (str/repeat "a")))
    (t/is (= "aaa" (str/repeat "a" 3)))
    (t/is (= nil (str/repeat nil 3))))

  (t/testing "slice"
    (t/is (= "bc" (str/slice "abc" 1)))
    (t/is (= "bc" (str/slice "abcd" 1 3)))
    (t/is (= "morning is upon u" (str/slice "The morning is upon us." 4 -2)))
    (t/is (= nil (str/slice nil 1))))

  (t/testing "strip-newlines"
    (t/is (= nil (str/strip-newlines nil)))
    (t/is (= "a b" (str/strip-newlines "a\n\nb"))))

  (t/testing "split"
    (t/is (= nil (str/split nil)))
    (t/is (= ["1" "2" "3"] (str/split "1 2 3")))
    (t/is (= ["1" "2" "3"] (str/split "1 2 3" " ")))
    (t/is (= ["1" "2" "3"] (str/split "1 2 3" #"\s")))
    (t/is (= ["1" "2 3"] (str/split "1 2 3" #"\s" 2)))
    (t/is (= ["1" "2" "3"] (str/split "1 2 3" \space)))
    (t/is (= ["1" "2 3"] (str/split "1,2 3" \,)))
    (t/is (= ["1" "2 3"] (str/split "1 2 3" \space 2))))

  (t/testing "replace"
    (t/is (= nil (str/replace nil #"aa" "kk")))
    (t/is (= "kk bb kk" (str/replace "aa bb aa" #"aa" "kk")))
    (t/is (= "kk bb kk" (str/replace "AA bb aa" #"(?i)aa" "kk")))
    (t/is (= "aa bb cc" (str/replace "aa bb cc" "(?:aa|bb)" "kk")))
    (t/is (= "kk kk cc" (str/replace "aa bb cc" #"(?:aa|bb)" "kk"))))

  (t/testing "replace-first"
    (t/is (= nil (str/replace-first nil #"aa" "kk")))
    (t/is (= "kk bb cc" (str/replace-first "AA bb cc" #"(?i)aa" "kk")))
    (t/is (= "kk bb cc" (str/replace-first "aa bb cc" #"aa" "kk"))))

  (t/testing "surround"
    (t/is (= nil (str/surround nil "-")))
    (t/is (= "-aaa-" (str/surround "aaa" "-")))
    (t/is (= "-aaa-" (str/surround "aaa" \-)))
    (t/is (= "-^-aaa-^-" (str/surround "aaa" "-^-"))))

  (t/testing "unsurround"
    (t/is (= nil (str/unsurround nil "-")))
    (t/is (= "aaa" (str/unsurround "-aaa-" "-")))
    (t/is (= "aaa" (str/unsurround "-aaa-" \-)))
    (t/is (= "aaa" (str/unsurround "-^-aaa-^-" "-^-"))))

  (t/testing "chars"
    (t/is (= nil (str/chars nil)))
    (t/is (= ["a", "b"] (str/chars "ab"))))

  (t/testing "reverse"
    (t/is (= nil (str/reverse nil)))
    (t/is (= "cba" (str/reverse "abc")))
    (t/is (= "anañam anañam rab 𝌆 oof"
             (str/reverse "foo 𝌆 bar mañana mañana"))))

  (t/testing "prune"
    (t/is (= nil (str/prune nil 8)))
    (t/is (= "Hello..." (str/prune "Hello World" 8)))
    (t/is (= "Hello (...)"
             (str/prune "Hello World" 10 " (...)")))
    (t/is (= "Hello world"
             (str/prune "Hello world" 11 " (...)")))
    (t/is (= "Hello World, I'm pruning..."
             (str/prune "Hello World, I'm pruning strings today!" 25))))

  (t/testing "join"
    (t/is (= "ab" (str/join ["a" "b"]))))

  (t/testing "quote"
    (t/is (= nil (str/quote nil)))
    (t/is (= "\"a\"" (str/quote "a")))
    (t/is (= "\"\"\"" (str/quote "\""))))

  (t/testing "unquote"
    (t/is (= "\"" (str/unquote "\"\"\"")))
    (t/is (= "a" (str/unquote "\"a\""))))

  (t/testing "slug"
    (t/is (= nil (str/slug nil)))
    (t/is (= "page-0001" (str/slug "page 0001")))
    (t/is (= "un-elephant-a-loree-du-bois"
             (str/slug "Un éléphant à l'orée du bois"))))

  (t/testing "clean"
    (t/is (= nil (str/clean nil)))
    (t/is (= "a b" (str/clean " a   b  ")))
    (t/is (= "23.12.2014 10:09:19" (str/clean "23.12.2014    10:09:19"))))

  (t/testing "escape-html"
    (t/is (= "&lt;div&gt;Blah blah blah&lt;/div&gt;"
             (str/escape-html "<div>Blah blah blah</div>"))))

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

  (t/testing "one-of?"
    (t/is (str/one-of? ["test" "test2" "test3"] "test3"))
    (t/is (str/one-of? '("test" "test2") "test2"))
    (t/is (not (str/one-of? ["test" "test2"] "test3")))
    (t/is (not (str/one-of? ["test" "test2"] nil))))

  (t/testing "to-bool"
    (t/is (empty-tests str/to-bool))
    (t/is (str/to-bool "1"))
    (t/is (str/to-bool "yes"))
    (t/is (str/to-bool "True"))
    (t/is (str/to-bool "on"))
    (t/is (str/to-bool "ON"))
    (t/is (not (str/to-bool "false")))
    (t/is (not (str/to-bool "hello"))))

  (t/testing "format"
    (t/is (= nil (str/format nil "pepe")))
    (t/is (= "hello pepe" (str/format "hello %s" "pepe")))
    (t/is (= "hello pepe" (str/format "hello %(name)s" {:name "pepe"}))))

  (t/testing "pad"
    (t/is (= nil (str/pad nil {:length 8})))
    (t/is (= "       1" (str/pad "1" {:length 8})))
    (t/is (= "00000001" (str/pad "1" {:length 8 :padding "0"})))
    (t/is (= "10000000" (str/pad "1" {:length 8 :padding "0" :type :right})))
    (t/is (= "00001000" (str/pad "1" {:length 8 :padding "0" :type :both})))
    (t/is (= "12345" (str/pad "12345" {:padding "0" :length 4}))))

  (t/testing "capital"
    (t/is (= nil (str/capital nil)))
    (t/is (= "" (str/capital "")))
    (t/is (= "Foo" (str/capital "foo")))
    (t/is (= "FooBar" (str/capital "fooBar"))))

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

  #?(:clj
     (t/testing "strip-suffix"
       (t/is (= nil (str/strip-suffix nil "foo")))
       (t/is (= "foobar" (str/strip-suffix "foobar-" "-")))))

  (t/testing "camel"
    (t/is (= nil (str/camel nil)))
    (t/is (= "mozTransform" (str/camel :-moz-transform)))
    (t/is (= "mozTransform" (str/camel :moz-transform)))
    (t/is (= "mozTransform" (str/camel "moz_transform")))
    (t/is (= "mozTransform" (str/camel "moz transform"))))

  (t/testing "kebab"
   (t/is (= nil (str/kebab nil)))
   (t/is (= "moz" (str/kebab "MOZ")))
   (t/is (= "dasherized-keyword" (str/kebab :dasherized-keyword)))
   (t/is (= "moz-transform" (str/kebab "MozTransform"))))


  (t/testing "snake"
    (t/is (= nil (str/snake nil)))
    (t/is (= "user_table" (str/snake :user-table)))
    (t/is (= "moz_transform" (str/snake "MozTransform"))))

  (t/testing "phrase"
    (t/is (= nil (str/phrase nil)))
    (t/is (= "A short phrase" (str/phrase :a-short-phrase)))
    (t/is (= "Capitalize dash camel case underscore trim"
             (str/phrase "  capitalize dash-CamelCase_underscore trim  "))))

  (t/testing "human"
    (t/is (= nil (str/human nil)))
    (t/is (= "human friendly" (str/human :human-friendly)))
    (t/is (= "nice for people to read" (str/human "NiceForPeopleToRead"))))

  (t/testing "title"
    (t/is (= nil (str/title nil)))
    (t/is (= "Mini²" (str/title "mini²")))
    (t/is (= "Mini's" (str/title "mini's")))
    (t/is (= "Mini S" (str/title "mini-s")))
    (t/is (= "Mini(s)" (str/title "mini(s)")))
    (t/is (= "My Name Is Epeli" (str/title "my name is epeli")))
    (t/is (= "Regular Keyword" (str/title :regular-keyword))))

  (t/testing "pascal"
    (t/is (= nil (str/pascal nil)))
    (t/is (= "SomeKeywordName" (str/pascal :*some-keyword-name*)))
    (t/is (= "SomeClassName" (str/pascal "_some_class_name_"))))

  (t/testing "js-selector"
    (t/is (= nil (str/js-selector nil)))
    (t/is (= "keywordizeKeys" (str/js-selector "keywordize-keys")))
    (t/is (= "SomeKeywordName" (str/js-selector :-some-keyword-name)))
    (t/is (= "SomeClassName" (str/js-selector "_some_class_name"))))

  (t/testing "css-selector"
    (t/is (= nil (str/css-selector nil)))
    (t/is (= "some-keyword-name" (str/css-selector "someKeywordName")))
    (t/is (= "-some-keyword-name" (str/css-selector :SomeKeywordName)))
    (t/is (= "-some-keyword-name" (str/css-selector "SomeKeywordName"))))

  (t/testing "keyword"
    (t/is (= nil (str/css-selector nil)))
    (t/is (= :keyword-this (str/keyword " keyword this")))
    (t/is (= :bar-foo/key-this (str/keyword "bar-foo" " Key_This")))
    (let [n "foo-bar"]
      (t/is (= :foo-bar/key-that (str/keyword n "KeyThat")))))

  (t/testing "lines"
    (t/is (= nil (str/lines nil)))
    (t/is (= ["foo" "bar"] (str/lines "foo\nbar"))))

  (t/testing "unlines"
    (t/is (= nil (str/unlines nil)))
    (t/is (= "foo\nbar" (str/unlines ["foo" "bar"])))
    (t/is (= "" (str/unlines []))))

  (t/testing "words"
    (t/is (= nil (str/words nil)))
    (t/is (= [] (str/words "")))
    (t/is (= ["test"] (str/words "test")))
    (t/is (= ["one" "two" "3"] (str/words "  one, two 3.  ")))
    (t/is (= ["re" "test."] (str/words " re,  test." #"[^, ]+"))))

  (t/testing "substr-between"
    (t/is (= nil (str/substr-between nil "" "")))
    (t/is (= nil (str/substr-between "" nil "")))
    (t/is (= nil (str/substr-between "" "" nil)))
    (t/is (= nil (str/substr-between "---foo>>bar" "<<" ">>")))
    (t/is (= nil (str/substr-between "---foo>>bar" "---" "<<")))
    (t/is (= "foo" (str/substr-between "---foo>>bar" "---" ">>")))
    (t/is (= "foo" (str/substr-between "---foo>>bar--foo1>>bar" "---" ">>"))))

  #?(:clj
     (t/testing "<<"
       (let [v 2]
         (t/is (= "the value is 2" (str/<< "the value is ~{v}")))
         (t/is (= "the value is 3" (str/<< "the value is ~(inc v)")))
         (t/is (= "the value is 4" (str/<< "the value is ~(-> v inc inc)")))
         (t/is (= "the value is 2" (str/<< "the value" " is ~{v}"))))))

  #?(:clj
     (t/testing "istr"
       (let [v 2]
         (t/is (= "the value is 2" (str/istr "the value is ~{v}")))
         (t/is (= "the value is 3" (str/istr "the value is ~(inc v)")))
         (t/is (= "the value is 4" (str/istr "the value is ~(-> v inc inc)")))
         (t/is (= "the value is 2" (str/istr "the value" " is ~{v}"))))))

  (t/testing "<<-"
    (t/is (= "first line\n  indented two\n\n    indented four\n"
             (str/<<- "first line
                        indented two

                          indented four
                      ")))
    (t/is (= "first\nsecond\n  third"
             (str/<<- #"\t" "first\n\tsecond\n\t  third")))))

#?(:cljs
   (do
     (enable-console-print!)
     (set! *main-cli-fn* #(t/run-tests))))

#?(:cljs
   (defmethod t/report [:cljs.test/default :end-run-tests]
     [m]
     (if (t/successful? m)
       (set! (.-exitCode js/process) 0)
       (set! (.-exitCode js/process) 1))))

#?(:clj
   (defn -main
     [& args]
     (t/run-tests 'cuerdas.tests)))
