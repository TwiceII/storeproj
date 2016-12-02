(ns storeproj.core
  (:require [bidi.bidi :as bidi]
            [yada.yada :as yada]
            [selmer.parser :as selmer]
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
  [res-id html-tmpl-name html-title]
  (let [html-path (str "public/templates/" html-tmpl-name)]
    (yada/resource {:id res-id
                    :produces {:media-type "text/html"
                               :charset "UTF-8"}
                    :methods {:get
                              {:response (fn[ctx]
                                           (respond-with-file ctx
                                                              (selmer/render-file html-path
                                                                                  {:page-title html-title})
                                                              nil))}}})))
;;                                          (respond-with-file ctx
;;                                                             (new java.io.File html-tmpl-path)
;;                                                             nil))}}}))

(def bidi-routes
  ["" [["/" (html-template-resource ::index "index.html" "Главная")]
       ["/login" (html-template-resource ::login "login.html" "Вход в систему")]
       ["/sales" (html-template-resource ::sales "sales.html" "Продажи")]
;;        ["/cashflows" (html-template-resource ::sales "resources/public/templates/cashflows.html")]
       ["/monitoring/sales" (html-template-resource ::sales-search "sales_search.html" "Поиск продаж")]
       ["/goods/details" (html-template-resource ::goods-details "goods_details.html" "Описание товара")]
       ["/goods/all" (html-template-resource ::goods-all "goods_search.html" "Все товары")]
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
