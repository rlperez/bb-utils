#!/usr/bin/env bb

(ns bb-utils.git
  (:require [babashka.process :refer [shell]]
            [babashka.fs :as fs]
            [clojure.string :as str]))

(defn git-branch
  [repo-path]
  (shell {:out :string :dir repo-path} "git" "branch"))

(defn git-status-short
  [repo-path]
  (shell {:out :string :dir repo-path} "git" "status" "--short"))

(defn git-pull
  [repo-path]
  (shell {:out :string :dir repo-path} "git" "pull"))

(defn get-path-or-default
  ([path]
   (get-path-or-default path nil))
  ([path rest]
   (let [path (or path "./")
         rest (or rest nil)]
     (str/replace (str path rest) "/./" "/"))))

(defn filter-current-branch
  [output]
  (let [branches (str/split-lines output)]
    (first (filter #(str/starts-with? % "*") branches))))

(defn get-current-branch
  ;; TODO: Detached head doesn't seem right.
  ;;       Probably should skip this if it happens
  [repo-path]
  (let [branch (filter-current-branch (git-branch repo-path))]
    (if branch
      (subs branch 2)
      nil)))

(defn changes? [line]
  (and (not-empty line)
       (and (>= (count line) 2)
            (not (= (subs line 0 2) "??")))))

(defn any-changes?
  [repo-path]
  (some changes? (str/split-lines (str (git-status-short repo-path)))))

(defn git-repo?
  [repo-path]
  (and (fs/directory? repo-path)
       (fs/exists? (str repo-path "/.git"))))

(defn get-repos
  [path]
  (let [base-path (get-path-or-default path)]
    (map #(.toString %) (filter git-repo? (fs/list-dir base-path)))))

(defn pull
  [repo-path]
  (if (any-changes? repo-path)
    (println (str "Skipping " repo-path ". Uncommitted changes found."))
    (git-pull repo-path)))

(defn pull-all
  [path]
  (let [path (get-path-or-default path)
        repos (get-repos path)]
    (doseq [repo-path repos]
      (let [full-repo-path (get-path-or-default path repo-path)]
        (pull full-repo-path)))))
