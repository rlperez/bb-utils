#!/usr/bin/env bb

(ns bb-utils.string
  (:import [java.util Base64]))

(defn- hex-to-base64 [hex-str]
  (let [bytes (byte-array (mapv (fn [[a b]]
                                  (unchecked-byte (Integer/parseInt (str a b) 16)))
                                (partition 2 hex-str)))
        base64-str (.encodeToString (java.util.Base64/getEncoder) bytes)]
    base64-str))

(defn from-base64 [base64-str]
  (String. (.decode (Base64/getDecoder) base64-str) "UTF-8"))

(defn from-hex [hex-str]
  (from-base64 (hex-to-base64 hex-str)))
