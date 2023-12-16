(require '[babashka.pods :as pods])
(pods/load-pod 'org.babashka/mysql "0.1.1")
(require '[pod.babashka.mysql :as mysql])
(require '[babashka.fs :as fs])
(require '[clojure.string :as str])

(def db {:dbtype   "mysql"
         :host     "localhost"
         :port     13306
         :dbname   "withings"
         :user     "user"
         :password "secret"})

(comment
  (mysql/execute! db ["select version()"])
  (mysql/execute! db ["select  * from measures"])

  (fs/readable? "2023-06-25.csv")
  (def lines (fs/read-all-lines "2023-06-25.csv"))
  (doseq [line lines]
    (when-not (str/starts-with? line ";")
      (let [[_ _ name _ cid secret email] (str/split line #",")]
        (mysql/execute! db ["insert into users (name,cid,secret,email)
                             values
                             (?,?,?,?)" name cid secret email]))))
  )

(defn insert-users-from-file
  [fname]
  (try
    (when-not (fs/readable? fname)
      (throw (Exception. (str fname "does not exist"))))
    (doseq [line (fs/read-all-lines fname)]
      (when-not (str/starts-with? line ";")
        (let [[_ _ name _ cid secret email] (str/split line #",")]
          (mysql/execute!
           db
           ["insert into users (name,cid,secret,email) values (?,?,?,?)"
            name cid secret email]))))
    (catch Exception e (println (.getMessage e)))))



(defn update-line-id
  [fname]
  (try
    (when-not (fs/readable? fname)
      (throw (Exception. (str fname "does not exist"))))
    (doseq [line (fs/read-all-lines fname)]
      (let [[_ _ name line_id] (map str/trim (str/split line #"\|"))]
        (println name line_id)
        (mysql/execute!
           db
           ["update users set line_id=? where name =?"
            line_id name])))
    (catch Exception e (println (.getMessage e)))))

(comment
  (insert-users-from-file "2023-06-25.txt")
  (update-line-id "2023-06-25-line-id.txt")
  :rcf)
