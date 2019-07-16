(ns newbie.handler
  (:require [compojure.api.sweet :refer :all]
            [newbie.domain.schema :refer :all]
            [newbie.service.user-service :as user]
            [newbie.service.weixin-service :as weixin]
            [newbie.service.base-service :as base]
            [ring.util.http-response :refer :all]
            [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.middleware.reload :refer [wrap-reload]]
            [schema.core :as s])
  (:gen-class))

(def app
  (api
    {:swagger
     {:ui "/"
      :spec "/swagger.json"
      :data {:info {:title "Newbie"
                    :description "Jiliguala newbie project"}
             :tags [{:name "api", :description "Api"}
                    {:name "weixin", :description "Weixin api"}]}}}

    (context "/api" []
      :tags ["api"]

      (GET "/hello-world" []
        :return Response
        :summary "Hello World"
        (ok (base/hello-world)))

      (GET "/weixin-info" []
        :return Response
        :tags ["weixin"]
        :query-params [code :- String, state :- String]
        :summary "微信回调接口new"
        (ok (weixin/weixin-info code state)))

      (GET "/weixin-auth-info" []
        :tags ["weixin"]
        :summary "需要在微信客户端中运行"
        (permanent-redirect (weixin/weixin-auth-info)))
      )

    (context "/user" []
      :tags ["user"]

      (POST "/register" []
        :summary "注册用户"
        :body [body {:openid s/Str
                     (s/optional-key :sex) s/Str
                     (s/optional-key :nickname) s/Str
                     (s/optional-key :country) s/Str
                     (s/optional-key :province) s/Str}]
        (ok (user/register body)))

      (POST "/login" []
        :summary "注册用户"
        :body [body {:openid s/Str}]
        (let [response (user/login (:openid body))]
          (if (:isSuccess response) (assoc-in (ok response) [:session :identity] body)
                                    (ok response))))

      (POST "/logout" []
        :summary "注销用户"
        :body [body {:openid s/Str}]
        (assoc-in (ok) [:session :identity] nil))

      (POST "/order" []
        :summary "下订单"
        :body [body {:openid s/Str
                     :pid s/Str}]
        (ok (user/order (:openid body) (:pid body))))
      )))

(def reload-app
  (wrap-reload #'app))

(defn -main
  [& args]
  (run-jetty reload-app {:port (Integer/valueOf (or (System/getenv "port") "3000"))}))
