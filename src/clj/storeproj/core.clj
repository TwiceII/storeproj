(ns storeproj.core
  (:require [bidi.bidi :as bidi]
            [yada.yada :as yada]
            [yada.resources.file-resource :refer [new-file-resource respond-with-file]]
            [schema.core :as s]
            [yada.resources.classpath-resource :refer [new-classpath-resource]]
            [buddy.sign.jwt :as jwt]
            [clj-time.core :as time]
            [clojure.java.io :as io])
  (:gen-class))

(def ^:const default-port 81)
(def ^:const secret "9eLPqOKtc3wiJImA69ybMXGVjnHMbZM9+pXs")

;; aleph-server
(defonce server (atom nil))


(defn html-template-resource
  [res-id html-tmpl-path]
  (yada/resource {:id res-id
                  :produces {:media-type "text/html"}
                  :methods {:get
                            {:response (fn[ctx]
                                         (respond-with-file ctx
                                                            (new java.io.File html-tmpl-path)
                                                            nil))}}}))

(def bidi-routes
  ["" [["/" (html-template-resource ::index "resources/public/templates/index.html")]
       ["/login" (html-template-resource ::login "resources/public/templates/login.html")]
       ["/sales" (html-template-resource ::sales "resources/public/templates/sales.html")]
       ["/goods/details" (html-template-resource ::goods-details "resources/public/templates/goods_details.html")]
       ["/goods/all" (html-template-resource ::goods-all "resources/public/templates/goods_search.html")]
       ["" (yada/yada (new-classpath-resource "public"))]
   ]])


;;;
;;; Server functions
;;;
(defn start-web-server!
  []
  (println "start-web-server!")
  (let [port default-port
        listener (yada/listener bidi-routes {:port port})]
    (reset! server listener)))


(defn restart!
  []
  ((:close @server))
  (start-web-server!))


(defn start-app!
  []
  (do
    (println "App started...")
    (start-web-server!)))


(defn -main [& args]
  (start-app!))
