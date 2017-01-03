(ns cuerdas.specs
  (:require
   #?(:clj
      [clojure.spec :as s]
      :cljs
      [cljs.spec :as s])))

(s/def ::nil-string (s/nilable string?))
(s/def ::char-or-str (s/nilable (s/or :str string? :char char?)))

(s/fdef cuerdas.core/empty?
  :args (s/cat :str ::nil-string)
  :ret  (s/nilable boolean?)
  :fn
  (s/or :nil #(and (nil? (:args %)) (nil? (:ret %)))
        :non-nil #(and (string? (:args %)) (boolean? (:ret %)))))

(s/fdef cuerdas.core/empty-or-nil?
  :args (s/cat :str ::nil-string)
  :ret  boolean?)

(s/fdef cuerdas.core/includes?
  :args (s/cat :str ::nil-string :subs ::char-or-str)
  :ret  (s/nilable boolean?))

(s/fdef cuerdas.core/slice
  :args (s/cat :str ::nil-string :begin int? :end (s/? int?))
  :ret  ::nil-string)

(s/fdef cuerdas.core/starts-with?
  :args (s/cat :str ::nil-string :prefix ::char-or-str)
  :ret  (s/nilable boolean?))

(s/fdef cuerdas.core/ends-with?
  :args (s/cat :str ::nil-string :suffix ::char-or-str)
  :ret  (s/nilable boolean?))

(s/fdef cuerdas.core/lower
  :args (s/cat :str ::nil-string)
  :ret ::nil-string)

(s/fdef cuerdas.core/upper
  :args (s/cat :str ::nil-string)
  :ret ::nil-string)
