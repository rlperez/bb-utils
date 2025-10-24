#!/usr/bin/env bb
(ns bb-utils.core
  (:require [babashka.cli :as cli]
            [bb-utils.string :refer [from-base64 from-hex]]))

(defn help [_]
  (println "Usage: bbut <command> [...args]")
  (println "\nAvailable commands:")
  (println "  string from-base64 <string>  Convert Base64 to string")
  (println "  string from-hex <string>     Convert hex to string")
  (println "\nUse 'bbut --help' for help."))

(def table
  [{:cmds ["string" "from-hex"] :fn (fn [{:keys [args]}] (println (from-hex (first args))))}
   {:cmds ["string" "from-base64"] :fn (fn [{:keys [args]}] (println (from-base64 (first args))))}
   {:cmds ["--help"] :fn help}
   {:cmds []         :fn help}])

(defn -main [& args]
  (try
    (cli/dispatch table args {:exec-args {}})
    (catch Exception e
      (binding [*out* *err*]
        (println "Error:" (.getMessage e)))
      (System/exit 1))))

(when (= *file* (System/getProperty "babashka.file"))
  (apply -main *command-line-args*))
