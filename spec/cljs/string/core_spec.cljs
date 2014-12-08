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
)
