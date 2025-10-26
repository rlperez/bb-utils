#!/usr/bin/env bb

(ns bb-utils.git
  (:require [babashka.process :refer [shell]]
            [babashka.fs :as fs]
            [clojure.string :as str]))

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
  (let [base-path (or path "./")]
    (map #(.toString %) (filter git-repo? (fs/list-dir base-path)))))

(defn pull-all
  [path]
  (let [base-path (or path "./")
        repos (get-repos base-path)]
    (map #((if (any-changes? (str base-path %))
             (println (str "Skipping " % ". Uncommitted changes found."))
             (shell {:out :string :dir (str base-path %)} "git" "pull")))
         repos)))
