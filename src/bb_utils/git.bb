#!/usr/bin/env bb

(ns bb-utils.git
  (:require [babashka.process :refer [shell]]
            [babashka.fs :as fs]
            [clojure.string :as str]))

(defn get-path-or-default
  ([path]
   (get-path-or-default path nil))
  ([path rest]
   (let [path (or path "./")
         rest (or rest nil)]
     (str/replace (str path rest) "/./" "/"))))

(defn- filter-current-branch
  [output]
  (let [branches (str/split-lines output)]
    (first (filter #(str/starts-with? % "*") branches))))

(defn- get-current-branch
  [path]
  (let [branch (-> (shell {:out :string :dir path} "git" "branch") :out filter-current-branch)]
    (if branch
      (subs branch 2)
      nil)))

(defn- change? [line]
  (and (not-empty line)
       (and (>= (count line) 2)
            (not (= (subs line 0 2) "??")))))

(defn- any-changes?
  [path]
  (some change? (str/split-lines (-> (shell {:out :string :dir path} "git" "status" "--short") :out str))))

(defn- git-repo?
  [path]
  (and (fs/directory? path)
       (fs/exists? (str path "/.git"))))

(defn get-repos
  [path]
  (let [base-path (get-path-or-default path)]
    (map #(.toString %) (filter git-repo? (fs/list-dir base-path)))))

(defn pull
  [path]
  (if (any-changes? path)
    (println (str "Skipping " path ". Uncommitted changes found."))
    (shell {:out :string :dir path} "git" "pull")))

(defn pull-all
  [path]
  (let [path (get-path-or-default path)
        repos (get-repos path)]
    (doseq [repo-path repos]
      (let [full-repo-path (get-path-or-default path repo-path)]
        (pull full-repo-path)))))
