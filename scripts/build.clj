(require '[cljs.build.api :as b])

(println "Building ...")

(let [start (System/nanoTime)]
  (b/build
   (b/inputs "test" "src" "assets")
   {:main 'cuerdas.core-tests
    :output-to "out/tests.js"
    :output-dir "out"
    :target :nodejs
    :optimizations :advanced
    :pretty-print true
    :language-in  :ecmascript5
    :language-out :ecmascript5
    :install-deps true
    :npm-deps {:xregexp "4.2.0"}
    :verbose true})
  (println "... done. Elapsed" (/ (- (System/nanoTime) start) 1e9) "seconds"))
