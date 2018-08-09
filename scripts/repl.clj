(require
  '[cljs.repl]
  '[cljs.repl.node])

(cljs.repl/repl
 (cljs.repl.node/repl-env)
 :output-dir "out"
 :install-deps true
 :npm-deps {:xregexp "4.2.0"}
 :cache-analysis true)
