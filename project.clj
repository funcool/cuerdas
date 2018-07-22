(defproject funcool/cuerdas "2.0.6"
  :description "The missing string manipulation library for clojure"
  :url "https://github.com/funcool/cuerdas"
  :license {:name "BSD (2-Clause)"
            :url "http://opensource.org/licenses/BSD-2-Clause"}

  :dependencies [[org.clojure/clojure "1.9.0" :scope "provided"]
                 [org.clojure/clojurescript "1.10.339" :scope "provided"]]
  :source-paths ["src" "assets"]
  :test-paths ["test"]

  :jar-exclusions [#"\.swp|\.swo|user.clj"]

  :profiles
  {:dev {:aliases {"test-all" ["with-profile" "dev,1.8:dev,1.7:dev" "test"]}
         :plugins [[lein-ancient "0.6.15"]]}

   :1.8 {:dependencies [[org.clojure/clojure "1.8.0"]]}
   :1.7 {:dependencies [[org.clojure/clojure "1.7.0"]]}})


