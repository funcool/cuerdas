(defproject cuerdas "0.3.2"
  :description "The missing string manipulation library for clojure"
  :url "https://github.com/funcool/cuerdas"

  :license {:name "BSD (2-Clause)"
            :url "http://opensource.org/licenses/BSD-2-Clause"}

  :dependencies []
  :source-paths ["src/clj" "src/cljs"]
  :jar-exclusions [#"\.cljx|\.swp|\.swo|user.clj"]

  :cljx {:builds [{:source-paths ["test"]
                   :output-path "output/test/clj"
                   :rules :clj}
                  {:source-paths ["test"]
                   :output-path "output/test/cljs"
                   :rules :cljs}]}

  :cljsbuild {:test-commands {"test" ["node" "output/tests.js"]}
              :builds [{:id "dev"
                        :source-paths ["output/test/cljs" "src/cljs"]
                        :notify-command ["node" "output/tests.js"]
                        :compiler {:output-to "output/tests.js"
                                   :output-dir "output/"
                                   :source-map true
                                   :static-fns true
                                   :cache-analysis false
                                   :main cuerdas.core-tests
                                   :optimizations :none
                                   :target :nodejs
                                   :pretty-print true}}]}

  :profiles {:dev {:dependencies [[org.clojure/clojure "1.6.0"]
                                  [org.clojure/clojurescript "0.0-3126"]
                                  [funcool/cljs-testrunners "0.1.0-SNAPSHOT"]]
                   :test-paths ["output/test/clj"]
                   :plugins [[com.keminglabs/cljx "0.6.0"
                              :exclusions [org.clojure/clojure]]
                             [lein-cljsbuild "1.0.4"]]}})

