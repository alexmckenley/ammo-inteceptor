(ns ammo-inteceptor.background
	(:require 
		[clojure.set :refer [rename-keys]]
		[ajax.core :refer [ajax-request json-request-format json-response-format]]))

(def headerPrefix "Ammo-")

(def restrictedHeaders '(Accept-Charset
	Accept-Encoding
	Access-Control-Request-Headers
	Access-Control-Request-Method
	Connection
	Content-Length
	Cookie
	Content-Transfer-Encoding
	Date
	Expect
	Host
	Keep-Alive
	Origin
	Referer
	TE
	Trailer
	Transfer-Encoding
	Upgrade
	User-Agent
	Via))

(defn js-func [f]
	(fn [& more] (clj->js (apply f (map #(js->clj % :keywordize-keys true) more)))))

(defn transform-headers [headers]
	(map (fn [a] (assoc a :name (clojure.string/replace (:name a) headerPrefix ""))) headers))

(defn on-before-send-headers [request]
	{:requestHeaders (transform-headers (:requestHeaders request))})

(defn prepend-headers [headers]
	(clojure.set/rename-keys headers (zipmap (map #(keyword %) restrictedHeaders) (map #(keyword (str headerPrefix %)) restrictedHeaders))))

(defn send-request [request _ responsefn]
	(let [opts {:uri (:url request) 
				:method (keyword (clojure.string/lower-case (:method request)))
				:params (:params request)
				:headers (prepend-headers (:headers request))
				:handler #(responsefn (clj->js {:error (not (first %))
												:data (last %)}))
				:format (json-request-format)
				:response-format (json-response-format {:keywords? true})}]
		(if (:debug request)
			(.log js/console (clj->js opts)))
		(ajax-request opts)
		true))

(defn register-event-listeners []
	(.addListener js/chrome.webRequest.onBeforeSendHeaders (js-func on-before-send-headers) (clj->js {:urls ["*://*.spotilocal.com/*"]}) #js ["blocking" "requestHeaders"])
	(.addListener js/chrome.runtime.onMessageExternal (js-func send-request)))

(defn init []
	(.log js/console "Registering event listeners...")
	(register-event-listeners))
