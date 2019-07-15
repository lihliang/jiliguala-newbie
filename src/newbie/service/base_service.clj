(ns newbie.service.base-service
  (:require [newbie.util.response-util :as res]))

(defn hello-world []
  (res/succResponse {:msg "Hello World"}))
