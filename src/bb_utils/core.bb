#!/usr/bin/env bb

(ns bb-utils.core
  (:require [babashka.cli :as cli]
            [bb-utils.string :refer [from-base64 from-hex]]
            [bb-utils.git :refer [get-repos pull-all]]))

(defn help [_]
  (println "Usage: bbut <command> [...args]")
  (println "\nAvailable commands:")
  (println "  string from-base64 <string>     Convert Base64 to string")
  (println "  string from-hex <string>        Convert hex to string")
  (println "  git get-repos [path]            Lists all git repos at path. Default path '.'")
  (println "  git pull-all  [path] [branch]   Pull all repos at path. Default path '.'. Default branch is current branch")
  (println "      Options:")
  (println "TODO      [branch]             Checkout the branch and pull")
  (println "TODO      --include-changed    Stashes changes, pull, then apply the changes")
  (println "TODO      --reset-branch       Returns repo to original branch, if combined with --include-changed will apply changes on original branch")
  (println "TODO      --merge              Use with [branch] and if current branch is not original branch it will merge original branch into given branch")
  (println "\nUse 'bbut --help' for help."))

(def table
  [{:cmds ["string" "from-hex"] :fn (fn [{:keys [args]}] (println (from-hex (first args))))}
   {:cmds ["string" "from-base64"] :fn (fn [{:keys [args]}] (println (from-base64 (first args))))}
   {:cmds ["git" "get-repos"] :fn (fn [{:keys [args]}] (run! println (get-repos (first args))))}
   {:cmds ["git" "pull-all"] :fn (fn [{:keys [args]}] (pull-all (first args)))}
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

