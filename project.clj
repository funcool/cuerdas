(defproject funcool/cuerdas "1.0.1"
  :description "The missing string manipulation library for clojure"
  :url "https://github.com/funcool/cuerdas"
  :license {:name "BSD (2-Clause)"
            :url "http://opensource.org/licenses/BSD-2-Clause"}

  :dependencies [[org.clojure/clojure "1.8.0" :scope "provided"]
                 [org.clojure/clojurescript "1.9.227" :scope "provided"]]
  :source-paths ["src" "assets"]
  :test-paths ["test"]

  :jar-exclusions [#"\.swp|\.swo|user.clj"])

