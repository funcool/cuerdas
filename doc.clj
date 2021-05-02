(require '[codox.main :as codox])

(codox/generate-docs
 {:output-path "doc/dist/latest"
  :metadata {:doc/format :markdown}
  :language :clojurescript
  :name "funcool/cuerdas"
  :themes [:rdash]
  :source-paths ["src"]
  :namespaces [#"^cuerdas\."]
  :source-uri "https://github.com/funcool/cuerdas/blob/master/{filepath}#L{line}"})
