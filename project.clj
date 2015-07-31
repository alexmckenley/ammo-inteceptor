(defproject ammo-inteceptor "0.1.0-SNAPSHOT"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0-RC1"]
                 [org.clojure/clojurescript "0.0-3308"]
                 [org.clojure/core.async "0.1.242.0-44b1e3-alpha"]
                 [khroma "0.0.2"]
                 [prismatic/dommy "0.1.2"]
                 [cljs-ajax "0.3.14"]]
  :source-paths ["src"]
  :profiles {:dev
             {:plugins [[com.cemerick/austin "0.1.3"]
                        [lein-cljsbuild "1.0.6"]
                        [lein-chromebuild "0.2.1"]]
              :cljsbuild
              {:builds
               {:main
                {:source-paths ["src"]
                 :compiler {:output-to "target/unpacked/ammo_inteceptor.js"
                            :output-dir "target/js"
                            :optimizations :whitespace
                            :pretty-print true}}}}}})
