(defproject funcool/cuerdas "2.1.0"
  :description "The missing string manipulation library for clojure"
  :url "https://github.com/funcool/cuerdas"
  :license {:name "BSD (2-Clause)"
            :url "http://opensource.org/licenses/BSD-2-Clause"}

  :dependencies []
  :source-paths ["src" "assets"]
  :test-paths ["test"]

  :jar-exclusions [#"\.swp|\.swo|user.clj"]

  :profiles
  {:dev {:dependencies [[org.clojure/clojure "1.9.0" :scope "test"]
                        [org.clojure/clojurescript "1.10.339" :scope "test"]]
         :aliases {"test-all" ["do" ["clean"] ["with-profile" "dev,1.8:dev,1.7:dev" "test"]]
                   "test-lumo" ["do" ["clean"] ["with-profile" "self-host" "tach" "lumo" "test"]]}
         :plugins [[lein-ancient "0.6.15"]]}

   :self-host {:exclusions [org.clojure/clojure
                            org.clojure/clojurescript]
               :tach {:test-runner-ns cuerdas.runner
                      :source-paths ["src" "test"]
                      :force-non-zero-exit-on-test-failure? true
                      :cache? false
                      :debug? true}
               :plugins [[lein-tach "1.0.0"]]}

   :1.8 {:dependencies [[org.clojure/clojure "1.8.0" :scope "test"]]}
   :1.7 {:dependencies [[org.clojure/clojure "1.7.0" :scope "test"]]}})
