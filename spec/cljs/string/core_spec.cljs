(ns string.core-spec
  (:require-macros [speclj.core :refer [describe it should should= should-not run-specs]])
  (:require [speclj.core]
            [string.core :as str]))

(describe "String basic operations"
  (it "lower"
    (should (= (str/lower "FOO") "foo")))

  (it "upper"
    (should (= (str/upper "foo") "FOO")))

  (it "capitalize"
    (should (= (str/capitalize "foo") "Foo")))

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

  (it "replace-all"
    (should= (str/replace-all "aa bb aa" #"aa" "kk") "kk bb kk"))

  (it "prune"
    (should= (str/prune "Hello World" 8) "Hello..."))
)
