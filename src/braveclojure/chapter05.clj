(ns braveclojure.chapter05)

(defn two-comp
  [f g]
  (fn [& args]
    (f (apply g args))))
;I encourage you to evaluate this code and use two-comp to compose two functions! 
;Also, try reimplementing Clojure’s comp function so you can compose any number of functions.

(def character
  {:name "Smooches McCutes"
   :attributes {:intelligence 10
                :strength 4
                :dexterity 5}})
(def c-int (comp :intelligence :attributes))
(def c-str (comp :strength :attributes))
(def c-dex (comp :dexterity :attributes))

; Ex 1
(def attr (:attributes character))
(println (attr :inteligence))

; Ex 2
; by manipulating the functions list to skip the initial required apply (to pass the argument list to a function
; we can have a cleaner function call inside the reduce function.
(defn my-comp
  [& fs]
  (fn [& args]
    (reduce (fn [arg current-function]
              (current-function arg)
              )
            (apply (last fs) args)
            (rest (reverse fs)))))

;in this example (http://stackoverflow.com/a/21418527/353971), reduce is used straight away, and the reduce function
;returns a new function that uses the rest parameter, BUT
;the main idea here is to use reduce to build a function from the previous functions, when
;the function list is empty, the final anonymous function is returned.
(defn my-comp2 [& fs]
  (reduce (fn [f g]
            #(f (apply g %&))) fs))

; Ex 3
; copied from source
(defn my-assoc-in
  [m [k & ks] v]
  (if ks
    (assoc m k (my-assoc-in (get m k) ks v))
    (assoc m k v)))

; Ex 4
(def x {:a { :b 1 }})
(update-in x [:a :b] inc)


; Ex 5
; java.lang.RuntimeException: Can't have fixed arity function with more params than variadic function
(defn my-update-in
  ([m v f & args] (my-update-in m v f args))
  ([m [k & ks] f args]
    (if ks
      (assoc m k (my-update-in (get m k) ks f args))
      (update m k (apply f (get m k) args)))))

; basically the same as the source.  You cannot overload the function because
; you could get the same exception as above, so the "trick" is to define a function
; inside a let scope.
(defn my-update-in
  [m v f & args]
  (let [up (fn up [m [k & ks] f args]
               (if ks
                 (assoc m k (up (get m k) ks f args))
                 (assoc m k (apply f (get m k) args))))]
    (up m v f args)))

