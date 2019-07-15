(defproject newbie "0.5.1.release"
  :description "Jiliguala newbie project"
  :url "http://example.com/FIXME"
  :min-lein-version "2.0.0"
  :dependencies [
                 [clj-http "3.10.0"]
                 [clj-time "0.15.0"]
                 [cheshire/cheshire "5.8.1"]
                 [cprop/cprop "0.1.13"]
                 [org.clojure/clojure "1.10.0"]
                 [metosin/compojure-api "1.1.11"]
                 [ring/ring-jetty-adapter "1.7.1"]
                 [ring/ring-devel "1.7.1"]
                 [org.clojure/tools.logging "0.4.1"]
                 [org.apache.logging.log4j/log4j-slf4j-impl "2.11.2"]
                 [ring/ring-defaults "0.3.2"]
                 [ring/ring-codec "1.1.2"]
                 [com.novemberain/monger "3.1.0"]
                 ]
  :plugins [[lein-ring "0.12.5"]
            [lein-cloverage "1.1.1"]]
  :ring {:handler newbie.handler/app}
  :profiles {
             :dev {:dependencies [[javax.servlet/javax.servlet-api "3.1.0"]
                                  [ring/ring-mock "0.3.2"]]
                   :resource-paths ["resources/dev"]}
             :prod {:resource-paths ["resources/prod"]}}
  )
