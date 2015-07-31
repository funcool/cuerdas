(require '[cljs.build.api :as b])

(b/watch (b/inputs "test" "src/cljs")
  {:main 'cuerdas.core-tests
   :target :nodejs
   :output-to "out/tests.js"
   :output-dir "out"
   :verbose true})
