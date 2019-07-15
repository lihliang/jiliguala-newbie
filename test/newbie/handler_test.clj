(ns newbie.handler-test
  (:require [clojure.test :refer :all]
            [cheshire.core :as cheshire]
            [ring.mock.request :as mock]
            [newbie.handler :refer :all]
            [newbie.handler :refer :all]
            [newbie.repository.mongodb :as mg]))

(defn db_init "init mongo db"
  [test-fn]
  (mg/clear-users)
  (mg/clear-orders)
  (mg/clear-products)
  (test-fn))

(deftest test-app
  (testing "main route"
    (let [response (app (mock/request :get "/api/hello-world"))
          body (cheshire/parse-string (slurp (:body response)) true)]
      (is (= (:status response) 200))
      (is (= body {:isSuccess true
                   :errcode 0
                   :errmsg ""
                   :result {:msg "Hello World"}}))))

  (testing "weixin auth route"
    (let [response (app (mock/request :get "/api/weixin-auth-info"))]
      (is (= (:status response) 308))))

  (testing "weixin info route"
    (let [response (app (-> (mock/request :get "/api/weixin-info")
                            (mock/query-string {:code "code" :state "state"})))
          body (cheshire/parse-string (slurp (:body response)) true)]
      (is (= (:status response) 200))
      (is (= (select-keys body [:isSuccess :errcode :result]) {:isSuccess false
                                                               :errcode 40029
                                                               :result {}}))))

  (testing "user register route"
    (let [response (app (-> (mock/request :post "/user/register")
                            (mock/json-body {:openid "test"
                                             :sex "1"})))
          body (cheshire/parse-string (slurp (:body response)) true)]
      (is (= (:status response) 200))
      (is (= body {:isSuccess true
                   :errcode 0
                   :errmsg ""
                   :result "register succeed"}))))

  (testing "user already register route"
    (let [response (app (-> (mock/request :post "/user/register")
                            (mock/json-body {:openid "test"
                                             :sex "1"})))
          body (cheshire/parse-string (slurp (:body response)) true)]
      (is (= (:status response) 200))
      (is (= body {:isSuccess false
                   :errcode 40100
                   :errmsg "already registered"
                   :result {}}))))

  (testing "user login route"
    (let [response (app (-> (mock/request :post "/user/login")
                            (mock/json-body {:openid "test"})))
          body (cheshire/parse-string (slurp (:body response)) true)
          result (cheshire/parse-string (:result body) true)]
      (is (= (:status response) 200))
      (is (= (select-keys body [:isSuccess :errcode :errmsg]) {:isSuccess true
                                                               :errcode 0
                                                               :errmsg ""}))
      (is (= (select-keys result [:openid :sex]) {:openid "test"
                                                  :sex "1"}))))

  (testing "unregister user login route"
    (let [response (app (-> (mock/request :post "/user/login")
                            (mock/json-body {:openid "test1"})))
          body (cheshire/parse-string (slurp (:body response)) true)]
      (is (= (:status response) 200))
      (is (= body {:isSuccess false
                   :errcode 40200
                   :errmsg "unregister user"
                   :result {}}))))
  )

(use-fixtures :once db_init)
(run-tests)