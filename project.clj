(defproject funcool/cuerdas "0.6.0"
  :description "The missing string manipulation library for clojure"
  :url "https://github.com/funcool/cuerdas"

  :license {:name "BSD (2-Clause)"
            :url "http://opensource.org/licenses/BSD-2-Clause"}

  :dependencies [[org.clojure/clojure "1.7.0" :scope "provided"]
                 [org.clojure/clojurescript "1.7.28" :scope "provided"]]
  :source-paths ["src"]
  :jar-exclusions [#"\.swp|\.swo|user.clj"])

