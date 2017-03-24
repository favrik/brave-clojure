(ns braveclojure.chapter04
  (:require [clojure.pprint :refer [pprint]]))

;try implementing map using reduce, and then do the same for filter and some

(defn reduce-map
  [f arg]
  (seq (reduce (fn [init value] (conj init (f value))) [] arg)))

(defn reduce-maps
  ([f col]
    (seq (reduce (fn [init value] (conj init (f value))) [] col)))
  ([f col & cols]
    (let [xols (cons col cols)]
      (reduce-maps (partial apply f)
                   (partition (count xols)
                              (apply interleave xols))))))

(defn reduce-filter
  [pred col]
  (reduce (fn [in nex] (when (pred nex)
                         (conj in nex))) [] col))


; code example
(def filename "suspects.csv")

(def vamp-keys [:name :glitter-index])

(defn str->int
  [str]
  (Integer. str))

(def conversions {:name identity
                  :glitter-index str->int})

(defn convert
  [vamp-key value]
  ((get conversions vamp-key) value))

(defn parse
  "Convert a CSV into rows of columns"
  [string]
  (map #(clojure.string/split % #",")
       (clojure.string/split string #"\n")))

(defn mapify
  "Return a seq of maps like {:name \"Edward Cullen\" :glitter-index 10}"
  [rows]
  (map (fn [unmapped-row]
         (reduce (fn [row-map [vamp-key value]]
                   (assoc row-map vamp-key (convert vamp-key value)))
                 {}
                 (map vector vamp-keys unmapped-row)))
       rows))

(defn glitter-filter
  [minimum-glitter records]
  (filter #(>= (:glitter-index %) minimum-glitter) records))

(glitter-filter 3 (mapify (parse (slurp filename))))

; Ex 1
(pprint (map :name (glitter-filter 3 (mapify (parse (slurp filename))))))


; Ex 2
(defn append
  [suspects new-suspect]
  (conj suspects new-suspect))

; Ex 3
(def key-validator {:name #(and ((complement clojure.string/blank?) %) (string? %))
                    :glitter-index #(and (number? %))})
(defn validate
  [keywords record]
  ((complement (partial some false?))
     (map
       (fn [[vamp-key value]] ((vamp-key keywords) value))
       record)))

(defn append-cond
  [suspects new-suspect]
  (if (validate key-validator new-suspect)
    (conj suspects new-suspect)
    suspects))

; Ex 4
(defn to-csv
  [suspects]
  (clojure.string/join
    "\n"
    (map (fn [[name val]] (str name "," val))
         (map #(vals %) suspects))))
