(ns braveclojure.chapter03)

(defn use-functions
  "str vector list hash-map hash-set"
  []
  (println (str "hola" "mundo"))
  (println (vector 1 :a 2))
  (println (list 2 3 4))
  (println (hash-map :a 3 :b 2 :c 4))
  (println (hash-set :a 3 :a 3 :b 3)))

(defn add-100
  [x]
  (println (+ x 100)))

(defn dec-maker
  [n]
  #(- % n))

(defn mapset
  [f args]
  (set (map f args)))

(def asym-parts [{:name "head" :size 3}
                 {:name "left-eye" :size 1}
                 {:name "left-ear" :size 1}
                 {:name "mouth" :size 1}
                 {:name "nose" :size 1}
                 {:name "neck" :size 2}
                 {:name "left-shoulder" :size 3}
                 {:name "left-upper-arm" :size 3}
                 {:name "chest" :size 10}
                 {:name "back" :size 10}
                 {:name "left-forearm" :size 3}
                 {:name "abdomen" :size 6}
                 {:name "left-kidney" :size 1}
                 {:name "left-hand" :size 2}
                 {:name "left-knee" :size 2}
                 {:name "left-thigh" :size 4}
                 {:name "left-lower-leg" :size 3}
                 {:name "left-achilles" :size 1}
                 {:name "left-foot" :size 2}])


(defn part-names
  [part n]
  (map #(str %1 %2) (take n (range 1 (inc n))) (take n (repeat part))))

(defn matching-parts
  [part n]
  (if (clojure.string/includes? (:name part) "left")
    (map #(hash-map :size (:size part) :name %) (part-names (clojure.string/replace (:name part) #"^left" "") n))
    (vector part)))

(defn parts-reducer
  [n]
  (fn [final-parts part]
    (into final-parts (matching-parts part n))))

(defn radial-body-parts
  [parts n]
  (reduce (parts-reducer n) [] parts))
