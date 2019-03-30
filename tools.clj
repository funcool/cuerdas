(require '[clojure.java.shell :as shell])
(require '[figwheel.main.api :as figwheel])
(require '[cljs.build.api :as api])
(require '[eftest.runner :as ef])

(defmulti task first)

(defmethod task :default
  [args]
  (let [all-tasks  (-> task methods (dissoc :default) keys sort)
        interposed (->> all-tasks (interpose ", ") (apply str))]
    (println "Unknown or missing task. Choose one of:" interposed)
    (System/exit 1)))

(defmethod task "test"
  [[_ exclude]]
  (let [tests (ef/find-tests "test")
        tests (if (string? exclude)
                (ef/find-tests (symbol exclude))
                tests)]
    (ef/run-tests tests
                  {:fail-fast? true
                   :capture-output? false
                   :multithread? false})
    (System/exit 1)))

(def options
  {:main 'cuerdas.core-tests
   :output-to "out/tests.js"
   :output-dir "out"
   :target :nodejs
   :optimizations :none
   :pretty-print true
   :language-in  :ecmascript5
   :language-out :ecmascript5
   :install-deps true
   :verbose true})

(defn build
  [optimizations]
  (api/build (api/inputs "src" "test")
             (cond->  (assoc options :optimizations optimizations)
               (= optimizations :none) (assoc :source-map true))))

(defmethod task "build-cljs"
  [[_ type]]
  (case type
    "none"     (build :none)
    "simple"   (build :simple)
    "advanced" (build :advanced)
    (do (println "Unknown argument to test task:" type)
        (System/exit 1))))

(defmethod task "test-cljs"
  [[_ type]]
  (letfn [(run-tests []
            (let [{:keys [out err]} (shell/sh "node" "out/tests.js")]
              (println out err)))

          (test-once []
            (build :none)
            (run-tests)
            (shutdown-agents))

          (test-watch []
            (println "Start watch loop...")
            (try
              (api/watch (api/inputs "src" "test")
                         (assoc options
                                :parallel-build false
                                :watch-fn run-tests
                                :cache-analysis false
                                :optimizations :none
                                :source-map false))
              (catch Exception e
                (println "ERROR:" e)
                (Thread/sleep 2000)
                (test-watch))))]

    (case type
      (nil "once") (test-once)
      "watch"      (test-watch)
      (do (println "Unknown argument to test task:" type)
          (System/exit 1)))))


(defmethod task "figwheel"
  [args]
  (figwheel/start
   {:id "dev"
    :options {:main 'cuerdas.core-tests
              ;; :output-to "out/tests.js"
              ;; :output-dir "out/tests"
              ;; :source-map true
              :install-deps true
              :target :nodejs}
    :config {:open-url false
             :auto-testing false
             :watch-dirs ["src" "test"]}}))

;;; Build script entrypoint. This should be the last expression.

(task *command-line-args*)
