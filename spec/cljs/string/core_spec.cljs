(ns string.core-spec
  (:require-macros [speclj.core :refer [describe it should should-not run-specs]])
  (:require [speclj.core]
            [string.core :as str]))

(describe "String basic operations"
  (it "lower works as expected"
    (should (= (str/lower "FOO") "foo")))
  (it "upper works as expected"
    (should (= (str/upper "foo") "FOO")))
)
