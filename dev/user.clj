(ns user
  (:require
   [clojure.spec.alpha :as s]
   [clojure.tools.namespace.repl :as r]
   [clojure.walk :refer [macroexpand-all]]
   [clojure.pprint :refer [pprint]]
   [clojure.test :as test]
   [clojure.repl :refer :all]
   [cuerdas.core :as str]
   [criterium.core :refer [quick-bench bench with-progress-reporting]]))

(defn- run-tests
  ([] (run-tests #"^cuerdas.*-test$"))
  ([o]
   (r/refresh)
   (cond
     (instance? java.util.regex.Pattern o)
     (test/run-all-tests o)

     (symbol? o)
     (if-let [sns (namespace o)]
       (do (require (symbol sns))
           (test/test-vars [(resolve o)]))
       (test/test-ns o)))))

;; --- Benchmarking Tools

(defmacro run-quick-bench
  [& exprs]
  `(with-progress-reporting (quick-bench (do ~@exprs) :verbose)))

(defmacro run-quick-bench'
  [& exprs]
  `(quick-bench (do ~@exprs)))

(defmacro run-bench
  [& exprs]
  `(with-progress-reporting (bench (do ~@exprs) :verbose)))

(defmacro run-bench'
  [& exprs]
  `(bench (do ~@exprs)))

