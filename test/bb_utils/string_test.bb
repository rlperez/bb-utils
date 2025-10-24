#!/usr/bin/env bb

(ns bb-utils.string-test
  (:require [bb-utils.string :refer [from-base64 from-hex]]
            [clojure.test :refer [deftest is testing run-tests]]))

(deftest string-tests
  (testing "Base64 conversions"
    (is (= "Hello" (from-base64 "SGVsbG8="))))
  (testing "Hex conversions"
    (is (= "Hello" (from-hex "48656c6c6f")))))

(defn -main []
  (run-tests))
