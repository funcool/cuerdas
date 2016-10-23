(defproject funcool/cuerdas "2.0.0"
  :description "The missing string manipulation library for clojure"
  :url "https://github.com/funcool/cuerdas"
  :license {:name "BSD (2-Clause)"
            :url "http://opensource.org/licenses/BSD-2-Clause"}

  :dependencies [[org.clojure/clojure "1.8.0" :scope "provided"]
                 [org.clojure/clojurescript "1.9.293" :scope "provided"]]
  :source-paths ["src" "assets"]
  :test-paths ["test"]

  :jar-exclusions [#"\.swp|\.swo|user.clj"]

  :profiles
  {:dev {:aliases {"test-all" ["with-profile" "dev,1.9:dev,1.7:dev" "test"]}}
   :1.9 {:dependencies [[org.clojure/clojure "1.9.0-alpha12"]]}
   :1.7 {:dependencies [[org.clojure/clojure "1.7.0"]]}})


