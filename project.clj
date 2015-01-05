(defproject cuerdas "0.3.0-SNAPSHOT"
  :description "The missing string manipulation library for clojure"
  :url "https://github.com/funcool/cuerdas"

  :license {:name "BSD (2-Clause)"
            :url "http://opensource.org/licenses/BSD-2-Clause"}

  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/clojurescript "0.0-2511"]]

  :source-paths ["src/clj" "src/cljs"]

  :cljsbuild {:test-commands {"test" ["phantomjs"  "bin/speclj" "target/tests.js"]}
              :builds [{:id "dev"
                        :source-paths ["target/spec/cljs" "src/cljs"]
                        :notify-command ["phantomjs" "bin/speclj" "target/tests.js"]
                        :compiler {:output-to "target/tests.js"
                                   :optimizations :whitespace
                                   :pretty-print true}}]}

  :cljx {:builds [{:source-paths ["spec"]
                   :output-path "target/spec/clj"
                   :rules :clj}
                  {:source-paths ["spec"]
                   :output-path "target/spec/cljs"
                   :rules :cljs}]}

  :profiles {:dev {:dependencies [[speclj "3.1.0"]]
                   :test-paths ["target/spec/clj"]
                   :plugins [[speclj "3.1.0"]
                             [com.keminglabs/cljx "0.5.0" :exclusions [org.clojure/clojure]]
                             [lein-cljsbuild "1.0.3"]]}})

