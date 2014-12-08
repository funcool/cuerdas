(defproject string.clj "0.1.0-SNAPSHOT"
  :description "The missing string manipulation library for clojure"
  :url "http://example.com/FIXME"
  :license {:name "BSD (2-Clause)"
            :url "http://opensource.org/licenses/BSD-2-Clause"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/clojurescript "0.0-2411"]]
  :source-paths ["src/clj" "src/cljs"]
  :test-paths ["test/clj"]
  :profiles {:dev {:dependencies [[speclj "3.1.0"]]}}
  :cljsbuild {:test-commands {"test" ["phantomjs"  "bin/speclj" "target/tests.js"]}
              :builds [{:id "dev"
                        :source-paths ["spec/cljs" "src/cljs"]
                        :notify-command ["phantomjs" "bin/speclj" "target/tests.js"]
                        :compiler {:output-to "target/tests.js"
                                   :optimizations :whitespace
                                   :pretty-print true}}]}

  :plugins [[speclj "3.1.0"]
            [lein-cljsbuild "1.0.3"]])

