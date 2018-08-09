(ns cuerdas.runner
  (:require [clojure.test :as test]
            [cuerdas.core-tests]))

(test/run-tests 'cuerdas.core-tests)
