(ns ammo-inteceptor.background
  (:require [khroma.log :as console]
            [khroma.runtime :as runtime]
            [cljs.core.async :refer [>! <!]]
            [ajax.core :refer [GET]])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(defn handler [response]
  (console/log response))

(defn js-func [f]
  (fn [& rest] (clj->js (apply f (map #(js->clj % :keywordize-keys true) rest)))))

(defn error-handler [{:keys [status status-text]}]
  (.log js/console (str "something bad happened: " status " " status-text)))

(defn transform-headers [headers]
  (map (fn [a] (assoc a :name (clojure.string/replace (:name a) "Test-" ""))) headers))

(defn on-before-send-headers [request]
    {:requestHeaders (transform-headers (:requestHeaders request))})

(defn get-token []
  (GET "https://tpcaahshvs.spotilocal.com:4371/simplecsrf/token.json?ref&cors" {:handle handler
                                                                                :error-handler error-handler
                                                                                :headers {:Test-Origin "https://embed.spotify.com"}}))

(defn init []
  (console/log "Requesting token from spotify...")
  (.addListener js/chrome.webRequest.onBeforeSendHeaders (js-func on-before-send-headers) (clj->js {:urls ["<all_urls>"]}) #js ["blocking" "requestHeaders"])
  (get-token))
